package com.example.projetws;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.Etudiant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.core.app.ShareCompat; // Assurez-vous d'importer ShareCompat
public class AfficherEtudiantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiants = new ArrayList<>();
    private static final String BASE_URL = "http://192.168.1.117:8080";
    private static final String LOAD_URL = BASE_URL + "/ws/loadEtudiant.php";
    private static final String DELETE_URL = BASE_URL + "/controller/deleteEtudiant.php?id=";

    private static final String ADD_URL = BASE_URL + "/controller/addEtudiant.php";
    private static final String UPDATE_URL = "http://192.168.1.117:8080/controller/updateEtudiant.php";
    private Bitmap selectedImageBitmap; // Pour stocker l'image sélectionnée

    // Lancer une activité pour sélectionner une image
    private ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Erreur lors de la sélection de l'image");
                    }
                }
            });
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu); // Remplacez menu_main par le nom de votre fichier XML
        return true; // Indique que le menu a été créé
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) { // Vérifiez l'ID de l'élément de menu
            String txt = "Stars"; // Le texte à partager
            String mimeType = "text/plain"; // Le type MIME
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle("Partager Stars")
                    .setText(txt)
                    .startChooser();
            return true; // Indique que l'événement a été traité
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_etudiants);

        setupRecyclerView();
        loadEtudiants();

    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewEtudiants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EtudiantAdapter(etudiants, this);
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void loadEtudiants() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, LOAD_URL, null,
                this::parseEtudiants,
                error -> showToast("Erreur de chargement : " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
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
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erreur lors du parsing des données : " + e.getMessage());
        }
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private final ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    showDeleteConfirmationDialog(viewHolder.getAdapterPosition());
                }
            };

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer cet étudiant ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteEtudiant(position))
                .setNegativeButton("Non", (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    private void deleteEtudiant(int position) {
        String url = DELETE_URL + etudiants.get(position).getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    etudiants.remove(position);
                    adapter.notifyItemRemoved(position);
                    showToast("Étudiant supprimé avec succès");
                },
                error -> {
                    showToast("Erreur lors de la suppression : " + error.getMessage());
                    adapter.notifyItemChanged(position);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    public void showUpdateDialog(Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier Étudiant");

        LinearLayout layout = createInputLayout(etudiant);
        builder.setView(layout);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String nom = ((EditText) layout.getChildAt(0)).getText().toString();
            String prenom = ((EditText) layout.getChildAt(1)).getText().toString();
            String ville = ((EditText) layout.getChildAt(2)).getText().toString();
            updateEtudiant(etudiant.getId(), nom, prenom, ville); // Ajout de la mise à jour d'image
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private LinearLayout createInputLayout(Etudiant etudiant) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(createEditText("Nom", etudiant.getNom()));
        layout.addView(createEditText("Prénom", etudiant.getPrenom()));
        layout.addView(createEditText("Ville", etudiant.getVille()));
        layout.addView(createImageView(etudiant.getImageBitmap())); // Afficher l'image actuelle
        return layout;
    }

    private EditText createEditText(String hint, String text) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(text);
        return editText;
    }

    private ImageView createImageView(Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(view -> selectImage());
        return imageView;
    }

    private void selectImage() {
        imagePickerLauncher.launch("image/*");
    }

    private void updateEtudiant(int id, String nom, String prenom, String ville) {
        String url = UPDATE_URL;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("nom", nom);
            jsonObject.put("prenom", prenom);
            jsonObject.put("ville", ville);
            if (selectedImageBitmap != null) {
                jsonObject.put("image", encodeImageToBase64(selectedImageBitmap));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    loadEtudiants();
                    showToast("Étudiant modifié avec succès");
                },
                error -> showToast("Erreur lors de la modification : " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }



    private void addEtudiant(String nom, String prenom, String ville) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nom", nom);
            jsonObject.put("prenom", prenom);
            jsonObject.put("ville", ville);
            if (selectedImageBitmap != null) {
                jsonObject.put("image", encodeImageToBase64(selectedImageBitmap));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ADD_URL, jsonObject,
                response -> {
                    loadEtudiants();
                    showToast("Étudiant ajouté avec succès");
                },
                error -> showToast("Erreur lors de l'ajout : " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
