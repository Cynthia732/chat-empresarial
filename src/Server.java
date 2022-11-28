import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server { //creando la clase del servidor

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) { //Constructor
        this.serverSocket = serverSocket;
    }

    public void startServer () { //este método será el responsable de mantener el servidor funcionando. Es el método principal
        try { //bloque de prueba, manejo de errores de entrada y salida
            while (!serverSocket.isClosed()){  //el while aqui indica que hasta que se conecte un cliente va a permanecer cerrado y cuando se conecte un cliente
                Socket socket = serverSocket.accept(); //cuando un cliente se conecta devuelve el objeto de socket
                System.out.println("Un nuevo usuario se ha conectado");
                ClientHandler clientHandler = new ClientHandler(socket); //crea la clase y objeto para manipular al cliente. El objeto clientHandler sera responsable de comunicarse con un cliente

                Thread thread = new Thread(clientHandler); //creando los hilos
                thread.start(); //metodo de inicio para comenzar con la ejecucion de los hilos
            }
        } catch (IOException e) { //excepciones de entrada y salida
        }
    }

    public void closeServerSocket(){ //este metodo se crea con el fin de que si ocurre un error se va a cerrar el servidor socket
        try{
            if (serverSocket != null) { //el if se ocupa para que si es nulo
                serverSocket.close(); //el serverSocket se cierra
            }
        } catch (IOException e) { //manejo de errores, excepciones de entrada y salida
            e.printStackTrace(); //El método printStackTrace()se usa para imprimir junto con otros detalles como el nombre de la clase y el número de línea donde ocurrió la excepción significa su rastreo inverso. Este método imprime un seguimiento de pila para este objeto Throwable en el flujo de salida de error estándar.
        }
    }

    public static void main(String[] args) throws IOException {  //metodo para ejecutar el programa y se agrega la excepción del metodo en lugar de hacer un catch
        ServerSocket serverSocket = new ServerSocket(1234); //se crea el SerververSocket con un numero de puerto
        Server server = new Server(serverSocket); //
        server.startServer(); //para ejecutar el servidor o mantenerlo en ejecucion lo llamaremos aqui


    }
}