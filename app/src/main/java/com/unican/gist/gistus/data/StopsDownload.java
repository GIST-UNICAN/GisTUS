package com.unican.gist.gistus.data;

import com.unican.gist.gistus.domain.Objects.EstimacionesList;

import io.reactivex.Observable;

/**
 * Created by Andres on 15/03/2018.
 */

public interface StopsDownload {
    Observable<EstimacionesList> getEstimaciones(String idparada);
}
