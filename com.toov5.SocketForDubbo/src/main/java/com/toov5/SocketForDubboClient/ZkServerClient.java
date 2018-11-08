package com.toov5.SocketForDubboClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;
import org.jboss.netty.channel.StaticChannelPipeline;
import org.jboss.netty.util.internal.SystemPropertyUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.w3c.dom.ls.LSInput;

public class ZkServerClient {
	public static List<String> listServer = new ArrayList<String>();
	private static ZkClient zkClient = new ZkClient("192.168.91.5");
	private static String parentService = "/service";

	public static void main(String[] args) {
		initServer();
		ZkServerClient client = new ZkServerClient();
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String name;
			try {
				name = console.readLine();
				if ("exit".equals(name)) {
					System.exit(0);
				}
				client.send(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 注册所有server
	public static void initServer() {
		// 读取所有节点 信息
	List<String> children = zkClient.getChildren(parentService);
	getChildren(zkClient, children);	
	zkClient.subscribeChildChanges(parentService, new IZkChildListener() {
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			System.out.println("注册中心服务列表信息有变化"); 
			getChildren(zkClient, currentChilds);//如果监听到有变化了 就要重新读取哦
		}
	});	
		
	}
  
 public static	void getChildren(ZkClient zkClient, List<String> children) {
	 listServer.clear();
	for (String ch : children) {
		String serverAddr = zkClient.readData(parentService + "/" + ch); // 拿到服务地址
		listServer.add(serverAddr);
	}
	System.out.println("服务接口地址"+listServer.toString()); 
}
	
	
	// 请求总数
	private static int reqCount = 1;
//  //服务个数	
//	private static int  serverCount = 0; //初始值是0

	// 获取当前server信息
	public static String getServer() {
		int index = reqCount % listServer.size();
		String address = listServer.get(index);
		System.out.println("客户端请求服务器" + address);
		reqCount++;
		return address;
	}

	public void send(String name) {

		String server = ZkServerClient.getServer();
		String[] cfg = server.split(":");

		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			out.println(name);
			while (true) {
				String resp = in.readLine();
				if (resp == null)
					break;
				else if (resp.length() > 0) {
					System.out.println("Receive : " + resp);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
