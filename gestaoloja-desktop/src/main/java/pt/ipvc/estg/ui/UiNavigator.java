package pt.ipvc.estg.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class UiNavigator {

    private static Stage stage;

    private UiNavigator() {
    }

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setMinWidth(1040);
        stage.setMinHeight(700);
    }

    public static void showLogin() {
        show("/ui/Login.fxml", "Nordic Studio - Iniciar sessao");
    }

    public static void showDashboard() {
        show("/ui/dashboard.fxml", "Nordic Curator - Dashboard");
    }

    public static void showSales() {
        show("/ui/sales.fxml", "Nordic Curator - Registar Venda");
    }

    public static void showClients() {
        show("/ui/clients.fxml", "Nordic Curator - Clientes");
    }

    public static void showProducts() {
        show("/ui/products.fxml", "Nordic Curator - Produtos e Stock");
    }

    public static void showWarranties() {
        show("/ui/warranties.fxml", "Nordic Curator - Garantias");
    }

    private static void show(String resourcePath, String title) {
        if (stage == null) {
            throw new IllegalStateException("Stage principal ainda nao foi configurado.");
        }

        try {
            URL resource = UiNavigator.class.getResource(resourcePath);
            if (resource == null) {
                throw new IllegalArgumentException("Recurso FXML nao encontrado: " + resourcePath);
            }

            Parent root = FXMLLoader.load(resource);
            double width = stage.getScene() == null ? 1180 : Math.max(stage.getScene().getWidth(), 1040);
            double height = stage.getScene() == null ? 760 : Math.max(stage.getScene().getHeight(), 700);
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar o ecra: " + resourcePath, e);
        }
    }
}
