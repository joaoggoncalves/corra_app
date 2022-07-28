package com.example.corra;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.corra.Adapter.CorridaRecyclerViewAdapter;
import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView mainRV;
    private ArrayList<Corrida> lista = new ArrayList<>();
    private CorridaRecyclerViewAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainRV = rootView.findViewById(R.id.recyclerViewMain);
        adapter = new CorridaRecyclerViewAdapter(this.getContext(), lista);
        mainRV.setAdapter(adapter);
        mainRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mainRV.setHasFixedSize(true);
        ActionBar barra = ((AppCompatActivity)getActivity()).getSupportActionBar();
        barra.setTitle(R.string.actionbartitlehome);
        //Lê do database (viewmodel)
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        viewmodel.getAllCorridas().observe(getViewLifecycleOwner(), corridas -> {
            Corrida corrida;
            for (int i=0; i<corridas.size(); i++) {
                //Log.d(TAG, "Corrida " + i + ": Tempo: " + corridas.get(i).tempo + " Distancia: " + corridas.get(i).velocidade * (corridas.get(i).tempo/3600));
                corrida = new Corrida(corridas.get(i).getUid(), corridas.get(i).getTempo(), corridas.get(i).getVelocidade(), corridas.get(i).getData());
                if (!lista.contains(corrida)) {
                    lista.add(i, corrida);
                    adapter.notifyItemInserted(i);
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        int indice = item.getGroupId();
        int id = lista.get(indice).getUid();
        if (item.getItemId() == 0) {
            viewmodel.deleteCorrida(id);
            lista.remove(indice);
            adapter.notifyItemRemoved(indice);
        }
        return super.onContextItemSelected(item);
    }
}