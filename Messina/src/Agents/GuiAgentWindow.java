package Agents;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import jade.core.Agent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GuiAgentWindow extends JFrame {
	 private Agent          myAgent;
//	  private LocationTableModel visitedSiteListModel;
//	  private JTable            visitedSiteList;
//	  private LocationTableModel availableSiteListModel;
//	  private JTable            availableSiteList;
//	  private JTextField counterText; 
//
//	  private static String MOVELABEL = "MOVE";
//	  private static String CLONELABEL = "CLONE";
//	  private static String EXITLABEL = "EXIT";
//	  private static String PAUSELABEL = "Stop Counter";
//	  private static String CONTINUELABEL = "Continue Counter";
//	  private static String REFRESHLABEL = "Refresh Locations";


		// Constructor
	 GuiAgentWindow(Agent a)
		{
			super();
			myAgent = a;
			setTitle("Google Map");
			setSize(1024,1024);

		        try {
		            String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=38.137294,15.451894&zoom=11&size=1024x1024&scale=2&maptype=roadmap";
		            imageUrl=imageUrl+"&markers=color:blue|label:A|38.2,15.4&markers=color:green|label:B|38.19,15.4&markers=color:red|label:C|38.21,15.4";
		            String destinationFile = "image.jpg";
		            String str = destinationFile;
		            URL url = new URL(imageUrl);
		            InputStream is = url.openStream();
		            OutputStream os = new FileOutputStream(destinationFile);

		            byte[] b = new byte[2048];
		            int length;

		            while ((length = is.read(b)) != -1) {
		                os.write(b, 0, length);
		            }

		            is.close();
		            os.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		            System.exit(1);
		        }

		        this.add(new JLabel(new ImageIcon((new ImageIcon("image.jpg")).getImage().getScaledInstance(1024,1024,
		                java.awt.Image.SCALE_SMOOTH))));

			////////////////////////////////
			// Set GUI window layout manager
		
//			JPanel main = new JPanel();
//			main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
//
//			JPanel counterPanel = new JPanel();
//			counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.X_AXIS));
//			
//			JButton pauseButton = new JButton("STOP COUNTER");
//			pauseButton.addActionListener(this);
//			JButton continueButton = new JButton("CONTINUE COUNTER");
//			continueButton.addActionListener(this);
//			JLabel counterLabel = new JLabel("Counter value: ");
//			counterText = new JTextField();
//			counterPanel.add(pauseButton);
//			counterPanel.add(continueButton);
//			counterPanel.add(counterLabel);
//			counterPanel.add(counterText);
//			
//			main.add(counterPanel);
//			
//		   ///////////////////////////////////////////////////
//			// Add the list of available sites to the NORTH part 
//			availableSiteListModel = new LocationTableModel();
//			availableSiteList = new JTable(availableSiteListModel);
//			availableSiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//			JPanel availablePanel = new JPanel();
//			availablePanel.setLayout(new BorderLayout());
//
//			JScrollPane avPane = new JScrollPane();
//			avPane.getViewport().setView(availableSiteList);
//			availablePanel.add(avPane, BorderLayout.CENTER);
//			availablePanel.setBorder(BorderFactory.createTitledBorder("Available Locations"));
//		  availableSiteList.setRowHeight(20);
//
//			main.add(availablePanel);
//			
//			TableColumn c;
//			c = availableSiteList.getColumn(availableSiteList.getColumnName(0));
//			c.setHeaderValue("ID");
//			c = availableSiteList.getColumn(availableSiteList.getColumnName(1));
//			c.setHeaderValue("Name");
//			c = availableSiteList.getColumn(availableSiteList.getColumnName(2));
//			c.setHeaderValue("Protocol");
//			c = availableSiteList.getColumn(availableSiteList.getColumnName(3));
//			c.setHeaderValue("Address");
//
//			///////////////////////////////////////////////////
//			// Add the list of visited sites to the CENTER part 
//			JPanel visitedPanel = new JPanel();
//			visitedPanel.setLayout(new BorderLayout());
//			visitedSiteListModel = new LocationTableModel();
//			visitedSiteList = new JTable(visitedSiteListModel);
//			JScrollPane pane = new JScrollPane();
//			pane.getViewport().setView(visitedSiteList);
//		  visitedPanel.add(pane,BorderLayout.CENTER);
//			visitedPanel.setBorder(BorderFactory.createTitledBorder("Visited Locations"));
//		  visitedSiteList.setRowHeight(20);
//
//			main.add(visitedPanel);
//
//				// Column names
//		
//			c = visitedSiteList.getColumn(visitedSiteList.getColumnName(0));
//			c.setHeaderValue("ID");
//			c = visitedSiteList.getColumn(visitedSiteList.getColumnName(1));
//			c.setHeaderValue("Name");
//			c = visitedSiteList.getColumn(visitedSiteList.getColumnName(2));
//			c.setHeaderValue("Protocol");
//			c = visitedSiteList.getColumn(visitedSiteList.getColumnName(3));
//			c.setHeaderValue("Address");
//
//		
//			/////////////////////////////////////////////////////////////////////
//			// Add the control buttons to the SOUTH part 
//			// Move button
//			JPanel p = new JPanel();
//			JButton b = new JButton(REFRESHLABEL);
//			b.addActionListener(this);
//			p.add(b);
//			b = new JButton(MOVELABEL);
//			b.addActionListener(this);
//			p.add(b);
//			b = new JButton(CLONELABEL);
//			b.addActionListener(this);
//			p.add(b);
//			// Exit button
//			b = new JButton(EXITLABEL);
//			b.addActionListener(this);
//			p.add(b);
//			main.add(p);
//			
//			getContentPane().add(main, BorderLayout.CENTER);
		}
}
