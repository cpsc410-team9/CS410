package visualisation;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VFrame extends JFrame {
	
	Dimension d =new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height); 

	public VFrame(String title){
		super(title);
		
		this.setPreferredSize(d);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.BLACK);
		
		this.setLayout(new BorderLayout());
		this.add(new PaintPane());
		this.setVisible(true);
	}
	
	public static void main(String[] args){
        VFrame frame = new VFrame("");
	}
	  
    protected class PaintPane extends JPanel {
    	Dimension d =new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
    	@Override
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		int locX, locY = 0;
    		for (int i = 0; i < 1000; i++) {
    			locX = (int) (Math.random() * d.width);
    			locY = (int) (Math.random() * d.height);
    			g.setColor(new Color(255, 255, 0));
    			g.drawLine(locX, locY, locX, locY);
    		}
    		this.setBackground(Color.BLACK);
    	}
    }


        
}
