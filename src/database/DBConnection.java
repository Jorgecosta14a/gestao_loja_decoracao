package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/ProjII";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1809";

    public static Connection getConnection() {

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Ligação à base de dados estabelecida!");
            return conn;

        } catch (SQLException e) {
            System.out.println("Erro ao ligar à base de dados.");
            e.printStackTrace();
            return null;
        }
    }
}