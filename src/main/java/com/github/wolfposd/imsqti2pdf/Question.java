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

import java.util.HashMap;

/**
 * Container to hold information about a QTI-question
 * 
 * @author wolf.posdorfer
 * 
 */
public class Question
{
    public String questiontext;

    /** SINGLE OR MULTIPLE */
    public String type;

    /** SINGLE OR MULTIPLE */
    public String outcomeScore;

    public HashMap<String, String> answers;
    public HashMap<String, Double> points;

    public int maxAnswers;

    /** Identifier or String */
    public BaseType basetype;

    public Question()
    {
        answers = new HashMap<String, String>();
        points = new HashMap<String, Double>();
    }

    @Override
    public String toString()
    {
        return "Question [questiontext=" + questiontext + ", type=" + type + ", answers=" + answers + ", points="
                + points + ", maxAnswers=" + maxAnswers + ", basetype=" + basetype + "]";
    }

    public enum BaseType
    {
        IDENTIFIER, STRING, UNKNOWN;

        /**
         * Returns the {@link BaseType} of the given String
         * 
         * @param s
         *            string to check
         * @return parsed {@link BaseType}
         */
        public static BaseType getBaseType(String s)
        {
            s = s.toLowerCase();
            if (s.equals("string"))
            {
                return STRING;
            }
            else if (s.equals("identifier"))
            {
                return IDENTIFIER;
            }
            else
            {
                return UNKNOWN;
            }
        }
    }

    public double getPointsPerAnswer()
    {
        double result = 0;

        for (double d : points.values())
        {
            if (d > result)
            {
                result = d;
            }
        }

        return result;
    }
}
