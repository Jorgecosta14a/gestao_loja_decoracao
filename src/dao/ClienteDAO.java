package dao;

import database.DBConnection;
import model.Cliente;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> getAllClientes() {

        List<Cliente> clientes = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM cliente");

            while (rs.next()) {

                int id = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String contacto = rs.getString("contacto");
                String nif = rs.getString("nif");

                Cliente cliente = new Cliente(id, nome, contacto, nif);

                clientes.add(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
    }
}