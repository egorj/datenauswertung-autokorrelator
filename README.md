# Auswertung simulierter Daten eines Autokorrelators mit mathematischen Methoden


## Inhaltsverzeichnis

1. [Projektübersicht](#projektübersicht)  
2. [Features im Überblick](#features-im-überblick)  
3. [System- und Softwarevoraussetzungen](#system--und-softwarevoraussetzungen)  
4. [Installation & Build](#installation--build)  
5. [Projektstruktur](#projektstruktur)  
6. [Konfiguration & Ausführung](#konfiguration--ausführung)  
7. [Mathematische Methoden](#mathematische-methoden)  
   - [1. Umrechnung & Normierung](#1-umrechnung--normierung)  
   - [2. Glättung (gleitender Mittelwert)](#2-glättung-gleitender-mittelwert)  
   - [3. Obere Einhüllende](#3-obere-einhüllende)  
   - [4. Pulsbreite (FWHM)](#4-pulsbreite-fwhm)  
8. [Nebenläufigkeit & Architektur](#nebenläufigkeit--architektur)  
9. [Eingabe- und Ausgabeformat](#eingabe--und-ausgabeformat)  
10. [Erweiterungsmöglichkeiten](#erweiterungsmöglichkeiten)  
11. [Technologien](#technologien)

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

## 4. Installation & Build

```bash
# Repository klonen
git clone https://github.com/<Ihr-Nutzername>/autokorrelator-simulation.git
cd autokorrelator-simulation

# Mit Maven bauen (öffnet target/AutokorrelatorSimulation.jar)
mvn clean package
