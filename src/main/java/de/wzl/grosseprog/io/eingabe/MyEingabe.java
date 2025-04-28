package de.wzl.grosseprog.io.eingabe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import de.wzl.grosseprog.domain.Util;
import de.wzl.grosseprog.exceptions.InputException;

public class MyEingabe extends Thread {

    // Klassen Attribute
    private Util verarbeiten;

    // Konstruktoren
    public MyEingabe() {

    }

    public MyEingabe(Util verarbeiten) {
        this.verarbeiten = verarbeiten;
    }

    // Thread A: Datenquelle einlesen (Datei 0.txt bis 9.txt)
    @Override
    public void run() {
        // Setzte Thread-Name auf Eingabe
        Thread.currentThread().setName("Eingabe");

        // Beginne das Einlesen der Daten
        while (true) {
            // counter geht von 0 bis 9
            // Hilft zu tracken, welche Datei gerade eingelesen wird
            int counter = 0;
            // Hier werden die eingelesenen x-Werte gespeichert
            ArrayList<Integer> xWerte = new ArrayList<>();
            // Hier werden die eingelesenen y-Werte gespeichert
            ArrayList<Integer> yWerte = new ArrayList<>();

            // Lese Dateien 0.txt bis 9.txt ein
            while (counter < 10) {
                System.out.println("Lese Datei: " + counter + ".txt");

                // Die Eingabedateien befinden sich im Ordner "inputdata"
                File inputDatei = new File("src/main/java/de/wzl/grosseprog/inputdata/" + counter + ".txt");
                Scanner scanner = null;

                try {
                    scanner = new Scanner(inputDatei);
                } catch (FileNotFoundException ex) {
                    System.out.println("Datei nicht gefunden");
                }

                while (scanner.hasNext()) {
                    String line = scanner.nextLine();

                    // Ignoriere Zeilen mit Kommentaren
                    if (line.startsWith("#")) {
                        continue;
                    } else {
                        // Werte sind mit Tabulator getrennt, daher splite an \t
                        String[] werte = new String[2];
                        werte = line.split("\\t");

                        // Prüfe Werte auf ganze Zahlen
                        int xWert = Integer.parseInt(werte[1]);
                        int yWert = Integer.parseInt(werte[0]);

                        // Speichere die Werte nur, wenn sie nicht negativ sind
                        if (xWert >= 0 && yWert >= 0) {
                            xWerte.add(xWert);
                            yWerte.add(yWert);
                        } else {
                            try {
                                throw new InputException("Es sind nur positive Zahlen erlaubt");
                            } catch (InputException e) {
                                System.out.println("Es sind nur positive Zahlen erlaubt");
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                }

                // Prüfung ob nicht alle y-Werte 0 sind
                // Denn dann würde das Maximum 0 sein
                // Bei der Normierung der y-Werte wird aber durch das Maximum geteilt
                // Wenn das Maximum aber 0 ist, würde das Programm crashen
                int anzahlYwerteNull = 0;
                for (int i = 0; i < yWerte.size(); i++) {
                    if (yWerte.get(i) == 0) {
                        anzahlYwerteNull++;
                    }
                }
                if (anzahlYwerteNull == yWerte.size()) {
                    try {
                        throw new InputException("Es dürfen nicht alle Y-Werte gleich 0 sein!");
                    } catch (InputException e) {
                        System.out.println("Es dürfen nicht alle Y-Werte gleich 0 sein!");
                        e.printStackTrace();
                    }
                    return;
                }

                // Übergebe die eingelesenen Daten an Thread B zur weiteren Verarbeitung
                verarbeiten.setXWerte(xWerte);
                verarbeiten.setYWerte(yWerte);
                // Übergebe die Nummer der eingelesenen Datei an Thread B
                verarbeiten.setCounter(counter);
                // Teile Thread B mit, dass alle Daten nun vollständig und bereit zur weiteren
                // Verarbeitung sind
                verarbeiten.setAlleDatenAngekommen(true);

                // Leere die Listen wieder für das Einlesen der nächsten Datei
                xWerte = new ArrayList<>();
                yWerte = new ArrayList<>();

                // Erhöhe den counter um 1, um die nächste Datei einzulesen
                counter++;

                // Pausiere diesen Thread A für 0,05 Sekunden
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Falls Thread B (Verarbeitung) fertig mit allen Dateien ist
            // Setzt er seine Klassenvariable "bereitsVerarbeitet" auf true
            // Damit weiß Thread A (Einlesen), dass das Einlesen nicht mehr nötig ist
            // Und beendet die while-Schleife
            if (verarbeiten.getBereitsVerarbeitet() == true) {
                break;
            }
        }

    }
}
