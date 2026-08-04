# Vehylo

Vehylo è un'applicazione Android estendibile per telemetria, mapping, diagnostica e strumenti utili a bordo.

Le sorgenti previste sono:

- adattatori OBD-II compatibili ELM327 via Bluetooth Classic;
- inclinometri e altri sensori BLE con protocollo configurabile;
- frame e segnali proprietari descritti da profili veicolo;
- sorgente demo integrata per sviluppare la UI senza hardware.

## Documentazione di prodotto

- [Visione di prodotto, analisi di mercato e roadmap](docs/PRODUCT_VISION_AND_MARKET.md)

Il documento strategico descrive bisogni degli utenti, posizionamento, moduli proposti, priorità, sicurezza, modello economico e roadmap fino alla versione 1.0. Le funzioni pianificate non devono essere considerate già implementate.

## Versione 0.3.0

### Telemetria estesa e dinamica

`TelemetryKey` non è più un enum chiuso: i mapping creati dall'utente possono generare metriche nuove senza modificare il codice sorgente. La dashboard mostra sia i parametri integrati sia i segnali personalizzati ricevuti.

I PID OBD-II generici inclusi comprendono:

- RPM e velocità;
- temperatura refrigerante, olio motore, aria aspirata e aria esterna;
- carico motore e posizione acceleratore;
- pressione carburante e collettore;
- livello carburante;
- tensione centralina.

Temperatura olio cambio, pressione turbo, temperature DPF/scarico, marcia, sterzo e altri dati non universalmente standardizzati sono predisposti come metriche e vengono acquisiti tramite profili/mapping specifici quando il veicolo li espone.

### Web radio

- stazioni aggiunte dall'utente tramite URL stream HTTP/HTTPS;
- riproduzione in foreground con media session;
- catalogo esposto come `MediaBrowserServiceCompat`;
- dichiarazione della categoria media per Android Auto;
- gestione play, pause e stop tramite controlli multimediali.

Gli URL devono puntare allo stream audio diretto, non alla pagina web dell'emittente.

### Dashcam

- anteprima CameraX della fotocamera posteriore;
- registrazione HD;
- audio opzionale;
- salvataggio in `Movies/Vehylo` tramite MediaStore.

La prima implementazione registra solo mentre la schermata dashcam è aperta. Loop recording, registrazione in background, protezione dei clip e trigger da urto richiedono un servizio camera dedicato e ulteriori verifiche sui dispositivi.

### Funzioni richieste dagli utenti

La sezione **Funzioni** contiene:

- catalogo dei moduli disponibili, configurabili o pianificati;
- form locale per descrivere una nuova funzionalità;
- apertura guidata di una issue nel repository GitHub.

Le estensioni basate su dati possono essere configurate senza codice: segnali, formule, unità, stazioni radio e decoder sensori. Nuovo codice eseguibile non viene scaricato dinamicamente: deve passare da una versione verificata dell'app.

### Mapping personalizzati

Un `SignalMapping` descrive:

- ID del frame;
- bit iniziale e lunghezza da 1 a 63 bit;
- big endian o little endian;
- valore signed o unsigned;
- scala, offset e unità;
- origine manuale, wizard, appresa o importata;
- confidenza opzionale per i mapping inferiti.

Il motore di autoapprendimento individua campi variabili, li confronta con valori di riferimento e propone mapping candidati. Ogni mapping appreso richiede validazione dell'utente.

### Diagnostica e scrittura futura

La diagnostica read-only include stato MIL e DTC stored, pending e permanent. `VehicleCommandGateway` mantiene le future operazioni modificative disabilitate per default e applica allowlist, soglia di rischio, veicolo fermo e autorizzazione a scadenza.

Non sono implementati bypass SecurityAccess, immobilizer, gestione chiavi o firmware flashing.

## Requisiti

- Android Studio compatibile con Android Gradle Plugin 9.1.1;
- JDK 17 o successivo;
- Android SDK 36;
- dispositivo Android 8.0 (API 26) o successivo;
- Android 9 o successivo per l'uso tramite Android Auto.

## Build

```bash
./gradlew test assembleDebug
```

L'APK debug viene prodotto in:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Sicurezza

- mantenere inizialmente i profili in sola lettura;
- non interrogare la rete CAN con frequenze eccessive;
- non accettare automaticamente un mapping inferito;
- registrare comando, profilo, centralina, conferma e risposta prima di introdurre scritture reali;
- non usare la configurazione, la dashcam o schermate complesse durante la guida;
- verificare sensori e mapping prima di usare i valori per decisioni di sicurezza.

## Prossimi incrementi

- persistenza Room di profili, mapping e preferenze;
- import/export JSON con migrazioni di schema;
- selezione adattatore OBD e dispositivi BLE dalla UI;
- acquisizione guidata dei frame durante azioni controllate;
- dashboard e allarmi completamente configurabili;
- loop recording dashcam e protezione clip;
- registro viaggi sincronizzato con GPS;
- supporto UDS read-only per profili specifici.

## Licenza

Apache-2.0.
