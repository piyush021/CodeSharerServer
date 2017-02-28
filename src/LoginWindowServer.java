import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginWindowServer extends JFrame {

	private JLabel labelUserName;
	private JLabel labelPassword;
	private JButton buttonStartServer;
	private JTextField textFieldUserName;
	private JPasswordField passwordFieldPassword;
	private JCheckBox checkBoxRunInSafeMode;
	private boolean isRunningInSafeMode=false;
	
		@SuppressWarnings("deprecation")
		public LoginWindowServer(){
			super();
			setTitle("CodeSharerServer");
			setIconImage(new ImageIcon(this.getClass().getResource("/mainIcon.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		    labelUserName = new JLabel("Enter Admin Username");
		    textFieldUserName = new JTextField(22);
		    labelPassword = new JLabel("Enter Admin Password");
		    passwordFieldPassword = new JPasswordField(22);
		    buttonStartServer = new JButton("Start Server");
		    checkBoxRunInSafeMode=new JCheckBox("Run In Safe Mode");
		    setLayout(new FlowLayout());
		    add(labelUserName);
		    add(textFieldUserName);
		    add(labelPassword);
		    add(passwordFieldPassword);
		    add(checkBoxRunInSafeMode);
		    add(buttonStartServer);
		    setSize(300,175);
		    setResizable(false);
		    setLocationRelativeTo(null);
		    setVisible(true);   
		    addWindowListener(new ExitApplication());
		    
		    checkBoxRunInSafeMode.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(checkBoxRunInSafeMode.isSelected()){
						isRunningInSafeMode=true;
					}else{
						isRunningInSafeMode=false;
					}
				}
			});
		    buttonStartServer.addActionListener(new ActionListener(){
		    	@Override
		    	public void actionPerformed(ActionEvent e){
				    final String stringUsername = textFieldUserName.getText();
				    String stringPassword = passwordFieldPassword.getText();
				    if(stringPassword.equals("")&& stringUsername.equals("")){
					    setVisible(false);
					    
					    new Thread(new Runnable(){
					    	@Override
					        public void run(){
					    		JFileChooser fileChooser=new JFileChooser();
							    fileChooser.setDialogTitle("Select Default Directory");
							    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							    fileChooser.setAcceptAllFileFilterUsed(false);
							    String defaultDirectory="";
							    while(true){
							    	if(fileChooser.showDialog(LoginWindowServer.this,"Select")==JFileChooser.APPROVE_OPTION){
							    		defaultDirectory=fileChooser.getSelectedFile().getAbsolutePath();
							    		break;
							    	}	
							    }
					    		//start both udp and tcp server
					    		new ServerWindow(defaultDirectory,isRunningInSafeMode).startServers();
					    	}	        
					    }).start();
					    LoginWindowServer.this.dispose();
				    }else{
				    	JOptionPane.showMessageDialog(getRootPane(),"Incorrect password or Username !!!","ERROR",JOptionPane.ERROR_MESSAGE);
				    }
		    	}
			});	   
	}

	public static void main(String[] args) {
		new LoginWindowServer();
	}

}
