/**
 * This Object class extends Observable class and override the protected
 * setChanged() method for other classes to integrate into their own structure.
 * 
 * @author ted.zhang
 */

package com.outgoing.model;

import java.util.Observable;

public class ObservableObject extends Observable {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setChanged(){
        super.setChanged();
    } 
    
    /**
     * this function should be override since its parent don't handle exception
     */
    @Override
    public void notifyObservers(Object data){
    	try{
    		super.notifyObservers(data);
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
}

