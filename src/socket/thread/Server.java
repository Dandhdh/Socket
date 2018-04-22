package socket.thread;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//主线程，启动时由用户选择是作为server还是client
public class Server {

    private String host;
    private int port;
    private Socket socket;
    private ServerSocket serverSocket;
    private OutputStream outputStream;

    //以服务端形式，创建会话
    public void runAsServer(int post) throws IOException{
        this.serverSocket = new ServerSocket(post);
        System.out.println("[log] server started at port " + port);

        //等待客户端的加入
        this.socket = serverSocket.accept();
        System.out.println("[log] successful connected with " + socket.getInetAddress());

        //启动监听线程
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
        Server chatSocket = new Server();
        System.out.print("input server port: ");
        int port = scanner.nextInt();
        try {
            chatSocket.runAsServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
