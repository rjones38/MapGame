import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class launcher {

	public static void main(String[] args) {
		Engine e = new Engine();
		JFrame f = new JFrame("MapModes");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel(new BorderLayout());
		f.add(p);
		p.add(e);
		e.init();
		f.pack();
		f.setSize(new Dimension(1215,1040));
		f.setVisible(true);
	}

}
