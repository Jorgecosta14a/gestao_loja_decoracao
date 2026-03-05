package model;

public class Cliente {

    private int id;
    private String nome;
    private String contacto;
    private String nif;

    public Cliente(int id, String nome, String contacto, String nif) {
        this.id = id;
        this.nome = nome;
        this.contacto = contacto;
        this.nif = nif;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getContacto() {
        return contacto;
    }

    public String getNif() {
        return nif;
    }

}