/*! \file URBIEvent.java
 *******************************************************************************

 File: URBIEvent.java
 Implementation of the URBIEvent class.

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

package liburbi.call;

/**
 * The URBIEvent class contains all the data which constitute an URBI event
 * received by the server and used by a registered callback.
 * <p>
 * <p>
 * @author Bastien Saltel
 */

public class URBIEvent
{
    /** The timestamp of the received message. */
	private Integer		timestamp;

    /**
	 * The tag of the command which associates the URBI event to the registered
	 * of the received message.
	 */
	private String		tag;

    /** The size of the binary data. */
	private Integer	size;

	/** The type of the binary data. */
	private String	type;

	/** The height of the binary data if this one contains image data. */
	private Integer	height;

	/** The width of the binary data if this one contains image data. */
	private Integer	width;

	/** The received binary data. */
	private byte	binBuffer[];

    /** The received command. */
	private String	cmd;

	/** The sample rate of the binary data if this one contains audio data. */
	private Float	sampleRate;

	/** The sample size of the binary data if this one contains audio data. */
	private Integer	nbOfBits;

	/** The number of channels of the binary data if this one contains audio data. */
	private Integer	nbOfChannels;

	/** The signed value of the binary data if this one contains audio data. */
	private boolean	signed;

	/**
     * Constructor for URBIEvent.
     * <p>
	 */
	public URBIEvent()
	{
	}

	/**
	 * Sets the timestamp of the received message.
     * <p>
	 */
	public void	setTimestamp(Integer str)
	{
		timestamp = str;
	}

	/**
	 * Returns the timestamp of the received message.
     * <p>
	 */
	public int		getTimestamp()
	{
		if (timestamp != null)
			return timestamp.intValue();
		return 0;
	}

	/**
	 * Sets the tag of the command.
     * <p>
	 */
	public void		setTag(String str)
	{
		tag = str;
	}

	/**
	 * Returns the tag of the command.
     * <p>
	 */
	public String	getTag()
	{
		return tag;
	}

	/**
	 * Sets the size of the binary data.
     * <p>
	 */
	public void	setSize(Integer str)
	{
		size = str;
	}

	/**
	 * Returns the size of the binary data.
     * <p>
	 */
	public int		getSize()
	{
		if (size != null)
			return size.intValue();
		return 0;
	}

	/**
	 * Sets the type of the binary data.
     * <p>
	 */
	public void		setType(String str)
	{
		type = str;
	}

	/**
	 * Returns the type of the binary data.
     * <p>
	 */
	public String	getType()
	{
		return type;
	}

	/**
	 * Sets the height of the binary data.
     * <p>
	 */
	public void	setHeight(Integer str)
	{
		height = str;
	}

	/**
	 * Returns the height of the binary data.
     * <p>
	 */
	public int		getHeight()
	{
		if (height != null)
			return height.intValue();
		return 0;
	}

	/**
	 * Sets the width of the binary data.
     * <p>
	 */
	public void	setWidth(Integer str)
	{
		width = str;
	}

	/**
	 * Returns the width of the binary data.
     * <p>
	 */
	public int		getWidth()
	{
		if (width != null)
			return width.intValue();
		return 0;
	}

	/**
	 * Sets the binary data.
     * <p>
	 */
	public void		setBinBuffer(byte buf[])
	{
		binBuffer = buf;
	}

	/**
	 * Returns the binary data.
     * <p>
	 */
	public byte[]	getBinBuffer()
	{
		return binBuffer;
	}

	/**
	 * Sets the command.
     * <p>
	 */
	public void		setCmd(String str)
	{
		cmd = str;
	}

	/**
	 * Returns the command.
     * <p>
	 */
	public String	getCmd()
	{
		return cmd;
	}

	/**
	 * Sets the sample rate of the binary data.
     * <p>
	 */
	public void	setSampleRate(Float str)
	{
		sampleRate = str;
	}

	/**
	 * Returns the sample rate of the binary data.
     * <p>
	 */
	public float		getSampleRate()
	{
		if (sampleRate != null)
			return sampleRate.floatValue();
		return 0;
	}

	/**
	 * Sets the sample size of the binary data.
     * <p>
	 */
	public void	setNbOfBits(Integer str)
	{
		nbOfBits = str;
	}

	/**
	 * Returns the sample size of the binary data.
     * <p>
	 */
	public int		getNbOfBits()
	{
		if (nbOfBits != null)
			return nbOfBits.intValue();
		return 0;
	}

	/**
	 * Sets the number of channels of the binary data.
     * <p>
	 */
	public void	setNbOfChannels(Integer str)
	{
		nbOfChannels = str;
	}

	/**
	 * Returns the number of channels of the binary data.
     * <p>
	 */
	public int		getNbOfChannels()
	{
		if (nbOfChannels != null)
			return nbOfChannels.intValue();
		return 0;
	}

	/**
	 * Sets the signed value of the binary data.
     * <p>
	 */
	public void	setSigned(String str)
	{
		if ((str != null) && (str.equals("1") == true))
			signed = true;
		else
			signed = false;
	}

	/**
	 * Returns the signed value of the binary data.
     * <p>
	 */
	public boolean	getSigned()
	{
		return signed;
	}
}

