package services;

import models.Role;
import models.Users;
import utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ServiceUsers implements CRUD<Users>{

    private Connection cnx ;



    public ServiceUsers() {
        cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void insertOne(Users user) throws SQLException {
        String sql = "INSERT INTO users (nom, prenom, email, password, date_creation, adresse, Raison_Sociale, telephone, dateDeNaissance, statut, cin, photo, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        LocalDate birthdate = user.getDateNaissance();
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthdate);



        try {
            // Définir automatiquement le statut et le rôle
            user.setStatut("Inactif");
            user.setRole(Role.CLIENT);

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
            statement.setString(13, user.getRole().toString()); // Assurez-vous que le rôle est converti en chaîne

            // Exécution de la requête d'insertion
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }
    }

    public void updateOne(Users user) throws SQLException {
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



    @Override
    public void deleteOne(Users user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(sql);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<Users> selectAll() throws SQLException {
        // Implémenter la sélection de tous les utilisateurs
        return null;
    }

    public Users getClientByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
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
            LocalDate dateNaissance = resultSet.getDate("dateDeNaissance").toLocalDate(); // Convertir en LocalDate
            String statut = resultSet.getString("statut");
            String cin = resultSet.getString("cin");
            byte[] photo = resultSet.getBytes("photo");
            String roleString = resultSet.getString("Role");
            Role role = Role.valueOf(roleString); // Convertir la chaîne en enum Role

            return new Users(id, nom, prenom, email, password, dateCreation, adresse, raisonSociale, telephone, dateNaissance, statut, cin, photo, role);
        }

        // Return null if user not found with the given email
        return null;
    }
  /*  public void addAdminManually() {
        String adminEmail = "admin@example.com"; // Email de l'administrateur
        String adminPassword = "forzacab"; // Remplacez "VotreMotDePasse" par le mot de passe souhaité
        LocalDate adminBirthDate = LocalDate.of(1990, 1, 1); // Date de naissance de l'administrateur

        // Hashage du mot de passe
        String hashedPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt());

        // Création d'une requête SQL pour insérer l'administrateur
        String sql = "INSERT INTO users (nom, prenom, email, password, date_creation, adresse, Raison_Sociale, telephone, dateDeNaissance, statut, cin, photo, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Exécution de la requête SQL
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            // Paramètres de la requête SQL
            statement.setString(1, "Admin");
            statement.setString(2, "Admin");
            statement.setString(3, adminEmail);
            statement.setString(4, hashedPassword); // Utilisation du mot de passe haché
            statement.setDate(5, new Date(System.currentTimeMillis())); // Date de création actuelle
            statement.setString(6, "Tunis");
            statement.setString(7, "CentralBank");
            statement.setInt(8, 255877888);
            statement.setDate(9, Date.valueOf(adminBirthDate));
            statement.setString(10, "Actif");
            statement.setString(11, "123456789"); // Valeur fictive pour le CIN
            statement.setBytes(12, null); // Photo, s'il y en a une
            statement.setString(13, Role.ADMIN.toString()); // Rôle ADMIN

            // Exécution de la requête d'insertion
            statement.executeUpdate();

            System.out.println("Administrateur ajouté avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'administrateur : " + e.getMessage());
        }
    } public static void main(String[] args) {
        ServiceUsers serviceUsers = new ServiceUsers();
        serviceUsers.addAdminManually();
    }*/
}


