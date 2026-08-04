# Vehylo

Vehylo è un MVP Android per raccogliere e visualizzare telemetria veicolo da:

- adattatori OBD-II compatibili ELM327 via Bluetooth Classic;
- inclinometri BLE con protocollo configurabile;
- sorgente demo integrata per sviluppare la UI senza hardware.

> Il progetto opera in sola lettura. Non invia comandi di codifica, scrittura ECU, reset adattamenti o cancellazione automatica dei DTC.

## Stato del progetto

La versione `0.1.0` include:

- dashboard Jetpack Compose;
- valori demo animati;
- parser PID OBD-II standard per RPM, velocità, temperatura refrigerante, acceleratore e tensione ECU;
- trasporto RFCOMM/SPP per adattatori ELM327 già associati;
- client BLE GATT configurabile;
- decoder di esempio dell'inclinometro: due `Float32` little-endian (`roll`, `pitch`);
- test unitari dei decoder.

La connessione a dispositivi reali non è ancora esposta dalla UI: indirizzo Bluetooth, UUID GATT e profilo del veicolo saranno aggiunti nel prossimo incremento.

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

- usare inizialmente solo PID in lettura;
- non interrogare la rete CAN con frequenze eccessive;
- non utilizzare l'app durante la guida se il dispositivo non è montato e l'interfaccia non è conforme alle norme locali;
- verificare sempre il protocollo dell'inclinometro prima di usare i valori per decisioni di sicurezza.

## Licenza

Apache-2.0.
