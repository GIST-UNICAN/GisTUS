package com.unican.gist.gistus.ui.map;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.kml.KmlLayer;
import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;
import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.Objects.Estimaciones;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class ParadasFragment extends Fragment {

    private MapView mapView;
    private GoogleMap map;
    private Boolean primeraVez = true;
    private String url_actualizar = "espiras/programa_ejecutar/estimacion_paradas.kml";
    private String archivo_actualizar = "archivo.kml";

    @Nullable
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    @Nullable
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;


    @Nullable
    @BindView(R.id.circleCountDownView)
    ContinuableCircleCountDownView circleCountDownView;


    public ParadasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.paradas, null);
        ButterKnife.bind(this, view);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //listener para el refresco, aunque no se usa
        circleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
            @Override
            public void onTick(long passedMillis) {

            }

            @Override
            public void onCompleted() {

            }
        });
        circleCountDownView.setTimer(60000);
        circleCountDownView.start();
        setHasOptionsMenu(true);
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
                zoomMap(map);
                //hacemos los markes clickables
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //el bus del seta tiene que ir aparte es como un niño tonto
                        String mostrar = "";
                        String html = marker.getSnippet().toString();
                        List<Estimaciones> estimacionesList = new ArrayList<>();
                        String linea;
                        Integer espera;
                        Integer distancia;
                        Document doc = Jsoup.parse(html);
                        Elements rows = doc.getElementsByTag("tr");
                        rows = rows.get(1).getElementsByTag("tr");
                        Log.d("numero", String.valueOf(rows.size()).toString());
                        Boolean primera = true;
                        for (Element row : rows) {
                            if (!primera) {
                                Elements columns = row.getElementsByTag("td");
                                Log.d("ele", columns.toString());

                                linea = columns.get(0).text();
                                espera = Integer.parseInt(columns.get(1).text());
                                distancia = Integer.parseInt(columns.get(2).text());
                                estimacionesList.add(new Estimaciones(linea, espera, distancia));
                            }
                            primera = false;

                        }
                        fragmentFromFragmentListener.OnFragmentEstimaciones(estimacionesList, marker.getTitle());
                        return false;
                    }
                });
                //actualizamos el mapa cada minuto
                Timer timer = new Timer();
                TimerTask hourlyTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showContent();
                                    downloadFile(url_actualizar, archivo_actualizar);
                                    restartTimer();
                                }
                            });
                        }
                    }
                };

                // schedule the task to run starting now and then every 20secs
                timer.schedule(hourlyTask, 0l, 60000);


            }
        });
    }

    private void restartTimer() {
        circleCountDownView.cancel();
        circleCountDownView.start();
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
                        showLoading();
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
        Log.d("ZOOM", "ZOOM");
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
        loadingLayout.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    /**
     * Method used to show the listView
     */
    public void showContent() {

        mapView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

    }


    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Mapa", "Satélite", "Terreno", "Híbrido"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = getActivity().getString(R.string.select_map);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = map.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.
                        Log.d("DIALOG", String.valueOf(item));
                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 0:
                                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;
                            case 1:
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.map_change:
                showMapTypeSelectorDialog();
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return false;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    public interface FragmentFromFragment {
        void OnFragmentEstimaciones(List estimaciones, String nombreParada);
    }

    ParadasFragment.FragmentFromFragment fragmentFromFragmentListener;

    @Override
    public void onAttach(Context context) {
        //give a context to the calls from other activities
        super.onAttach(context);
        if (context instanceof ParadasFragment.FragmentFromFragment) {
            fragmentFromFragmentListener = (ParadasFragment.FragmentFromFragment) context;
        } else {
            throw new ClassCastException(context.toString() + " must implements  MainScreenFragment.OnNewSurveyClicked");
        }
    }
}
