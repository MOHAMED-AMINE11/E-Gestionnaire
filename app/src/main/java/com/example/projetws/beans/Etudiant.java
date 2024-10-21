package com.example.projetws.beans;

import android.graphics.Bitmap;

public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    private String ville;
    private String sexe;
    private Bitmap imageBitmap; // Image sous forme de Bitmap

    // Constructeur avec tous les attributs
    public Etudiant(int id, String nom, String prenom, String ville, String sexe, Bitmap imageBitmap) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.ville = ville;
        this.sexe = sexe;
        this.imageBitmap = imageBitmap; // Image stockée sous forme de Bitmap
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public Bitmap getImageBitmap() { return imageBitmap; }
    public void setImageBitmap(Bitmap imageBitmap) { this.imageBitmap = imageBitmap; }
}
