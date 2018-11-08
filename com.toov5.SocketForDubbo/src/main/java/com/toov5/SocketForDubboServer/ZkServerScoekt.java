package com.toov5.SocketForDubboServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.I0Itec.zkclient.ZkClient;

//##ServerScoekt�����
public class ZkServerScoekt implements Runnable {
	private static int port = 8081; // socket �������������ʹ�õ� �˿ں�
	private static String parentService = "/service";
	private static ZkClient zkClient = new ZkClient("192.168.91.5");

	public static void main(String[] args) throws IOException {
		ZkServerScoekt server = new ZkServerScoekt(port); // ���캯������port
		regServer(); // ȥzkע��
		Thread thread = new Thread(server);
		thread.start();
	}

	public ZkServerScoekt(int port) {
		ZkServerScoekt.port = port;
	}

	// ע�����
	public static void regServer() {
		// �ȴ������ڵ� �ж�
		if (!zkClient.exists(parentService)) {
			// ������ �ڵ�
			zkClient.createPersistent(parentService); // �������ڵ㣨�־û���

		} //���ھͲ��ù���
		// �����ӽڵ�
		String serverKey = parentService + "/server_" + port; // Լ���˸��淶
		if (!zkClient.exists(serverKey)) { 
			zkClient.createEphemeral(serverKey, "127.0.0.1:" + port); // ��ʱ�� �����ɾ���Ǳ���� ɾ��һ�� �ڴ��� �������µ� ���ػ�ȡ�� ���и��ؾ���
		} else {
			zkClient.delete(serverKey);
		}
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server start port:" + port);
			Socket socket = null;
			while (true) {
				socket = serverSocket.accept();
				new Thread(new ServerHandler(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (Exception e2) {

			}
		}
	}

}
