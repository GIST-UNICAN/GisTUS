package com.unican.gist.gistus.ui.map;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.kml.KmlLayer;
import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import static android.content.ContentValues.TAG;

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
                        String mostrar="";
                        String line = marker.getSnippet().toString();
                        Log.d("line",line);
                        if(marker.getTitle().equalsIgnoreCase("Vehiculo: 100")){
                            Log.d("100",line);
                            String pattern = "Linea(.*)estado(.*)Temperatura(.*)Humedad(.*)PM(.*)O3(.*)NO2(.*)";
                            // Create a Pattern object
                            Pattern r = Pattern.compile(pattern);
                            // Now create matcher object.
                            Matcher m = r.matcher(line);

                            while(m.find()){
                                mostrar= "Línea: "+m.group(1)+" \n"
                                        + "Estado: "+ m.group(2)+" \n"
                                        + "Temperatura: "+ m.group(3)+" \n"
                                        + "Humedad: "+ m.group(4)
                                        +" \n"+ "PM: "+ m.group(5)
                                        +" \n"+ "O3: "+ m.group(6)
                                        +" \n"+ "NO2: "+ m.group(7);
                            }
                        }
                        else {
                            String pattern = "^Linea(.*)estado(.*)";
                            // Create a Pattern object
                            Pattern r = Pattern.compile(pattern);
                            // Now create matcher object.
                            Matcher m = r.matcher(line);
                            while(m.find()){
                                mostrar= "Línea: "+m.group(1)+" \n"+ "Estado: "+ m.group(2);
                            }
                        }

                        new FancyAlertDialog.Builder(getActivity())
                                .setTitle(marker.getTitle())
                                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                                .setMessage(mostrar)
                                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                                .setNegativeBtnText("")
                                .setPositiveBtnText("Cerrar")
                                .setNegativeBtnBackground(Color.parseColor("#ffffff"))  //Don't pass R.color.colorvalue
                                .setAnimation(Animation.POP)
                                .isCancellable(false)
                                .setIcon(R.drawable.ic_info, Icon.Visible)
                                .OnPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        downloadFile(url_actualizar, archivo_actualizar);
                                    }
                                })
                                .build();


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
                return true;

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
}
