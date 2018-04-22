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

    //实现单向通信
    public void sendSingle(String message) throws IOException {
        try {
            this.outputStream.write(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        this.outputStream.close();
        this.socket.close();
    }

    //双向通信
    public void sendMessage(String message) throws IOException {
        //this.socket = new Socket(this.serverHost,this.serverPort);
        this.outputStream = socket.getOutputStream();
        this.outputStream.write(message.getBytes("UTF-8"));
        this.socket.shutdownOutput(); // 告诉服务器，所有的发送动作已经结束，之后只能接收
        this.inputStream = socket.getInputStream();
        String receipt = StreamToString(inputStream);
        System.out.println("got receipt: " + receipt);
        this.inputStream.close();
        this.socket.close();
    }

    /**
     * 多次发送信息
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
        // 首先要把message转换成bytes以便处理
        byte[] bytes = message.getBytes("UTF-8");
        // 接下来传输两个字节的长度，依然使用移位实现
        int length = bytes.length;
        this.outputStream.write(length >> 8); // write默认一次只传输一个字节
        this.outputStream.write(length);
        // 传输完长度后，再正式传送消息
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

    //将消息一行一行发送
//    public static void main(String[] args) {
//        BaseSocketClient cc = new BaseSocketClient("127.0.0.1", 9799);
//        try {
//            cc.connetServer();
//            Scanner sc = new Scanner(System.in);
//            while (sc.hasNext()) {
//                //每一行信息发送一次
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
