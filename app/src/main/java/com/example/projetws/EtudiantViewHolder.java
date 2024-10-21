package com.example.projetws;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetws.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EtudiantViewHolder extends RecyclerView.ViewHolder {
    CircleImageView imageEtudiant;
    TextView nomEtudiant, prenomEtudiant;
    Button btnModifier;

    public EtudiantViewHolder(@NonNull View itemView) {
        super(itemView);
        imageEtudiant = itemView.findViewById(R.id.imageEtudiant);
        nomEtudiant = itemView.findViewById(R.id.nomEtudiant);
        prenomEtudiant = itemView.findViewById(R.id.prenomEtudiant);
        btnModifier = itemView.findViewById(R.id.btnModifier);
    }
}
