/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import jade.core.Agent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author Daniel
 */
public class AdminAgent extends Agent {
    
    private List<Socket> blackList = new ArrayList<Socket>();
    private Socket admin = null;
    private DataOutputStream dos;
    private BufferedReader entrada;
    
    protected void setup() {
        System.out.println("\n --> Hola, soy el agente: " + getAID().getName());
        
        addBehaviour(new TickerBehaviour(this, 10000) {
            
            protected void onTick() {
                //CHECAMOS LAS CONEXIONES EXISTENTES
            }
        });
    }
    
    public void AsignarAdmin(Socket newClient)
    {
        try {
            dos = new DataOutputStream(newClient.getOutputStream());
            //entrada = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
        }
        catch(IOException e) {
            
        }
        if(admin != null && !blackList.contains(newClient))
        {
            //CODIGO PARA ENVIARLE AL CLIENTE LA PETICION DE ADMINISTRACION
        }
    }
    
    public void ObtenerRespuesta(Socket newClient, boolean answer)
    {
        if(answer && admin == null)
        {
            admin = newClient;
        }
        else
        {
            blackList.add(newClient);
        }
    }
}
