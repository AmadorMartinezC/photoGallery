package com.app.imagegalery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    final private List<Image> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private String name;
        private final ImageView iView;
        private final TextView tView;
        private final EditText eText;
        private final Button editTextBtn;

        public ViewHolder(View view) {
            super(view);
            iView = view.findViewById(R.id.iView);
            tView = view.findViewById(R.id.tView);
            eText = view.findViewById(R.id.editText);
            editTextBtn = view.findViewById(R.id.editBtn);
            editTextBtn.setOnClickListener(v -> {
                if (!eText.getText().toString().equals("")){
                    String comment = eText.getText().toString();
                    tView.setText(comment);
                    InternalAccessData.saveComment(v.getContext(), name, comment);
                } else {
                    Toast.makeText(v.getContext(), "No hay texto para añadir", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public String getName() {
            return name;
        }

        public void setName (String name) {
            this.name = name;
        }

        public ImageView getImageView() {
            return iView;
        }

        public TextView getTextView() {
            return tView;
        }

        public Button getEditCommentButton() {
            return editTextBtn;
        }

    }

    public ImageAdapter(List<Image> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setName(localDataSet.get(position).getName());
        holder.getImageView().setImageBitmap(localDataSet.get(position).getBitmap());
        holder.getTextView().setText(localDataSet.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}