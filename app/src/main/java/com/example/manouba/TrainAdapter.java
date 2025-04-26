package com.example.manouba;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {
    private List<Train> trainList;

    public TrainAdapter(List<Train> trainList) {
        this.trainList = trainList;
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_item, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int position) {
        Train train = trainList.get(position);

        holder.direction.setText(train.getDirection());
        holder.tunisTime.setText(train.getTunis());
        holder.saiidaTime.setText(train.getSaiidaManoubia());
        holder.mellassineTime.setText(train.getMellassine());
        holder.erraoudhaTime.setText(train.getErraoudha());
        holder.leBardoTime.setText(train.getLeBardo());
        holder.elBortalTime.setText(train.getElBortal());
        holder.manoubaTime.setText(train.getManouba());
        holder.lesOrangersTime.setText(train.getLesOrangers());
        holder.gobaaTime.setText(train.getGobaa());
        holder.gobaaVilleTime.setText(train.getGobaaVille());

    }




    @Override
    public int getItemCount() {
        return trainList.size();
    }

    public static class TrainViewHolder extends RecyclerView.ViewHolder {
        TextView  direction, tunisTime,saiidaTime,mellassineTime, erraoudhaTime, leBardoTime,
                elBortalTime, manoubaTime, lesOrangersTime, gobaaTime, gobaaVilleTime;

        public TrainViewHolder(View itemView) {
            super(itemView);
            direction = itemView.findViewById(R.id.direction);
            tunisTime = itemView.findViewById(R.id.tunisTime);
            saiidaTime = itemView.findViewById(R.id.saiidaTime);
            mellassineTime = itemView.findViewById(R.id.mellassineTime);
            erraoudhaTime = itemView.findViewById(R.id.erraoudhaTime);
            leBardoTime = itemView.findViewById(R.id.leBardoTime);
            elBortalTime = itemView.findViewById(R.id.elBortalTime);
            manoubaTime = itemView.findViewById(R.id.manoubaTime);
            lesOrangersTime = itemView.findViewById(R.id.lesOrangersTime);
            gobaaTime = itemView.findViewById(R.id.gobaaTime);
            gobaaVilleTime = itemView.findViewById(R.id.gobaaVilleTime);
            // etc.
        }
    }
}

