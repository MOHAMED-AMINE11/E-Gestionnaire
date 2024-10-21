package com.example.projetws;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {

    private static final String LOAD_URL = "http://192.168.1.117:8080/ws/loadEtudiant.php";
    private static final String UPDATE_URL = "http://192.168.1.117:8080/controller/updateEtudiant.php";

    private final List<Etudiant> etudiants;
    private final Context context;

    public EtudiantAdapter(List<Etudiant> etudiants, Context context) {
        this.etudiants = etudiants;
        this.context = context;
        loadEtudiants(); // Charger les étudiants à l'initialisation
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);

        holder.nom.setText(etudiant.getNom());
        holder.prenom.setText(etudiant.getPrenom());
        holder.ville.setText(etudiant.getVille());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Etudiant_Detail.class);
            intent.putExtra("ETUDIANT_ID", etudiant.getId()); // Remplacez par l'ID réel
            context.startActivity(intent);
        });
        Bitmap bitmap = etudiant.getImageBitmap();
        if (bitmap != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(bitmap)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.logo);
        }

        holder.btnModifier.setOnClickListener(v -> showUpdateDialog(etudiant));
    }
    private void showStudentDetails(int id) {
        String url = "http://192.168.1.117:8080/ws/loadoneEtudiant.php?id=" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String nom = response.getString("nom");
                        String prenom = response.getString("prenom");
                        String ville = response.getString("ville");
                        String sexe = response.getString("sexe");
                        String imageString = response.getString("image");
                        Bitmap imageBitmap = decodeBase64(imageString);

                        // Afficher les informations dans une popup
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Détails de l'Étudiant");

                        // Créer un layout pour la popup
                        LinearLayout layout = new LinearLayout(context);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        // Ajout des éléments à la popup
                        ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(imageBitmap);
                        layout.addView(imageView);

                        TextView textViewNom = new TextView(context);
                        textViewNom.setText("Nom: " + nom);
                        layout.addView(textViewNom);

                        TextView textViewPrenom = new TextView(context);
                        textViewPrenom.setText("Prénom: " + prenom);
                        layout.addView(textViewPrenom);

                        TextView textViewVille = new TextView(context);
                        textViewVille.setText("Ville: " + ville);
                        layout.addView(textViewVille);

                        TextView textViewSexe = new TextView(context);
                        textViewSexe.setText("Sexe: " + sexe);
                        layout.addView(textViewSexe);

                        builder.setView(layout);
                        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        builder.show();

                    } catch (JSONException e) {
                        Log.e("DEBUG", "Error parsing student details: ", e);
                        showToast("Erreur lors de la récupération des détails de l'étudiant.");
                    }
                },
                error -> {
                    Log.e("DEBUG", "Network Error: ", error);
                    showToast("Erreur de réseau lors de la récupération des détails.");
                }
        );

        Volley.newRequestQueue(context).add(request);
    }


    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    private void showUpdateDialog(Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modifier Étudiant");

        LinearLayout layout = createInputLayout(etudiant);
        builder.setView(layout);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String nom = ((EditText) layout.getChildAt(0)).getText().toString();
            String prenom = ((EditText) layout.getChildAt(1)).getText().toString();
            String ville = ((EditText) layout.getChildAt(2)).getText().toString();

            etudiant.setNom(nom);
            etudiant.setPrenom(prenom);
            etudiant.setVille(ville);

            updateEtudiant(etudiant);
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateEtudiant(Etudiant etudiant) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", etudiant.getId());
            jsonObject.put("nom", etudiant.getNom());
            jsonObject.put("prenom", etudiant.getPrenom());
            jsonObject.put("ville", etudiant.getVille());
            Log.d("DEBUG", "JSON Request: " + jsonObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, UPDATE_URL, jsonObject,
                    response -> {
                        Log.d("DEBUG", "Response: " + response.toString());
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if (status.equals("success")) {
                                loadEtudiants();  // Recharger la liste
                                showToast("Étudiant modifié avec succès !");
                            } else {
                                showToast("Erreur : " + message);
                            }
                        } catch (JSONException e) {
                            Log.e("DEBUG", "JSON Error: ", e);
                            showToast("Erreur JSON : " + e.getMessage());
                        }
                    },
                    error -> {
                        Log.e("DEBUG", "Network Error: ", error);
                        String responseBody = error.networkResponse != null
                                ? new String(error.networkResponse.data, StandardCharsets.UTF_8)
                                : "Erreur inconnue";
                        showToast("Erreur réseau : " + responseBody);
                    }
            );

            Volley.newRequestQueue(context).add(request);
        } catch (JSONException e) {
            Log.e("DEBUG", "JSON Creation Error: ", e);
            showToast("Erreur lors de la création de l'objet JSON : " + e.getMessage());
        }
    }



    private void loadEtudiants() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, LOAD_URL, null,
                this::parseEtudiants,
                error -> showToast("Erreur de chargement : " + error.getMessage())
        );

        Volley.newRequestQueue(context).add(request);
    }

    private void parseEtudiants(JSONArray response) {
        try {
            etudiants.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                String imageString = obj.getString("image");
                Bitmap imageBitmap = decodeBase64(imageString);

                Etudiant etudiant = new Etudiant(
                        obj.getInt("id"), obj.getString("nom"),
                        obj.getString("prenom"), obj.getString("ville"),
                        obj.getString("sexe"), imageBitmap
                );
                etudiants.add(etudiant);
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Erreur lors du parsing des données : " + e.getMessage());
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    private Bitmap decodeBase64(String imageString) {
        byte[] decodedBytes = Base64.getDecoder().decode(imageString);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private LinearLayout createInputLayout(Etudiant etudiant) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText editNom = new EditText(context);
        editNom.setHint("Nom");
        editNom.setText(etudiant.getNom());
        layout.addView(editNom);

        EditText editPrenom = new EditText(context);
        editPrenom.setHint("Prénom");
        editPrenom.setText(etudiant.getPrenom());
        layout.addView(editPrenom);

        EditText editVille = new EditText(context);
        editVille.setHint("Ville");
        editVille.setText(etudiant.getVille());
        layout.addView(editVille);

        return layout;
    }

    private void showToast(String message) {
        ((Activity) context).runOnUiThread(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }


    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nom, prenom, ville;
        ImageView image;
        Button btnModifier;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.nomEtudiant);
            prenom = itemView.findViewById(R.id.prenomEtudiant);
            ville = itemView.findViewById(R.id.villeEtudiant);
            image = itemView.findViewById(R.id.imageEtudiant);
            btnModifier = itemView.findViewById(R.id.btnModifier);
        }
    }
}
