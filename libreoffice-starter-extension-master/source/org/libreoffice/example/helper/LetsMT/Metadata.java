package org.libreoffice.example.helper.LetsMT;

public class Metadata {
    private String Value;

    private String Key;

    public String getValue ()
    {
        return Value;
    }

    public void setValue (String Value)
    {
        this.Value = Value;
    }

    public String getKey ()
    {
        return Key;
    }

    public void setKey (String Key)
    {
        this.Key = Key;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Value = "+Value+", Key = "+Key+"]";
    }
}
