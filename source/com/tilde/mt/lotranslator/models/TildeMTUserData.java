package com.tilde.mt.lotranslator.models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class TildeMTUserData {
	@SerializedName("identifier")
	public String Identifier;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("userGroups")
    public ArrayList<TildeMTUserGroup> Groups;
	
	@SerializedName("activeGroup")
    public String ActiveGroup;
	
	@Override
	public String toString() {
		return String.format("User Data [id: %s, activeGroup: %s]", Identifier, ActiveGroup);
	}
}
