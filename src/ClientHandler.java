import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//Esta clase implementara la interfaz ejecutable es por eso que se implementa Runnable, esto hará que las instancias sean ejecutadas por un hilo separado
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //Lista de matrices estatica de cada objeto de ClientHandler que se ha instanciado
    //El objetivo de esta matriz es realizar un seguimiento de todos los clientes para que cada vez que un cliente envie un mensaje podamos recorrer la matriz de clientes y enviar el mensaje a cada cliente.
    //Esta matriz hace posible la comunicacion (enviar mensajes) entre varios clientes en lugar de solo uno.

    private Socket socket; // Propiedad, este socket pasa desde la clase Servidor aquí se usa para establecer una conexión entre el cliente y el servidor
    private BufferedReader bufferedReader; //lector almacenado, se usa para leer datos, especificamente mensajes que se han enviado desde el cliente
    private BufferedWriter bufferedWriter; //Escritor almacenado, se usa para enviar datos, mensajes al cliente que han sido enviados desde otros clientes, que luego se transmitiran usando la matriz
    private String clientUsername; //Nombre de usuario, se usa para representar a cada cliente

    public ClientHandler (Socket socket) { //Constructor. El objeto ClientHandler se esta creando a partir de la clase Servidor
        try {   //Se configuran las propiedades de la clase en un bloque de prueba
            this.socket = socket;
            //Ahora se configura el lector y escritor almacenado en buffer desde el Socket
            //Como anotación, un socket es la conexión emtre el servidor, el controlador o el cliente y cada conexión del socket tiene un flujo de salida que se puede usar para enviar datos a quien este conectado y el socket también tiene un flujo de entrada que se usa para leer los datos a quien este conectado
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //Configurando el flujo de salida del socket. //Este es un flujo de caracteres porque queremos enviar caracteres  //como anotación, en Java hay 2 tipos de flujos: Flujos de bytes y flujos de strings. En el flujo de salida del Socket necesitamos un flujo de strings (caracteres) porque estamos enviando mensajes y en Java los flujos de caracteres terminan con bufferedWriter (escritor) mientras que los flujos de bytes terminan con el flujo de palabras (string)
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //En este buffer hará que la comunicación sea mas eficiente --> this.bufferedWriter = new BufferedWriter
            this.clientUsername = bufferedReader.readLine(); //Aquí el cliente va a enviar su nombre de usuario
            clientHandlers.add(this); // Se agrega al cliente a la lista de arreglos para que pueda ser parte del chat y pueda recibir mensajes de otros usuarios. This representa un objeto de ClientHandler
            broadcastMessage ("SERVER: " + clientUsername + "has entered the chat"); //Este es un metodo de mensaje de difusión
        } catch (IOException e){ //Excepción de entrada y salida
            closeEverything(socket, bufferedReader, bufferedWriter); //Este es un metodo para cerrar ttodo, cierra el socket y las transmisiones
        }

    }

    @Override
    public void run() { //metodo de ejecución. Ejecuta un subproceso separado y lo que se quiere hacer en el subproceso separado es escuchar mensajes
        //Escuchar los mensajes es una operación de bloqueo y una operación de bloqueo significa que el programa se bloqueara hasta que se complete la operación // Como nota: Si no estuvieramos usando varios subprocesos el programa estaria atascado esperando un mensaje del cliente por lo que tenemos un subproceso esperando mensajes y otro subproceso trabajando con el resto de la aplicación.
        String messageFromClient; //Variable de cadena se usa para contener el mensaje recibido de un cliente

        while (socket.isConnected()) {// Ahora escuchemos los mensajes del cliente mientras hay conexión con el cliente, para esto ocupamos un ciclo infinito en terminos del tiempo que se este conectado el cliente al socket --> Mientras el socket esta conectado
            try {
                messageFromClient = bufferedReader.readLine(); //Debido a que estamos escuchando mensajes del cliente lo que se va a hacer es leer del bufferedReader para poder establecer el mensaje del cliente. El programa se mantendra aqui hasta que se reciba un mensaje del cliente y es por eso que se debe ejecutar esto en un hilo separado para que el resto de la aplicación no se detenga.
                broadcastMessage(messageFromClient); //Se transmite el mensaje con la función metodo broadcastMessage
            } catch (IOException e) { //Excepciones de entrada y salida
                closeEverything(socket, bufferedReader, bufferedWriter); //Metodo closeEverything  para cerrar ttodo, cierra el socket y las transmisiones
                break; //Cuando el cliente se desconecta el break nos saca del ciclo while


            }
        }
    }

    public void broadcastMessage(String messageToSend) { //Método de transmisión que sirve para enviar el mensaje que un cliente escribio a todos en el chat grupal
        for (ClientHandler clientHandler : clientHandlers) { //La lista de matrices del controlador de clientes se va a recorrer aquí y enviar un mensaje a cada cliente conectado
            try { //bloque de prueba
                if (!clientHandler.clientUsername.equals(clientUsername)) { //Con este if se transmite el mensaje del cliente excepto al mismo cliente que envio ese mensaje
                    clientHandler.bufferedWriter.write(messageToSend); //si no es igual se transmite el mensaje al cliente
                    clientHandler.bufferedWriter.newLine(); //El cliente esta aquí a la espera de una nueva linea aquí se envia un nuevo caracter de linea.
                    clientHandler.bufferedWriter.flush(); // Con flush lo que hacemos es vaciar el bufferedWriter, se tiene que vaciar manualmente el bufer.
                }

            } catch (IOException e) { //Excepción de entrada y salida
                closeEverything(socket, bufferedReader, bufferedWriter); //Se cierra ttodo
            }
        }
    }
    public void removeClientHandler(){ //Metodo para señalar que un usuario ha abandonado el chat, aquí se elimina un controlador del cliente
        clientHandlers.remove(this); //Se elimina el controlador de clientes de la lista de arreglos
        broadcastMessage("Server: " + clientUsername + "has left the chat");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) { //Este método se usa para cerrar la conexión y transmisiones
        removeClientHandler();
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
}

