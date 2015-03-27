import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/** GUI
 * 
 */
public class GUI{

	public static String[] pseudocode;
	private static JTextArea textArea;
	
	public static void main(String[] args) throws AWTException {
		
		new GUI();
		
	}
	
/** Creates GUI
 * 
 */
	public GUI() throws AWTException{
		
		JFrame window = new JFrame("Pseudocode to VisualLogic");
		
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        JPanel headerPanel = new JPanel();
        JPanel inputPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        
        JLabel header = new JLabel("Insert Pseudocode below");
        
        JButton button = new JButton("Enter");
        textArea = new JTextArea(40, 50);

        JScrollPane textScroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        						 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        headerPanel.add(header);
        inputPanel.add(textScroll);
        buttonPanel.add(button, BorderLayout.SOUTH);
        window.add(headerPanel, BorderLayout.NORTH);
        window.add(inputPanel, BorderLayout.CENTER);
        window.add(buttonPanel, BorderLayout.SOUTH);
        window.pack();
        window.setVisible(true);
        
        button.addActionListener(new ActionListener()  {

            
            public void actionPerformed(ActionEvent e) {

                pseudocode = textArea.getText().split("\\n");
                
                
                
                new Thread(new Runnable() {
                    @Override 
                    public void run() {
                    	try {
                            new Flowchart(pseudocode);
                            
                        }
                        catch (AWTException exception) {
                        	System.out.println("Flowchart execution failed");
                            
                        }
                    } 
                }).start();

            } 
        });
        
        
	}
	
}
