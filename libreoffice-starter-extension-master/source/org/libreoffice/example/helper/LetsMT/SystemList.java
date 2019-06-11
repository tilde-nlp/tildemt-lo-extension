package org.libreoffice.example.helper.LetsMT;

public class SystemList {
	 private System[] System;

	    public System[] getSystem ()
	    {
	        return System;
	    }

	    public void setSystem (System[] System)
	    {
	        this.System = System;
	    }

	    @Override
	    public String toString()
	    {
	        return "SystemList [System = "+System+"]";
	    }
}