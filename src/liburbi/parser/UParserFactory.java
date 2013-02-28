/*! \file UParserFactory.java
 *******************************************************************************

 File: UParserFactory.java
 Implementation of the UParserFactory class.

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

/*
 * The UParserFactory interface provides a means for the programmer to
 * provide his own URBI Parser implementations for use by all classes
 * derived from.
 * <p>
 * <p>
 * @author Bastien Saltel
 * @see DefaultUParserFactory
 */

public interface UParserFactory
{
    /**
     * Creates a the parser.
     * <p>
	 */
    public UParser createParser();

    /**
     * Creates the parser associated with the given URBI client
     * <p>
     * @param client The URBI client to use.
     */
    public UParser createParser(UClient client);
}
