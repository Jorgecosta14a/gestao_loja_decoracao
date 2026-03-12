package dao;

import database.DBConnection;
import model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoDAO {

    public Produto obterProdutoPorId(int idProduto) {
        String sql = "SELECT id_produto, nome, preco, quantidade_stock FROM produto WHERE id_produto = ?";

        // O teu DBConnection.getConnection() é chamado aqui!
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Produto(
                        rs.getInt("id_produto"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade_stock")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao procurar produto na Base de Dados: " + e.getMessage());
        }
        return null; // Retorna nulo se o produto não existir na BD
    }
}