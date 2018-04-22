package socket.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

//消息拉取线程
public class ListenThread implements Runnable{

    private Socket socket;
    private InputStream inputStream;

    public ListenThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() throws RuntimeException{
        try{
            this.inputStream = socket.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
            throw  new RuntimeException(e.getMessage());
        }

        while (true){
            try{

                //根据消息长度来获取消息
                int first = this.inputStream.read();
                if(first == -1){
                    //输入流已经关闭，无需要继续读取
                    throw  new RuntimeException("disconnected");
                }

                int second = this.inputStream.read();
                int msgLenght = (first<<8)+second;
                byte[] readBuffer = new byte[msgLenght];
                this.inputStream.read(readBuffer);

                System.out.println("message from [" +socket.getInetAddress()+"]: " + new String(readBuffer,"UTF-8"));
            }catch (IOException e){
                e.printStackTrace();
                throw  new RuntimeException(e.getMessage());
            }
        }
    }
}
