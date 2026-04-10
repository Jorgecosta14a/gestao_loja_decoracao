package bll;

import dao.ClienteDAO;
import model.Cliente;
import java.util.List;

public class ClienteService {

    private ClienteDAO clienteDAO = new ClienteDAO();

    public void registarCliente(Cliente c) {
        clienteDAO.inserirCliente(c);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.buscarTodosClientes();
    }

    public Cliente buscarPorId(int id) {
        return clienteDAO.buscarClientePorId(id);
    }

    public void editarCliente(Cliente c) {
        clienteDAO.atualizarCliente(c);
    }

    public String apagarCliente(int id) {
        try {
            clienteDAO.removerCliente(id);
            return "✅ Cliente removido com sucesso!";
        } catch (Exception e) {
            return "❌ Erro: Não pode apagar um cliente que já tem vendas registadas!";
        }
    }
}