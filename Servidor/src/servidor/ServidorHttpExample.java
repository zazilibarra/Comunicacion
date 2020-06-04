/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

/**
 *
 * @author USUARIO DELL
 */
public class ServidorHttpExample implements Runnable {
    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    
    static final int PORT = 8080;
    
    static final boolean verbose = true;
    
    private Socket connect;
    
    public ServidorHttpExample(Socket c){
        connect = c;
    }

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
                }
                
                File file = new File(WEB_ROOT,fileRequested);
                int fileLength = (int)file.length();
                String content = getContentType(fileRequested);
                
                if(method.equals("GET")){
                    byte[] fileData = readFileData(file,fileLength);
                    
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server Http from Ssaurel: 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println();
                    out.flush();
                    
                    dataOut.write(fileData,0,fileLength);
                    dataOut.flush();
                }
                
                if(verbose){
                    System.out.println("File " + fileRequested + " of type " + content + " returned");
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
    
    private String getContentType(String fileRequested){
        if(fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")){
            return "text/html";
        }else {
            return "text/plain";
        }
    }
    
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
                ServidorHttpExample servidorWeb = new ServidorHttpExample(serverHttp.accept());
                
                if(verbose){
                    System.out.println("Conexion establecida( " + new Date() + " )");
                }
                
                Thread thread = new Thread(servidorWeb);
                thread.start();
            }
        } catch (IOException ex) {
            System.out.println("Error en la conexion con el servidor\n" + ex.getMessage());
        }
        
    }
    
}
