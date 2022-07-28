package com.example.corra.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.corra.Model.Corrida;
import com.example.corra.R;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CorridaRecyclerViewAdapter extends RecyclerView.Adapter<CorridaRecyclerViewAdapter.ViewHolder> {

    private List<Corrida> mList;
    private LayoutInflater mInflater;

    public CorridaRecyclerViewAdapter(Context context, List<Corrida> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = data;
    }

    //Mostra horas
    public static String getDateFromMillis(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(millis));
    }

    //Não mostra horas
    public static String getDateFromMillis2(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(millis));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.corrida_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dataTv.setText(mList.get(position).getData().substring(0,10));
        if (Duration.ofMillis(mList.get(position).getTempo()).getSeconds() > 3600) {
            String tempo = getDateFromMillis(mList.get(position).getTempo());
            String formatada = tempo.substring(0,2) + "h " + tempo.substring(3,5) + "m" + tempo.substring(6,8);
            holder.tempoTv.setText(formatada);
        } else {
            String tempo = getDateFromMillis2(mList.get(position).getTempo());
            String formatada = tempo.substring(0,2) + "m " + tempo.substring(3,5) + "s";
            holder.tempoTv.setText(formatada);
        }
        holder.distTv.setText(String.format(Locale.getDefault(), "%.2f", (mList.get(position).getVelocidade()*(mList.get(position).getTempo()/3600.0))));
        holder.horaTv.setText(mList.get(position).getData().substring(11));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView dataTv;
        TextView distTv;
        TextView tempoTv;
        TextView horaTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dataTv = itemView.findViewById(R.id.datacorrida);
            distTv = itemView.findViewById(R.id.distanciacorrida);
            tempoTv = itemView.findViewById(R.id.tempocorrida);
            horaTv = itemView.findViewById(R.id.horacorrida);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (menu != null) {
                if (v != null) {
                    menu.add(this.getAdapterPosition(), 0, 0, "Remover Corrida");
                }
            }
        }
    }
}
