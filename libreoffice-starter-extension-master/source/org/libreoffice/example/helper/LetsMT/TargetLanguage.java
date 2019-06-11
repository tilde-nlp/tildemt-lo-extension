package org.libreoffice.example.helper.LetsMT;

public class TargetLanguage {
	 private String Code;

    private Name Name;

    public String getCode ()
    {
        return Code;
    }

    public void setCode (String Code)
    {
        this.Code = Code;
    }

    public Name getName ()
    {
        return Name;
    }

    public void setName (Name Name)
    {
        this.Name = Name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Code = "+Code+", Name = "+Name+"]";
    }
}
