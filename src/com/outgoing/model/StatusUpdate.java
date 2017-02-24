package com.outgoing.model;

import org.json.JSONArray;

/**
 * A value object use to transfer information about changed to my 
 * all observable objects.
 * 
 * @author ted.zhang
 */

public class StatusUpdate{
    
    /**
     * Observable event types
     */
    public enum Updates {
    	check_version_request,
    	connection_fail,
    	login_request,
    	get_user_info_request,
    	get_event_request,
    	get_event_info_request,
    	update_user_location_request,
    	update_user_info_request,
    	location_remark,
    	add_marker_request,
    	get_marker_request
    }

    private Updates updateType = null;
    
    /*
     * The values that will be tranfered.
     */
    private JSONArray response = null;
    
    /**
     * Empty constructor to conform with JavaBean requirements.
     */
    public StatusUpdate(){}
    
    /**
     * The constructor that allows us to specify the update type and any 
     * relevant information.
     * 
     * @param updateType The type of update that has occurred.
     * @param payload Any relevant information that we would like to pass at 
     * the same time.
     */
    public StatusUpdate(Updates updateType, JSONArray response){
        this.updateType = updateType;
        this.response = response;
    }
    
    /**
     * Sets the type of update that has occurred.
     * 
     * @param upateType The type of update that has occurred.
     */
    public void setUpdateType(Updates upateType){
        this.updateType = upateType;
    }
    
    /**
     * Gets the type of update that has occurred.
     * 
     * @return the Type of update <code>Updates</code> that has occurred.
     */
    public Updates getUpdateType(){
        return this.updateType;
    }
    
    /**
     * Sets any information considered relevant to this update.
     * 
     * @param payload Any relevant information that we would like to pass at
     * the same time.
     */
    public void setPayload(JSONArray response){
        this.response = response;
    }
    
    /**
     * Gets any information considered relevant to this update.
     * 
     * @return Any relevant information that we would like to pass at the same
     * time.
     */
    public JSONArray getPayload(){
        return this.response;
    }
}
