/*! \file UClient.java
 *******************************************************************************

 File: UClient.java
 Implementation of the UClient class.

 This file is part of 
 liburbi
 (c) Bastien Saltel, 2004.

 Permission to use, copy, modify, and redistribute this software for
 non-commercial use is hereby granted.

 This software is provided "as is" without warranty of any kind,
 either expressed or implied, including but not limited to the
 implied warranties of fitness for a particular purpose.

 For more information, comments, bug reports: http://urbi.sourceforge.net

 **************************************************************************** */

package liburbi;

import liburbi.net.SocketClient;
import liburbi.parser.UParser;
import liburbi.call.UCallbackSupport;
import liburbi.call.URBIEvent;
import liburbi.call.UCallbackListener;

import java.nio.*;
import java.io.*;

//import javax.sound.sampled.*;

/**
 * The UClient class is the main class of the Java URBI library.
 * Instanciating an object of this class means to open a connection
 * to the URBI server and to process command.
 * <p>
 * <p>
 * @author Bastien Saltel
 */

public class	UClient
{
	private static long		timestamp;

    /** The default URBI port. */
	public static final	int	URBI_PORT = 54000;

    /** The default URBI buffer length. */
	public static final int	URBI_BUFLEN = 240000;		

	/** The hostname to connect to. */
	protected String	host;

	/** The port to connect to. */
	protected int	port;

	/** The buffer length. */
	protected int	buflen;

	/** The URBI parser associated with the URBI client. */
	private UParser	parser;

	/** The Receive buffer. */
	private StringBuffer		recvBuffer;

	/** The Receive buffer position. */
	private int					recvBufferPosition;

	/** The Send buffer. */
	private StringBuffer		sendBuffer;

	/** The Callback monitoring object. */
	protected UCallbackSupport		callbackSupport;

	/** The Socket associated with the URBI client. */
	protected		SocketClient	channel;

	/** A status variable indicating if the send buffer contains data to send . */
	protected		boolean requestSent = true;

    /**
     * Default constructor for UClient. Initializes buflen to URBI_BUFLEN, port to
	 * URBI_PORT (54000), allocates recvBuffer, sendBuffer, parser and callbackSupport.
	 * Finally connects to the given hostname at the URBI_PORT.
	 * <p>
	 * @param host  The hostname to connect to.
     */
	public UClient(String host)
	{
		timestamp = System.currentTimeMillis();
		this.host = host;
		this.buflen = URBI_BUFLEN;
		this.port = URBI_PORT;

		recvBuffer = new StringBuffer(buflen);

		sendBuffer = new StringBuffer(buflen);

		parser = new UParser(this);

		callbackSupport = new UCallbackSupport();

		channel = new SocketClient(this, parser);

		connect();

		channel.start();
	}

    /**
     * Constructor for UClient. Allocates recvBuffer, sendBuffer, parser and callbackSupport
	 * and connects to the given hostname at the given port.
	 * <p>
	 * @param host  The hostname to connect to.
	 * @param port  The port to connect to.
	 * @param buflen The length of the buffer.
     */
	public UClient(String host, int port, int buflen)
	{
		timestamp = System.currentTimeMillis();

		this.host = host;
		this.buflen = buflen;
		this.port = port;

		recvBuffer = new StringBuffer(buflen);

		sendBuffer = new StringBuffer(buflen);

		parser = new UParser(this);

		callbackSupport = new UCallbackSupport();

		channel = new SocketClient(this, parser);

		connect();

		channel.start();
	}

    /**
     * Opens a Socket channel connected to a remote host at the client's port
	 * <p>
	 */
	public void	connect()
	{
		try
            {
				channel.connect(this.host, this.port);
				System.out.println("Connected to " + host + ".");

			}
		catch (Exception e)
            {
				System.err.println("Exception while connecting : " + e.getMessage());
            }
	}

	/*
	 * Closes the Socket channel.
	 * <p>
	 */
	public void	disconnect()
    {
		try
            {
				channel.disconnect();
			}
		catch (Exception e)
            {
				System.err.println("Exception while disconnecting : " + e.getMessage());
			}
    }

	/*
	 * Sends a string through the Socket channel.
	 * <p>
	 * @param arg  The string representing the sending message.
	 * @param size The size of the sending message
	 */
	final synchronized public int		effectiveSend(String arg) throws IOException
	{
		ByteBuffer		byteBuffer = ByteBuffer.wrap(arg.getBytes());
		int		nbytes = channel.getChannel().write(byteBuffer);
		while (byteBuffer.remaining() != 0)
			nbytes += channel.getChannel().write(byteBuffer);
		return nbytes;
	}

	/*
	 * Sends a binary buffer through the Socket channel.
	 * <p>
	 * @param buf  The binary buffer.
	 */
	final synchronized public int		effectiveSend(byte[] buf, int len) throws IOException
	{
		ByteBuffer		byteBuffer = ByteBuffer.wrap(buf, 0, len);
		int		nbytes = channel.getChannel().write(byteBuffer);
		while (byteBuffer.remaining() != 0)
			nbytes += channel.getChannel().write(byteBuffer);
		return nbytes;
	}

	/*
	 * Sets the request value.
	 * <p>
	 */
	public void		setRequestSent(boolean i)
	{
		requestSent = i;
	}

	/**
     * Returns true if the client already has data to send.
     * <p>
     * @return True if the client already has data to send,
     *         false otherwise.
     */
	public boolean	getRequestSent()
	{
		return requestSent;
	}

	/*
	 * Appends a string in the send buffer.
	 * <p>
	 * @param arg  The string representing the sending message.
	 */
	final public int send(String arg) throws IOException
	{  
	  effectiveSend(arg);
	  return 0;
	}

	/*
	 * Appends a file in the send buffer.
	 * <p>
	 * @param file  The name of the file.
	 */
	final public int	sendFile(String filename) throws IOException
	{

		String	line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		requestSent = false;
		while ((line = reader.readLine()) != null)
			{
				effectiveSend(line);
			}
		return 0;
	}

	/*
	 * Returns the Receive buffer.
	 * <p>
	 */
	public StringBuffer		getRecvBuffer()
	{
		return recvBuffer;
	}

	/*
	 * Returns the Send buffer.
	 * <p>
	 */
	public StringBuffer		getSendBuffer()
	{
		return sendBuffer;
	}

	/*
	 * Receives a byte.
	 * <p>
	 * @param c  The byte.
	 */
	public   void		receive(String str)
	{
		recvBuffer.append(str);
		recvBufferPosition += str.length();
	}

	/*
	 * Removes the characters in a substring of the Receive buffer. The substring begins at the
	 * specified offset.
	 * <p>
	 * @param offset  The beginning index
	 * @param len  The len of the removed substring
	 */
	public void		deleteRecvBuffer(int offset, int len)
	{
		recvBuffer.delete(offset, len);
		recvBufferPosition -= len;
	}

	public void		deleteAllSendBuffer()
	{
		sendBuffer.delete(0, sendBuffer.length());
	}

	/*
	 * Begins the parsing process of the URBI parser.
	 * <p>
	 */
	public   void		parseCommand()
	{
		parser.parse();
	}

	/*
	 * Notifies a registered callback that an event has been received.
	 * <p>
	 * @param event  The received event.
	 */
	final  public void		call(URBIEvent event)
	{
		callbackSupport.notifyUCallbackListener(event);
	}

    /**
     * Registers a callback associated with the given tag.
     * <p>
     * @param listener The registered callback.
     * @param tag The tag associated with the registered callback.
	 */
	public void		setCallback(UCallbackListener listener, String tag)
	{
		callbackSupport.addUCallbackListener(listener, tag);
	}

    /**
     * Removes the registered callback associated with the given tag.
     * <p>
     * @param tag The tag associated with the removed callback.
	 */
	public void		deleteCallback(String tag)
	{
		callbackSupport.removeUCallbackListener(tag);
	}

	/*
	 * Returns the Receive buffer position.
	 * <p>
	 */
	public int		getRecvBufferPosition()
	{
		return recvBufferPosition;
	}

	/*
	 * Sets the Receive buffer position.
	 * <p>
	 */
	public void		setRecvBufferPosition(int i)
	{
		recvBufferPosition = i;
	}

	/*
	 * Returns the buffer length.
	 * <p>
	 */
	public int		getBuflen()
	{
		return buflen;
	}

	/*
	 * Sets the buffer length.
	 * <p>
	 */
	public void		setBuflen(int i)
	{
		buflen = i;
	}

    /**
     * Prints a string associated with a timestamp in milliseconds.
     * <p>
     * @param arg The string.
	 */
	public static void		printTime(String arg)
	{
		long diff = System.currentTimeMillis() - timestamp;
				
		System.out.println("[" + diff + "] " + arg);
	}

}

