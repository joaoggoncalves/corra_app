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

import java.util.List;

public class CorridaRecyclerViewAdapter extends RecyclerView.Adapter<CorridaRecyclerViewAdapter.ViewHolder> {

    private List<Corrida> mList;
    private LayoutInflater mInflater;

    public CorridaRecyclerViewAdapter(Context context, List<Corrida> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.corrida_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dataTv.setText(mList.get(position).getData());
        holder.tempoTv.setText(String.valueOf(mList.get(position).getTempo()));
        holder.distTv.setText(String.valueOf(mList.get(position).getVelocidade()*(mList.get(position).getTempo()/3600.0)));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView dataTv;
        TextView distTv;
        TextView tempoTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dataTv = itemView.findViewById(R.id.datacorrida);
            distTv = itemView.findViewById(R.id.distanciacorrida);
            tempoTv = itemView.findViewById(R.id.tempocorrida);
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
