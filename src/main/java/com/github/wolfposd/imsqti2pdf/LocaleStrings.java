package com.github.wolfposd.imsqti2pdf;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class LocaleStrings {

    private static Properties _properties;

    private static final String BASENAME = "i18n/language_";
    private static final String ENDNAME = ".txt";

    static {
        InputStream instream = LocaleStrings.class.getClassLoader().getResourceAsStream(BASENAME + Locale.getDefault().getLanguage() + ENDNAME);

        Properties properties = new Properties();
        try 
        {
            properties.load(instream);
        } 
        catch (Exception e) 
        {
            System.err.println("Language: " + Locale.getDefault().getLanguage() + " not found, using English instead");
            try 
            {
                instream = LocaleStrings.class.getClassLoader().getResourceAsStream(BASENAME + "en" + ENDNAME);
                properties.load(instream);
            }
            catch(Exception e1)
            {
                System.err.println("Language: English not found, aborting");
                System.exit(1);
            }
        }

        _properties = properties;
    }

    public static String getString(String propertyName) 
    {
        if (_properties == null)
            return propertyName;
        else
            return _properties.getProperty(propertyName);
    }

}
