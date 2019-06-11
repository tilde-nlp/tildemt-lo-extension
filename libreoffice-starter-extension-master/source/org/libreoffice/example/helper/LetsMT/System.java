package org.libreoffice.example.helper.LetsMT;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.Metadata;

public class System {
	private SourceLanguage SourceLanguage;

    private Description Description;

    private Metadata[] Metadata;

    private Title Title;

    private String ID;

    private String Domain;

    private TargetLanguage TargetLanguage;

    public SourceLanguage getSourceLanguage ()
    {
        return SourceLanguage;
    }

    public void setSourceLanguage (SourceLanguage SourceLanguage)
    {
        this.SourceLanguage = SourceLanguage;
    }

    public Description getDescription ()
    {
        return Description;
    }

    public void setDescription (Description Description)
    {
        this.Description = Description;
    }

    public Metadata[] getMetadata ()
    {
        return Metadata;
    }

    public void setMetadata (Metadata[] Metadata)
    {
        this.Metadata = Metadata;
    }

    public Title getTitle ()
    {
        return Title;
    }

    public void setTitle (Title Title)
    {
        this.Title = Title;
    }

    public String getID ()
    {
        return ID;
    }

    public void setID (String ID)
    {
        this.ID = ID;
    }

    public String getDomain ()
    {
        return Domain;
    }

    public void setDomain (String Domain)
    {
        this.Domain = Domain;
    }

    public TargetLanguage getTargetLanguage ()
    {
        return TargetLanguage;
    }

    public void setTargetLanguage (TargetLanguage TargetLanguage)
    {
        this.TargetLanguage = TargetLanguage;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [SourceLanguage = "+SourceLanguage+", Description = "+Description+", Metadata = "+Metadata+", Title = "+Title+", ID = "+ID+", Domain = "+Domain+", TargetLanguage = "+TargetLanguage+"]";
    }
}
