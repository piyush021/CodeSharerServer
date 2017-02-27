import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginWindowServer extends JFrame {

	private JLabel labelUserName;
	private JLabel labelPassword;
	private JButton buttonSubmit;
	private JTextField textFieldUserName;
	private JPasswordField passwordFieldPassword;
	
		@SuppressWarnings("deprecation")
		public LoginWindowServer(){
			super();
			setTitle("CodeSharerServer");
			setIconImage(new ImageIcon(this.getClass().getResource("/mainIcon.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		    labelUserName = new JLabel("Enter ADMIN Username");
		    textFieldUserName = new JTextField(20);
		    labelPassword = new JLabel("Enter ADMIN Password");
		    passwordFieldPassword = new JPasswordField(20);
		    buttonSubmit = new JButton("Start Server");
		    setLayout(new FlowLayout());
		    add(labelUserName);
		    add(textFieldUserName);
		    add(labelPassword);
		    add(passwordFieldPassword);
		    add(buttonSubmit);
		    setSize(300,175);
		    setResizable(false);
		    setLocationRelativeTo(null);
		    setVisible(true);   
		    addWindowListener(new ExitApplication());
		    
		    buttonSubmit.addActionListener(new ActionListener(){
		    	@Override
		    	public void actionPerformed(ActionEvent e){
				    final String stringUsername = textFieldUserName.getText();
				    String stringPassword = passwordFieldPassword.getText();
				    if(stringPassword.equals("")&& stringUsername.equals("")){
					    setVisible(false);
					    new Thread(new Runnable(){
					    	@Override
					        public void run(){
					    		//start both udp and tcp server
					    		new ServerWindow().startServers();
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
