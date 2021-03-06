/*! \file UParser.java
 *******************************************************************************

 File: UParser.java
 Implementation of the UParser class.

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

package liburbi.parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Integer;

import liburbi.UClient;
import liburbi.call.URBIEvent;

/**
 * The UParser provides the basic operations that are required of a parser
 * of the URBI language designed to be used by URBI clients, storing a reference
 * to its associated
 * URBI client
 * @author Bastien Saltel
 * @see UParserFactory
 */
public class	UParser
{
    /** The default UParserFactory shared by all UParser instances. */
    private static final DefaultUParserFactory __DEFAULT_PARSER_FACTORY = new DefaultUParserFactory();

    /** The parser's UParserFactory. */
    protected DefaultUParserFactory _parserFactory_;

    /** The URBI event generated by the operations of the parser. */
	private URBIEvent		event;

    /** The URBI client associated with the parser. */
	private UClient	client;
 
    /**
     * Default constructor for UParser. Initializes event and client to null,
	 * _parserFactory_ to a shared instance of DefaultUParserFactory.
     * <p>
	 */
	public UParser()
	{
		_parserFactory_ = __DEFAULT_PARSER_FACTORY;
		this.event = null;
		this.client = null;
	}

    /**
     * Constructor for UParser. Initializes event to null,
	 * and _parserFactory_ to a shared instance of DefaultUParserFactory.
     * <p>
     * @param client The reference to the associated URBI client.
	 */
	public UParser(UClient client)
	{
		_parserFactory_ = __DEFAULT_PARSER_FACTORY;
		this.event = null;
		this.client = client;
	}

	/**
	 * Returns the event generated by the operations of the parser.
     * <p>
	 */
	public URBIEvent	getEvent()
	{
		return event;
	}

	/**
	 * Sets the event generated by the operations of the parser.
     * <p>
	 */
	public void		setEvent(URBIEvent event)
	{
		this.event = event;
	}

	/**
	 * Starts the main operation of the parser.
	 * <p>
	 */
	public void		parse()
	{
		int				endline = 0;
		Integer	size = null;
		String cmd = null;
		Integer	timestamp = null;
		String	tag = null;
		String type = null;
		Integer height = null;
		Integer width = null;
		Float	sampleRate = null;
		Integer sampleSizeInBits = null;
		Integer channels = null;
		String signed = null;
		String currentCmd = null;
		String binCmd = null;
		String header = null;

		while (client.getRecvBufferPosition() != 0)
			{
				endline = client.getRecvBuffer().indexOf("\n");
				if (endline == -1)
					return;
				currentCmd = client.getRecvBuffer().substring(0, endline);
				if (currentCmd.startsWith("[") == true)
					{
						int		end = currentCmd.indexOf("]");
						if (end == -1)
							{
								if (event == null)
									{
										System.out.println("Syntax error");
										System.exit(1);
									}
								else
									break;
							}
						header = currentCmd.substring(1, end);
						Pattern p1 = Pattern.compile("([0-9]+):([A-Za-z0-9_]+)");
						Pattern p2 = Pattern.compile("([0-9]+)");
						Matcher m1 = p1.matcher(header);
						Matcher m2 = p2.matcher(header);
						
						if (m1.matches() == true)
							{
								timestamp = new Integer(m1.group(1));
								tag = m1.group(2);
							}
						else if (m2.matches() == true)
							{
								timestamp = new Integer(m2.group(1));
								tag = null;
							}
						else
							{
								timestamp = null;
								tag = new String("notag");
							}
						cmd = currentCmd.substring(end + 1, currentCmd.length());
					}
				else
					{
						if (event == null)
							{
								timestamp = null;
								tag = new String("notag");
							}
						else
							break;
						cmd = currentCmd;
					}
				if (event == null)
					{
						event = new URBIEvent();
						event.setTag(tag);
						event.setTimestamp(timestamp);
					}
				int		mark = cmd.indexOf("BIN ");

				if (mark == -1)
					{
						event.setCmd(cmd);

						client.deleteRecvBuffer(0, endline + 1);
						client.call(event);
						event = null;
					}
				else
					{
						binCmd = cmd.substring(mark + 4, cmd.length());
						Pattern p1 = Pattern.compile(" *([0-9]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) *");
						Matcher m1 = p1.matcher(binCmd);
						Pattern p2 = Pattern.compile(" *([0-9]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) +([A-Za-z0-9_]+) *");
						Matcher m2 = p2.matcher(binCmd);
						Pattern p3 = Pattern.compile(" *([0-9]+) +([A-Za-z0-9_]+) *");
						Matcher m3 = p3.matcher(binCmd);
						Pattern p4 = Pattern.compile(" *([0-9]+) *");
						Matcher m4 = p4.matcher(binCmd);
						
						if (m1.matches() == true)
							{
								size = new Integer(m1.group(1));
								type = m1.group(2);
								if ((type.equals("wav") == false) && (type.equals("raw") == false))
									{
										width = new Integer(m1.group(3));
										height = new Integer(m1.group(4));
									}
								else
									{
										channels = new Integer(m1.group(3));
										sampleRate = new Float(m1.group(4));
										sampleSizeInBits = new Integer(m1.group(5));
										signed = m1.group(6);
									}
							}
						else if (m2.matches() == true)
							{
								size = new Integer(m2.group(1));
								type = m2.group(2);
								width = new Integer(m2.group(3));
								height = new Integer(m2.group(4));
							}
						else if (m3.matches() == true)
							{
								size = new Integer(m3.group(1));
								type = m3.group(2);
							}
						else if (m4.matches() == true)
							{
								size = new Integer(m4.group(1));
							}
						else
							{
								System.out.println("Syntax error");
								System.exit(1);
							}
						event.setSize(size);
						event.setType(type);
						event.setWidth(width);
						event.setHeight(height);
						event.setSampleRate(sampleRate);
						event.setNbOfBits(sampleSizeInBits);
						event.setNbOfChannels(channels);
						event.setSigned(signed);

						client.deleteRecvBuffer(0, endline + 1);
						break;
					}
			}
	}
}

