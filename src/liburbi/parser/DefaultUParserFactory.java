/*! \file DefaultUParserFactory.java
 *******************************************************************************

 File: DefaultUParserFactory.java
 Implementation of the DefaultUParserFactory class.

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

import liburbi.UClient;

/**
 * DefaultUParserFactory implements the UParserFactory interface
 * <p>
 * <p>
 * @author Bastien Saltel
 * @see UParserFactory
 * @see UParser
 */
public class DefaultUParserFactory implements UParserFactory
{
    /**
     * Creates an URBI parser.
     * <p>
	 */
    public UParser createParser()
	{
		return new UParser();
	}

    /**
     * Creates an URBI parser associated with the given URBI client
     * <p>
     * @param client The URBI client to use.
     */
    public UParser createParser(UClient client)
    {
        return new UParser(client);
    }
}
