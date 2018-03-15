package com.unican.gist.gistus.domain.Utils;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by andres on 08/05/2017.
 */

public abstract class UseCase {

    protected  abstract Observable buildUseCaseObservable();

    @SuppressWarnings("unchecked")
    public void execute(DisposableObserver useCaseSubscriber) {
        this.buildUseCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(useCaseSubscriber);
    }



}
