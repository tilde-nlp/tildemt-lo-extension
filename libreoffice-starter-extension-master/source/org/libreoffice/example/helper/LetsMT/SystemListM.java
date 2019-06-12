package org.libreoffice.example.helper.LetsMT;

public class SystemListM {
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
	        return "SystemListM [System = "+System+"]";
	    }
}