package com.network.netty.book01.chapter04.demo01;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {
    public void serve(int port) throws IOException {

        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);

        try {
            for(;;) {

                // 接受连接
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                // 创建一个新的线程来处理该连接
                new Thread(new Runnable() {
                    OutputStream out;
                    @Override
                    public void run() {
                        try {
                            out = clientSocket.getOutputStream();

                            // 将消息写给已连接的客户端
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();

                            // 关闭连接
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // nothing to do
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException exx) {
            exx.printStackTrace();
        }
    }
}
