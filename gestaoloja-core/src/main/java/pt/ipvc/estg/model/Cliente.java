package pt.ipvc.estg.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String contacto;

    @Column(length = 20)
    private String nif;

    public Cliente() {
    }

    public Cliente(String nome, String contacto, String nif) {
        this.nome = nome;
        this.contacto = contacto;
        this.nif = nif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    @Override
    public String toString() {
        if (nif == null || nif.isBlank()) {
            return nome;
        }
        return nome + " (" + nif + ")";
    }
}
