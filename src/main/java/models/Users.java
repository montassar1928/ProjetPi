package models;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Users {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private Date dateCreation;
    private String adresse;
    private String raisonSociale;
    private int telephone;
    private LocalDate dateNaissance;
    private String cin;
    private byte[] photo;
    private String statut;
    private Role role;

    public Users(int id, String nom, String prenom, String email, String password,
                 Date dateCreation, String adresse, String raisonSociale,
                 int telephone, LocalDate dateNaissance, String statut, String cin,
                 byte[] photo, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.dateCreation = dateCreation;
        this.adresse = adresse;
        this.raisonSociale = raisonSociale;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.statut = statut;
        this.cin = cin;
        this.photo = photo;
        this.role = role;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Date getDateCreation() { return dateCreation; }
    public String getAdresse() { return adresse; }
    public String getRaisonSociale() { return raisonSociale; }
    public int getTelephone() { return telephone; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getCin() { return cin; }
    public byte[] getPhoto() { return photo; }
    public String getStatut() { return statut; }
    public Role getRole() { return role; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public void setTelephone(int telephone) { this.telephone = telephone; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setCin(String cin) { this.cin = cin; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(cin, users.cin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cin);
    }
}



