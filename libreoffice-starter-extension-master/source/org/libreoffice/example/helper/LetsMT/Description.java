package org.libreoffice.example.helper.LetsMT;

public class Description {
    private String Language;

    private String Text;

    public String getLanguage ()
    {
        return Language;
    }

    public void setLanguage (String Language)
    {
        this.Language = Language;
    }

    public String getText ()
    {
        return Text;
    }

    public void setText (String Text)
    {
        this.Text = Text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Language = "+Language+", Text = "+Text+"]";
    }
}
