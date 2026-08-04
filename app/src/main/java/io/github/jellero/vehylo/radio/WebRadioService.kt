package io.github.jellero.vehylo.radio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import io.github.jellero.vehylo.MainActivity

class WebRadioService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stationStore: RadioStationStore
    private var mediaPlayer: MediaPlayer? = null
    private var currentStation: RadioStation? = null

    override fun onCreate() {
        super.onCreate()
        stationStore = RadioStationStore(this)
        createNotificationChannel()

        mediaSession = MediaSessionCompat(this, SESSION_TAG).apply {
            setCallback(
                object : MediaSessionCompat.Callback() {
                    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                        mediaId?.let(stationStore::find)?.let(::play)
                    }

                    override fun onPlay() {
                        val player = mediaPlayer
                        if (player != null && runCatching { !player.isPlaying }.getOrDefault(false)) {
                            player.start()
                            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                            currentStation?.let { startForeground(NOTIFICATION_ID, buildNotification(it)) }
                        } else {
                            currentStation?.let(::play)
                        }
                    }

                    override fun onPause() {
                        mediaPlayer?.takeIf { runCatching { it.isPlaying }.getOrDefault(false) }?.pause()
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
                    }

                    override fun onStop() {
                        stopPlayback()
                    }
                }
            )
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        updatePlaybackState(PlaybackStateCompat.STATE_NONE)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot = BrowserRoot(ROOT_ID, null)

    override fun onLoadChildren(
        parentMediaId: String,
        result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        if (parentMediaId != ROOT_ID) {
            result.sendResult(emptyList())
            return
        }
        val items = stationStore.list().map { station ->
            val description = MediaDescriptionCompat.Builder()
                .setMediaId(station.id)
                .setTitle(station.name)
                .setSubtitle("Web radio")
                .build()
            MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
        }
        result.sendResult(items)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        when (intent?.action) {
            ACTION_PLAY -> {
                val id = intent.getStringExtra(EXTRA_STATION_ID)
                val station = id?.let(stationStore::find)
                if (station != null) play(station)
            }

            ACTION_STOP -> stopPlayback()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopPlayback(stopService = false)
        mediaSession.release()
        super.onDestroy()
    }

    private fun play(station: RadioStation) {
        startForeground(NOTIFICATION_ID, buildNotification(station, connecting = true))
        currentStation = station
        mediaPlayer?.release()
        updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(station.streamUrl)
            setOnPreparedListener { player ->
                player.start()
                mediaSession.setMetadata(
                    android.support.v4.media.MediaMetadataCompat.Builder()
                        .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID, station.id)
                        .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE, station.name)
                        .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST, "Vehylo WebRadio")
                        .build()
                )
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                startForeground(NOTIFICATION_ID, buildNotification(station))
            }
            setOnErrorListener { _, _, _ ->
                updatePlaybackState(PlaybackStateCompat.STATE_ERROR)
                stopForeground(STOP_FOREGROUND_REMOVE)
                true
            }
            prepareAsync()
        }
    }

    private fun stopPlayback(stopService: Boolean = true) {
        mediaPlayer?.runCatching { stop() }
        mediaPlayer?.release()
        mediaPlayer = null
        currentStation = null
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (stopService) stopSelf()
    }

    private fun updatePlaybackState(state: Int) {
        val actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_STOP
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(actions)
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build()
        )
    }

    private fun buildNotification(station: RadioStation, connecting: Boolean = false): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(station.name)
            .setContentText(if (connecting) "Connessione in corso" else "Vehylo WebRadio")
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Web radio",
                    NotificationManager.IMPORTANCE_LOW,
                )
            )
        }
    }

    companion object {
        const val ROOT_ID = "vehylo.radio.root"
        const val ACTION_PLAY = "io.github.jellero.vehylo.radio.PLAY"
        const val ACTION_STOP = "io.github.jellero.vehylo.radio.STOP"
        const val EXTRA_STATION_ID = "station_id"

        private const val SESSION_TAG = "VehyloWebRadio"
        private const val CHANNEL_ID = "vehylo_webradio"
        private const val NOTIFICATION_ID = 2301
    }
}
