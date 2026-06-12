package pt.ipvc.estg.bll;

import pt.ipvc.estg.dao.ClienteDAO;
import pt.ipvc.estg.model.Cliente;

import java.util.List;

public class ClienteService {

    public static final String NIF_CONSUMIDOR_FINAL = "999999990";

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public Cliente registarCliente(Cliente cliente) {
        if (cliente == null || cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Indique o nome do cliente.");
        }

        cliente.setNome(cliente.getNome().trim());
        cliente.setContacto(cliente.getContacto() == null ? "" : cliente.getContacto().trim());
        cliente.setNif(cliente.getNif() == null ? "" : cliente.getNif().trim());

        if (!cliente.getNif().isEmpty() && clienteDAO.buscarPorNif(cliente.getNif()) != null) {
            throw new IllegalArgumentException("Ja existe um cliente com esse NIF.");
        }

        return clienteDAO.inserirCliente(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.buscarTodosClientes();
    }

    public List<Cliente> listarIdentificados() {
        return listarTodos().stream()
                .filter(cliente -> !NIF_CONSUMIDOR_FINAL.equals(cliente.getNif()))
                .toList();
    }

    public Cliente garantirConsumidorFinal() {
        Cliente existente = clienteDAO.buscarPorNif(NIF_CONSUMIDOR_FINAL);
        if (existente != null) {
            return existente;
        }
        return clienteDAO.inserirCliente(new Cliente("Consumidor Final", "", NIF_CONSUMIDOR_FINAL));
    }

    public Cliente buscarPorId(int id) {
        return clienteDAO.buscarClientePorId(id);
    }

    public Cliente editarCliente(Cliente cliente) {
        return clienteDAO.atualizarCliente(cliente);
    }

    public String apagarCliente(int id) {
        try {
            clienteDAO.removerCliente(id);
            return "Cliente removido com sucesso.";
        } catch (Exception e) {
            return "Erro: nao pode apagar um cliente que ja tem vendas registadas.";
        }
    }
}
