package com.unican.gist.gistus.ui.splash;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.DownloadEstimecionesUSECASE;
import com.unican.gist.gistus.domain.Objects.Estimaciones;
import com.unican.gist.gistus.domain.Objects.EstimacionesList;
import com.unican.gist.gistus.domain.Objects.Paradas;
import com.unican.gist.gistus.ui.map.ParadasFragment;
import com.unican.gist.gistus.ui.map.TransportMapFragment;
import com.unican.gist.gistus.ui.map.list_paradas_estimaciones.EstimacionesFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class
MainActivity extends AppCompatActivity implements ParadasFragment.FragmentFromFragment, EstimacionesFragment.OnListFragmentInteractionListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        //gestion de la toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setTitleTextColor(getApplication().getResources().getColor(R.color.white));
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new TransportMapFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.map_buses:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, new TransportMapFragment()).commit();
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return false;
            case R.id.map_stops:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, new ParadasFragment()).commit();
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return false;
            case R.id.stop_search:
                new SimpleSearchDialogCompat(this, "Busca parada...",
                        "Introduce nombre o id", null, getParadas(),
                        new SearchResultListener<Paradas>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   Paradas item, int position) {
                                new DownloadEstimecionesUSECASE(String.valueOf(item.getId())).execute(new MainActivity.getEstimaciones());
                                Toast.makeText(MainActivity.this, item.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).show();
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return false;
            default:
                return false;



        }
    }

    private ArrayList getParadas() {
        ArrayList<Paradas> paradasList= new ArrayList<>();
        paradasList.add(new Paradas("Los ciruelos 44",1));
        return paradasList;
    }


    @Override
    public void OnFragmentEstimaciones(List estimaciones, String titulo_parada) {

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new EstimacionesFragment(estimaciones, titulo_parada)).commit();

    }

    @Override
    public void onListFragmentInteraction(Estimaciones item) {

    }

    @Override
    public void obBackPressed() {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new ParadasFragment()).commit();
    }
    private final class getEstimaciones extends DisposableObserver<EstimacionesList> implements Consumer{
        EstimacionesList estimacionesList;
        @Override
        public void onNext(EstimacionesList estimacionesList) {
            this.estimacionesList=estimacionesList;
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            List<Estimaciones> estimacionesListMandar= new ArrayList<>();
            for(Estimaciones estimacion: estimacionesList.getData()){
                estimacionesListMandar.add(new Estimaciones(estimacion.linea_destino+" a "+estimacion.destino,estimacion.minutos,estimacion.metros));
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_content, new EstimacionesFragment(estimacionesListMandar, "LINEA")).commit();
        }

        @Override
        public void accept(Object o) throws Exception {

        }
    }
}
