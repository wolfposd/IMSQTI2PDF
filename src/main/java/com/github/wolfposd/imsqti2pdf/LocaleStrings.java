/*
 * MIT License
 *
 * IMS QTI to PDF
 * Copyright (c) 2017 wolfposd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
