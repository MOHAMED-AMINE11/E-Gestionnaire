package com.example.projetws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1; // Code pour la sélection d'image
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private Button selectImage;
    private ImageView imageView;
    private Uri imageUri; // URI de l'image sélectionnée
    private RequestQueue requestQueue;

    // URL de l'API pour l'insertion
    private static final String INSERT_URL = "http://192.168.1.117:8080/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        // Initialisation des vues
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        add = findViewById(R.id.add);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        selectImage = findViewById(R.id.selectImage);
        imageView = findViewById(R.id.imageView);

        // Initialiser le Spinner avec les villes
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ville, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ville.setAdapter(adapter);

        // Définir les listeners
        add.setOnClickListener(this);
        selectImage.setOnClickListener(this);

        // Initialiser la queue de requêtes Volley
        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v) {
        if (v == add) { // Vérifie si le bouton cliqué est le bouton d'ajout
            ajouterEtudiant();
        } else if (v == selectImage) { // Vérifie si le bouton cliqué est le bouton de sélection d'image
            openImageChooser();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Sélectionnez une image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            imageView.setVisibility(View.VISIBLE); // Affiche l'image sélectionnée
        }
    }

    private void ajouterEtudiant() {
        if (isInputValid()) {
            StringRequest request = new StringRequest(Request.Method.POST, INSERT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("AddEtudiant", "Response: " + response);
                            handleResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("AddEtudiant", "Erreur lors de l'ajout de l'étudiant : " + error.getMessage());
                            Toast.makeText(getApplicationContext(), "Erreur réseau. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    String sexe = m.isChecked() ? "homme" : "femme";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("nom", nom.getText().toString().trim());
                    params.put("prenom", prenom.getText().toString().trim());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);
                    params.put("image", getStringImage(imageUri)); // Ajouter l'image encodée
                    return params; // Retourner les paramètres à envoyer
                }
            };
            requestQueue.add(request); // Ajouter la requête à la queue
        }
    }

    private String getStringImage(Uri uri) {
        Bitmap bitmap;
        String encodedImage = "";
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT); // Encoder en Base64
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage; // Retourner l'image encodée
    }

    private boolean isInputValid() {
        if (nom.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Veuillez entrer le nom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (prenom.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Veuillez entrer le prénom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ville.getSelectedItem() == null || ville.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Veuillez sélectionner une ville", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!m.isChecked() && !f.isChecked()) {
            Toast.makeText(this, "Veuillez sélectionner le sexe", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // Tous les champs sont valides
    }

    private void handleResponse(String response) {
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getBoolean("success")) {
                // Affichage des détails de l'étudiant ajouté
                Log.d("AddEtudiant", "Étudiant ajouté: " + responseJson.getJSONObject("etudiant").toString());
                Toast.makeText(getApplicationContext(), "Étudiant ajouté avec succès.", Toast.LENGTH_SHORT).show();
                resetFields(); // Réinitialise les champs après ajout
            } else {
                Log.e("AddEtudiant", "Erreur: " + responseJson.getString("message"));
                Toast.makeText(getApplicationContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("AddEtudiant", "Erreur de parsing JSON: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Etudiant ajouté avec succés.", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetFields() {
        nom.setText("");
        prenom.setText("");
        ville.setSelection(0); // Remet le spinner à la première position
        m.setChecked(false);
        f.setChecked(false);
        imageView.setImageURI(null);
        imageView.setVisibility(View.GONE); // Cache l'ImageView après la réinitialisation
    }
}
