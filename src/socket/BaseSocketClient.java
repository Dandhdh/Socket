package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

/**
 * The very basic socket client that only send one single message.
 */

public class BaseSocketClient {
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public BaseSocketClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public void connetServer() throws IOException {
        this.socket = new Socket(this.serverHost, this.serverPort);
        this.outputStream = socket.getOutputStream();
        // why the output stream?
    }

    //ʵ�ֵ���ͨ��
    public void sendSingle(String message) throws IOException {
        try {
            this.outputStream.write(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        this.outputStream.close();
        this.socket.close();
    }

    //˫��ͨ��
    public void sendMessage(String message) throws IOException {
        //this.socket = new Socket(this.serverHost,this.serverPort);
        this.outputStream = socket.getOutputStream();
        this.outputStream.write(message.getBytes("UTF-8"));
        this.socket.shutdownOutput(); // ���߷����������еķ��Ͷ����Ѿ�������֮��ֻ�ܽ���
        this.inputStream = socket.getInputStream();
        String receipt = StreamToString(inputStream);
        System.out.println("got receipt: " + receipt);
        this.inputStream.close();
        this.socket.close();
    }

    /**
     * ��η�����Ϣ
     */
    public void send(String message) throws IOException {
        String sendMsg = message + "\n"; // we mark \n as a end of line.
        try {
            this.outputStream.write(sendMsg.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
//        this.outputStream.close();
//        this.socket.shutdownOutput();
    }

    public String StreamToString(InputStream inputStream) throws IOException{
        byte[] readBuffer = new byte[1024];
        StringBuffer stringBuffer = new StringBuffer();

        int msgLen=0;
        while ( (msgLen = inputStream.read(readBuffer)) != -1){
            stringBuffer.append(new String(readBuffer,0,msgLen,"UTF-8"));
        }
        return stringBuffer.toString();
    }

    public void sendMessageByLen(String message) throws IOException {
        // ����Ҫ��messageת����bytes�Ա㴦��
        byte[] bytes = message.getBytes("UTF-8");
        // ���������������ֽڵĳ��ȣ���Ȼʹ����λʵ��
        int length = bytes.length;
        this.outputStream.write(length >> 8); // writeĬ��һ��ֻ����һ���ֽ�
        this.outputStream.write(length);
        // �����곤�Ⱥ�����ʽ������Ϣ
        this.outputStream.write(bytes);
    }



//    public static void main(String[] args) {
//        BaseSocketClient bc = new BaseSocketClient("127.0.0.1",9799);
//        try {
//            bc.connetServer();
//            bc.sendMessage("Hi from mark.");
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //����Ϣһ��һ�з���
//    public static void main(String[] args) {
//        BaseSocketClient cc = new BaseSocketClient("127.0.0.1", 9799);
//        try {
//            cc.connetServer();
//            Scanner sc = new Scanner(System.in);
//            while (sc.hasNext()) {
//                //ÿһ����Ϣ����һ��
//                String line = sc.nextLine();
//                cc.send(line);
//            }
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //sendMsgByLen
    public static void main(String[] args) {
        BaseSocketClient lc = new BaseSocketClient("127.0.0.1",9799);
        try {
            lc.connetServer();
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                lc.sendMessage(sc.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
