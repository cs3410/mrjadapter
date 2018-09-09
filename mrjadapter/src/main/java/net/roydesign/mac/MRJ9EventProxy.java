/*******************************************************************************

	File:		MRJ9EventProxy.java
	Author:		Danny Qiu <cs3410-staff@cornell.edu>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://mrjadapter.dev.java.net/license.html>

	Change History:
	09/09/18	Created this file - Danny

*******************************************************************************/

package net.roydesign.mac;

import net.roydesign.event.ApplicationEvent;

import java.awt.Desktop;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitHandler;
import java.io.File;

/**
 * Implementation of an application event proxy which supports Mac OS
 * virtual machines that use com.apple.eawt as their integration mechanism.
 * This corresponds to MRJ 9 and above which implements Java 9 and above.
 * @see MRJEventProxy
 *
 * @version MRJ Adapter 9
 */
class MRJ9EventProxy extends MRJEventProxy
{
	/**
	 * The single instance of MRJ9EventProxy.
	 */
	private static MRJ9EventProxy instance;

	/**
	 * The <code>java.awt.Desktop</code> that we get the
	 * native MRJ events from.
	 */
	private Desktop desktop;

	/**
	 * Get the single instance of this class.
	 * @return the single instance of <code>MRJ9EventProxy</code>
	 */
	public static MRJ9EventProxy getInstance()
	{
		if (instance == null)
			instance = new MRJ9EventProxy();
		return instance;
	}

	/**
	 * Construct an MRJ 9 event proxy
	 */
	private MRJ9EventProxy()
	{
		desktop = Desktop.getDesktop();
		Handler handler = new Handler();
		desktop.setAboutHandler(handler);
		desktop.setOpenFileHandler(handler);
		desktop.setPreferencesHandler(handler);
		desktop.setPrintFileHandler(handler);
		desktop.setQuitHandler(handler);
	}

	/**
	 * Get whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method always returns false.
	 * @return whether the Preferences menu item is enabled
	 */
	public boolean isPreferencesEnabled()
	{
		// Java 9 is only supported on modern Mac OS X with preferences enabled
		return true;
	}

	/**
	 * Set whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method does nothing.
	 * @param enabled whether the menu item is enabled
	 */
	public void setPreferencesEnabled(boolean enabled)
	{
		// Java 9 is only supported on modern Mac OS X with preferences enabled
	}

	/**
	 * This class implements the listener that handles native events
	 * which it then relays to MRJ Adapter using our unified interface.
	 */
	private class Handler implements AboutHandler, OpenFilesHandler, PreferencesHandler, PrintFilesHandler, QuitHandler
	{
		public void handleAbout(java.awt.desktop.AboutEvent e)
		{
			fireMenuEvent(ApplicationEvent.ABOUT);
		}

		public void handlePreferences(java.awt.desktop.PreferencesEvent e)
		{
			fireMenuEvent(ApplicationEvent.PREFERENCES);
		}

		public void handleQuitRequestWith(java.awt.desktop.QuitEvent e, java.awt.desktop.QuitResponse resp)
		{
			fireApplicationEvent(ApplicationEvent.QUIT_APPLICATION);
			resp.cancelQuit();
		}

		public void openFiles(java.awt.desktop.OpenFilesEvent e)
		{
			for (File f : e.getFiles()) {
				fireDocumentEvent(ApplicationEvent.OPEN_DOCUMENT, f);
			}
		}

		public void printFiles(java.awt.desktop.PrintFilesEvent e)
		{
			for (File f : e.getFiles()) {
				fireDocumentEvent(ApplicationEvent.PRINT_DOCUMENT, f);
			}
		}
	}
}
