package utils;

import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        // Obtenez l'instance de ConnexionBase et récupérez la connexion
        Connection connection = DBConnection.getInstance().getCnx();
        if (connection != null) {
            System.out.println("Succès de la connexion à la base de données !");
        } else {
            System.out.println("Échec de la connexion à la base de données.");
        }
    }
}

