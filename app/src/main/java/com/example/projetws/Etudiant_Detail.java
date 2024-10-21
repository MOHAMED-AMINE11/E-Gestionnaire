package com.example.projetws;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class Etudiant_Detail extends AppCompatActivity {

    private TextView nomTextView, prenomTextView, villeTextView, sexeTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialisation des TextViews et de l'ImageView
        nomTextView = findViewById(R.id.nomTextView);
        prenomTextView = findViewById(R.id.prenomTextView);
        villeTextView = findViewById(R.id.villeTextView);
        sexeTextView = findViewById(R.id.sexeTextView);
        imageView = findViewById(R.id.imageView); // Assurez-vous d'avoir un ImageView pour afficher l'image

        // Récupération de l'ID de l'étudiant
        int etudiantId = getIntent().getIntExtra("ETUDIANT_ID", -1);

        if (etudiantId != -1) {
            loadOneEtudiant(etudiantId); // Appel de la méthode pour charger les infos
        } else {
            Toast.makeText(this, "Échec de la récupération des détails de l'étudiant", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOneEtudiant(int etudiantId) {
        String url = "http://192.168.1.117:8080/ws/loadoneEtudiant.php?id=" + etudiantId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Récupération des données
                            String nom = response.getString("nom");
                            String prenom = response.getString("prenom");
                            String ville = response.getString("ville");
                            String sexe = response.getString("sexe");
                            String image = response.getString("image");

                            // Affichage des données dans les TextViews
                            nomTextView.setText(nom);
                            prenomTextView.setText(prenom);
                            villeTextView.setText(ville);
                            sexeTextView.setText(sexe);

                            // Affichage de l'image
                            byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            imageView.setImageBitmap(bitmap);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Etudiant_Detail.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Etudiant_Detail.this, "Erreur de réseau : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajout de la requête à la file d'attente
        queue.add(jsonObjectRequest);
    }
}
