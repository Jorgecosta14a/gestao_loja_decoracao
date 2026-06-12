package pt.ipvc.estg.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("GestaoLojaDecoracaoPU");

    private JpaUtil() {
    }

    public static EntityManager criarEntityManager() {
        return emf.createEntityManager();
    }

    public static void fechar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
