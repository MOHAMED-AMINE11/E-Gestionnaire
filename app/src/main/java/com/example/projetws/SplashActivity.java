package com.example.projetws;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetws.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Applique l'animation au logo
        ImageView logo = findViewById(R.id.logo);


        // Applique l'animation au titre
        TextView title = findViewById(R.id.splashTitle);


        // Redirection vers la LoginActivity après un délai
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ferme la SplashActivity
        }, 3000); // 3 secondes
    }

}
