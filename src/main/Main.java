package main;

import bll.ClienteService;
import bll.VendaService;
import model.Cliente;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== SISTEMA DE GESTÃO DE DECORAÇÃO ===");
        System.out.println("A iniciar o sistema...\n");


        System.out.println(">> TESTE 1: Listar Clientes Registados na BD");
        ClienteService clienteService = new ClienteService();
        List<Cliente> clientes = clienteService.getClientes();

        if (clientes != null && !clientes.isEmpty()) {
            for (Cliente c : clientes) {
                System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome() +
                        " | Contacto: " + c.getContacto() + " | NIF: " + c.getNif());
            }
        } else {
            System.out.println("Nenhum cliente encontrado na base de dados.");
        }
        System.out.println("--------------------------------------------------\n");


        System.out.println(">> TESTE 2: Registar Venda (Validação de Stock pela BLL)");
        VendaService vendaService = new VendaService();

        System.out.println("\n[Cenário A] - Tentar vender 2 unidades do produto 1:");
        vendaService.registarVenda(1, 2, 1, 1);

        System.out.println("\n[Cenário B] - Tentar vender 10 unidades do produto 1:");
        vendaService.registarVenda(1, 10, 1, 1);

        System.out.println("\n==================================================");
    }
}