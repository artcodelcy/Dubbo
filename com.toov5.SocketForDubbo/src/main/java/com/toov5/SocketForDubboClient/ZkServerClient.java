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

	// ע������server
	public static void initServer() {
		// ��ȡ���нڵ� ��Ϣ
	List<String> children = zkClient.getChildren(parentService);
	getChildren(zkClient, children);	
	zkClient.subscribeChildChanges(parentService, new IZkChildListener() {
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			System.out.println("ע�����ķ����б���Ϣ�б仯"); 
			getChildren(zkClient, currentChilds);//����������б仯�� ��Ҫ���¶�ȡŶ
		}
	});	
		
	}
  
 public static	void getChildren(ZkClient zkClient, List<String> children) {
	 listServer.clear();
	for (String ch : children) {
		String serverAddr = zkClient.readData(parentService + "/" + ch); // �õ������ַ
		listServer.add(serverAddr);
	}
	System.out.println("����ӿڵ�ַ"+listServer.toString()); 
}
	
	
	// ��������
	private static int reqCount = 1;
//  //�������	
//	private static int  serverCount = 0; //��ʼֵ��0

	// ��ȡ��ǰserver��Ϣ
	public static String getServer() {
		int index = reqCount % listServer.size();
		String address = listServer.get(index);
		System.out.println("�ͻ������������" + address);
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
