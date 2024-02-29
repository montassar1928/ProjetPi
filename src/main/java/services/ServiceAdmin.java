package services;

import models.Role;
import models.Users;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdmin implements CRUD<Users>{
    private Connection cnx ;



    public ServiceAdmin() {
        cnx = DBConnection.getInstance().getCnx();
    }
    @Override
    public void insertOne(Users user) throws SQLException {
        String sql = "INSERT INTO users (nom, prenom, email, password, date_creation, adresse, Raison_Sociale, telephone, dateDeNaissance, statut, cin, photo, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        LocalDate birthdate = user.getDateNaissance();
        java.sql.Date sqlDate = birthdate != null ? java.sql.Date.valueOf(birthdate) : null;

        try {
            // Définir automatiquement le statut et le rôle
            user.setStatut("Actif");
            user.setRole(Role.BANQUE);

            // Exécution de la requête SQL en utilisant la connexion à la base de données
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, hashedPassword);
            statement.setDate(5, new java.sql.Date(user.getDateCreation().getTime()));
            statement.setString(6, user.getAdresse());
            statement.setString(7, user.getRaisonSociale());
            statement.setInt(8, user.getTelephone());
            statement.setDate(9, sqlDate);
            statement.setString(10, user.getStatut());
            statement.setString(11, user.getCin());
            statement.setBytes(12, user.getPhoto());
            statement.setString(13, user.getRole().toString());

            // Exécution de la requête d'insertion
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }
    }


    @Override
    public void updateOne(Users user) throws SQLException {
        String sql = "UPDATE users SET nom=?, prenom=?, email=?, adresse=?, Raison_Sociale=?, telephone=?, dateDeNaissance=?, photo=? WHERE id=?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getAdresse());
            statement.setString(5, user.getRaisonSociale());
            statement.setInt(6, user.getTelephone());
            statement.setDate(7, java.sql.Date.valueOf(user.getDateNaissance()));
            statement.setBytes(8, user.getPhoto());
            statement.setInt(9, user.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("L'utilisateur a été mis à jour avec succès.");
            } else {
                System.out.println("Aucune mise à jour effectuée pour cet utilisateur.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }


    @Override
    public void deleteOne(Users user) throws SQLException {
        String sql = "UPDATE users SET statut = 'Inactif' WHERE id = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, user.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le compte utilisateur a été rendu inactif avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la désactivation du compte utilisateur : " + e.getMessage());
        }
    }




    public  Users getAdminByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ? AND role = 'ADMIN'";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String nom = resultSet.getString("nom");
            String prenom = resultSet.getString("prenom");
            String password = resultSet.getString("password");
            Date dateCreation = resultSet.getDate("date_creation");
            String adresse = resultSet.getString("adresse");
            String raisonSociale = resultSet.getString("Raison_Sociale");
            int telephone = resultSet.getInt("telephone");
            LocalDate dateNaissance = resultSet.getDate("dateDeNaissance").toLocalDate();
            String statut = resultSet.getString("statut");
            String cin = resultSet.getString("cin");
            byte[] photo = resultSet.getBytes("photo");
            Role role = Role.ADMIN; // Vous pouvez définir le rôle directement ici

            return new Users(id, nom, prenom, email, password, dateCreation, adresse, raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
        }

        // Retourne null si aucun administrateur n'est trouvé avec l'e-mail donné
        return null;
    }

    @Override
    public List<Users> selectAll() throws SQLException {
        List<Users> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE Role = ? AND statut = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, Role.BANQUE.toString());
            statement.setString(2, "Actif"); // Seuls les utilisateurs avec un statut "Actif" seront récupérés

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Date dateCreation = resultSet.getDate("date_creation");
                    String adresse = resultSet.getString("adresse");
                    String raisonSociale = resultSet.getString("Raison_Sociale");
                    int telephone = resultSet.getInt("telephone");

                    // Vérifier si la date de naissance est nulle
                    LocalDate dateNaissance = null;
                    Date dateObject = resultSet.getDate("dateDeNaissance");
                    if (dateObject != null) {
                        dateNaissance = dateObject.toLocalDate();
                    }

                    String statut = resultSet.getString("statut");
                    String cin = resultSet.getString("cin");
                    byte[] photo = resultSet.getBytes("photo");
                    Role role = Role.BANQUE; //

                    Users user = new Users(id, nom, prenom, email, password, dateCreation, adresse,
                            raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
                    usersList.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return usersList;
    }
    public void updatePass(Users user) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            // Hasher le nouveau mot de passe
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            // Paramètres de la requête SQL
            statement.setString(1, hashedPassword);
            statement.setInt(2, user.getId()); // Assurez-vous que l'ID de l'utilisateur est correctement défini

            // Exécution de la requête de mise à jour
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Le mot de passe de l'utilisateur a été mis à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour du mot de passe de l'utilisateur.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe de l'utilisateur : " + e.getMessage());
            e.printStackTrace(); // Ajoutez ceci pour obtenir une trace complète de l'exception
        }
    }

    }





