package com.unican.gist.gistus.domain.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.unican.gist.gistus.domain.Objects.Estimaciones;

import java.util.List;

/**
 * Created by Andres on 15/03/2018.
 */

public class EstimacionesList {
    public EstimacionesList(List<Estimaciones> data) {
        this.data = data;
    }

    public List<Estimaciones> getData() {
        return data;
    }

    public void setData(List<Estimaciones> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private List<Estimaciones> data = null;
}
