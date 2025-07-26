/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Personal
 */
public class cliente extends persona {
    private String nit;

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public void agregar() {
        System.out.println("nit: " + getNit() );
        System.out.println("nombres: " + this.getNombres() );
        System.out.println("apellidos: " + this.getApellidos() );
        System.out.println("dirección " + this.getDirección() );
        System.out.println("telefono: " + this.getTelefono() );
        System.out.println("fecha nacimiento: " + this.getFecha_nacimiento() );
    }
}
