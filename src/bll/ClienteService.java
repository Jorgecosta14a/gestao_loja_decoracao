package bll;

import dao.ClienteDAO;
import model.Cliente;

import java.util.List;

public class ClienteService {

    private ClienteDAO clienteDAO;

    public ClienteService() {
        clienteDAO = new ClienteDAO();
    }

    public List<Cliente> getClientes() {
        return clienteDAO.getAllClientes();
    }
}