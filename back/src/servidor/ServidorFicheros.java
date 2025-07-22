package servidor;

import java.io.*;
import java.net.*;

import hilo.Hilo;

public class ServidorFicheros {
    
	//Variables
	private static final int puerto = 6000;
    public static final String servidor_ficheros = "C:\\Users\\marla\\OneDrive\\Desktop\\DAM\\PROGRAMACIÓN DE SERVICIOS Y PROCESOS\\Servidor\\servidor_ficheros";

    public static void main(String[] args) {
        
    	// Crear el directorio si no existe
        File directorio = new File(servidor_ficheros);
        
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            
        	System.out.println("Servidor escuchando. Esperando conexiones...");

            while (true) {
                
            	Socket cliente = servidor.accept();
                System.out.println("Hilo conectado desde: " + cliente.getInetAddress());

                // Iniciar el hilo 
                new Hilo(cliente).start();;
         
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
