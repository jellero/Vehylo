# Vehylo — Visione di prodotto, analisi di mercato e roadmap

**Stato:** documento strategico di prodotto  
**Data di riferimento:** 4 agosto 2026  
**Ambito:** testo e pianificazione; questo documento non implica che tutte le funzioni descritte siano già implementate.

---

## 1. Sintesi esecutiva

Vehylo non deve posizionarsi come un altro lettore OBD-II generico. Dashboard, lettura dei codici guasto, grafici e PID standard sono ormai requisiti minimi di categoria e vengono già offerti da prodotti maturi.

L’opportunità più interessante consiste nel costruire una **piattaforma veicolo estendibile**, capace di:

1. identificare ciò che una specifica automobile rende realmente disponibile;
2. aiutare l’utente a scoprire e mappare segnali non documentati;
3. trasformare dati tecnici in informazioni comprensibili e utilizzabili;
4. correlare telemetria, diagnostica, viaggi, manutenzione, GPS, sensori esterni e video;
5. creare profili veicolo condivisibili, versionati e verificati dalla comunità;
6. consentire richieste di nuove funzionalità direttamente dall’app;
7. introdurre in futuro operazioni di servizio e scrittura soltanto tramite procedure deterministiche, controllate e auditate.

La tesi di prodotto è:

> **Vehylo è la piattaforma che scopre, comprende ed estende i dati della tua automobile.**

Il vantaggio competitivo non sarà il semplice collegamento a un adattatore ELM327, ma la combinazione di:

- profili veicolo verificati;
- mapping guidato e autoapprendimento assistito;
- cronologia personale del comportamento del veicolo;
- diagnostica spiegabile;
- fusione di più sorgenti dati;
- estensioni dichiarative configurabili dagli utenti;
- modello economico trasparente;
- funzioni modificative strettamente controllate.

---

## 2. Obiettivo del prodotto

Vehylo deve rendere accessibili dati e funzioni del veicolo a tre livelli di competenza:

### Livello 1 — Proprietario normale

L’utente vuole sapere:

- se il veicolo sta bene;
- cosa significa una spia;
- se può continuare a guidare;
- quando effettuare manutenzione;
- quanto consuma;
- quali dati sono anomali;
- se una riparazione o una procedura richiedono un’officina.

Non deve conoscere PID, CAN ID, endianess, bit field o servizi UDS.

### Livello 2 — Appassionato e utilizzatore avanzato

L’utente vuole:

- dashboard personalizzate;
- dati specifici del proprio modello;
- log, grafici e confronti;
- allarmi;
- profili importabili;
- procedure guidate;
- integrazione con sensori BLE e hardware esterno;
- analisi di viaggio, pista, traino, fuoristrada o camper.

### Livello 3 — Tecnico, sviluppatore e integratore

L’utente vuole:

- accesso ai frame e ai dati grezzi;
- mapping bit-level;
- formule e decoder;
- importazione ed esportazione;
- analisi temporale e correlazione;
- supporto multi-centralina e multi-sorgente;
- profili versionati;
- strumenti di validazione;
- API locali e integrazioni con hardware e sistemi esterni.

L’interfaccia deve quindi separare nettamente una **modalità semplice** da una **modalità Studio**, evitando di costringere tutti gli utenti a utilizzare strumenti tecnici.

---

## 3. Segmenti di mercato

### 3.1 Telemetria OBD generica

Prodotti rappresentativi: Car Scanner, Torque e applicazioni equivalenti.

Funzioni considerate ormai di base:

- collegamento ELM327;
- PID Mode 01;
- dashboard;
- grafici;
- registrazione dati;
- lettura e cancellazione DTC;
- freeze frame;
- PID personalizzati;
- profili specifici per veicolo.

**Implicazione per Vehylo:** queste funzioni sono necessarie, ma da sole non differenziano il prodotto.

### 3.2 Diagnostica avanzata, service e codifica

Prodotti rappresentativi: Carista, Carly e strumenti specifici per marca.

Valore percepito:

- scansione di più centraline;
- reset manutenzione;
- procedure EPB;
- codifiche di comfort;
- registrazione batteria;
- funzioni normalmente riservate a strumenti professionali.

Problemi ricorrenti:

- compatibilità incompleta;
- funzioni differenti tra modelli apparentemente simili;
- paywall scoperti tardi;
- abbonamenti poco graditi;
- rischio operativo;
- aspettative eccessive rispetto a ciò che il veicolo supporta.

**Implicazione per Vehylo:** mostrare la compatibilità reale prima del pagamento e introdurre scritture soltanto per profili verificati.

### 3.3 Dashboard CAN e progetti personalizzati

Prodotti rappresentativi: RealDash e soluzioni basate su hardware custom.

Utenti tipici:

- preparatori;
- appassionati di motorsport;
- costruttori di kit car;
- installatori di tablet permanenti;
- camperisti e fuoristradisti;
- sviluppatori ESP32, Arduino e CAN gateway.

Richieste tipiche:

- CAN multiplo;
- import/export;
- formule;
- telecamere;
- pulsanti fisici;
- autostart;
- dashboard completamente personalizzabili;
- sorgenti dati simultanee.

**Implicazione per Vehylo:** offrire una modalità Studio potente, ma supportata da wizard e profili pronti.

### 3.4 Gestione dell’automobile

Prodotti rappresentativi: Fuelio, Drivvo e registri manutenzione.

Funzioni ad alta frequenza d’uso:

- rifornimenti;
- consumi;
- costi;
- promemoria;
- manutenzioni;
- assicurazione, bollo e revisioni;
- archivio ricevute;
- veicoli multipli.

**Implicazione per Vehylo:** il diario veicolo genera valore anche quando non ci sono guasti e aumenta la conservazione degli utenti nel tempo.

### 3.5 Dashcam

Prodotti rappresentativi: Droid Dashcam, DailyRoads e dashcam hardware dedicate.

Funzioni considerate mature:

- loop recording;
- registrazione in background;
- avvio automatico;
- protezione clip;
- GPS;
- sovraimpressione di velocità e coordinate;
- rilevamento urti.

Problemi ricorrenti:

- surriscaldamento del telefono;
- limitazioni dei produttori in background;
- consumi elevati;
- affidabilità diversa tra dispositivi;
- gestione complessa dello spazio.

**Implicazione per Vehylo:** una dashcam generica non è sufficiente. Il valore distintivo deve derivare dalla sincronizzazione con OBD, GPS, inclinometro e altri sensori.

### 3.6 Veicoli elettrici e ibridi

Prodotti emergenti e specializzati si concentrano su:

- stato di carica;
- salute batteria;
- tensioni delle celle;
- dispersione tra celle;
- temperature;
- potenza di carica e rigenerazione;
- efficienza;
- degrado osservato;
- report pre-acquisto.

**Implicazione per Vehylo:** EV e ibride richiedono profili specifici e metodologie trasparenti. Il valore SOH comunicato dal BMS non deve essere presentato automaticamente come misura assoluta e infallibile.

---

## 4. Problemi e richieste ricorrenti degli utenti

### 4.1 Compatibilità incerta

L’utente spesso non sa prima dell’acquisto:

- se l’adattatore funzionerà;
- quali centraline saranno raggiungibili;
- quali parametri saranno disponibili;
- se il modello supporta una specifica procedura;
- se un PID proprietario è valido per la propria versione ECU;
- se la frequenza di aggiornamento sarà sufficiente.

#### Risposta di prodotto: Compatibility Scan

Vehylo deve eseguire gratuitamente una scansione iniziale che produca:

- VIN quando disponibile;
- protocollo OBD/CAN;
- qualità e latenza dell’adattatore;
- centraline rilevate;
- PID standard supportati;
- frequenze di aggiornamento effettive;
- profili compatibili;
- funzioni disponibili;
- funzioni sperimentali;
- funzioni non supportate;
- motivazione dell’eventuale incompatibilità.

La scansione deve avvenire prima di qualsiasi acquisto di profilo o modulo.

### 4.2 Dati specifici del veicolo

Gli utenti richiedono frequentemente:

- temperatura olio cambio;
- temperatura olio motore;
- pressione turbo;
- pressione olio;
- stato e saturazione DPF;
- temperature EGT;
- rigenerazione DPF;
- marcia inserita;
- angolo sterzo;
- pressione pneumatici;
- stato porte e luci;
- parametri ABS e airbag;
- correzioni iniettori;
- misfire per cilindro;
- stato batteria 12 V;
- dati batteria di trazione;
- coppia richiesta ed erogata;
- temperature di inverter e motori elettrici.

Molti di questi segnali non sono standardizzati. Vehylo deve quindi trattare il mapping e i profili veicolo come una funzione centrale, non accessoria.

### 4.3 Personalizzazione senza complessità eccessiva

Gli utenti vogliono personalizzazione, ma non vogliono necessariamente diventare tecnici CAN.

Vehylo deve offrire:

- dashboard predefinite per uso quotidiano;
- modelli per traino, pista, fuoristrada, camper, EV e diagnostica;
- drag-and-drop dei widget;
- unità e formati configurabili;
- soglie e allarmi guidati;
- un editor avanzato separato;
- anteprima dei dati;
- validazione delle formule;
- rollback delle modifiche alla dashboard.

### 4.4 Diagnosi comprensibili

Un codice guasto isolato non è sufficiente. Per ogni problema Vehylo dovrebbe mostrare:

- codice e centralina di origine;
- stato stored, pending, permanent o storico;
- data e condizioni della prima e ultima comparsa;
- freeze frame;
- gravità stimata;
- possibile impatto sulla guida;
- sintomi compatibili;
- cause possibili ordinate per evidenza;
- valori anomali correlati;
- controlli non invasivi consigliati;
- dati mancanti per aumentare la certezza;
- indicazione esplicita dell’incertezza;
- possibilità di esportare un report per il meccanico.

Vehylo non deve presentare una correlazione come una diagnosi certa.

### 4.5 Prezzi trasparenti

Gli utenti mostrano forte resistenza verso:

- abbonamenti imposti per funzioni locali;
- compatibilità verificabile soltanto dopo il pagamento;
- rinnovi automatici poco chiari;
- pubblicità invasiva;
- account obbligatori senza necessità tecnica.

#### Modello raccomandato

- OBD standard e Compatibility Scan gratuiti;
- acquisto una tantum per Vehylo Pro locale;
- profili veicolo premium acquistabili separatamente;
- abbonamento soltanto per servizi continuativi con costo reale, come cloud, sincronizzazione multi-dispositivo, elaborazione remota, flotte o backup video;
- funzionamento offline delle funzioni locali;
- nessuna pubblicità durante l’uso in auto.

---

## 5. Posizionamento strategico

### 5.1 Posizionamento principale

Vehylo deve essere presentato come:

> una piattaforma modulare che riconosce il veicolo, scopre i dati disponibili, permette di aggiungere segnali e trasforma telemetria e diagnostica in informazioni pratiche.

### 5.2 Cosa non deve diventare

Vehylo non deve essere percepito come:

- un semplice clone di Torque;
- una dashcam generica con OBD aggiunto;
- un’app radio con strumenti auto;
- uno strumento di hacking ECU;
- un marketplace di plugin eseguibili non verificati;
- una chat AI generica senza accesso contestuale ai dati reali;
- un’app che promette compatibilità universale.

### 5.3 Elementi distintivi

1. **Compatibility Scan prima del pagamento.**
2. **Mapping Studio con esperimenti guidati.**
3. **Database comunitario di profili verificati.**
4. **Diagnostica spiegabile e basata sui dati osservati.**
5. **Baseline personale del veicolo.**
6. **Fusione tra OBD, CAN, BLE, GPS e video.**
7. **Estensioni dichiarative create dagli utenti.**
8. **Service Gateway sicuro per future scritture.**
9. **Modello economico semplice e trasparente.**
10. **Privacy locale per impostazione predefinita.**

---

## 6. Pilastri funzionali

## 6.1 Compatibility Scan

### Obiettivo

Determinare in modo riproducibile ciò che il veicolo e l’adattatore supportano realmente.

### Funzioni

- verifica Bluetooth, BLE, Wi-Fi o USB;
- identificazione adattatore e firmware;
- misurazione latenza, errori e stabilità;
- rilevamento protocollo;
- lettura VIN quando consentita;
- scansione PID Mode 01 supportati;
- rilevamento centraline e indirizzi diagnostici;
- confronto con profili disponibili;
- matrice delle funzioni;
- esportazione report;
- suggerimento dell’adattatore quando quello utilizzato è inadeguato.

### Output

Ogni funzione deve avere uno stato esplicito:

- disponibile;
- disponibile con profilo;
- sperimentale;
- non verificata;
- non supportata;
- bloccata per ragioni di sicurezza.

---

## 6.2 Dashboard e telemetria

### Requisiti minimi

- widget numerici;
- indicatori circolari e lineari;
- grafici temporali;
- stato booleano;
- testo ed enumerazioni;
- mappe e posizione;
- unità configurabili;
- layout telefono e tablet;
- orientamento verticale e orizzontale;
- modalità notturna;
- frequenza di aggiornamento configurabile;
- priorità dei segnali;
- log CSV e JSON;
- replay delle sessioni.

### Dashboard predefinite

- quotidiana;
- diagnostica;
- raffreddamento;
- DPF diesel;
- cambio automatico;
- batteria 12 V;
- EV/ibrida;
- traino;
- fuoristrada;
- pista;
- camper;
- test su strada.

### Allarmi

Gli allarmi devono supportare:

- soglia alta o bassa;
- intervallo valido;
- variazione troppo rapida;
- persistenza temporale;
- combinazione di più segnali;
- confronto con baseline personale;
- avviso visivo, sonoro e vocale;
- soppressione durante condizioni non pertinenti;
- cronologia degli eventi.

---

## 6.3 Vehylo Mapping Studio

### Obiettivo

Permettere la creazione di nuovi segnali senza modificare il codice dell’app.

### Mapping manuale

Un mapping deve poter descrivere:

- sorgente;
- bus o protocollo;
- centralina;
- CAN ID o identificatore risposta;
- bit iniziale;
- lunghezza;
- endianess;
- signed/unsigned;
- scala;
- offset;
- unità;
- formula;
- enumerazioni;
- frequenza attesa;
- condizioni di validità;
- valore non disponibile;
- livello di confidenza;
- versione e provenienza.

### Esperimenti guidati

Il wizard deve poter chiedere azioni controllate come:

- accendere e spegnere una luce;
- premere gradualmente l’acceleratore;
- girare lentamente il volante;
- cambiare posizione del selettore mantenendo il veicolo fermo;
- avviare il climatizzatore;
- attendere il riscaldamento del motore;
- confrontare un valore con uno strumento esterno;
- inclinare il veicolo o usare un inclinometro BLE;
- registrare un evento DPF;
- eseguire una breve prova su strada in sicurezza.

### Analisi automatica

Il motore deve cercare:

- frame che cambiano durante l’esperimento;
- bit e campi correlati;
- segnali booleani;
- valori enumerati;
- contatori;
- periodicità;
- scala e offset probabili;
- endianess;
- valori signed;
- formule candidate;
- correlazione con un valore di riferimento;
- stabilità tra più sessioni.

### Regola fondamentale

L’autoapprendimento non può conoscere automaticamente il significato semantico di un campo soltanto perché varia. Ogni mapping inferito deve essere presentato come candidato e validato dall’utente.

---

## 6.4 Profili veicolo comunitari

### Identità del profilo

Un profilo deve essere legato, per quanto possibile, a:

- marca;
- modello;
- anno o intervallo anni;
- piattaforma;
- motore;
- cambio;
- mercato geografico;
- centralina;
- numero hardware;
- versione software;
- protocollo;
- adattatori verificati.

### Stati di fiducia

- bozza personale;
- importato;
- suggerito;
- confermato da utenti;
- verificato con campioni;
- verificato da maintainer;
- deprecato;
- incompatibile con una specifica versione ECU.

### Evidenze richieste

- descrizione dell’esperimento;
- campioni anonimizzati;
- numero di veicoli confermati;
- condizioni in cui il mapping è valido;
- casi di fallimento;
- versione del decoder;
- autore e revisori;
- firma del pacchetto;
- changelog.

### Distribuzione

I profili devono essere pacchetti dati dichiarativi. Non devono contenere codice arbitrario eseguibile.

---

## 6.5 Diagnostica spiegabile

### Livello base

- stato MIL;
- DTC stored;
- DTC pending;
- DTC permanent;
- freeze frame;
- readiness monitor;
- dati live correlati;
- cancellazione DTC soltanto con conferma esplicita e spiegazione delle conseguenze.

### Livello avanzato

- scansione multi-centralina;
- Mode 06;
- identificazione ECU;
- DTC specifici del costruttore;
- cronologia dei guasti;
- test guidati non invasivi;
- report per officina;
- confronto prima/dopo una riparazione.

### Assistente diagnostico

L’assistente può:

- riassumere il problema;
- proporre controlli;
- evidenziare dati mancanti;
- correlare eventi;
- creare una sessione di registrazione;
- suggerire una dashboard diagnostica;
- generare un report.

Non può:

- dichiarare certezza senza evidenza;
- eseguire scritture direttamente;
- aggirare protezioni ECU;
- suggerire procedure per immobilizer, chiavi o sistemi antifurto;
- nascondere i limiti della compatibilità.

---

## 6.6 Health Timeline e baseline personale

### Obiettivo

Imparare il comportamento normale del singolo veicolo e rilevare deviazioni progressive.

### Esempi di baseline

- tensione minima durante l’avviamento;
- tempo di riscaldamento;
- temperatura olio a un determinato carico;
- pressione collettore e turbo;
- fuel trim;
- frequenza rigenerazioni DPF;
- distanza media tra DTC;
- consumo a velocità costante;
- temperature cambio durante traino;
- dispersione tra celle EV;
- potenza di ricarica a diverse temperature.

### Tipi di avviso

- valore fuori soglia assoluta;
- deviazione dalla propria media;
- tendenza peggiorativa;
- comportamento diverso rispetto a condizioni simili;
- sensore incoerente con segnali correlati;
- dato improvvisamente assente.

### Requisito di trasparenza

Ogni avviso deve mostrare:

- dati utilizzati;
- intervallo temporale;
- confronto effettuato;
- livello di confidenza;
- possibili spiegazioni alternative.

---

## 6.7 Report per veicolo usato

### Scopo

Creare un rapporto tecnico ripetibile prima dell’acquisto o della vendita.

### Contenuti possibili

- identificazione veicolo;
- adattatore e versione app;
- data, luogo opzionale e durata del test;
- centraline rilevate;
- DTC correnti, pending, permanenti e storici;
- freeze frame;
- readiness monitor;
- chilometraggi esposti da moduli differenti;
- stato airbag quando accessibile;
- stato batteria 12 V;
- avviamento;
- temperature;
- pressioni;
- DPF;
- test drive;
- anomalie rispetto al profilo;
- dati EV e batteria di trazione;
- parti del test non disponibili;
- firma e hash del report.

Il report deve distinguere chiaramente tra:

- dato letto direttamente;
- valore calcolato;
- inferenza;
- opinione o suggerimento.

---

## 6.8 Registro viaggi, costi e manutenzione

### Viaggi

- partenza e arrivo;
- distanza;
- durata;
- percorso GPS opzionale;
- consumo;
- carburante o energia utilizzata;
- temperature massime;
- eventi e allarmi;
- stile di guida;
- condizioni ambientali;
- note e tag.

### Manutenzione

- interventi;
- ricambi;
- officina;
- costo;
- chilometraggio;
- documenti e ricevute;
- garanzie;
- prossima scadenza;
- promemoria per data, chilometri, ore motore o condizione reale.

### Costi

- carburante;
- energia;
- assicurazione;
- bollo;
- revisione;
- pneumatici;
- parcheggi e pedaggi;
- riparazioni;
- costo per chilometro;
- costo annuale.

---

## 6.9 Incident Recorder e dashcam telemetrica

### Obiettivo

Sincronizzare video e dati per documentare un evento.

### Sorgenti correlate

- video;
- audio opzionale;
- GPS;
- velocità OBD;
- acceleratore;
- freno quando disponibile;
- angolo sterzo;
- marcia;
- inclinometro BLE;
- accelerometro e giroscopio del telefono;
- DTC comparsi durante l’evento;
- condizioni ambientali.

### Funzioni

- loop recording;
- protezione manuale del clip;
- protezione automatica su urto;
- intervallo precedente e successivo configurabile;
- sovraimpressione opzionale;
- gestione spazio;
- riduzione qualità per temperatura elevata;
- arresto sicuro in caso di surriscaldamento;
- esportazione pacchetto con manifest e hash.

### Limiti

Il sistema non deve promettere valore probatorio automatico. Integrità del file, catena degli hash e metadati aumentano la tracciabilità ma non sostituiscono requisiti legali o perizie.

---

## 6.10 Web radio e funzioni media

La web radio è una funzione di comodità e frequenza d’uso, non il principale vantaggio competitivo.

Funzioni utili:

- stazioni personali;
- import/export playlist;
- preferiti;
- metadati brano;
- ripristino automatico;
- gestione errori di rete;
- buffer configurabile;
- comandi vocali;
- supporto Android Auto nella categoria media;
- riduzione automatica del volume durante avvisi critici.

La dashboard telemetrica completa deve rimanere su telefono, tablet o Android Automotive OS finché le categorie e le regole Android Auto non consentono esplicitamente l’esperienza desiderata.

---

## 6.11 Veicoli elettrici e ibridi

### Metriche

- SOC;
- SOH dichiarato;
- capacità osservata;
- tensione pacco;
- corrente;
- potenza;
- temperature;
- celle min/max;
- dispersione celle;
- isolamento quando disponibile;
- rigenerazione;
- efficienza;
- consumi servizi ausiliari;
- velocità e curva di ricarica;
- preriscaldamento;
- storico delle sessioni.

### Principio metodologico

Vehylo deve mostrare la provenienza di ogni indicatore di salute:

- comunicato dal BMS;
- calcolato da una sessione;
- stimato da più sessioni;
- confrontato con un profilo;
- non disponibile.

---

## 6.12 Sensori esterni, hardware e automazione

### Sorgenti supportabili

- OBD Bluetooth Classic;
- OBD BLE;
- Wi-Fi;
- USB;
- SocketCAN;
- sensori BLE;
- inclinometri;
- TPMS aftermarket;
- sensori temperatura e pressione;
- GPS esterno;
- ESP32 e Arduino;
- gateway CAN;
- telecamere USB o IP;
- action camera.

### Integrazioni

- MQTT;
- Home Assistant;
- webhook in uscita controllati;
- esportazione locale;
- API LAN opzionale;
- Tasker o automazioni Android;
- backup su storage scelto dall’utente.

### Esempi

- avviso batteria 12 V bassa;
- apertura garage all’arrivo;
- promemoria manutenzione;
- stato carica EV;
- posizione parcheggio;
- sincronizzazione del diario viaggi;
- allarme temperatura in camper o vano tecnico.

---

## 6.13 Richieste e contributi degli utenti

### Dall’app

L’utente deve poter:

- proporre una funzione;
- indicare veicolo e hardware;
- allegare una sessione anonimizzata;
- votare richieste esistenti;
- seguire lo stato;
- ricevere la notifica quando la funzione è disponibile;
- candidarsi come tester.

### Classificazione delle richieste

- nuovo segnale;
- nuovo profilo;
- dashboard;
- formula o decoder;
- sensore BLE;
- diagnostica;
- procedura di servizio;
- integrazione hardware;
- import/export;
- accessibilità;
- traduzione;
- bug.

### Gestione

Ogni richiesta dovrebbe ricevere:

- stato;
- priorità;
- veicoli interessati;
- dati necessari;
- livello di rischio;
- eventuale workaround;
- versione pianificata o motivazione del rifiuto.

---

## 6.14 Service Gateway e scritture future

### Sequenza raccomandata

1. sola lettura;
2. cancellazione DTC con conferma;
3. reset manutenzione;
4. procedure reversibili e a basso rischio;
5. funzioni di servizio specifiche per modello;
6. codifiche con backup e rollback;
7. scritture più sensibili soltanto dopo verifica estesa.

### Controlli obbligatori

- funzione disabilitata per impostazione predefinita;
- profilo firmato;
- centralina e versione compatibili;
- veicolo fermo;
- tensione adeguata;
- precondizioni dichiarate;
- comando in allowlist;
- anteprima degli effetti;
- conferma esplicita specifica;
- autorizzazione a scadenza;
- backup del valore originale;
- log completo;
- verifica post-operazione;
- rollback quando tecnicamente possibile.

### Esclusioni

Vehylo non deve implementare:

- bypass SecurityAccess;
- immobilizer;
- programmazione chiavi;
- furto o aggiramento antifurto;
- firmware flashing generico;
- modifica di chilometraggi;
- disattivazione di sistemi di sicurezza o emissioni;
- procedure non verificabili o prive di recupero.

---

## 7. Architettura di estensibilità dal punto di vista prodotto

Le estensioni create dagli utenti devono essere dichiarative.

### Tipi consentiti

- mapping;
- formule;
- unità;
- enumerazioni;
- dashboard;
- allarmi;
- stazioni radio;
- protocolli sensore descrivibili a dati;
- sequenze diagnostiche read-only;
- modelli di report;
- workflow guidati senza codice arbitrario.

### Tipi che richiedono aggiornamento dell’app

- nuovi driver nativi;
- nuovi stack di protocollo;
- accesso a nuove API Android;
- servizi in background;
- nuove funzioni di scrittura;
- codice eseguibile;
- componenti con privilegi elevati.

Questo modello permette all’utente di estendere Vehylo senza trasformare l’app in un sistema che scarica ed esegue codice non verificato.

---

## 8. Esperienza utente

### 8.1 Primo avvio

1. scelta della modalità semplice o avanzata;
2. aggiunta veicolo;
3. collegamento adattatore;
4. Compatibility Scan;
5. selezione profilo;
6. dashboard consigliata;
7. spiegazione dei dati disponibili e mancanti;
8. impostazione privacy e registrazione.

### 8.2 Stati di connessione chiari

L’utente deve distinguere:

- Bluetooth connesso;
- adattatore raggiunto;
- ECU raggiunta;
- protocollo inizializzato;
- flusso dati attivo;
- profilo applicato;
- dati obsoleti;
- connessione degradata.

### 8.3 Guida sicura

Durante la marcia:

- niente wizard complessi;
- niente configurazione;
- niente tastiere;
- pulsanti grandi;
- modalità ad alto contrasto;
- avvisi vocali;
- interazioni ridotte;
- blocco delle funzioni pericolose;
- possibilità per il passeggero soltanto quando rilevabile e consentito.

---

## 9. Privacy, sicurezza e affidabilità

### Privacy

- elaborazione locale per impostazione predefinita;
- account non obbligatorio per funzioni locali;
- consenso separato per cloud e telemetria anonima;
- posizione disattivabile;
- anonimizzazione dei campioni condivisi;
- rimozione VIN e identificativi personali dai profili pubblici;
- controllo esplicito della conservazione dei video;
- esportazione e cancellazione dati.

### Sicurezza dei dati

- profili firmati;
- hash dei pacchetti;
- migrazioni di schema;
- backup;
- importazione validata;
- limiti alle formule;
- nessuna esecuzione arbitraria;
- audit delle scritture.

### Affidabilità

- watchdog della connessione;
- riconnessione controllata;
- gestione adattatori ELM327 difettosi;
- frequenze di polling conservative;
- rilevamento dati obsoleti;
- timestamp monotoni;
- separazione tra valore ricevuto e valore interpolato;
- test su diversi produttori Android;
- protezione termica della dashcam.

---

## 10. Modello economico

### Gratuito

- aggiunta veicolo;
- Compatibility Scan;
- PID OBD standard principali;
- lettura DTC base;
- dashboard base;
- una quantità limitata di log locali;
- richiesta nuove funzioni;
- importazione di profili gratuiti.

### Vehylo Pro — acquisto una tantum

- dashboard avanzate;
- mapping manuale;
- Mapping Studio;
- log illimitati locali;
- allarmi avanzati;
- import/export;
- report;
- diario manutenzione;
- personalizzazione completa;
- più veicoli.

### Pacchetti veicolo

- profili premium verificati;
- diagnostica specifica;
- dashboard dedicate;
- procedure di servizio supportate;
- aggiornamenti del pacchetto per un periodo dichiarato.

### Servizi in abbonamento opzionale

- sincronizzazione cloud;
- backup multi-dispositivo;
- elaborazione remota intensiva;
- flotte;
- condivisione famiglia o team;
- conservazione video;
- report professionali e portale web.

### Principi commerciali

- prezzo mostrato prima del collegamento;
- compatibilità verificata prima dell’acquisto;
- descrizione precisa delle funzioni sbloccate;
- nessun rinnovo nascosto;
- modalità offline funzionante;
- rimborso o gestione chiara quando un profilo risulta incompatibile.

---

## 11. Priorità di prodotto

### Priorità A — Fondamenta indispensabili

1. persistenza affidabile;
2. selezione reale adattatori;
3. gestione connessione;
4. Compatibility Scan;
5. import/export profili;
6. dashboard persistenti;
7. logger e replay;
8. privacy e backup;
9. test su hardware reale.

### Priorità B — Differenziazione

1. Mapping Studio guidato;
2. profili comunitari verificati;
3. diagnostica spiegabile;
4. baseline personale;
5. report usato;
6. allarmi contestuali;
7. fusione multi-sorgente.

### Priorità C — Frequenza d’uso

1. diario viaggi;
2. manutenzione e costi;
3. web radio;
4. promemoria;
5. Android Auto media;
6. posizione parcheggio.

### Priorità D — Espansione

1. Incident Recorder;
2. EV/ibrido avanzato;
3. MQTT e Home Assistant;
4. hardware esterno;
5. portale profili;
6. service gateway.

### Non prioritario nel breve periodo

- marketplace di plugin eseguibili;
- firmware flashing;
- codifica universale;
- dashboard complessa su Android Auto senza categoria esplicita;
- social network generico;
- chat AI scollegata dai dati reali.

---

## 12. Roadmap proposta

## Vehylo 0.4 — Connessione, persistenza e compatibilità

- Room o livello di persistenza equivalente;
- veicoli e profili salvati;
- selezione adattatore OBD e sensori BLE;
- gestione permessi completa;
- Compatibility Scan;
- scanner PID supportati;
- qualità adattatore;
- import/export JSON versionato;
- dashboard persistenti;
- data logger e replay;
- download APK e processo release ripetibile.

### Criterio di uscita

Un utente deve poter installare l’app, aggiungere un veicolo, selezionare un adattatore, conoscere la compatibilità, salvare il profilo e ritrovare tutto dopo il riavvio.

## Vehylo 0.5 — Mapping Studio

- acquisizione frame reale;
- sessioni prima/durante/dopo;
- esperimenti guidati;
- correlazione con riferimento;
- segnali booleani ed enumerati;
- formule;
- editor avanzato;
- validazione multi-sessione;
- pacchetti profilo;
- condivisione tramite file e GitHub;
- sistema di confidenza.

### Criterio di uscita

Un utente avanzato deve poter scoprire, validare, salvare ed esportare almeno un segnale proprietario senza modificare il codice dell’app.

## Vehylo 0.6 — Diagnostica e salute

- scansione multi-centralina read-only;
- freeze frame;
- Mode 06;
- timeline DTC;
- spiegazioni contestuali;
- dashboard diagnostiche generate;
- baseline personale;
- anomalie e trend;
- report per officina;
- report pre-acquisto iniziale.

### Criterio di uscita

Un DTC deve essere accompagnato da contesto, dati osservati, incertezza e un report esportabile.

## Vehylo 0.7 — Viaggi, manutenzione e Incident Recorder

- registro viaggi;
- GPS opzionale;
- consumi e costi;
- manutenzioni;
- ricevute;
- promemoria;
- loop recording;
- protezione clip;
- sincronizzazione OBD/GPS/video;
- pacchetto incidente con hash;
- protezione termica.

### Criterio di uscita

Vehylo deve creare valore quotidiano anche senza guasti e deve produrre una sessione viaggio riproducibile con dati sincronizzati.

## Vehylo 0.8 — EV, integrazioni e hardware

- profili EV/ibridi;
- celle e temperature;
- sessioni di ricarica;
- metriche di salute trasparenti;
- MQTT;
- Home Assistant;
- sensori esterni configurabili;
- GPS esterno;
- gateway ESP32/Arduino;
- multi-sorgente simultanea.

### Criterio di uscita

L’utente deve poter combinare almeno due sorgenti reali e visualizzarle nella stessa timeline.

## Vehylo 0.9 — Community e profili verificati

- catalogo profili;
- identità veicolo dettagliata;
- firme;
- versioni;
- compatibilità ECU;
- voti e conferme;
- campioni anonimizzati;
- moderazione;
- richieste integrate;
- beta testing per modello.

### Criterio di uscita

Un profilo deve poter essere pubblicato, verificato, aggiornato e ritirato senza ambiguità sulla compatibilità.

## Vehylo 1.0 — Piattaforma stabile

- percorso onboarding completo;
- qualità connessione elevata;
- Compatibility Scan maturo;
- dashboard e mapping stabili;
- diagnostica spiegabile;
- diario e report;
- comunità profili;
- release firmate;
- documentazione completa;
- telemetria privacy-first;
- politica di supporto e migrazione.

### Criterio di uscita

La 1.0 deve essere affidabile per uso quotidiano in sola lettura, estendibile senza codice e chiara sui limiti di ogni veicolo.

## Dopo la 1.0 — Service Gateway

- reset manutenzione;
- procedure di servizio a basso rischio;
- backup e rollback;
- profili firmati;
- audit;
- beta chiusa per modello;
- codifiche reversibili selezionate.

---

## 13. Metriche di successo

### Attivazione

- percentuale di installazioni che completano il Compatibility Scan;
- percentuale di adattatori collegati con successo;
- tempo medio al primo dato valido;
- percentuale di utenti che salvano un veicolo.

### Qualità

- disconnessioni per ora;
- errori di parsing;
- dati obsoleti;
- crash per sessione;
- compatibilità dichiarata correttamente;
- tasso di profili ritirati per errore.

### Valore

- sessioni telemetriche per utente;
- dashboard create;
- allarmi configurati;
- DTC spiegati ed esportati;
- viaggi registrati;
- manutenzioni salvate;
- mapping creati e validati.

### Comunità

- profili pubblicati;
- conferme per profilo;
- veicoli coperti;
- tempo medio di risposta a una richiesta;
- percentuale di richieste risolte con un mapping senza aggiornamento dell’app.

### Ricavi

- conversione dopo Compatibility Scan;
- rimborso per incompatibilità;
- acquisto Vehylo Pro;
- acquisto pacchetti veicolo;
- rinnovo soltanto dei servizi cloud opzionali.

### Fiducia

- percentuale di utenti che comprendono i limiti della diagnosi;
- segnalazioni di compatibilità ingannevole;
- incidenti legati a procedure modificative;
- trasparenza del changelog e dei profili.

---

## 14. Strategia di ingresso sul mercato

### Fase 1 — Appassionati e sviluppatori

Obiettivo: migliorare mapping, profili e affidabilità.

Canali:

- GitHub;
- forum di marca;
- community OBD e CAN;
- gruppi di camper, fuoristrada e track day;
- sviluppatori ESP32 e Arduino;
- beta per specifici modelli.

Offerta:

- app open e documentata;
- profili condivisibili;
- strumenti di analisi;
- riconoscimento dei contributori;
- roadmap pubblica.

### Fase 2 — Proprietari normali

Obiettivo: semplificare diagnostica, manutenzione e compatibilità.

Messaggi:

- scopri cosa supporta la tua auto prima di pagare;
- capisci una spia con i dati reali del tuo veicolo;
- conserva lo storico della tua automobile;
- porta al meccanico un report chiaro.

### Fase 3 — Professionisti indipendenti

Utenti:

- meccanici;
- installatori;
- compravenditori;
- periti;
- flotte leggere;
- preparatori.

Funzioni:

- report ripetibili;
- gestione più veicoli;
- profili verificati;
- esportazione;
- portale web;
- procedure di servizio selezionate.

---

## 15. Rischi principali e mitigazioni

### Frammentazione dei veicoli

**Rischio:** stesso modello con ECU e segnali differenti.  
**Mitigazione:** identità profilo dettagliata, versioni, Compatibility Scan e conferme multiple.

### Adattatori di bassa qualità

**Rischio:** dati errati, latenze e disconnessioni.  
**Mitigazione:** test adattatore, qualità misurata, limiti di polling e lista hardware verificato.

### Mapping errati

**Rischio:** valore plausibile ma falso.  
**Mitigazione:** evidenze, confidenza, validazione multi-sessione, confronto con strumenti esterni e stato sperimentale visibile.

### Diagnosi eccessivamente sicure

**Rischio:** l’utente interpreta un’ipotesi come certezza.  
**Mitigazione:** spiegabilità, dati mancanti, alternative e avvertenze contestuali.

### Scritture dannose

**Rischio:** centralina bloccata o configurazione errata.  
**Mitigazione:** allowlist, profili firmati, tensione e veicolo fermo, backup, conferma, audit, rollout per modello.

### Distrazione alla guida

**Rischio:** uso di schermate complesse.  
**Mitigazione:** modalità guida, blocco configurazione, avvisi vocali e rispetto delle categorie automotive.

### Privacy

**Rischio:** posizione, VIN, video e abitudini di guida sono dati sensibili.  
**Mitigazione:** local-first, consenso granulare, anonimizzazione ed esportazione/cancellazione.

### Surriscaldamento dashcam

**Rischio:** arresto del telefono o perdita video.  
**Mitigazione:** monitoraggio termico, riduzione qualità, limiti di durata e test per dispositivo.

### Prodotto troppo ampio

**Rischio:** molte funzioni incomplete.  
**Mitigazione:** roadmap a criteri di uscita, moduli disaccoppiati e priorità alle fondamenta.

---

## 16. Decisioni di prodotto raccomandate

1. Il mapping è il nucleo strategico di Vehylo.
2. Il Compatibility Scan deve essere gratuito.
3. La compatibilità deve essere dichiarata per veicolo e versione ECU, non per marca in generale.
4. La modalità semplice deve nascondere la complessità tecnica.
5. La modalità Studio deve esporre dati grezzi e strumenti avanzati.
6. I profili comunitari devono essere dichiarativi, firmati e versionati.
7. Le funzioni locali non devono richiedere un abbonamento.
8. Il cloud deve essere opzionale.
9. La web radio resta una funzione complementare.
10. La dashcam diventa strategica soltanto quando sincronizzata con la telemetria.
11. L’AI deve utilizzare dati reali, mostrare l’incertezza e non comandare direttamente le ECU.
12. Le scritture arrivano dopo la stabilità read-only e soltanto per procedure verificate.
13. Android Auto deve essere utilizzato solo nelle categorie ammesse; configurazione e dashboard complesse restano sul dispositivo.
14. EV e ibride richiedono profili e metodologie specifiche.
15. Ogni funzione deve indicare chiaramente disponibile, sperimentale, non verificata o non supportata.

---

## 17. Backlog sintetico

### Must have

- persistenza;
- connessione hardware reale;
- Compatibility Scan;
- PID supportati;
- dashboard configurabile;
- logging e replay;
- import/export;
- mapping reale;
- profili versionati;
- diagnostica read-only affidabile;
- privacy e sicurezza.

### Should have

- Mapping Studio guidato;
- profili comunitari;
- baseline veicolo;
- report officina e usato;
- viaggi e manutenzione;
- allarmi avanzati;
- sensori BLE configurabili;
- EV/ibrido.

### Could have

- Incident Recorder;
- MQTT e Home Assistant;
- telecamere esterne;
- portale web;
- flotte;
- dashboard marketplace;
- comandi vocali avanzati.

### Not now

- plugin eseguibili arbitrari;
- flashing firmware;
- chiavi e immobilizer;
- modifica chilometraggio;
- disattivazione emissioni o sicurezza;
- promessa di compatibilità universale.

---

## 18. Definizione di completezza di una funzione

Una funzione non è completa soltanto perché esiste una schermata. Deve includere:

- caso d’uso definito;
- compatibilità dichiarata;
- gestione errori;
- stato vuoto;
- persistenza;
- privacy;
- test;
- documentazione;
- telemetria operativa opzionale e anonima;
- accessibilità;
- comportamento offline;
- esportazione quando pertinente;
- limiti espliciti;
- criteri di sicurezza;
- migrazione dei dati;
- rollback quando modifica configurazioni.

---

## 19. Fonti e segnali di mercato considerati

Il documento deriva dall’analisi di:

- pagine e recensioni pubbliche di Car Scanner;
- Torque;
- Carista;
- Carly;
- FIXD;
- RealDash e relativo forum;
- Fuelio;
- Drivvo;
- Droid Dashcam;
- DailyRoads;
- applicazioni EV specializzate;
- documentazione ufficiale Android for Cars e Android Auto;
- discussioni di community su PID proprietari, CAN, sensori e dashboard;
- documentazione e studi tecnici sulla variabilità delle metriche di salute delle batterie.

Le recensioni pubbliche sono segnali qualitativi e non costituiscono da sole una misura statistica rappresentativa. Le priorità indicate sono raccomandazioni di prodotto e devono essere validate con:

- interviste;
- analytics privacy-first;
- beta per modello;
- test di prezzo;
- misurazione della compatibilità;
- osservazione dell’utilizzo reale.

---

## 20. Direzione finale

Vehylo deve partire da una base molto affidabile e procedere per livelli:

1. collegare;
2. identificare;
3. leggere;
4. registrare;
5. spiegare;
6. apprendere;
7. condividere;
8. prevedere;
9. assistere;
10. modificare soltanto quando è sicuro e verificato.

La combinazione più interessante non è «OBD + radio + dashcam», ma:

> **profilo verificato del veicolo + dati reali + mapping comunitario + cronologia personale + strumenti modulari.**

Questa direzione permette a Vehylo di servire proprietari normali, appassionati, sviluppatori, installatori, compratori di auto usate e professionisti indipendenti senza rinunciare a trasparenza, sicurezza e controllo dell’utente.
