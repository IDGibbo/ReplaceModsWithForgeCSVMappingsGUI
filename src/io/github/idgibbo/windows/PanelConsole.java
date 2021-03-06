package io.github.idgibbo.windows;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.io.*;

public class PanelConsole extends WindowAdapter implements WindowListener,  ActionListener, Runnable
{
	private JFrame frame;
	private JTextArea textArea;
	private Thread reader;
	private Thread reader2;
	private boolean quit;

	private final PipedInputStream pin=new PipedInputStream();
	private final PipedInputStream pin2=new PipedInputStream();
	
	private boolean showing = false;

	// TODO Fork (Save Console function)
	final JFileChooser fc = new JFileChooser();
	public void saveConsole() {

		final JFileChooser SaveConsole = new JFileChooser();
		SaveConsole.setApproveButtonText("Save");
		int actionDialog = SaveConsole.showOpenDialog(frame);
		if (actionDialog != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File fileName = new File(SaveConsole.getSelectedFile() + ".txt");
		BufferedWriter outFile = null;
		try {
			outFile = new BufferedWriter(new FileWriter(fileName));

			textArea.write(outFile);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (outFile != null) {
				try {
					outFile.close();
				} catch (IOException e) { }
			}
		}
	}

	public PanelConsole()
	{
		// Gives the systems theme TODO Fork
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }

		// create all components and add them
		frame=new JFrame("Output Console");
		Dimension frameSize=new Dimension(400, 300);
		int x=(int)(frameSize.width/2);
		int y=(int)(frameSize.height/2);
		frame.setBounds(x,y,frameSize.width,frameSize.height);
		textArea=new JTextArea();
		textArea.setFont(new Font("", Font.PLAIN, 12)); // TODO Fork
		textArea.setEditable(false);
		
		// auto scrolling
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JButton clearConsole=new JButton("Clear Console"); // TODO Fork
		JButton saveConsole=new JButton(new AbstractAction("<html><center>Save<br />CNSL</html>") { @Override public void actionPerformed(ActionEvent arg0) { saveConsole();}}); // TODO Fork (Save button)

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(textArea),BorderLayout.CENTER);
		frame.getContentPane().add(saveConsole ,BorderLayout.EAST); // TODO Fork (Save button)
		frame.getContentPane().add(clearConsole ,BorderLayout.SOUTH);
		frame.setVisible(showing);

		frame.addWindowListener(this);
		clearConsole.addActionListener(this);

		try
		{
			PipedOutputStream pout=new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout,true));
		}
		catch (java.io.IOException io)
		{
			textArea.append("Couldn't redirect STDOUT to this console\n"+io.getMessage());
		}
		catch (SecurityException se)
		{
			textArea.append("Couldn't redirect STDOUT to this console\n"+se.getMessage());
		}

		try
		{
			PipedOutputStream pout2=new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2,true));
		}
		catch (java.io.IOException io)
		{
			textArea.append("Couldn't redirect STDERR to this console\n"+io.getMessage());
		}
		catch (SecurityException se)
		{
			textArea.append("Couldn't redirect STDERR to this console\n"+se.getMessage());
		}

		quit=false; // signals the Threads that they should exit

		// Starting two separate threads to read from the PipedInputStreams
		reader=new Thread(this);
		reader.setDaemon(true);
		reader.start();
		reader2=new Thread(this);
		reader2.setDaemon(true);
		reader2.start();
	}
	
	public void quit() {
		this.notifyAll(); // stop all threads
		try { reader.join(1000);pin.close();   } catch (Exception e){}
		try { reader2.join(1000);pin2.close(); } catch (Exception e){}
		frame.dispose();
	}

	public synchronized void actionPerformed(ActionEvent evt)
	{
		textArea.setText("");
	}

	public synchronized void run()
	{
		try
		{
			while (Thread.currentThread()==reader)
			{
				try { this.wait(100);}catch(InterruptedException ie) {}
				if (pin.available()!=0)
				{
					String input=this.readLine(pin);
					textArea.append(input);
				}
				if (quit) return;
			}

			while (Thread.currentThread()==reader2)
			{
				try { this.wait(100);}catch(InterruptedException ie) {}
				if (pin2.available()!=0)
				{
					String input=this.readLine(pin2);
					textArea.append(input);
				}
				if (quit) return;
			}
		} catch (Exception e)
		{
			textArea.append("\nConsole reports an Internal error.");
			textArea.append("The error is: "+e);
		}


	}

	public synchronized String readLine(PipedInputStream in) throws IOException
	{
		String input="";
		do
		{
			int available=in.available();
			if (available==0) break;
			byte b[]=new byte[available];
			in.read(b);
			input=input+new String(b,0,b.length);
		}while( !input.endsWith("\n") &&  !input.endsWith("\r\n") && !quit);
		return input;
	}
	
	public void toggle() {
		showing = !showing;
		frame.setVisible(showing);
	}

}
