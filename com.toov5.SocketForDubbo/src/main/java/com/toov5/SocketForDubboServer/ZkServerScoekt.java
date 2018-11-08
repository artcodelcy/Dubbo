package com.toov5.SocketForDubboServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.I0Itec.zkclient.ZkClient;

//##ServerScoekt服务端
public class ZkServerScoekt implements Runnable {
	private static int port = 8081; // socket 服务启动后的所使用的 端口号
	private static String parentService = "/service";
	private static ZkClient zkClient = new ZkClient("192.168.91.5");

	public static void main(String[] args) throws IOException {
		ZkServerScoekt server = new ZkServerScoekt(port); // 构造函数传入port
		regServer(); // 去zk注册
		Thread thread = new Thread(server);
		thread.start();
	}

	public ZkServerScoekt(int port) {
		ZkServerScoekt.port = port;
	}

	// 注册服务
	public static void regServer() {
		// 先创建父节点 判断
		if (!zkClient.exists(parentService)) {
			// 创建父 节点
			zkClient.createPersistent(parentService); // 创建父节点（持久化）

		} //存在就不用管了
		// 创建子节点
		String serverKey = parentService + "/server_" + port; // 约定了个规范
		if (!zkClient.exists(serverKey)) { 
			zkClient.createEphemeral(serverKey, "127.0.0.1:" + port); // 临时的 上面的删除是必须的 删除一遍 在创建 保持最新的 本地获取到 进行负载均衡
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
