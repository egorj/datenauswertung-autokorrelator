# Auswertung simulierter Daten eines Autokorrelators mit 4 mathematischen Methoden


## Inhaltsverzeichnis

1. [Projektübersicht](#projektübersicht)  
2. [Features im Überblick](#features-im-überblick)  
3. [System- und Softwarevoraussetzungen](#system--und-softwarevoraussetzungen) 
4. [Mathematische Methoden](#mathematische-methoden)  
   - [1. Umrechnung & Normierung](#1-umrechnung--normierung)  
   - [2. Glättung (gleitender Mittelwert)](#2-glättung-gleitender-mittelwert)  
   - [3. Obere Einhüllende](#3-obere-einhüllende)  
   - [4. Pulsbreite (FWHM)](#4-pulsbreite-fwhm)  
5. [Nebenläufigkeit & Architektur](#nebenläufigkeit--architektur)   
6. [Erweiterungsmöglichkeiten](#erweiterungsmöglichkeiten)  
7. [Technologien](#technologien)

---

## 1. Projektübersicht

Dieses Java-Projekt simuliert den kontinuierlichen Betrieb eines optischen Autokorrelators.  

Zehn Textdateien (`0.txt`…`9.txt`) mit simulierten Detektordaten werden in 20 Hz-Takt (alle 0,05 s) eingelesen, vier aufeinander folgende mathematische Auswertungen werden durchgeführt, und die Ergebnisse werden in Ausgabedateien `out0.txt`…`out9.txt` geschrieben. Zur Abbildung realer Timing-Unsicherheiten laufen Einlesen, Verarbeitung und Ausgabe in jeweils eigenen Threads.  

---

## 2. Features im Überblick

- **Kontinuierliches Einlesen** von zehn Dateien in Schleife  
- **Vier analytische Auswertungsmethoden**:
  1. Umrechnung und Normierung  
  2. Glättung per gleitendem Mittelwert  
  3. Bestimmung der oberen Einhüllenden  
  4. Berechnung der Pulsbreite (FWHM)  
- **Dreifach-Nebenläufigkeit** (Einlesen, Verarbeitung, Ausgabe)  
- **Automatische Programm-Terminierung** nach Fertigstellung aller Ausgabedateien  
- **Modularer Aufbau** über Java-Interfaces zur einfachen Austauschbarkeit von Algorithmen und Datenquellen  
- **Konfigurierbare Parameter** für Dateipfade und Endungen  
- **Kommandozeilen- und Batch-Ausführung**  

---

## 3. System- und Softwarevoraussetzungen

- **Java**: Version 12 oder höher (OpenJDK/Oracle JDK)  
- **Build-Tool**: Maven (optional)  
- **Betriebssystem**: Plattformunabhängig, getestet unter Windows 10 (64-Bit)  
- **Hardware**: Mindestens 8 GB RAM, Dual-Core CPU (empfohlen 16 GB RAM, Quad-Core)  
- **Optional für Visualisierung**: Python 3.7+ mit Matplotlib 3.x  

---

## 4. Mathematische Methoden

Alle Methoden implementieren das Interface `IMathMethoden` und laufen in Thread B:

### 1. Umrechnung & Normierung
- **Umrechnung (x → Pikosekunden)**
    
$$x_{\mathrm{ps}} \=\ \frac{266.3\*x \-\ 132.3}{1000}$$

- **Normierung (y)**  

$$
y_{\mathrm{norm}} \=\ \frac{y}{\max(y)}
$$

### 2. Glättung (gleitender Mittelwert)
- Fenstergröße n = (0,002 * N) - 1 wenn gerade und n = (0,002 * N) wenn ungerade

- Für jeden Index \(k\):
  - **Randbereiche** (\(k < tau\) oder \(k > N - 1 - tau\)): symmetrisches Fenster  
  - **Sonst**: klassischer gleitender Mittelwert über \(n\) Werte

$$
\tilde x_k \=\ \frac{1}{n} * \sum_{i=k-\tau}^{k+\tau} x_i,
\qquad
n = 
\begin{cases}
\lfloor 0.002\,N\rfloor - 1, & \text{falls }\lfloor0.002\,N\rfloor\text{ gerade},\\
\lfloor 0.002\,N\rfloor,     & \text{sonst},
\end{cases}
\quad
\tau = \frac{n - 1}{2}
$$

### 3. Obere Einhüllende
1. Bestimme Index (i_max) des globalen Maximums in der normierten y-Liste.  
2. **Von links**: Für (0  <=  k  <=  i_max) jeweils das bisher höchste y für jede x-Position speichern.  
3. **Von rechts**: Analog von (N - 1) bis \(i_max), dabei Ergebnisse rückwärts einspeichern. 

### 4. Pulsbreite (FWHM)
1. **Grundlinie**: Mittelwert der ersten 1% der y-Werte (Intensitäten).
2. **Halbe Höhe**: $$h_{\mathrm{halb}} \=\ \frac{max(y)\+ Grundlinie}{2}$$
3. Suche in der oberen Einhüllenden links/rechts die ersten Punkte ≥ Halbe Höhe → Indizes L/R.  
4. **FWHM** = Differenz der zugehörigen x-Werte

---

## 5. Nebenläufigkeit & Architektur

- **Thread A (MyEingabe)**  
  - Zyklisches Einlesen der Dateien `0.txt`–`9.txt` (20 Hz, 50 ms Sleep)  
  - Filtert Kommentarzeilen (`#`) & validiert (Integer‐Parsing, ≥ 0, y-Max ≠ 0)  
  - Übergibt rohe x-/y-Listen an Thread B über Setter  

- **Thread B (Util)**  
  - Implementiert `IMathMethoden` mit den vier Auswertungsmethoden  
  - Ruft Methoden sequenziell auf  
  - Übergibt Ergebnisse an Thread C über Setter  

- **Thread C (MyAusgabe)**  
  - Schreibt Ausgabedateien `out0.txt`–`out9.txt` (Tab-separierte Tripel)  
  - Verhindert Doppelverarbeitung über Boolean-Array `[10]`  
  - Terminierung, wenn alle zehn Dateien erstellt sind  

- **Main.java**  
  ```java
  MyAusgabe ausgabe = new MyAusgabe();
  Util verarbeitung = new Util(ausgabe);
  Thread einlesen = new MyEingabe(verarbeitung);
  einlesen.start();
  verarbeitung.start();
  ausgabe.start();
  einlesen.join();
  verarbeitung.join();
  ausgabe.join();

---

## 6. Erweiterungsmöglichkeiten

- Eingebaute Visualisierung in Java (Swing/JavaFX) statt externem Python-Skript

- Persistente Speicherung: Datenbankanbindung (PostgreSQL, Microsoft SQL Server)

- Konfigurationsdateien: JSON/YAML für Pfade, Endungen, Fenstergrößen

- Erweiterte CLI: Log-Level, Thread-Pools, dynamische Parameter zur Laufzeit

---

## 7. Technologien

- Programmiersprache: Java 12+

- Build & Dependency: Maven

- Nebenläufigkeit: java.lang.Thread (Standardbibliothek)

- Visualisierung (optional): Python 3.7+ & Matplotlib 3.x

- Diagramme: draw.io, Visual Paradigm (UML‐Diagramme im dokumentation‐Ordner)
