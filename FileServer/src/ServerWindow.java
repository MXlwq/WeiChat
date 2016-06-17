import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWindow extends Frame{
	private SocketServer server;
	private Label label;
	
	public ServerWindow(String title){
		super(title);
		server = new SocketServer(9000);
		label = new Label();
		add(label, BorderLayout.PAGE_START);
		label.setText("�������Ѿ�����");
		this.addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e) {
				new Thread(new Runnable() {			
					
					public void run() {
						try {
							server.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
			
			public void windowIconified(WindowEvent e) {
			}
			
			
			public void windowDeiconified(WindowEvent e) {
			}
			
			
			public void windowDeactivated(WindowEvent e) {
			}
			
			
			public void windowClosing(WindowEvent e) {
				 server.quit();
				 System.exit(0);
			}
			
			
			public void windowClosed(WindowEvent e) {
			}
			
			
			public void windowActivated(WindowEvent e) {
			}
		});
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerWindow window = new ServerWindow("�ļ��ϴ������"); 
		window.setSize(300, 300); 
		window.setVisible(true);
	}

}