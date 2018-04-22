package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 先启动服务器，在启动客户端
 */
public class BaseSocketServer {

    private ServerSocket server;
    private Socket socket;
    private int port;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * 我们使用了一个大小为MAX_BUFFER_SIZE的byte数组作为缓冲区，
     * 然后从输入流中取出字节放置到缓冲区，再从缓冲区中取出字节构建到字符串中去，
     * 这在输入流文件很大时非常有用
     */
    private static final int MAX_BUFFER_SIZE = 1024;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public BaseSocketServer(int port) {
        this.port = port;
    }


    //单向通信
    public void runServerSingle() throws IOException {
        this.server = new ServerSocket(this.port);

        System.out.println("base socket server started.");
        // the code will block here till the request come.
        this.socket = server.accept();

        this.inputStream = this.socket.getInputStream();

        byte[] readBytes = new byte[MAX_BUFFER_SIZE];

        int msgLen;
        StringBuilder stringBuilder = new StringBuilder();

        while ((msgLen = inputStream.read(readBytes)) != -1) {
            stringBuilder.append(new String(readBytes,0,msgLen,"UTF-8"));
        }

        System.out.println("get message from client: " + stringBuilder);

        inputStream.close();
        socket.close();
        server.close();
    }


    //双向通信
    public void runServer() throws IOException {
        this.server = new ServerSocket(port);
        this.socket = server.accept();
        this.inputStream = socket.getInputStream();

        String message = StreamToString(inputStream);

        System.out.println("received message: " + message);

        this.socket.shutdownInput(); // 告诉客户端接收已经完毕，之后只能发送

        // write the receipt.

        this.outputStream = this.socket.getOutputStream();
        String receipt = "We received your message: " + message;
        outputStream.write(receipt.getBytes("UTF-8"));

        this.outputStream.close();
        this.socket.close();
    }


    /**
     * 多次传输信息
     * 使用换行符\n来标记一次发送的结束，
     * 服务端每接收到一个消息，就打印一次，并且使用了Scanner来简化操作:
     */
    public void run() throws IOException {
        this.server = new ServerSocket(this.port);

        System.out.println("base socket server started.");

        this.socket = server.accept();
        // the code will block here till the request come.

        this.inputStream = this.socket.getInputStream();
        Scanner sc = new Scanner(this.inputStream);
        while (sc.hasNextLine()) {
            System.out.println("get info from client: " + sc.nextLine());
        } // 循环接收并输出消息内容
        this.inputStream.close();
        socket.close();
    }

    /**
     * 根据长度界定信息的结束
     * 即发送消息变成两步骤
     * 1、发送消息的长度
     * 2、发送消息
     */
    public void runServerByLen() throws IOException {
        this.server = new ServerSocket(this.port);
        this.socket = server.accept();
        this.inputStream = socket.getInputStream();
        byte[] bytes;
        while (true) {
            // 先读第一个字节
            int first = inputStream.read();
            if (first == -1) {
                // 如果是-1，说明输入流已经被关闭了，也就不需要继续监听了
                this.socket.close();
                break;
            }
            // 读取第二个字节
            int second = inputStream.read();

            // 用位运算将两个字节拼起来成为真正的长度
            int length = (first << 8) + second;

            // 构建指定长度的字节大小来储存消息即可
            bytes = new byte[length];

            inputStream.read(bytes);
            System.out.println("receive message: " + new String(bytes,"UTF-8"));
        }
    }



    public String StreamToString(InputStream inputStream) throws IOException{
        byte[] readBuffer = new byte[1024];
        StringBuffer stringBuffer = new StringBuffer();

        int msgLen=0;
        while ( (msgLen = inputStream.read(readBuffer)) != -1){
            stringBuffer.append(new String(readBuffer,0,msgLen,"UTF-8"));
        }
        return new String(stringBuffer);
    }


    //双向通信
    public static void main(String[] args) {
        BaseSocketServer bs = new BaseSocketServer(9799);
        try {
            //bs.runServerSingle();
            //bs.runServer();
            //bs.run();
            bs.runServerByLen();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}