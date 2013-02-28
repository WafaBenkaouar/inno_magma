/*! \file UCallbackListener.java
 *******************************************************************************

 File: UCallbackListener.java
 Implementation of the UCallbackListener class.

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
 * The UCallbackListener interface provides a means for the programmer to
 * provide his own callbacks implementations.
 * <p>
 * <p>
 * @author Bastien Saltel
 * @see UCallbackList
 * @see UCallbackSupport
 */

public interface		UCallbackListener
{
    /**
     * Performs the specific action of the callback.
     * <p>
     * @param event The event containing all the data needed for the
	 * action.
	 */
	public void		actionPerformed(URBIEvent event);
}
