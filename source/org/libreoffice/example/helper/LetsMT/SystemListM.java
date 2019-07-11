package org.libreoffice.example.helper.LetsMT;

public class SystemListM {
	 private SystemSMT[] System;

	    public SystemSMT[] getSystem ()
	    {
	        return System;
	    }

	    public void setSystem (SystemSMT[] System)
	    {
	        this.System = System;
	    }

	    @Override
	    public String toString()
	    {
	        return "SystemListM [SystemSMT = "+System+"]";
	    }
}