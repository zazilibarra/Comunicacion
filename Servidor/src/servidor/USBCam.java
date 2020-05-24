package Servidor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import java.io.FileWriter;

public class USBCam extends Thread{

    static final File WEB_ROOT = new File(".");
    static double framerate = 10.0;
    static MJPEGGenerator m;
    static final String JPG_FILE = "test.jpg";
    private Socket connect;
    static USBCam servidorWeb;
    
    public USBCam(Socket c) {
        connect = c;
    }
    
    @Override
    public void run() {
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
        String fileRequested = null;
        
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            
            String input = in.readLine();
            
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken().toLowerCase();
            boolean isFile = true;
            
            if(isFile){
                    File file = new File(WEB_ROOT,"camera/test.jpg");
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
                
                    if(true){
                        System.out.println("File " + fileRequested + " of type " + content + " returned");
                    }
                }
            
        } catch(Exception e) {
            System.err.println("Server Error "+ e.getMessage() );
        }
        finally{
            try {
                in.close();
                dataOut.close();
                connect.close();
            } catch (IOException ex) {
                System.err.println("Error closing the stream " + ex.getMessage());
            }
            
            if(true){
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

    public static BufferedImage mat2BI(Mat matrix) {
        final int cols = matrix.cols();
        final int rows = matrix.rows();
        final int elemSize = (int) matrix.elemSize();
        final byte[] data = new byte[cols * rows * elemSize];
        int type;
        matrix.get(0, 0, data);
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }
        final BufferedImage image2 = new BufferedImage(cols, rows, type);
        image2.getRaster().setDataElements(0, 0, cols, rows, data);
        try {
            Calendar c1 = Calendar.getInstance();
            Date dateOne = c1.getTime();
            File outputfile = new File("camera/" + "test.jpg");//dateOne.getTime() + "image.jpg");
            ImageIO.write(image2, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image2;
    }

    public static void GenerarVideo() throws Exception{
        Calendar c1 = Calendar.getInstance();
        Date dateOne = c1.getTime();
        m = new MJPEGGenerator(new File(dateOne.getTime() + "Ex02FF.avi"), 640, 480, framerate, 50);
        File photoDir = new File("camera/");
        File[] files = photoDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().endsWith("jpg")) {
                    return true;
                }
                return false;
            }
        });
        for (int i = 0; i < 50; i++) {
            System.out.println("processing file " + i);
            ImageIcon ii = new ImageIcon(files[i].getCanonicalPath());
            m.addImage(ii.getImage());
        }
        photoDir.delete();
        m.finishAVI();
    }
    
    public static void SendFrame()
    {
        int PORT = 8080;
        try {
            InetAddress addr = InetAddress.getByName("192.168.1.68");
            ServerSocket serverHttp = new ServerSocket(PORT,0,addr);
            System.out.println("Servidor iniciado en el puerto " + PORT + " ...");
            while(true){
                servidorWeb = new USBCam(serverHttp.accept());
                
                if(true){
                    System.out.println("Conexion establecida( " + new Date() + " )");
                }
                
                servidorWeb.start();
            }
        } catch (IOException ex) {
            System.out.println("Error en la conexion con el servidor\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat frame = new Mat();
        VideoCapture capture = new VideoCapture(0);
        JFrame jframe = new JFrame("Title");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);
        //int counter = 0;
        
        new Thread(() -> {
        SendFrame();
        }).start();

        while (true) {
            if (capture.read(frame)) {

                ImageIcon image = new ImageIcon(mat2BI(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();
                /*counter++;
                if(counter == 50){
                    counter = 0;
                    GenerarVideo();
                }*/
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
