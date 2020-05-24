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
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class USBCam {

    static double framerate = 10.0;
    static MJPEGGenerator m;

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
            File outputfile = new File("camera/" + dateOne.getTime() + "image.jpg");
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

    public static void main(String[] args) throws Exception{
        //USBCam Camara = new USBCam();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat frame = new Mat();
        VideoCapture capture = new VideoCapture(0);
        //capture.set(Videoio.CAP_PROP_POS_FRAMES, 10);
        JFrame jframe = new JFrame("Title");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);
        int counter = 0;

        while (true) {
            if (capture.read(frame)) {

                ImageIcon image = new ImageIcon(mat2BI(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();
                counter++;
                if(counter == 50){
                    counter = 0;
                    GenerarVideo();
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
