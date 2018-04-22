package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * ���������������������ͻ���
 */
public class BaseSocketServer {

    private ServerSocket server;
    private Socket socket;
    private int port;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * ����ʹ����һ����СΪMAX_BUFFER_SIZE��byte������Ϊ��������
     * Ȼ�����������ȡ���ֽڷ��õ����������ٴӻ�������ȡ���ֽڹ������ַ�����ȥ��
     * �����������ļ��ܴ�ʱ�ǳ�����
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


    //����ͨ��
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


    //˫��ͨ��
    public void runServer() throws IOException {
        this.server = new ServerSocket(port);
        this.socket = server.accept();
        this.inputStream = socket.getInputStream();

        String message = StreamToString(inputStream);

        System.out.println("received message: " + message);

        this.socket.shutdownInput(); // ���߿ͻ��˽����Ѿ���ϣ�֮��ֻ�ܷ���

        // write the receipt.

        this.outputStream = this.socket.getOutputStream();
        String receipt = "We received your message: " + message;
        outputStream.write(receipt.getBytes("UTF-8"));

        this.outputStream.close();
        this.socket.close();
    }


    /**
     * ��δ�����Ϣ
     * ʹ�û��з�\n�����һ�η��͵Ľ�����
     * �����ÿ���յ�һ����Ϣ���ʹ�ӡһ�Σ�����ʹ����Scanner���򻯲���:
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
        } // ѭ�����ղ������Ϣ����
        this.inputStream.close();
        socket.close();
    }

    /**
     * ���ݳ��Ƚ綨��Ϣ�Ľ���
     * ��������Ϣ���������
     * 1��������Ϣ�ĳ���
     * 2��������Ϣ
     */
    public void runServerByLen() throws IOException {
        this.server = new ServerSocket(this.port);
        this.socket = server.accept();
        this.inputStream = socket.getInputStream();
        byte[] bytes;
        while (true) {
            // �ȶ���һ���ֽ�
            int first = inputStream.read();
            if (first == -1) {
                // �����-1��˵���������Ѿ����ر��ˣ�Ҳ�Ͳ���Ҫ����������
                this.socket.close();
                break;
            }
            // ��ȡ�ڶ����ֽ�
            int second = inputStream.read();

            // ��λ���㽫�����ֽ�ƴ������Ϊ�����ĳ���
            int length = (first << 8) + second;

            // ����ָ�����ȵ��ֽڴ�С��������Ϣ����
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


    //˫��ͨ��
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