/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

/**
 *
 * @author USUARIO DELL
 */
public class Usuario {
    
    private String Nombre;
    private String IP;
    
    public Usuario(String nombre, String ip){
        Nombre = nombre;
        IP = ip;
    }
    
    public String getNombre(){return Nombre;}
    public String getIP() { return IP; }
    
}
