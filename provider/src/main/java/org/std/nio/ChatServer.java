package org.std.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ChatServer implements Runnable{
	//选择器
	private Selector selector;
	//注册ServerSocketChannel后的选择键
	private SelectionKey serverKey;
	//标识是否执行
	private boolean isRun;
	
	private List<String> userNames;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ChatServer(int port){
		isRun = true;
		userNames = new ArrayList<String>();
		init(port);
	}
	
	private void init(int port) {
		try {
			//获得选择器实例
			selector = Selector.open();
			//获得服务器套接字实例
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			//绑定端口号
			serverChannel.bind(new InetSocketAddress(port));
			//设置为非阻塞
			serverChannel.configureBlocking(false);
			//将ServerSocketChannel注册到选择器，指定其行为为“等待接受连接”
			serverKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("server socket channel is starting...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			//轮询选择器选择键
			while (isRun){
				//选择一组已准备进行IO操作的通道key，等于一时表示有这种key
				int n = selector.select();
				if (n > 0){
					//从选择器上获取已选择的key的集合并进行迭代
					Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
					while (iter.hasNext()){
						SelectionKey key = iter.next();
						//若此key的通道是等待接受新的套接字连接
						if (key.isAcceptable()){
							//删除key，否则只后新连接将被堵塞无法连接服务
							iter.remove();
							//获取key相应的通道
							ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
							
							//接受新的连接返回和client对等的套接字通道
							SocketChannel channel = serverChannel.accept();
							if (channel == null){
								continue;
							}
							//设置为非阻塞
							channel.configureBlocking(false);
							//将这个套接字通道注册到选择器，指定其行为为读
							channel.register(selector, SelectionKey.OP_READ);
						}
						//此通道的行为是读
						if (key.isReadable()){
							redMsg(key);
						}
						
						if (key.isWritable()){
							writeMsg(key);
						}
					}
				}
			}
		} catch (Exception e) {
			
		}
	}

	private void writeMsg(SelectionKey key) throws IOException {
		//获取key相应的套接字通道
		SocketChannel channel = (SocketChannel) key.channel();
		//创建一个大小为1024K的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		StringBuffer sb = new StringBuffer();
		int count = channel.read(buffer);
		if (count > 0){
			//翻转缓冲区（将缓冲区有写进数据模式变成读出数据模式）
			buffer.flip();
			//将缓存区的数据转成String
			sb.append(new String(buffer.array(), 0, count));
		}
		String content = sb.toString();
		if (content.indexOf("open_") != -1){
			String name = content.substring(5);
			printInfo(name);
			userNames.add(name);
			//获取选择器已获取的key并迭代
			Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
			while (iter.hasNext()){
				SelectionKey selKey = iter.next();
				//若不是服务器套接字通道的key，则将数据设置到此key中，并更新此key感兴趣的动作
				if (selKey != serverKey){
					selKey.attach(userNames);
					selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
				}
			}
		}else if (content.indexOf("exit_") != -1){
			String name = content.substring(5);
			//删除用户名称
			userNames.remove(name);
			//将close字符附加到key
			key.attach("close");
			key.interestOps(SelectionKey.OP_WRITE);
			//获取选择器上已选择的key并迭代
			//将更新后的名称列表数据附加到每个套接字通道的key上，并重设key感兴趣的操作
			Iterator<SelectionKey> iter = key.selector().selectedKeys().iterator();
			while (iter.hasNext()){
				SelectionKey selKey = iter.next();
				if (selKey != serverKey){
					selKey.attach(name);
					selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
				}
			}
			printInfo(name + " offline");
		}else{
			//读取Client聊天消息
			String name = content.substring(0, content.indexOf("^"));
			String msg = content.substring(content.indexOf("^")+1);
			printInfo("(" + name + ")说：" + msg);
			String smsg = name + " " + sdf.format(new Date()) + "\n" + msg + "\n";
			Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
			while (iter.hasNext()){
				SelectionKey selKey = iter.next();
				if (selKey != serverKey){
					selKey.attach(smsg);
					selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
				}
			}
		}
	}

	private void redMsg(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		Object obj = key.attachment();
		//将key的附加数据设置为空
		key.attach("");
		//附加值为“close”，则取消key，并关闭相应通道
		if (obj.toString().equals("close")){
			key.cancel();
			channel.socket().close();
			channel.close();
		}else{
			//将数据写入数据通道
			channel.write(ByteBuffer.wrap(obj.toString().getBytes()));
		}
		//重设此key的兴趣
		key.interestOps(SelectionKey.OP_READ);
	}
	
	private void printInfo(String str) {
		System.out.println("[" + sdf.format(new Date()) + "] -> " + str);
	}

	public static void main(String[] args) {
		ChatServer server = new ChatServer(9999);
		new Thread(server).start();
	}
}
