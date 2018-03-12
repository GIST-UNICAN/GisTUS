package com.unican.gist.gistus.ui.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.kml.KmlLayer;
import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransportMapFragment extends Fragment {

    private MapView mapView;
    private GoogleMap map;
    private Boolean primeraVez = true;
    private String url_actualizar = "espiras/programa_ejecutar/lineas_bus.kml";
    private String archivo_actualizar = "archivo.kml";
    private String xml_lineas ="espiras/programa_ejecutar/xml_lineas_bus.xml";

    @Nullable
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    @Nullable
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    @Nullable
    @BindView(R.id.lines_spinner)
    Spinner linesSpinner;


    public TransportMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_transport_map, null);
        ButterKnife.bind(this, view);
        loadSpinnerToolbar();
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //capturamos el spinner
        linesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                if (!primeraVez) {
                    if (linesSpinner.getSelectedItem().toString().equalsIgnoreCase("Todas las Líneas")) {
                        downloadFile("espiras/programa_ejecutar/lineas_bus.kml", "archivo.kml");
                        url_actualizar = "espiras/programa_ejecutar/lineas_bus.kml";
                        archivo_actualizar = "archivo.kml";
                    } else {
                        downloadFile("espiras/programa_ejecutar/lineas_bus_" + linesSpinner.getSelectedItem().toString() + ".kml", linesSpinner.getSelectedItem().toString() + ".kml");
                        url_actualizar = "espiras/programa_ejecutar/lineas_bus_" + linesSpinner.getSelectedItem().toString() + ".kml";
                        archivo_actualizar = linesSpinner.getSelectedItem().toString() + ".kml";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(43.4722,
                                -3.8199));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

                map.moveCamera(center);
                map.animateCamera(zoom);
                //actualizamos el mapa cada minuto
                Timer timer = new Timer();
                TimerTask hourlyTask = new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadFile(url_actualizar, archivo_actualizar);
                            }
                        });
                    }
                };

                // schedule the task to run starting now and then every minute
                timer.schedule(hourlyTask, 0l, 60000);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void downloadFile(final String url, final String name) {
        showLoading();
        final String path = getActivity().getFilesDir().getAbsolutePath();
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getUnsafeOkHttpClient())
                .baseUrl("https://193.144.208.142/")
                .build();
        final RxDownload rxDownload = RxDownload.getInstance(getContext())
                .maxThread(3) //Set the max thread
                .maxRetryCount(3)
                .retrofit(retrofit);  //Single instance
        rxDownload.getInstance(getContext())
                .download(url, name, path)//just pass url
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadStatus>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DownloadStatus downloadStatus) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("ARCHIVO", "ok");
                        File[] files = rxDownload.getRealFiles(name, path);
                        if (files != null) {
                            try {
                                FileInputStream fileInputStream = new FileInputStream(files[0].getAbsoluteFile());
                                KmlLayer layer = new KmlLayer(map, fileInputStream, getContext());
                                map.clear();
                                layer.addLayerToMap();
                                zoomMap(map);
                                primeraVez = false;
                                showContent();

                            } catch (FileNotFoundException e) {
                                showError();
                                e.printStackTrace();
                            } catch (XmlPullParserException e) {
                                showError();
                                e.printStackTrace();
                            } catch (IOException e) {
                                showError();
                                e.printStackTrace();
                            }

                        }
                    }


                });

    }

    private void zoomMap(GoogleMap map) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(43.4722,
                        -3.8199));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method used to show error view
     */
    public void showError() {
        mapView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);

    }

    /**
     * Method used to show the loading view
     */
    public void showLoading() {
        linesSpinner.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    /**
     * Method used to show the listView
     */
    public void showContent() {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(43.4722,
                        -3.8199));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        map.moveCamera(center);
        map.animateCamera(zoom);
        mapView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        linesSpinner.setVisibility(View.VISIBLE);

    }

    private void loadSpinnerToolbar() {
        //hay que componer el spinner selector de lineas
        Constants constantes= new Constants();
        List listaDescarga = constantes.lineas_mostrar;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner, listaDescarga);
        adapter.setDropDownViewResource(R.layout.spinner);
        linesSpinner.setVisibility(View.VISIBLE);
        linesSpinner.setAdapter(adapter);
    }
}
