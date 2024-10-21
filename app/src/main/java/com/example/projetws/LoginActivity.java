package com.example.projetws; // Remplacez par votre package

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Vérifie les informations d'identification
        if ("admin".equals(username) && "123456".equals(password)) {
            // Accès autorisé, redirige vers l'écran de choix
            Intent intent = new Intent(LoginActivity.this, ChoixActivity.class);
            startActivity(intent);
            finish(); // Optionnel : Termine l'activité de connexion
        } else {
            // Accès refusé
            Toast.makeText(LoginActivity.this, "Nom d'utilisateur ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
        }
    }
}

