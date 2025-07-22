package hilo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import servidor.ServidorFicheros;

public class Hilo extends Thread {
    
	//Variables
	private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    
    //Constructor
    public Hilo(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        
    	try {
            
    		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		salida = new PrintWriter(socket.getOutputStream(), true);
    		salida.println("Bienvenid@ al servidor de ficheros!");
    		

            // Leer la opción seleccionada 
            System.out.println("Esperando opción del cliente...");
            String opcionCliente = entrada.readLine();

            if (opcionCliente == null) {
                System.out.println("Error: No se recibió opción del cliente.");
                return;
            }

            int opcion = Integer.parseInt(opcionCliente);
            System.out.println("Opción recibida del cliente: " + opcion);

            switch (opcion) {
                case 1:
                    listarFicheros();
                    break;
                case 2:
                    System.out.println("El cliente quiere descargar un fichero.");
                    bajarFichero();
                    break;
                case 3:
                    System.out.println("El cliente quiere subir un fichero.");
                    subirFichero();
                    break;
                default:
                	salida.println("Opción no válida.");
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        
        } finally {
            
        	try {
                if (entrada != null) entrada.close();
                if (salida != null) salida.close();
                socket.close();
            
        	} catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Listar ficheros en el servidor
    private void listarFicheros() throws IOException {
        
    	File archivo = new File(ServidorFicheros.servidor_ficheros);
        String[] listaFicheros = archivo.list();

        if (listaFicheros != null && listaFicheros.length > 0) {
        	salida.println("Ficheros disponibles:");
            
            for (String fichero : listaFicheros) {
            	salida.println(fichero);
            }
        } else {
        	salida.println("No hay ficheros disponibles.");
        }
    }

    // Descargar un fichero del servidor
    private void bajarFichero() throws IOException {
        
    	System.out.println("Esperando nombre del fichero...");
        String nombreFichero = entrada.readLine();

        if (nombreFichero == null) {
            
        	System.out.println("Error: No se recibió el nombre del fichero.");
            salida.println("ERROR: No se recibió el nombre del fichero.");
            salida.flush();
            return;
        }

        System.out.println("El cliente ha solicitado el archivo: " + nombreFichero);

        File fichero = new File(ServidorFicheros.servidor_ficheros + "/" + nombreFichero);

        if (fichero.exists() && fichero.isFile()) {
            
        	System.out.println("Archivo encontrado. Tamaño: " + fichero.length() + " bytes.");
            salida.println("Iniciando descarga...");
            salida.flush();
            salida.println(fichero.length());
            salida.flush();

            System.out.println("Preparando archivo para enviar...");
            
            try (FileInputStream entrada = new FileInputStream(fichero);
                 BufferedInputStream bufferEntrada = new BufferedInputStream(entrada);
                 OutputStream salida = socket.getOutputStream()) {

                byte[] bufferDatos = new byte[1024];
                int bytesLeidos;
                int totalBytesEnviados = 0;

                while ((bytesLeidos = bufferEntrada.read(bufferDatos)) != -1) {
                	    
                	    salida.write(bufferDatos, 0, bytesLeidos);
                	    salida.flush(); 
                        totalBytesEnviados += bytesLeidos;
                        System.out.println("Enviando " + bytesLeidos + " bytes. Total enviados: " + totalBytesEnviados);
                }

                salida.flush();
                socket.shutdownOutput(); 
                System.out.println("Archivo enviado correctamente. Total bytes enviados: " + totalBytesEnviados);
            
            } catch (IOException e) {
                System.out.println("Error al enviar el archivo: " + e.getMessage());
            }

        } else {
        	
            System.out.println("El archivo no existe en el servidor.");
            salida.println("ERROR: El fichero no existe en el servidor.");
            salida.flush();
        }
    }


    // Subir un fichero al servidor
    private void subirFichero() throws IOException {
        
    	System.out.println("Esperando nombre del fichero a recibir...");
        String archivo = entrada.readLine();

        if (archivo == null || archivo.trim().isEmpty()) {
            
        	System.out.println("Error: No se recibió el nombre del fichero.");
        	salida.println("ERROR: No se recibió el nombre del fichero.");
        	salida.flush();
            return;
        }

        File archivoServidor = new File(ServidorFicheros.servidor_ficheros + File.separator + archivo);
        salida.println("Iniciando subida...");
        salida.flush();

        try (FileOutputStream archivoSalida = new FileOutputStream(archivoServidor);
             BufferedOutputStream bufferSalida = new BufferedOutputStream(archivoSalida);
             InputStream entrada = socket.getInputStream()) {

            byte[] bufferDatos = new byte[4096]; 
            int bytesLeidos;
            int totalBytesRecibidos = 0;

            System.out.println("Recibiendo el archivo desde el cliente...");

            while ((bytesLeidos = entrada.read(bufferDatos)) != -1) {
                	
            		bufferSalida.write(bufferDatos, 0, bytesLeidos);
                	totalBytesRecibidos += bytesLeidos;
            }

            bufferSalida.flush();

            System.out.println("Archivo recibido exitosamente: " + archivo + " (" + totalBytesRecibidos + " bytes)");
            salida.println("Fichero subido correctamente.");
            salida.flush();

        } catch (IOException e) {
            
        	System.out.println("Error al recibir el archivo.");
        	salida.println("ERROR: No se pudo recibir el fichero.");
        }
    }



}

