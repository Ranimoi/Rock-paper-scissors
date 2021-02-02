package client;

import java.awt.Color;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
//import java.util.logging.Level;
//import java.util.logging.Logger;

public class client {

    static Socket clientSocket;
    static JComboBox usersComboBox;
    static JTextField textField;
    static JLabel info;
    static JButton play;
    static JButton rock;
    static JButton paper;
    static JButton scissors;
    static String choice;
    static JFrame frame;

    public static void main(String[] args) throws Exception {
        choice = "";
        //Create the GUI frame and components
        frame = new JFrame("RPS Game Client");
        frame.setLayout(null);
        frame.setBounds(100, 100, 600, 550);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel nameLabel = new JLabel("Client Name");
        nameLabel.setBounds(20, 40, 150, 30);
        frame.getContentPane().add(nameLabel);

        textField = new JTextField();
        textField.setBounds(100, 40, 170, 30);
        frame.getContentPane().add(textField);

        JButton connect = new JButton("Connect");
        connect.setBounds(300, 40, 100, 30);
        frame.getContentPane().add(connect);

        JLabel playwith = new JLabel("Play with:");
        playwith.setBounds(20, 100, 150, 30);
        frame.getContentPane().add(playwith);

        usersComboBox = new JComboBox();
        usersComboBox.setBounds(100, 100, 170, 30);
        frame.getContentPane().add(usersComboBox);
        usersComboBox.setEnabled((true));

        play = new JButton("Play");
        play.setBounds(300, 100, 100, 30);
        frame.getContentPane().add(play);

        rock = new JButton("Rock");
        rock.setBounds(200, 150, 100, 100);
        frame.getContentPane().add(rock);
        rock.setVisible(false);

        rock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataOutputStream outToServer = null;
                try {
                    //create an output stream
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());

                    String sendingSentence = "-choice,rock\n";
                    choice = "Rock";
                    outToServer.writeBytes(sendingSentence);
                    info.setText("You chose Rock. Waiting for opponent choice...");

                    scissors.setEnabled(false);
                    rock.setEnabled(false);
                    paper.setEnabled(false);
                } catch (Exception ex) {
                    //Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        paper = new JButton("Paper");
        paper.setBounds(200, 260, 100, 100);
        frame.getContentPane().add(paper);
        paper.setVisible(false);

        paper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataOutputStream outToServer = null;
                try {
                    //create an output stream
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());

                    String sendingSentence = "-choice,paper\n";
                    choice = "Paper";
                    outToServer.writeBytes(sendingSentence);
                    info.setText("You chose Paper. Waiting for opponent choice...");

                    scissors.setEnabled(false);
                    rock.setEnabled(false);
                    paper.setEnabled(false);
                } catch (Exception ex) {
                    //Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        scissors = new JButton("Scissors");
        scissors.setBounds(200, 370, 100, 100);
        frame.getContentPane().add(scissors);
        scissors.setVisible(false);

        scissors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataOutputStream outToServer = null;
                try {
                    //create an output stream
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());

                    String sendingSentence = "-choice,scissors\n";
                    choice = "Scissors";
                    outToServer.writeBytes(sendingSentence);
                    info.setText("You chose Scissors. Waiting for opponent choice...");

                    scissors.setEnabled(false);
                    rock.setEnabled(false);
                    paper.setEnabled(false);
                } catch (Exception ex) {
                    //Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        info = new JLabel("Connect to start playing");
        info.setBounds(10, 480, 500, 30);
        frame.getContentPane().add(info);
        info.setForeground(Color.BLUE);

        /*JScrollPane receivedTextAreaScroll = new JScrollPane(receivedTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        receivedTextAreaScroll.setBounds(20, 100, 460, 300);
        frame.getContentPane().add(receivedTextAreaScroll);*/
        //Action listener when connect button is pressed
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    try {
                        if (connect.getText().equals("Connect")) { //if pressed to connect

                            //create a new socket to connect with the server application
                            clientSocket = new Socket("localhost", 6789);

                            //call function StartThread
                            StartThread();

                            usersComboBox.setVisible(true);
                            textField.setEnabled(false);

                            //change the Connect button text to disconnect
                            connect.setText("Disconnect");
                            usersComboBox.setEnabled(true); 
                        } else { //if pressed to disconnect

                            if (play.getText().equals("Play")) {

                            } else {
                                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                                Object selectedItem = usersComboBox.getSelectedItem(); //selected item is the one who is selected to play

                                String sendingSentence = "stopgame," + textField.getText(); //first non available player that wants to play

                                if (selectedItem != null) {
                                    sendingSentence += "," + selectedItem;//Second reavailable player that will play
                                }

                                sendingSentence += "\n";

                                outToServer.writeBytes(sendingSentence);
                                outToServer.flush();
                                int i = 0; 
                                while(i < 1000000)i++;
                                rock.setVisible(false);
                                rock.setEnabled(true);
                                paper.setVisible(false);
                                paper.setEnabled(true);
                                scissors.setVisible(false);
                                scissors.setEnabled(true);
                                usersComboBox.setEnabled(true);
                                play.setText("Play");

                                info.setText("");
                            }

                            //create an output stream and send a Remove message to disconnect from the server
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes("-Remove\n");
                            outToServer.flush();
                            int i = 0; 
                            while(i < 1000000)i++;
                            //close the client's socket
                            clientSocket.close();

                            //make the GUI components invisible
                            usersComboBox.setEnabled(false);
                            textField.setEnabled(true);

                            //change the Connect button text to connect
                            connect.setText("Connect");
                        }

                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        });

        //Action listener when PLAY button is pressed
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (play.getText().equals("Play")) {

                        //create an output stream
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                        Object selectedItem = usersComboBox.getSelectedItem(); //selected item is the one who is selected to play

                        String sendingSentence = "startgame," + textField.getText(); //first non available player that wants to play

                        if (selectedItem != null) {
                            sendingSentence += "," + selectedItem;//Second non available player that will play
                        }

                        sendingSentence += "\n";

                        outToServer.writeBytes(sendingSentence);

                        info.setText("Play request has been sent");
                        play.setText("Stop");
                    } else {
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                        Object selectedItem = usersComboBox.getSelectedItem(); //selected item is the one who is selected to play

                        String sendingSentence = "stopgame," + textField.getText(); //first non available player that wants to play

                        if (selectedItem != null) {
                            sendingSentence += "," + selectedItem;//Second reavailable player that will play
                        }

                        sendingSentence += "\n";

                        outToServer.writeBytes(sendingSentence);

                        rock.setVisible(false);
                        rock.setEnabled(true);
                        paper.setVisible(false);
                        paper.setEnabled(true);
                        scissors.setVisible(false);
                        scissors.setEnabled(true);
                        usersComboBox.setEnabled(true);
                        play.setText("Play");

                        info.setText("");
                    }
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }

        });

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {                 
                try {
                    if (play.getText().equals("Stop")) {
                        System.out.println("Here stopping");
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                        Object selectedItem = usersComboBox.getSelectedItem(); //selected item is the one who is selected to play

                        String sendingSentence = "stopgame," + textField.getText(); //first non available player that wants to play

                        if (selectedItem != null) {
                            sendingSentence += "," + selectedItem;//Second reavailable player that will play
                        }

                        sendingSentence += "\n";

                        outToServer.writeBytes(sendingSentence);
                        outToServer.flush();
                        int i = 0; 
                        while(i < 1000000)i++;
                        rock.setVisible(false);
                        rock.setEnabled(true);
                        paper.setVisible(false);
                        paper.setEnabled(true);
                        scissors.setVisible(false);
                        scissors.setEnabled(true);
                        usersComboBox.setEnabled(true);
                        play.setText("Play");
                    }
                    
                     //create an output stream and send a Remove message to disconnect from the server
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Remove\n");
                    outToServer.flush();
                    int i = 0; 
                    while(i < 1000000)i++;
                    //close the client's socket
                    clientSocket.close();

                    //make the GUI components invisible
                    usersComboBox.setEnabled(false);
                    textField.setEnabled(true);

                    //change the Connect button text to connect
                    connect.setText("Connect");
                    System.out.println("here");
                    frame.dispose();
                    System.exit(0);
                } catch (Exception ex) {
                }

            }
        });
        //Disconnect on close
//        frame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent we) {
//
//                try {
//
//                    //create an output stream and send a Remove message to disconnect from the server
//                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//                    outToServer.writeBytes("-Remove\n");
//
//                    //close the client's socket
//                    clientSocket.close();
//
//                    //change the Connect button text to connect
//                    connect.setText("Connect");
//
//                    System.exit(0);
//
//                } catch (Exception ex) {
//                    System.out.println(ex.toString());
//                }
//
//            }
//        });

        frame.setVisible(true);

    }

    //Thread to always read messages from the server and print them in the textArea
    private static void StartThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //create a buffer reader and connect it to the socket's input stream
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String receivedSentence;

                    //always read received messages and append them to the textArea
                    while (true) {

                        receivedSentence = inFromServer.readLine();
                        //System.out.println(receivedSentence);

                        if (receivedSentence.startsWith("-Connected")) {
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            info.setText("Connected Successfully");
                            String sendingSentence = "-Name," + textField.getText() + "\n";
                            outToServer.writeBytes(sendingSentence);
                        } else if (receivedSentence.startsWith("-CurrentUsers")) {

                            usersComboBox.removeAllItems();

                            String[] strings = receivedSentence.split(",");

                            for (int i = 1; i < strings.length; i++) {
                                if (!strings[i].equals(textField.getText())) {
                                    usersComboBox.addItem(strings[i]);
                                }
                            }
                        } else if (receivedSentence.startsWith("-Availableusers")) {
                            usersComboBox.removeAllItems();

                            String[] strings = receivedSentence.split(",");

                            for (int i = 1; i < strings.length; i++) {
                                if (!strings[i].equals(textField.getText())) {
                                    usersComboBox.addItem(strings[i]);
                                }
                            }
                        } else if (receivedSentence.startsWith("-result")) {
                            System.out.println("Received result here as. " + receivedSentence);
                            rock.setEnabled(true);
                            paper.setEnabled(true);
                            scissors.setEnabled(true);

                            String[] strings = receivedSentence.split(",");
                            info.setText(strings[1]);
                            rock.setEnabled(true);
                            paper.setEnabled(true);
                            scissors.setEnabled(true);
                            frame.repaint();
                        } else if (receivedSentence.startsWith("-challenged") || receivedSentence.startsWith("-accepted")) {
                            String[] strings = receivedSentence.split(",");
                            play.setText("Stop");
                            usersComboBox.setSelectedItem(strings[1]);
                            usersComboBox.setEnabled(false);
                            rock.setVisible(true);
                            paper.setVisible(true);
                            scissors.setVisible(true);

                            info.setText("Playing against " + strings[1]);
                        } else if (receivedSentence.startsWith("-rejected")) {

                            rock.setVisible(false);
                            paper.setVisible(false);
                            scissors.setVisible(false);
                            usersComboBox.setEnabled(true);
                            play.setText("Play");

                            info.setText("Selected player is not available");
                        } else if (receivedSentence.startsWith("-stopped")) {

                            rock.setVisible(false);
                            rock.setEnabled(true);
                            paper.setVisible(false);
                            paper.setEnabled(true);
                            scissors.setVisible(false);
                            scissors.setEnabled(true);
                            usersComboBox.setEnabled(true);
                            play.setText("Play");

                            info.setText("You can not play with this player, Please choose another player");
                        } else if (receivedSentence.startsWith("-waiting")) {
                            String[] strings = receivedSentence.split(",");

                            info.setText("You chose " + choice + ". Waiting for " + strings[1] + " to play...");
                        }
                    }

                } catch (Exception ex) {

                }

            }
        }).start();

    }

}

