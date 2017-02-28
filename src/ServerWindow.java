import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ServerWindow extends JFrame {
	JLabel labelOutputMessage;
	boolean isRunningInSafeMode;
	String defaultDirectory;
	public ServerWindow(String defaultDirectory,boolean isRunningInSafeMode){
		super();
		this.defaultDirectory=defaultDirectory;
		this.isRunningInSafeMode=isRunningInSafeMode;
		setTitle("CodeSharerServer");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/mainIcon.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		labelOutputMessage=new JLabel("Server is running......",SwingConstants.CENTER);
		add(labelOutputMessage);
		setVisible(true);
		setSize(300,300);
		setResizable(false);
		setLocationRelativeTo(null);
		addWindowListener(new ExitApplication());		
	}
	
	public void startServers(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				new UDPServerToSendIP(ServerWindow.this).startUDPServer();
			}
		}).start();
		new TCPServer(ServerWindow.this,defaultDirectory).startTCPServer();
	}

}
