package de.wzl.grosseprog.io.ausgabe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyAusgabe extends Thread {
    // Klassen Attribute
    private float pulsbreite;
    private int indexL;
    private int indexR;
    private ArrayList<Double> xWerte;
    private ArrayList<Double> yWerte;
    private ArrayList<Double> obereEinhuellende;
    private int counter;
    private int[] bereitsVerarbeitet = new int[10];
    private volatile boolean alleDatenVerarbeitet = false;

    // Konstruktoren
    public MyAusgabe() {

    }

    public MyAusgabe(float pulsbreite, int indexL, int indexR, ArrayList<Double> xWerte, ArrayList<Double> yWerte,
            ArrayList<Double> obereEinhuellende) {
        this.pulsbreite = pulsbreite;
        this.indexL = indexL;
        this.indexR = indexR;
        this.xWerte = xWerte;
        this.yWerte = yWerte;
        this.obereEinhuellende = obereEinhuellende;
    }

    // Getter & Setter
    public float getPulsbreite() {
        return this.pulsbreite;
    }

    public void setPulsbreite(float pulsbreite) {
        this.pulsbreite = pulsbreite;
    }

    public int getIndexL() {
        return this.indexL;
    }

    public void setIndexL(int indexL) {
        this.indexL = indexL;
    }

    public int getIndexR() {
        return this.indexR;
    }

    public void setIndexR(int indexR) {
        this.indexR = indexR;
    }

    public ArrayList<Double> getXWerte() {
        return this.xWerte;
    }

    public void setXWerte(ArrayList<Double> xWerte) {
        this.xWerte = xWerte;
    }

    public ArrayList<Double> getYWerte() {
        return this.yWerte;
    }

    public void setYWerte(ArrayList<Double> yWerte) {
        this.yWerte = yWerte;
    }

    public ArrayList<Double> getObereEinhuellende() {
        return this.obereEinhuellende;
    }

    public void setObereEinhuellende(ArrayList<Double> obereEinhuellende) {
        this.obereEinhuellende = obereEinhuellende;
    }

    public boolean isAlleDatenVerarbeitet() {
        return this.alleDatenVerarbeitet;
    }

    public boolean getAlleDatenVerarbeitet() {
        return this.alleDatenVerarbeitet;
    }

    public void setAlleDatenVerarbeitet(boolean alleDatenVerarbeitet) {
        this.alleDatenVerarbeitet = alleDatenVerarbeitet;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int newCounter) {
        this.counter = newCounter;
    }

    public int[] getBereitsVerarbeitet() {
        return this.bereitsVerarbeitet;
    }

    // Thread C: Daten Ausgabe in Datei
    @Override
    public void run() {
        // Setzte Thread-Name auf Ausgabe
        Thread.currentThread().setName("Ausgabe");

        while (true) {
            // Warte, bis alle Daten für die Ausgabe angekommen sind
            while (!this.alleDatenVerarbeitet) {
            }
            setAlleDatenVerarbeitet(false);

            // Prüfe, ob die zu Schreibende Datei bereits ausgegeben wurde
            if (this.bereitsVerarbeitet[this.counter] == 1) {
                continue;
            } else {
                System.out.println("Schreibe in Datei: out" + this.counter + ".txt");
                File outputDatei = new File("src/main/java/de/wzl/grosseprog/inputdata/out" + this.counter + ".txt");

                try {
                    FileWriter writer = new FileWriter(outputDatei);

                    writer.write("# FWHM = " + this.pulsbreite + ", " + this.indexL + ", " + this.indexR + "\n");
                    writer.write("# pos\tint\tenv\n");

                    for (int i = 0; i < this.xWerte.size(); i++) {
                        writer.write(
                                this.xWerte.get(i) + "\t" + this.yWerte.get(i) + "\t" + this.obereEinhuellende.get(i)
                                        + "\n");
                    }

                    writer.flush();
                    writer.close();

                    // Da diese Datei bereits ausgegeben wurde
                    // Setzte den entsprechenden Eintrag im Array "bereitsVerarbeitet" auf 1
                    // Um gleiche Dateien nicht mehrfach auszugeben
                    this.bereitsVerarbeitet[this.counter] = 1;

                } catch (IOException ex) {
                    System.out.println("Fehler beim Schreiben der Datei");
                }
            }

            // Wenn alle Dateien ausgegeben wurden, wurden alle Felder im Array
            // "bereitsVerarbeitet" auf 1 gesetzt
            // Damit ergibt sich die Summe von 10 und
            // der Thread kann die while-Schleife beenden
            // Damit terminiert das gesamte Programm
            if (Arrays.stream(this.bereitsVerarbeitet).sum() == 10) {
                break;
            }
        }
    }

}
