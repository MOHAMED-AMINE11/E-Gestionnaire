package com.example.projetws;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ici, mets l'ID de l'étudiant dont tu veux mettre à jour l'image
        int etudiantId = 1; // Remplace par l'ID de l'étudiant
        // Ici, mets le chemin de l'image que tu souhaites insérer
        String imagePath = "/storage/emulated/0/Pictures/image1.jpg"; // Remplace par le chemin de ton image

        // Appel de la méthode pour mettre à jour l'image de l'étudiant
        updateEtudiantImage(etudiantId, imagePath);
    }

    // Méthode pour mettre à jour l'image d'un étudiant
    private void updateEtudiantImage(int id, String imagePath) {
        new UpdateImageTask().execute(id, imagePath);
    }

    // AsyncTask pour exécuter la mise à jour de l'image en arrière-plan
    private class UpdateImageTask extends AsyncTask<Object, Void, Boolean> {
        private int id;
        private String imagePath;

        @Override
        protected Boolean doInBackground(Object... params) {
            id = (int) params[0];          // Récupération de l'ID de l'étudiant
            imagePath = (String) params[1]; // Récupération du chemin de l'image

            // Connexion à la base de données MySQL
            String url = "jdbc:mysql://localhost:3306/school1"; // Change 'school1' avec le nom de ta base
            String user = "root"; // Ton utilisateur MySQL
            String password = ""; // Ton mot de passe MySQL

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Requête SQL pour mettre à jour l'image
                String sql = "UPDATE Etudiants SET image = ? WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(sql);

                // Lecture de l'image à partir du fichier
                FileInputStream inputStream = new FileInputStream(new File(imagePath));
                statement.setBinaryStream(1, inputStream, (int) new File(imagePath).length());
                statement.setInt(2, id); // Spécifie l'ID de l'étudiant

                // Exécution de la requête
                int rows = statement.executeUpdate();
                inputStream.close();

                return rows > 0; // Renvoie vrai si une ligne a été mise à jour
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Renvoie faux en cas d'erreur
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Image mise à jour avec succès pour l'étudiant avec ID " + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Erreur lors de la mise à jour de l'image pour l'étudiant avec ID " + id, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
