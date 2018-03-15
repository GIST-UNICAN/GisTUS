package com.unican.gist.gistus.data;

import com.unican.gist.gistus.domain.Objects.EstimacionesList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Andres on 15/03/2018.
 */

public interface ApiStops {
    @Headers("Content-Type: application/json")
    @GET("pasos/parada_estimacion/{id_parada}")
    Observable<EstimacionesList> getEstimacionesList(@Path("id_parada") String idparada);
}
