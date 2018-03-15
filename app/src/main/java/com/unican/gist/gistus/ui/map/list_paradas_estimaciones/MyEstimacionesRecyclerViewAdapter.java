package com.unican.gist.gistus.ui.map.list_paradas_estimaciones;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unican.gist.gistus.R;
import com.unican.gist.gistus.domain.Estimaciones;
import com.unican.gist.gistus.ui.map.list_paradas_estimaciones.EstimacionesFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyEstimacionesRecyclerViewAdapter extends RecyclerView.Adapter<MyEstimacionesRecyclerViewAdapter.ViewHolder> {

    private final List<Estimaciones> estimacionesList;
    private final OnListFragmentInteractionListener mListener;

    public MyEstimacionesRecyclerViewAdapter(List<Estimaciones> items, OnListFragmentInteractionListener listener) {
        estimacionesList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_estimaciones, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.titulo.setText( estimacionesList.get(position).linea_destino);
        holder.minutos.setText(String.valueOf(estimacionesList.get(position).minutos/60));
        String metros=String.valueOf(estimacionesList.get(position).metros)+" metros";
        holder.metros.setText(metros);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.elementoEstimacion);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return estimacionesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView minutos;
        public final TextView metros;
        public final TextView titulo;
        public Estimaciones elementoEstimacion;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            minutos = (TextView) view.findViewById(R.id.tiempo_rellena);
            metros = (TextView) view.findViewById(R.id.distancia_rellena);
            titulo = (TextView) view.findViewById(R.id.linea_destino);
        }

        @Override
        public String toString() {
            return super.toString() + " '"  + "'";
        }
    }
}
