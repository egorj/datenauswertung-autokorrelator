package de.wzl.grosseprog;

import de.wzl.grosseprog.domain.Util;
import de.wzl.grosseprog.io.ausgabe.MyAusgabe;
import de.wzl.grosseprog.io.eingabe.MyEingabe;

public class Main {

    /**
     * excecuted at start of application
     *
     * @param args
     */
    public static void main(String... args) {

        if (args == null || args.length == 0) {
            args = new String[] { "./inputdata" };
        }

        MyAusgabe ausgeben = new MyAusgabe();
        Util verarbeiten = new Util(ausgeben);
        Thread einlesen = new MyEingabe(verarbeiten);

        einlesen.start();
        verarbeiten.start();
        ausgeben.start();

        try {
            einlesen.join();
            verarbeiten.join();
            ausgeben.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
