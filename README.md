# Vehylo

Vehylo è un'applicazione Android per telemetria, mapping e diagnostica veicolo.

Le sorgenti previste sono:

- adattatori OBD-II compatibili ELM327 via Bluetooth Classic;
- inclinometri BLE con protocollo configurabile;
- frame e segnali proprietari descritti da profili veicolo;
- sorgente demo integrata per sviluppare la UI senza hardware.

## Stato del progetto

La versione `0.2.0` introduce le fondazioni per mapping personalizzati, diagnostica e future operazioni di scrittura controllate.

### Telemetria

- dashboard Jetpack Compose;
- valori demo animati;
- parser PID OBD-II standard per RPM, velocità, temperatura refrigerante, acceleratore e tensione ECU;
- trasporto RFCOMM/SPP per adattatori ELM327 già associati;
- client BLE GATT configurabile;
- decoder di esempio dell'inclinometro: due `Float32` little-endian (`roll`, `pitch`).

### Mapping personalizzati

Un `SignalMapping` descrive:

- ID del frame;
- bit iniziale e lunghezza da 1 a 63 bit;
- big endian o little endian;
- valore signed o unsigned;
- scala, offset e unità;
- origine manuale, wizard, appresa o importata;
- confidenza opzionale per i mapping inferiti.

L'app include un wizard Compose in quattro passaggi e un motore di autoapprendimento assistito.

L'autoapprendimento non attribuisce autonomamente un significato semantico certo a byte sconosciuti. Può:

1. individuare campi che cambiano nei frame registrati;
2. confrontarli con un valore di riferimento;
3. stimare posizione, endianess, segno, scala e offset;
4. ordinare i candidati per correlazione, errore e confidenza;
5. richiedere la validazione dell'utente prima del salvataggio.

I profili veicolo sono versionati e possono contenere mapping e capacità diagnostiche differenti per marca, modello e centralina.

### Diagnostica

Il servizio read-only supporta attualmente:

- stato MIL e numero DTC confermati tramite Mode 01 PID 01;
- DTC memorizzati tramite Mode 03;
- DTC pending tramite Mode 07;
- DTC permanenti tramite Mode 0A;
- parsing delle risposte ELM327 con o senza spazi.

L'interfaccia diagnostica reale richiede ancora la selezione dell'adattatore e la gestione della sessione dalla UI.

### Scrittura futura

È presente un `VehicleCommandGateway`, ma la scrittura è **disabilitata per default**. Per eseguire un comando devono essere soddisfatte tutte le condizioni seguenti:

- `writeEnabled` esplicitamente attivo;
- service ID presente in allowlist;
- rischio entro la soglia configurata;
- velocità veicolo disponibile e inferiore a 0,5 km/h;
- conferma testuale corrispondente al comando;
- autorizzazione monouso valida per 30 secondi.

Non sono implementati bypass SecurityAccess, immobilizer, gestione chiavi, firmware flashing o attivazione automatica di comandi proprietari.

## Requisiti

- Android Studio compatibile con Android Gradle Plugin 9.1.1;
- JDK 17 o successivo;
- Android SDK 37;
- dispositivo Android 8.0 (API 26) o successivo.

## Build

```bash
./gradlew test assembleDebug
```

L'APK debug viene prodotto in:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Protocollo inclinometro

Il decoder predefinito interpreta otto byte:

```text
byte 0..3  roll  Float32 little-endian
byte 4..7  pitch Float32 little-endian
```

Per un sensore diverso, passare un decoder personalizzato a `BleInclinometerSource`.

## Android Auto

Vehylo nasce come applicazione Android per telefono/tablet. Una dashboard telemetrica personalizzata non appartiene attualmente alle categorie standard pubblicabili tramite la Car App Library. Il codice Android Auto non viene quindi dichiarato nel manifest per evitare una classificazione impropria. Un'eventuale integrazione futura dovrà usare una categoria ufficialmente supportata oppure Android Automotive OS su hardware controllato.

## Sicurezza

- mantenere inizialmente i profili in sola lettura;
- non interrogare la rete CAN con frequenze eccessive;
- non accettare automaticamente un mapping inferito;
- registrare comando, profilo, centralina, conferma e risposta prima di introdurre scritture reali;
- non utilizzare l'app durante la guida se il dispositivo non è montato e l'interfaccia non è conforme alle norme locali;
- verificare sempre il protocollo dell'inclinometro prima di usare i valori per decisioni di sicurezza.

## Prossimi incrementi

- persistenza Room dei profili;
- import/export JSON con migrazioni di schema;
- acquisizione guidata dei frame durante azioni controllate;
- selezione adattatore e dispositivi BLE dalla UI;
- schermata DTC collegata al trasporto reale;
- log di audit firmato per operazioni modificative;
- supporto UDS read-only per profili specifici.

## Licenza

Apache-2.0.
