package de.wzl.grosseprog.domain;

import java.util.ArrayList;

public interface IMathMethoden {

    public ArrayList<Double> umrechnungInPikoSek(ArrayList<Integer> xWerte);

    public ArrayList<Double> normierung(ArrayList<Integer> yWerte);

    public ArrayList<Double> glaettung(ArrayList<Double> xWerte);

    public ArrayList<Double> obereEinhuellende(ArrayList<Double> xWerte, ArrayList<Double> yWerte);

    public double[] pulsbreite(ArrayList<Double> xWerte, ArrayList<Double> yWerte, ArrayList<Double> obereEinhuellende);
}
