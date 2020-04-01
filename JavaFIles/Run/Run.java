
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

public class Run {
    public static void main(String[] args) {
        Thread client = new Cliente("Cliente");
        client.start();
    }
}