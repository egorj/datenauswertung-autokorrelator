package de.wzl.grosseprog.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.wzl.grosseprog.io.ausgabe.MyAusgabe;

public class Util extends Thread implements IMathMethoden {
    // Klassen Attribute
    private ArrayList<Integer> xWerte;
    private ArrayList<Integer> yWerte;
    private int counter;
    private MyAusgabe ausgeben;
    private volatile boolean alleDatenAngekommen = false;
    private boolean bereitsVerarbeitet = false;

    // Konstruktoren
    public Util(ArrayList<Integer> xWerte, ArrayList<Integer> yWerte) {
        this.xWerte = xWerte;
        this.yWerte = yWerte;
    }

    public Util(MyAusgabe ausgeben) {
        this.ausgeben = ausgeben;
    }

    public Util() {

    }

    // Getter & Setter
    public ArrayList<Integer> getXWerte() {
        return this.xWerte;
    }

    public void setXWerte(ArrayList<Integer> xWerte) {
        this.xWerte = xWerte;
    }

    public ArrayList<Integer> getYWerte() {
        return this.yWerte;
    }

    public void setYWerte(ArrayList<Integer> yWerte) {
        this.yWerte = yWerte;
    }

    public MyAusgabe getAusgeben() {
        return this.ausgeben;
    }

    public void setAusgeben(MyAusgabe ausgeben) {
        this.ausgeben = ausgeben;
    }

    public boolean isAlleDatenAngekommen() {
        return this.alleDatenAngekommen;
    }

    public boolean getAlleDatenAngekommen() {
        return this.alleDatenAngekommen;
    }

    public void setAlleDatenAngekommen(boolean alleDatenAngekommen) {
        this.alleDatenAngekommen = alleDatenAngekommen;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int newCounter) {
        this.counter = newCounter;
    }

    public boolean getBereitsVerarbeitet() {
        return this.bereitsVerarbeitet;
    }

    // 1.1 Umrechnung der x-Werte
    @Override
    public ArrayList<Double> umrechnungInPikoSek(ArrayList<Integer> xWerte) {
        ArrayList<Double> ret = new ArrayList<>();

        for (int i = 0; i < xWerte.size(); i++) {
            double xAlt = (double) xWerte.get(i);
            double xNeu = ((xAlt / (Math.pow(2, 18) - 1)) * 266.3) - 132.3;
            ret.add(xNeu);
        }

        return ret;
    }

    // 1.2 Normierung der y-Werte
    @Override
    public ArrayList<Double> normierung(ArrayList<Integer> yWerte) {
        ArrayList<Double> ret = new ArrayList<>();

        double yMax = (double) Collections.max(yWerte);

        for (int i = 0; i < yWerte.size(); i++) {
            double yAlt = (double) yWerte.get(i);
            double yNeu = yAlt / yMax;
            ret.add(yNeu);
        }

        return ret;
    }

    // 2. Glättung der Daten
    @Override
    public ArrayList<Double> glaettung(ArrayList<Double> xWerte) {
        ArrayList<Double> ret = new ArrayList<>();
        int n;
        // groesse entspricht dem Wert abgerundet(0,002 * N) aus der Aufgabe
        int groesse = (int) Math.floor(xWerte.size() * 0.002);

        // Prüfe, ob groesse gerade oder ungerade ist
        // Und setzte n entsprechend
        if (groesse % 2 == 0) {
            n = groesse - 1;
        } else {
            n = groesse;
        }

        // t entspricht "Tau" aus der Aufgabe
        int t = (n - 1) / 2;

        // Berechne Summe von x-Dach
        double summe = 0;
        for (int k = 0; k < xWerte.size(); k++) {
            if (k < t) {
                for (int i = 0; i < n; i++) {
                    summe += xWerte.get(i);
                }
            } else if (k > xWerte.size() - 1 - t) {
                int ueberlauf = k + t - (xWerte.size() - 1);
                int startIndex = k - t - ueberlauf;
                for (int i = startIndex; i < xWerte.size(); i++) {
                    summe += xWerte.get(i);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    summe += xWerte.get(k - t + i);
                }
            }
            // xNeu entspicht x-k = Summe von x-Dach geteilt durch n
            double xNeu = summe / n;
            ret.add(xNeu);
            // Setzte summe auf 0 für den nächsten Schleifen-Durchlauf
            summe = 0;
        }

        return ret;
    }

    // 3. Obere Einhüllende
    @Override
    public ArrayList<Double> obereEinhuellende(ArrayList<Double> xWerte, ArrayList<Double> yWerte) {
        ArrayList<Double> ret = new ArrayList<>();

        // Finde das Maximum der y-Werte und seine Position in der Liste
        double yMax = Collections.max(yWerte);
        int yMaxPos = yWerte.indexOf(yMax);

        // Hier werden alle y-Werte gespeichert, die zu einem bestimmten x-Wert gehören
        ArrayList<Double> yWerteZuEinemXwert = new ArrayList<>();

        // Von Links
        for (int i = 0; i <= yMaxPos; i++) {
            // Iteriere über alle x-Werte von 0 bis Index des Maximums der y-Werte (= "von
            // links")
            double xWert = xWerte.get(i);
            ArrayList<Integer> xWertePositionen = new ArrayList<>();
            // Speichere die Position des x-Wertes
            xWertePositionen.add(i);
            // Finde alle weiteren in der Liste vorkommenden x-Werte
            // und speichere deren Position ebenfalls
            for (int j = 0; j < xWerte.size(); j++) {
                if (xWerte.get(j) == xWert) {
                    xWertePositionen.add(j);
                }
            }

            // Nun suche alle y-Werte, die zu dem x-Wert zugeordnet wurden
            // und speichere diese in der Liste "yWerteZuEinemXwert"
            for (int k = 0; k < xWertePositionen.size(); k++) {
                yWerteZuEinemXwert.add(yWerte.get(xWertePositionen.get(0)));
            }
            // Finde aus allen gemerkten y-Werten eines x-Wertes
            // den größten Wert, also das Maximum dieser
            ret.add(Collections.max(yWerteZuEinemXwert));
        }

        // Von Rechts
        yWerteZuEinemXwert = new ArrayList<>();
        ArrayList<Double> retRechteSeite = new ArrayList<>();
        for (int i = xWerte.size() - 1; i > yMaxPos; i--) {
            double xWert = xWerte.get(i);
            ArrayList<Integer> xWertePositionen = new ArrayList<>();
            xWertePositionen.add(i);
            for (int j = 0; j < xWerte.size(); j++) {
                if (xWerte.get(j) == xWert) {
                    xWertePositionen.add(j);
                }
            }

            for (int k = 0; k < xWertePositionen.size(); k++) {
                yWerteZuEinemXwert.add(yWerte.get(xWertePositionen.get(0)));
            }

            retRechteSeite.add(Collections.max(yWerteZuEinemXwert));
        }

        // Da hier "von rechts" gearbeitet wurde
        // Musste eine Hilfs-Liste "retRechteSeite" erstellt werden
        // Damit die Ergebnisse beim returnen in der richtigen Reihenfolge stehen
        for (int i = retRechteSeite.size() - 1; i >= 0; i--) {
            ret.add(retRechteSeite.get(i));
        }

        return ret;
    }

    // 4. Pulsbreite
    @Override
    public double[] pulsbreite(ArrayList<Double> xWerte, ArrayList<Double> yWerte,
            ArrayList<Double> obereEinhuellende) {
        double[] ret = new double[3];
        // Für die Berechnung der Grundlinie
        // Entspricht dem äußersten linken 1% der Intensitätswerte
        int groesse = (int) Math.round(xWerte.size() * 0.01);
        ArrayList<Double> yTeil = new ArrayList<>();

        // Finde zu den äußeren 1% die dazugehörigen y-Werte
        for (int i = 0; i < groesse; i++) {
            yTeil.add(yWerte.get(i));
        }

        // Berechne den Durchschnitt = mittlere Höhe der Werte
        double summe = 0;
        for (int i = 0; i < yTeil.size(); i++) {
            summe += yTeil.get(i);
        }
        double durchschnitt = summe / yTeil.size();

        // Finde das Maximum der y-Werte und seine Position in der Liste
        double yMax = Collections.max(yWerte);
        int yMaxPos = yWerte.indexOf(yMax);
        // Berechne die halbe Höhe, auf der die gesuchten Punkte L und R liegen
        double halbeHoehe = (yMax + durchschnitt) / 2;

        // Iteriere über die Obere Einhüllende "von links"
        // Und nimm den ersten Wert, der größer-gleich der halben Höhe ist
        double y1 = 0;
        for (int i = 0; i < yMaxPos; i++) {
            if (obereEinhuellende.get(i) >= halbeHoehe) {
                y1 = obereEinhuellende.get(i);
                break;
            }
        }

        // Iteriere über die Obere Einhüllende "von rechts"
        // Und nimm den ersten Wert, der größer-gleich der halben Höhe ist
        double y2 = 0;
        for (int i = obereEinhuellende.size() - 1; i > yMaxPos; i--) {
            if (obereEinhuellende.get(i) >= halbeHoehe) {
                y2 = obereEinhuellende.get(i);
                break;
            }
        }

        // Bestimme die Positionen der gefundenen Werte auf der halben Höhe
        int y1Pos = yWerte.indexOf(y1);
        int y2Pos = yWerte.indexOf(y2);

        // Bestimme die gesuchten Punkte L und R
        double punktL = xWerte.get(y1Pos);
        double punktR = xWerte.get(y2Pos);

        // Berechne die Pulsbreite
        double pulsbreite = Math.abs(punktR - punktL);

        ret[0] = pulsbreite;
        ret[1] = y1Pos;
        ret[2] = y2Pos;

        return ret;
    }

    // Thread B: Daten Verarbeitung
    @Override
    public void run() {
        // Setzte Thread-Name auf Verarbeitung
        Thread.currentThread().setName("Verarbeitung");

        while (true) {
            // Warte, bis alle Daten für die Verarbeitung angekommen sind
            while (!this.alleDatenAngekommen) {

            }
            setAlleDatenAngekommen(false);

            System.out.println("Verarbeite Datei: " + this.counter + ".txt");

            // 1. Umrechnung und Normierung der Daten
            ArrayList<Double> xWerteInPikoSek = umrechnungInPikoSek(this.xWerte);
            ArrayList<Double> yWerteNormiert = normierung(this.yWerte);

            // 2. Glättung der Daten
            ArrayList<Double> xWerteGeglaettet = glaettung(xWerteInPikoSek);

            // 3. Obere Einhüllende
            ArrayList<Double> obereEinhuellende = obereEinhuellende(xWerteGeglaettet, yWerteNormiert);

            // 4. Pulsbreite
            double[] pulsbreiteDaten = pulsbreite(xWerteGeglaettet, yWerteNormiert, obereEinhuellende);

            // Übergabe der Daten an den Ausgabe-Thread (Thread C)
            ausgeben.setPulsbreite((float) pulsbreiteDaten[0]);
            ausgeben.setIndexL((int) pulsbreiteDaten[1]);
            ausgeben.setIndexR((int) pulsbreiteDaten[2]);
            ausgeben.setXWerte(xWerteGeglaettet);
            ausgeben.setYWerte(yWerteNormiert);
            ausgeben.setObereEinhuellende(obereEinhuellende);
            // Übergebe die Nummer der verarbeiteten Datei an Thread C
            ausgeben.setCounter(this.counter);
            // Teile Thread C mit, dass alle Daten nun vollständig und bereit zur Ausgabe in
            // eine Datei sind
            ausgeben.setAlleDatenVerarbeitet(true);

            // Falls Thread C (Ausgabe) fertig mit allen Dateien ist
            // Hat er alle Elemente seines Arrays "bereitsVerarbeitet" auf 1 gesetzt
            // Damit ist die Summe des Arrays 10, und Thread B weiß, dass alles fertig ist
            // Um dies auch Thread A (Einlesen) mitzuteilen, setzt Thread B seine Variable
            // "bereitsVerarbeitet" auch auf true (Thread A kann diese dann auslesen)
            // Und beendet die while-Schleife
            if (Arrays.stream(ausgeben.getBereitsVerarbeitet()).sum() == 10) {
                this.bereitsVerarbeitet = true;
                break;
            }
        }
    }

}
