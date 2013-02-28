/*! \file SocketClient.java
 *******************************************************************************

 File: SocketClient.java
 Implementation of the SocketClient class.

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

package liburbi.net;

import liburbi.parser.UParser;
import liburbi.UClient;
import liburbi.call.URBIEvent;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.nio.charset.*;
import java.net.*;
import java.util.*;

public class SocketClient implements Runnable
{
    /** The default port the client should connect to. */
    protected int defaultPort;

	protected boolean	isConnected;	

	/** The thread launched associated with the Socket channel. */
	private Thread thread;

	/** A status variable indicating if the Socket channel is currently threaded. */
	private boolean threaded = false;

    /** The URBI parser associated with the Socket channel. */
	private UParser	parser;

	/** The Socket selector associated with the socket. */
	private Selector		selector = null;

	/** The Socket channel associated with the socket. */
	private SocketChannel	channel;

	/** A status variable indicating if the socket tends to receive binary data. */
	private boolean	isBinary = false;

	/**
	 * The binary data retrieved from the Socket channel if this one has been
	 * in binary mode.
	 */
	private	byte	data[] = null;

 
	/** The binary data position. */ 
	private int		dataPosition;

	/** The URBI client associated with the socket. */
	private	UClient	client;

    /**
     * Default constructor for SocketClient.
	 * <p>
     */
    public SocketClient(UClient client, UParser	parser)
    {
		this.client = client;
		this.parser = parser;

		try
			{
				this.selector = SelectorProvider.provider().openSelector();
				this.channel = SelectorProvider.provider().openSocketChannel();
			}
		catch (IOException e)
			{
			}

        defaultPort = 0;
		isConnected = false;
		thread = new Thread(this);
		thread.setName("SocketClient");
    }

    /**
     * Opens a Socket connected to a remote host and originating from the current
	 *	host at a system assigned port.
     * <p>
     * @param add   The InetSocketAddress containing the name of the remote host
	 *	and the port.
     * @exception IOException If the socket could not be opened.
     */
    public void connect(InetSocketAddress add) throws IOException
    {
		channel.connect(add);
		channel.configureBlocking(false);
		isConnected = true;
    }

    /**
     * Opens a Socket connected to a remote host at the specified port and
     * originating from the current host at a system assigned port.
     * <p>
     * @param hostname  The name of the remote host.
     * @param port  The port to connect to on the remote host.
     * @exception IOException If the socket could not be opened.
     */
    public void connect(String hostname, int port) throws IOException
    {
		InetSocketAddress		add = new InetSocketAddress(hostname, port);

		channel.connect(add);
		channel.configureBlocking(false);
		isConnected = true;
		add = null;
    }

    /**
     * Opens a Socket connected to a remote host at the default port and
     * originating from the current host at a system assigned port.
     * <p>
     * @param hostname  The name of the remote host.
     * @exception IOException If the socket could not be opened.
     */
    public void connect(String hostname) throws IOException
    {
		InetSocketAddress		add = new InetSocketAddress(hostname, defaultPort);

		channel.connect(add);
		channel.configureBlocking(false);
		isConnected = true;
		add = null;
    }

    /**
     * Disconnects the socket connection.
     * You should call this method after you've finished using the class
     * instance and also before you call
     * again.  isConnected is set to false,
     * <p>
     */
    public void disconnect() throws IOException
    {
		channel.finishConnect();
		this.selector.close();
		isConnected = false;
    }

    /**
     * Returns true if the client is currently connected to a server.
     * <p>
     * @return True if the client is currently connected to a server,
     *         false otherwise.
     */
    public boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Starts the thread for this Socket channel.
     * <p>
	 */
    public void start()
    {
		int		priority;

        priority = Thread.currentThread().getPriority() + 1;
        if (priority > Thread.MAX_PRIORITY)
			priority = Thread.MAX_PRIORITY;
        thread.setPriority(priority);
        thread.setDaemon(true);
        thread.start();
        threaded = true;
	}

    /**
     * Sets the default port the SocketClient should connect to when a port
     * is not specified.  The defaultPort variable stores this value.  
	 * If never set, the default port is equal to zero.
     * <p>
     * @param port  The default port to set.
     */
    public void setDefaultPort(int port)
    {
        defaultPort = port;
    }

    /**
     * Returns the current value of the default port (stored in defaultPort).
     * <p>
     * @return The current value of the default port.
     */
    public int getDefaultPort()
    {
        return defaultPort;
    }

	/*
	 * Returns the Socket channel.
	 * <p>
	 */
	public SocketChannel	getChannel()
	{
		return channel;
	}

	/*
	 * Sets the Socket channel.
	 * <p>
	 */
	public void		setSocketChannel(SocketChannel socket)
	{
		channel = socket;
	}

	/**
	 * Starts the receive operation.
	 * <p>
	 */
	public void		receive()
	{
		ByteBuffer		byteBuffer = ByteBuffer.allocate(240000);

		try
			{
				channel.read(byteBuffer);
			}
		catch (IOException e)
			{
			}
		byteBuffer.flip();

		Charset	charset = Charset.forName("US-ASCII");
		CharsetDecoder	decoder = charset.newDecoder();
		CharBuffer		charBuffer = CharBuffer.allocate(240000);
		CoderResult cr = null;

		while (byteBuffer.remaining() > 0)
			{
				if (isBinary == false)
					{
						cr = decoder.decode(byteBuffer, charBuffer, false);
						charBuffer.flip();
						String	result = charBuffer.toString();
						client.receive(result);
						client.parseCommand();
					}
				if ((isBinary == true || cr.isMalformed() == true) && parser.getEvent() != null)
					{
						isBinary = true;
						URBIEvent event = parser.getEvent();
						if (data == null)
							{
								data = new byte[event.getSize()];
								dataPosition = 0;
								if (client.getRecvBufferPosition() > 0)
									{
										byte[]	tmp = client.getRecvBuffer().toString().getBytes();
										
										for (int i = 0; i < tmp.length; i++, dataPosition++)
											data[i] = tmp[i];
										client.deleteRecvBuffer(0, client.getRecvBufferPosition());
									}
							}
						int tmp;
						if (byteBuffer.remaining() < (data.length - dataPosition))
							tmp = byteBuffer.remaining();
						else
							tmp = data.length - dataPosition;
						byteBuffer.get(data, dataPosition, tmp);
						dataPosition += tmp;
						
						if (dataPosition == data.length)
							{
								event.setBinBuffer(data);
								client.call(event);
								dataPosition = 0;
								data = null;
								isBinary = false;
								parser.setEvent(null);
							}
					}
			}
		byteBuffer.clear();
	}


	/**
	 * The run method called by this Socket channel's thread.
     * <p>
	 */
    public void run()
    {
		try
			{
				channel.register(selector,  SelectionKey.OP_READ );

				while (selector.select() > 0)
					{
						Set		keys = selector.selectedKeys();
						Iterator i = keys.iterator();
						while (i.hasNext())
							{
								SelectionKey	key = (SelectionKey)i.next();
								i.remove();
								
								SocketChannel	currentChannel = (SocketChannel)key.channel();
								if (key.isConnectable())
									{
										if (currentChannel.isConnectionPending())
											{
												currentChannel.finishConnect();
											}
									}
								else if (key.isReadable())
									{
										receive();
									}
							}
					}
				threaded = false;
			}
		catch (IOException e)
			{
				threaded = false;
				e.printStackTrace();
			}
	}
}


