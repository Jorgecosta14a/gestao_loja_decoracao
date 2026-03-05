package main;

import bll.ClienteService;
import model.Cliente;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("A iniciar o sistema...");

        ClienteService clienteService = new ClienteService();

        List<Cliente> clientes = clienteService.getClientes();

        for (Cliente c : clientes) {
            System.out.println("ID: " + c.getId());
            System.out.println("Nome: " + c.getNome());
            System.out.println("Contacto: " + c.getContacto());
            System.out.println("NIF: " + c.getNif());
            System.out.println("----------------------");
        }

    }
}