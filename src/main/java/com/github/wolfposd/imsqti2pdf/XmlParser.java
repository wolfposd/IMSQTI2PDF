/*
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.github.wolfposd.imsqti2pdf.Question.BaseType;

/**
 * XML-Parser for QTI-formatted files<br>
 * 
 * @see XmlParser#getAllQuestionsFromDirectory(String)
 * @author w.posdorfer
 * 
 */
public class XmlParser
{

    private Question _question = new Question();

    private XmlParser()
    {
    }

    /**
     * Returns a List of Questions
     * 
     * @param directory
     *            from where to load questions
     */
    public static ArrayList<Question> getAllQuestionsFromDirectory(String directory)
    {
        ArrayList<Question> questions = new ArrayList<Question>();

        File dir = new File(directory);

        if (dir.isDirectory())
        {
            for (File f : dir.listFiles())
            {
                if (f.isFile() && f.getName().endsWith(".xml") && !f.getName().contains("imsmanifest.xml"))
                {
                    XmlParser parser = new XmlParser();
                    parser.loadMainParts(f.getAbsolutePath());
                    parser.loadQuestionText(f.getAbsolutePath());
                    questions.add(parser._question);
                }
            }
        }

        return questions;
    }

    private void loadQuestionText(String file)
    {
        StringBuffer buffer = new StringBuffer();
        try
        {
            FileReader reader = new FileReader(file);
            BufferedReader bufread = new BufferedReader(reader);

            String line = "";
            while ((line = bufread.readLine()) != null)
            {
                buffer.append(line);
            }

            bufread.close();
            reader.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String text = buffer.toString();

        if (text.length() > 0)
        {
            final String itemBody = "itemBody";
            int indexofBeginning = text.indexOf(itemBody) + itemBody.length();
            int indexofEnd = text.indexOf("choiceInteraction");

            if (indexofEnd < indexofBeginning)
            {
                indexofEnd = text.indexOf("/itemBody");
            }
            String bodytext = text.substring(indexofBeginning + 1, indexofEnd - 1).trim();

            if (bodytext.contains("textEntryInteraction"))
            {
                bodytext = bodytext.substring(0, bodytext.indexOf("<textEntryInteraction responseI"));
            }
            _question.questiontext = getEncodedString(bodytext);
        }

    }

    private void loadMainParts(String dateiname)
    {
        try
        {
            XmlPullParser parser = new MXParser();
            FileReader reader = new FileReader(dateiname);
            parser.setInput(reader);

            processXML(parser);

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processXML(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        int eventType = xpp.getEventType();

        String name = "";

        do
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                name = xpp.getName();

                if (name.equals("responseDeclaration"))
                {
                    for(int i = 0; i < xpp.getAttributeCount();i++)
                    {
                        String attrName = xpp.getAttributeName(i);
                        switch(attrName.toLowerCase())
                        {
                            case "basetype":
                                _question.basetype = BaseType.getBaseType(xpp.getAttributeValue(i));
                                break;
                            case "cardinality":
                                _question.type = xpp.getAttributeValue(i);
                                break;
                        }
                        
                    }

                }
                else if (name.equals("mapEntry"))
                {
                    String key = xpp.getAttributeValue(0);
                    Double value = Double.parseDouble(xpp.getAttributeValue(1).toString());
                    _question.points.put(key, value);
                }
                else if (name.equals("outcomeDeclaration"))
                {
                    if (xpp.getAttributeValue(0).toString().equals("SCORE"))
                    {
                        _question.outcomeScore = xpp.getAttributeValue(1);
                    }
                }
                else if (name.equals("choiceInteraction"))
                {
                    _question.maxAnswers = findMaxChoices(xpp);
                }
                else if (name.equals("simpleChoice"))
                {
                    String key = xpp.getAttributeValue(0);
                    String value = xpp.nextText();
                    _question.answers.put(key, getEncodedString(value));
                }
            }
            else if (eventType == XmlPullParser.END_TAG)
            {
                // NOTHING ON END TAG
            }
            eventType = xpp.next();
        }
        while (eventType != XmlPullParser.END_DOCUMENT);

    }

    public int findMaxChoices(XmlPullParser xpp)
    {
        for (int i = 0; i < xpp.getAttributeCount(); i++)
        {
            String name = xpp.getAttributeName(i);

            if (name.equals("maxChoices"))
            {

                return Integer.parseInt(xpp.getAttributeValue(i));
            }
        }

        return -1;
    }

    private String getEncodedString(String s)
    {
        String result = s;

        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("windows"))
        {
            try
            {
                result = new String(s.getBytes(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
            }
        }

        return result;
    }
}
