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

import java.util.ArrayList;

public class Startup
{

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            new MainWindow();
        }
        else
        {
            String folder = null;
            String output1 = null;
            String output2 = null;

            if (args.length == 3)
            {
                folder = args[0];
                output1 = args[1];
                output2 = args[2];
            }
            else
            {
                System.out.println("Not enough parameters given");
                System.out.println("java -jar imsqti2pdf.jar {path/to/questions} {output/output.pdf} {output/output_solutions.pdf}");
                System.out.println("java -jar imsqti2pdf.jar /home/Desktop/questions/ /home/Desktop/questions/questions.pdf /home/Desktop/questions/questions_solution.pdf");
                System.exit(1);
            }

            PDFCreator cret = new PDFCreator();
            try
            {
                System.out.println("Reading IMS QTI files");
                ArrayList<Question> qlist = XmlParser.getAllQuestionsFromDirectory(folder);

                System.out.println("Creating PDF. Step 1/3");
                PageCounter p = new PageCounter();
                cret.createPDF(output1, qlist, false, p, 200, folder); // to count pages, do a test run
                System.out.println("Creating PDF. Step 2/3");
                cret.createPDF(output1, qlist, false, null, p.getNumberPages(), folder);
                System.out.println("Creating PDF. Step 3/3");
                cret.createPDF(output2, qlist, true, null, p.getNumberPages(), folder);
                
                System.out.println("Conversion is done");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
