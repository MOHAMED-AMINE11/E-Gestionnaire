package com.example.projetws;

import com.example.projetws.beans.Etudiant;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Déclaration de l'endpoint DELETE
    @DELETE("controller/deleteEtudiant.php/{id}")
    Call<Void> deleteEtudiant(@Path("id") int id);

    // Singleton pour accéder à l'instance d'ApiService
    class Instance {
        private static final String BASE_URL = "https://example.com/api/"; // Remplace par ton URL de base
        private static ApiService instance;

        public static ApiService getInstance() {
            if (instance == null) {
                // Création de l'instance Retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Initialisation du service ApiService
                instance = retrofit.create(ApiService.class);
            }
            return instance;
        }
    }
    @PUT("controller/updateEtudiant.php")
    Call<Void> updateEtudiant(@Query("id") int id, @Query("nom") String nom, @Query("prenom") String prenom, @Query("ville") String ville);

}
