package com.unican.gist.gistus.domain;

import com.unican.gist.gistus.data.ResourcesStops;
import com.unican.gist.gistus.data.StopsDownload;
import com.unican.gist.gistus.domain.Utils.UseCase;

import io.reactivex.Observable;

/**
 * Created by Andres on 15/03/2018.
 */

public class DownloadEstimecionesUSECASE extends UseCase {
    private String paradaId;

    public DownloadEstimecionesUSECASE(String paradaId) {
        this.paradaId = paradaId;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        StopsDownload repo = ResourcesStops.getInstance();
        return repo.getEstimaciones(paradaId);
    }
}
