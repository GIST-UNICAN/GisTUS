package com.unican.gist.gistus.domain.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Estimaciones {

    @SerializedName("Linea")
    @Expose
    public final String linea_destino;
    @SerializedName("Tiempo_v1")
    @Expose
    public final Integer minutos;
    @SerializedName("Distancia_v1")
    @Expose
    public final Integer metros;
    @SerializedName("Destino_v1")
    @Expose
    public String destino;

    public Estimaciones(String linea_destino, Integer minutos, Integer metros) {
        this.linea_destino = linea_destino;
        this.minutos = minutos;
        this.metros = metros;
    }

    public Estimaciones(String linea_destino, Integer minutos, Integer metros, String destino) {
        this.linea_destino = linea_destino;
        this.minutos = minutos;
        this.metros = metros;
        this.destino = destino;
    }


}
