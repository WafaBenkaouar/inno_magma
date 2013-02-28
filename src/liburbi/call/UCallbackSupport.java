/*! \file UCallbackSupport.java
 *******************************************************************************

 File: UCallbackSupport.java
 Implementation of the UCallbackSupport class.

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

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * UCallbackSupport implements the UCallbackList interface
 * <p>
 * <p>
 * @author Bastien Saltel
 * @see UCallbackList
 * @see UCallbackListener
 */
public class		UCallbackSupport implements UCallbackList
{
    /** The container of the registered callbacks. */
	private Hashtable		tab;

    /**
     * Constructor for UCallbackSupport.
	 */
	public UCallbackSupport()
	{
		tab = new Hashtable();
	}

    /**
     * Removes all the registered callbacks.
     * <p>
	 */
	public void		removeAll()
	{
		tab.clear();
	}

    /**
     * Removes the registered callback associated with the given tag.
     * <p>
     * @param tag The tag associated with the removed callback.
	 */
	public void		removeUCallbackListener(String tag)
	{
		tab.remove(tag);
	}

    /**
     * Registers a callback associated with the given tag.
     * <p>
     * @param listener The registered callback.
     * @param tag The tag associated with the registered callback.
	 */
	public void	addUCallbackListener(UCallbackListener listener,
									 String tag)
	{
		tab.put(tag, listener);
	}

    /**
     * Calls the actionPerformed method of all the registered callbacks.
     * <p>
	 */
	public void		notifyUCallbackListeners()
	{
		for (Enumeration e = tab.elements(); e.hasMoreElements(); )
			{
				UCallbackListener listener = (UCallbackListener)tab.get(e.nextElement());

				listener.actionPerformed(null);
			}
	}

    /**
     * Calls the actionPerformed method of a registered callback
	 * associated with the given event.
     * <p>
     * @param event The event associated with the registered callback.
	 */			 
	public void		notifyUCallbackListener(URBIEvent event)
	{
		UCallbackListener listener=null;
		try {
			listener = (UCallbackListener)tab.get(event.getTag());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		if (listener != null)
			{
				listener.actionPerformed(event);
			}
		else if (event.getTimestamp() != 0)
			System.out.println("[" + event.getTimestamp() + ":" + event.getTag() + "] " + event.getCmd());
	}

    /**
     * Returns the callback associated with the given tag.
     * <p>
     * @param tag The tag associated with the callback.
	 * @return The callback or null
	 */
	public UCallbackListener		getUCallbackListener(String tag)
	{
		return (UCallbackListener)tab.get(tag);
	}
}
