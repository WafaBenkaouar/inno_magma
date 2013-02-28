/*! \file UCallbackList.java
 *******************************************************************************

 File: UCallbackList.java
 Implementation of the UCallbackList class.

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
 * The UCallbackList interface provides a means for the programmer to
 * provide his own callbacks container implementations.
 * <p>
 * <p>
 * @author Bastien Saltel
 */

public interface		UCallbackList
{
    /**
     * Removes all the registered callbacks.
     * <p>
	 */
	public void		removeAll();

    /**
     * Removes the registered callback associated with the given tag.
     * <p>
     * @param tag The tag associated with the removed callback.
	 */
	public void		removeUCallbackListener(String		tag);

    /**
     * Registers a callback associated with the given tag.
     * <p>
     * @param listener The registered callback.
     * @param tag The tag associated with the registered callback.
	 */
	public void		addUCallbackListener(UCallbackListener listener,
										 String tag);

    /**
     * Returns the callback associated with the given tag.
     * <p>
     * @param tag The tag associated with the callback.
	 * @return The callback or null.
	 */
	public UCallbackListener		getUCallbackListener(String tag);

    /**
     * Calls the actionPerformed method of all the registered callbacks.
     * <p>
	 */
	public void		notifyUCallbackListeners();

    /**
     * Calls the actionPerformed method of a registered callback
	 * associated with the given event.
     * <p>
     * @param event The event associated with the registered callback.
	 */
	public void		notifyUCallbackListener(URBIEvent event);
}
