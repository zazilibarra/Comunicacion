/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author USUARIO DELL
 */

//Esta clase esta destinada para ofrecer una interfaz web al usuario al mandar una peticion GET al servidor.
public class ServidorHttp extends Thread {
    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String TOPICS_FILE = "topics.html";
    static final String USERS_FILE = "users.html";
    static final String SENSORS_FILE = "sensors.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    static final String JSON_FILE = "getinfo.json";
    
    static final int PORT = 8080;
    
    static final boolean verbose = true;
    
    private Socket connect;
    
    public ServidorHttp(Socket c){
        connect = c;
    }

    //Este hilo atiende constantemente las peticiones que se realizan para regresarles la informacion que se necesita.
    @Override
    public void run() {
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
        String fileRequested = null;
        
        try{
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            
            String input = in.readLine();
            
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken().toLowerCase();
            boolean isFile = false;
            
            if(!method.equals("GET") && !method.equals("HEAD")){
                if(verbose){
                    System.out.println("501 Not implemented " + method + " method");
                }
                
                File file = new File(WEB_ROOT,METHOD_NOT_SUPPORTED);
                int fileLength = (int)file.length();
                String contentMimeType = "text/html";
                byte[] fileData = readFileData(file,fileLength);
                
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server Http from Ssaurel: 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + contentMimeType);
                out.println("Content-length: " + fileLength);
                out.println();
                out.flush();
                
                dataOut.write(fileData,0,fileLength);
                dataOut.flush();
                
            }else{
                if(fileRequested.endsWith("/")){
                    fileRequested+= DEFAULT_FILE;
                    isFile = true;
                }
                else if(fileRequested.endsWith("getinfo")){
                    fileRequested = "/" + JSON_FILE;
                    isFile = true;
                }
                else if(fileRequested.endsWith("topics")){
                    fileRequested = "/" + TOPICS_FILE;
                    isFile = true;
                }
                else if(fileRequested.endsWith("users")){
                    fileRequested = "/" + USERS_FILE;
                    isFile = true;
                }
                else if(fileRequested.endsWith("sensors")){
                    fileRequested = "/" + SENSORS_FILE;
                    isFile = true;
                }
                else {
                    fileRequested = "/" + FILE_NOT_FOUND;
                    isFile = true;
                }
                
                if(isFile){
                    File file = new File(WEB_ROOT,fileRequested);
                    int fileLength = (int)file.length();
                    String content = getContentType(fileRequested);
                    
                    if(method.equals("GET")){
                        byte[] fileData = readFileData(file,fileLength);

                        /*out.println("HTTP/1.1 200 OK");
                        out.println("Server Http from Ssaurel: 1.0");
                        out.println("Date: " + new Date());
                        out.println("Content-type: " + content);
                        out.println("Content-length: " + fileLength);
                        out.println();*/
                        out.write("HTTP/1.0 200 OK\r\n");
                        out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
                        out.write("Content-Type: " + content + "\r\n");
                        out.write("Content-Length: "+ fileLength +"\r\n");
                        out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
                        out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
                        out.write("\r\n");
                        out.flush();

                        dataOut.write(fileData,0,fileLength);
                        dataOut.flush();
                    }
                
                    if(verbose){
                        System.out.println("File " + fileRequested + " of type " + content + " returned");
                    }
                }else{
                    //dataOut.write((jsonData.toString() + ".html").getBytes("UTF-8"));
                    //dataOut.flush();
                }
            }
        }
        catch(FileNotFoundException ex){
            try {
                fileNotFound(out,dataOut,fileRequested);
            } catch (IOException ex1) {
                System.err.println("Error with the file not found "+ ex1.getMessage() );
            }
        }
        catch(Exception ex){
            System.err.println("Server Error "+ ex.getMessage() );
        }
        finally{
            try {
                in.close();
                dataOut.close();
                connect.close();
            } catch (IOException ex) {
                System.err.println("Error closing the stream " + ex.getMessage());
            }
            
            if(verbose){
                System.out.println("Conection closed.\n");
            }
        }
    }
    
    //Se lee el archivo que se va a regresar al cliente, ya sea la interfaz de usuario o el json con la informacion
    //actual de los sensores.
    private byte[] readFileData(File file, int fileLength) throws FileNotFoundException, IOException{
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        
        try{
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        }finally{
            if(fileIn != null){
                fileIn.close();
            }
        }
        
        return fileData;
    }
    
    //Se obtiene el tipo de contenido para la respuesta HTTP
    private String getContentType(String fileRequested){
        if(fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")){
            return "text/html";
        }else {
            return "application/json";
        }
    }
    
    //Controla la excepcion de archivo no encontrado en caso de que llegara a suceder.
    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException{
        File file = new File(WEB_ROOT,FILE_NOT_FOUND);
        int fileLength = (int)file.length();
        String content = "text/plain";
        byte[] fileData = readFileData(file,fileLength);
                
        out.println("HTTP/1.1 404 File Not Found");
        out.println("Server Http from Ssaurel: 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
        
        if(verbose){
            System.out.println("File " + fileRequested + " Not Found");
        }
    }
    
    public static void main(String[] args){
        try {
            InetAddress addr = InetAddress.getByName("192.168.1.66");
            ServerSocket serverHttp = new ServerSocket(PORT,0,addr);
            System.out.println("Servidor iniciado en el puerto " + PORT + " ...");
            while(true){
                ServidorHttp servidorWeb = new ServidorHttp(serverHttp.accept());
                
                if(verbose){
                    System.out.println("Conexion establecida( " + new Date() + " )");
                }
                servidorWeb.start();
                //Thread thread = new Thread(servidorWeb);
                //thread.start();
            }
        } catch (IOException ex) {
            System.out.println("Error en la conexion con el servidor\n" + ex.getMessage());
        }
        
    }
    
    public void updateJsonData(JSONObject[] arrJson){
        try{
            String verify, putData;
            String data = Helper.JsonArrayToString(arrJson);
            
            File file = new File(WEB_ROOT,JSON_FILE);
            //file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(data);
            bw.flush();
            bw.close();
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
//
//            while( (verify=br.readLine()) != null ){ //***editted
//                       //**deleted**verify = br.readLine();**
//                if(verify != null){ //***edited
//                    putData = verify.replaceAll("here", "there");
//                    bw.write(putData);
//                }
//            }
//            br.close();


        }catch(IOException e){
        e.printStackTrace();
        }
    }
}
