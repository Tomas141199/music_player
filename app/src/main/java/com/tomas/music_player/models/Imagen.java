package com.tomas.music_player.models;

public class Imagen {
    private int id;
    private String nombre;
    private byte[] ruta;
    private int tipo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public byte[] getRuta() {
        return ruta;
    }

    public void setRuta(byte[] ruta) {
        this.ruta = ruta;
    }
}
