package com.example.projetws; // Remplacez par votre nom de package

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ChoixActivity extends AppCompatActivity {

    private Button addStudentButton;
    private Button viewStudentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix); // Assurez-vous que cela pointe vers votre fichier XML

        addStudentButton = findViewById(R.id.addStudentButton);
        viewStudentsButton = findViewById(R.id.viewStudentsButton);

        // Redirection vers l'activité d'ajout d'étudiant
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoixActivity.this, AddEtudiant.class); // Créez AddStudentActivity
                startActivity(intent);
            }
        });

        // Redirection vers l'activité d'affichage des étudiants
        viewStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoixActivity.this, AfficherEtudiantsActivity.class); // Créez ViewStudentsActivity
                startActivity(intent);
            }
        });
    }
}
