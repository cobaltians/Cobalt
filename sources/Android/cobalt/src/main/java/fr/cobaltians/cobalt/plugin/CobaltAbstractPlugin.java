/**
 *
 * CobaltAbstractPlugin
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.plugin;

import java.util.Vector;

import org.json.JSONObject;

/**
 * 
 * @author Sébastien Famel
 */
public abstract class CobaltAbstractPlugin {

    /*******************************************************************************************************
     * MEMBERS
     *******************************************************************************************************/
	//TODO : Need to declare sInstance in each plugin
	//protected static CobaltAbstractPlugin sInstance;
	
	/**
	 * {@link Vector} containing all {@link CobaltPluginWebContainer}s which sent at least one message to this {@link CobaltAbstractPlugin} inherited singleton.
	 */
	protected Vector<CobaltPluginWebContainer> mWebContainerVector = new Vector<CobaltPluginWebContainer>();

	/*******************************************************************************
     * METHODS
     *******************************************************************************/
    
	/**
	 * Add the specified {@link CobaltPluginWebContainer} at the end of {@link #mWebContainerVector} if absent.
	 * @param webContainer the CobaltPluginWebContainer to add to {@link #mWebContainerVector}.
	 * @return true if webContainer was absent from {@link #mWebContainerVector}, false otherwise.
	 */
	public final boolean addWebContainer(CobaltPluginWebContainer webContainer) {
    	if (! mWebContainerVector.contains(webContainer)) {
    		mWebContainerVector.addElement(webContainer);
    		
    		return true;
    	}
    	
    	return false;
    }

    /*****************************************************************************************
     * ABSTRACT METHODS
     *****************************************************************************************/
    
    /**
     * Called when a {@link CobaltPluginWebContainer} has sent a message to this {@link CobaltAbstractPlugin} inherited singleton.
     * @param webContainer the {@link CobaltPluginWebContainer} which sent the message.
     * @param message the message sent by the {@link CobaltPluginWebContainer}.
     */
    public abstract void onMessage(CobaltPluginWebContainer webContainer, JSONObject message);
}
