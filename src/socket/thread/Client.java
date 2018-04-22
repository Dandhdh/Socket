package socket.thread;

import java.net.Socket;;

import socket.thread.ListenThread;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//主线程，启动时由用户选择是作为server还是client
public class Client {

    private String host;
    private int port;
    private Socket socket;
    private ServerSocket serverSocket;
    private OutputStream outputStream;

    // 以客户端形式启动，加入会话
    public void runAsClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        System.out.println("[log] successful connected to server " + socket.getInetAddress());
        Thread listenThread = new Thread(new ListenThread(this.socket));
        listenThread.start();
        waitAndSend();
    }

    public void waitAndSend() throws IOException {
        this.outputStream = this.socket.getOutputStream();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            this.sendMessage(sc.nextLine());
        }
    }

    public void sendMessage(String message) throws IOException {
        byte[] msgBytes = message.getBytes("UTF-8");
        int length = msgBytes.length;
        outputStream.write(length>>8);
        outputStream.write(length);
        outputStream.write(msgBytes);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client chatSocket = new Client();
        System.out.print("input server host: ");
        String host = scanner.nextLine();
        System.out.print("input server port: ");
        int port = scanner.nextInt();
        try {
            chatSocket.runAsClient(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
