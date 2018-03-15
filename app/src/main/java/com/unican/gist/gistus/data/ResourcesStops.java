package com.unican.gist.gistus.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unican.gist.gistus.domain.Objects.EstimacionesList;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.unican.gist.gistus.domain.Utils.Constants.URL_SERVER;


/**
 * Created by Andres on 15/03/2018.
 */

public class ResourcesStops implements StopsDownload {
    ApiStops service;

    private static final ResourcesStops INSTANCE = new ResourcesStops();

    public static StopsDownload getInstance() {
        return INSTANCE;
    }

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private ResourcesStops() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)

                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(ApiStops.class);
    }


    @Override
    public Observable<EstimacionesList> getEstimaciones(String idparada) {
        return service.getEstimacionesList(idparada);
    }
}

class NullOnEmptyConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                if (body.contentLength() == 0) return null;
                return delegate.convert(body);
            }
        };
    }
}

