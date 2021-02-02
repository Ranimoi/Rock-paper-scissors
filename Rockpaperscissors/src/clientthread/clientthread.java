package clientthread;

import client.client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;

public class clientthread extends Thread {

    //the ClientServiceThread class extends the Thread class and has the following parameters
    public int id;
    public String name; //client name
    public Socket connectionSocket; //client connection socket
    ArrayList<clientthread> Clients; //list of all clients connected to the server
    public boolean available;
    public clientthread opponent;
    public String choice;

    //constructor function
    public clientthread(int id, String name, Socket connectionSocket, ArrayList<clientthread> Clients) {

        this.id = id;
        this.name = name;
        this.connectionSocket = connectionSocket;
        this.Clients = Clients;
        this.available = true;
        this.choice = null;
    }

    public String getWinner() {
        
        String myChoice = choice;
        String OpChoice = opponent.choice;
        
        String result = "";
        
        if(myChoice.equalsIgnoreCase(OpChoice)){
            result = "It was a draw";
        }else if(myChoice.equalsIgnoreCase("Scissors") && OpChoice.equalsIgnoreCase("Rock")){
            result = myChoice +" X "+OpChoice+"..."+opponent.name+" wins";
        }else if(myChoice.equalsIgnoreCase("Scissors") && OpChoice.equalsIgnoreCase("Paper")){
            result = myChoice +" X "+OpChoice+"...You win";
        }else if(myChoice.equalsIgnoreCase("Rock") && OpChoice.equalsIgnoreCase("Paper")){
            result = myChoice +" X "+OpChoice+"..."+opponent.name+" wins";
        }else if(myChoice.equalsIgnoreCase("Rock") && OpChoice.equalsIgnoreCase("Scissors")){
            result = myChoice +" X "+OpChoice+"...You win";
        }else if(myChoice.equalsIgnoreCase("Paper") && OpChoice.equalsIgnoreCase("Scissors")){
            result = myChoice +" X "+OpChoice+"..."+opponent.name+" wins";
        }else if(myChoice.equalsIgnoreCase("Paper") && OpChoice.equalsIgnoreCase("Rock")){
            result = myChoice +" X "+OpChoice+"...You win";
        }
        
        return result;
    }
    
    //thread's run function
    public void run() {

        try {

            //create a buffer reader and connect it to the client's connection socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));;
            String clientSentence;
            DataOutputStream outToClient;

            //always read messages from client
            while (true) {

                clientSentence = inFromClient.readLine();
                if(clientSentence == null) continue;
                //ckeck the start of the message
                if (clientSentence.startsWith("-choice")) {

                    String[] strings = clientSentence.split(",");
                    choice = strings[1];
                    System.out.println("Got choice for " + name + " as " + choice);
                    if (opponent.choice != null) {

                        String result1 = getWinner();
                        String result2 = opponent.getWinner();
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        outToClient.writeBytes("-result," + result1 + "\n");
                        System.out.println("Here 2 "+opponent.name);
                        outToClient.flush();

                        outToClient = new DataOutputStream(opponent.connectionSocket.getOutputStream());
                        outToClient.writeBytes("-result," + result2 + "\n");
                        System.out.println("Written to oppoenent");
                        outToClient.flush();
                        
                        choice = null;
                        opponent.choice = null;
                    } else {
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        outToClient.writeBytes("-waiting," + opponent.name + "\n");
                    }
                }

                if (clientSentence.startsWith("-Remove")) { //Remove Client

                    for (int i = 0; i < Clients.size(); i++) {

                        if (Clients.get(i).id == id) {
                            Clients.remove(i);
                            break;
                        }
                    }

                }

                if (clientSentence.startsWith("startgame")) {
                    choice = null;
                    opponent = null;
                    available = false;
                    String[] strings = clientSentence.split(",");
                    String firstplayer = strings[1];//everything disable for him+ new display
                    String secondplayer = strings[2];

                    if (Clients.size() > 0) {

                        boolean isAvailable = true;
                        for (int i = 0; i < Clients.size(); i++)//UPDATE THE CLIENTSLIST
                        {
                            if ((Clients.get(i).name).equals(secondplayer)) {
                                if (Clients.get(i).available) {
                                    Clients.get(i).available = false;
                                    opponent = Clients.get(i);
                                    opponent.opponent = this;
                                } else {
                                    isAvailable = false;
                                }
                            }
                        }

                        if (isAvailable) {
                            available = false;

                            String clientsList = "-Availableusers";

                            for (int i = 0; i < Clients.size(); i++) {
                                if (!Clients.get(i).name.equals(name) && !Clients.get(i).name.equals(secondplayer));
                                {
                                    if (Clients.get(i).available) {
                                        clientsList += "," + Clients.get(i).name;
                                    }
                                }
                            }
                            for (int i = 0; i < Clients.size(); i++) {
                                if (Clients.get(i).available) {
                                    outToClient = new DataOutputStream(Clients.get(i).connectionSocket.getOutputStream());
                                    outToClient.writeBytes(clientsList + "\n");
                                }
                            }

                            outToClient = new DataOutputStream(opponent.connectionSocket.getOutputStream());
                            outToClient.writeBytes("-challenged," + name + "\n");

                            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                            outToClient.writeBytes("-accepted," + opponent.name + "\n");
                        } else {
                            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                            outToClient.writeBytes("-rejected," + "\n");
                        }

                    }
                }
                if (clientSentence.startsWith("stopgame")) {
                    String[] string = clientSentence.split(",");

                    available = true;
                    choice = null;
                    if (opponent != null) {
                        opponent.choice = null;
                        opponent.available = true;
                    }

                    String clientsList = "-Availableusers";

                    for (int i = 0; i < Clients.size(); i++) {

                        if (Clients.get(i).available) {
                            clientsList += "," + Clients.get(i).name;
                        }

                    }
                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).available) {
                            outToClient = new DataOutputStream(Clients.get(i).connectionSocket.getOutputStream());
                            outToClient.writeBytes(clientsList + "\n");
                        }
                    }

                    outToClient = new DataOutputStream(opponent.connectionSocket.getOutputStream());
                    outToClient.writeBytes("-stopped," + name + "\n");

//                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//                    outToClient.writeBytes("-stopped," + opponent.name + "\n");

                    opponent = null;
                }

            }

        } catch (Exception ex) {
            //Logger.getLogger(clientthread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
