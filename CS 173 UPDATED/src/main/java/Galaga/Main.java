package Galaga;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends JFrame{

   /**
	 * 
	 */
	private static final long serialVersionUID = -5595232444437430346L;

   private JFrame mainFrame;
   private Label headerLabel;
   private Label statusLabel;
   private Panel controlPanel;
   
   public Main(){

      prepareGUI();
   }

   public static void main(String[] args){
      Main main = new Main();
      main.showButton();
   }


   private void prepareGUI(){

      mainFrame = new JFrame("Galaga Woo");
     
  	
    	mainFrame.setLayout(new BorderLayout());
    	mainFrame.setContentPane(new JLabel(new ImageIcon("/sprites/027.jpg")));
    	mainFrame.setLayout(new FlowLayout());
    	mainFrame.setSize(399,399);
    	mainFrame.setSize(400,400);
      mainFrame.setLayout(new GridLayout(3, 1));
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });

      headerLabel = new Label();
      headerLabel.setAlignment(Label.CENTER);
      statusLabel = new Label();        
      statusLabel.setAlignment(Label.CENTER);
      statusLabel.setSize(350,100);

      controlPanel = new Panel();
      controlPanel.setLayout(new FlowLayout());

      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);

      mainFrame.setVisible(true);  
   }

   private void showButton(){
      headerLabel.setText("Choose Game Mode"); 

      Button blitz = new Button("Blitz");
      Button timetrial = new Button("Time Trial");
      Button normal = new Button("Free Mode");

      blitz.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new Blitz();
           
         }
      });

      timetrial.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TimeTrial();
             
         }
      });

      normal.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
        	 new FreeMode();
             
         }
      });

      controlPanel.add(blitz);
      controlPanel.add(timetrial);
      controlPanel.add(normal);       

      mainFrame.setVisible(true);  
   }
}	