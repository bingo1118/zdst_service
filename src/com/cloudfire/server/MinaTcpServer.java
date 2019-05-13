package com.cloudfire.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.cloudfire.until.GetTime;
import com.cloudfire.until.IntegerTo16;

public class MinaTcpServer extends IoHandlerAdapter {
	public static final int PORT = 14567;  
    public MinaTcpServer() throws IOException {  
        NioSocketAcceptor acceptor = new NioSocketAcceptor();  
        acceptor.setHandler(this);  
        acceptor.bind(new InetSocketAddress(PORT));  
        System.out.println("TCP�����������˿ڣ�" + PORT);  
    }  
    public static void main(String[] args) throws IOException {  
        new MinaTcpServer();  
    }  
    @Override  
    public void messageReceived(IoSession session, Object message)  
            throws Exception {  
        IoBuffer bbuf = (IoBuffer) message;  
        byte[] byten = new byte[bbuf.limit()];  
        bbuf.get(byten, bbuf.position(), bbuf.limit());  
        System.out.println(GetTime.ConvertTimeByLong()+"�յ���Ϣ��" + IntegerTo16.bytes2Hex(byten));  
        byte[] bts = new byte[10];  
        for(int i=0;i<10;i++){  
            bts[i] = (byte)i;  
        }  
        IoBuffer buffer = IoBuffer.allocate(10);  
        buffer.put(bts);  
        buffer.flip();  
        session.write(buffer);  
//      // �õ����еĿͻ���Session  
//      Collection<IoSession> sessions = session.getService().getManagedSessions().values();  
//      // �����пͻ��˷�������  
//      for (IoSession sess : sessions) {  
//          sess.write(buffer);  
//      }  
    }  
    @Override  
    public void sessionClosed(IoSession session) throws Exception {  
        System.out.println("�Ự�ر�");  
    }  
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause)  
            throws Exception {  
        System.out.println("�Ự�쳣");  
        super.exceptionCaught(session, cause);  
    }  
    @Override  
    public void messageSent(IoSession iosession, Object obj) throws Exception {  
        System.out.println("�������Ϣ����");  
        super.messageSent(iosession, obj);  
    }  
    @Override  
    public void sessionCreated(IoSession iosession) throws Exception {  
        System.out.println("�Ự����");  
        super.sessionCreated(iosession);  
    }  
    @Override  
    public void sessionIdle(IoSession iosession, IdleStatus idlestatus)  
            throws Exception {  
        System.out.println("�Ự����");  
        super.sessionIdle(iosession, idlestatus);  
    }  
    @Override  
    public void sessionOpened(IoSession iosession) throws Exception {  
        System.out.println("�Ự��");  
        super.sessionOpened(iosession);  
    } 
}
