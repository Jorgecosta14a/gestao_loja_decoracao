package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.DadosIniciaisService;
import pt.ipvc.estg.dao.JpaUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        prepararBaseDados();
        UiNavigator.setStage(stage);
        stage.setOnCloseRequest(event -> JpaUtil.fechar());
        UiNavigator.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void prepararBaseDados() {
        try {
            new DadosIniciaisService().garantirDados();
        } catch (RuntimeException e) {
            System.err.println("Nao foi possivel preparar dados iniciais: " + e.getMessage());
        }
    }
}
