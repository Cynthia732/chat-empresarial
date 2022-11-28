import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) { //Constructor para instanciar cada propiedad
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter); //metodo para cerrar ttodo
        }

    }

    public static void main(String[] args) throws IOException { //se crea el método principal
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234); //se crea el objeto del socket que se parará al cliente para hacer una conexión con el puerto en el que el servidor está escuchando //local host porque es en nuestra propia computadora
        Client client = new Client(socket, username); //objecto cliente
        client.listenForMessage(); //se ejecutan los 2 metodos para escuchar y
        client.sendMessage(); //enviar mensajes

    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try { //bloque de prueba para ver si realmente el cliente salio o ocurrio un error
            if (bufferedReader != null) { //Se verifica mediante un if. Asegurandonos de que no ttodo sea igual a nulo para que cuando se llame al metodo de cierre no obtebgamos una excepción
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) { //capturas de entrada y salida
            e.printStackTrace(); //se imprime el seguimiento de pila
        }
    }

    public void sendMessage() { //Método para enviar mensajes al controlador de clientes
        try { //bloque de prueba
            bufferedWriter.write(username); //Ingresando el nombre del usuario
            bufferedWriter.newLine();//Nueva linea
            bufferedWriter.flush(); //vaciando el bufer
            //Este bloque es lo que la clase ClientHandler esta esperando--> this.clientUsername = bufferedReader.readLine();

            Scanner scanner = new Scanner(System.in); //Se crea el objeto escaner para escanear --> obtener información de la consola
            while (socket.isConnected()) { //Con este while continuaremos enviando mensajes mientras todavia haya una conexión con el servidor
                String messageToSend = scanner.nextLine(); //Aquí se obtiene lo que el usuario escribio en la consola con el escaner y scanner.nextLine hace que cuando el usuario presiona enter en la terminal lo que escriba se capturara en la variable
                bufferedWriter.write(username + ": " + messageToSend);//y luego usaremos el bufferedWriter para escribir el mensaje que esta enviando el usuario
                bufferedWriter.newLine(); //nueva linea
                bufferedWriter.flush(); //vaciamos
            }

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);  //se cierra ttodo
        }
    }

    public void listenForMessage() throws IOException { //se va a crear un metodo para escuchar los mensajes del servidor y serán los mensajes transmitidos de otros usuarios
        new Thread(new Runnable() { //aquí se va a usar un nuevo hilo --> será una operación de bloqueo.Se crea un nuevo subproceso al que le pasamos un objeto ejecutable
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) { //se va a hacer mientras estemos conectados al servidor
                    try {
                        msgFromGroupChat = bufferedReader.readLine(); //va a leer el mensaje
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);  //se cierra ttodo
                    }
                }

            }
        }).start(); //se llama al método de inicio para comenzar el hilo --> para que cuando un cliente envie algo tendrá un hilo separado

    }
}

