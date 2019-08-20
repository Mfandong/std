package org.std.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
/** https://www.cnblogs.com/mfrbuaa/p/5124066.html */
public class ClientService {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 9999;
	private static SocketChannel socketChannel;
	
	private static Object lock = new Object();
	private static ClientService service;
	
	public static ClientService getInstance(){
		if (service == null){
			synchronized (lock) {
				try {
					if (service == null){
						service = new ClientService();
					}
				} catch (Exception e) {
				}
			}
		}
		return service;
	}
	
	private ClientService() throws IOException {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(HOST, PORT));
	}
	
	public void sendMsg(String msg){
		try {
			while  (!socketChannel.finishConnect()){
				
			}
			socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
		} catch (Exception e) {
		}
	}
	
	public String receiveMsg() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.clear();
		StringBuffer sb = new StringBuffer();
		int count = 0;
		String msg = null;
		try {
			while ((count = socketChannel.read(buffer)) > 0) {
				sb.append(new String(buffer.array(), 0, count));
			}
			if (sb.length() > 0) {
				msg = sb.toString();
				if ("close".equals(sb.toString())) {
					msg = null;
					socketChannel.close();
					socketChannel.socket().close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
}
