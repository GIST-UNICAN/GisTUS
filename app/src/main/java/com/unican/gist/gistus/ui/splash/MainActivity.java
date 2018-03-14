package com.unican.gist.gistus.ui.splash;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.Estimaciones;
import com.unican.gist.gistus.ui.map.ParadasFragment;
import com.unican.gist.gistus.ui.map.TransportMapFragment;
import com.unican.gist.gistus.ui.map.list_paradas_estimaciones.EstimacionesFragment;

import java.util.List;

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
            default:
                return false;



        }
    }

    @Override
    public void OnFragmentEstimaciones(List estimaciones) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new EstimacionesFragment(estimaciones)).commit();

    }

    @Override
    public void onListFragmentInteraction(Estimaciones item) {

    }
}
