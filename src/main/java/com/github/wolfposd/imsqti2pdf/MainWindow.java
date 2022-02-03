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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

/**
 * MainWindow Tool
 * 
 * @author wolf.posdorfer
 */
public class MainWindow
{
    private MainWindowGui _gui;

    public MainWindow()
    {
        _gui = new MainWindowGui();
        setUp();
        registerListeners();
        _gui.show();
    }

    private void setUp()
    {
        try
        {
            _gui.getInputfield().setText(new File(".").getCanonicalPath());
        }
        catch (IOException e)
        {
        }
    }

    private void inputSelectAction()
    {
        String nullstring = null;

        String inputtext = _gui.getInputfield().getText();
        JFileChooser chooser = new JFileChooser(inputtext.length() == 0 ? nullstring : inputtext);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(_gui.getFrame());

        File f = chooser.getSelectedFile();

        if (f != null && result == JFileChooser.APPROVE_OPTION)
        {
            _gui.getInputfield().setText(f.getAbsolutePath());
        }
        else
        {
            _gui.getInputfield().setText(inputtext);
        }
    }

    private void convertAction()
    {
        final String folder = _gui.getInputfield().getText();

        String output = folder + "/" + _gui.getOutputfield().getText();
        if (!output.endsWith(".pdf"))
        {
            output += ".pdf";
        }

        final String outputsolution = output.replace(".pdf", "_solution.pdf");

        _gui.getProgresslabel().setForeground(Color.BLACK);
        _gui.getProgresslabel().setText("Progress: Starting conversion");

        System.out.println("Reading XML from file " + folder);
        ArrayList<Question> qlist = XmlParser.getAllQuestionsFromDirectory(folder);
        System.out.println("Done reading XML");

        _gui.getProgresslabel().setText("Progress: Done Reading, Creating PDF");
        
        System.out.println("Setup page counter");
        PageCounter pageCounter = new PageCounter();

        try
        {
            System.out.println("Starting PDF conversion");
            final PDFCreator pdf = new PDFCreator();
            
            System.out.println("Creating pdf with fake page count");
            pdf.createPDF(output, qlist, false, pageCounter, 42, folder);
            System.out.println("Creating pdf with real page count");
            pdf.createPDF(output, qlist, false, null, pageCounter.getNumberPages(), folder);
            System.out.println("Creating pdf with real page count for solution");
            pdf.createPDF(outputsolution, qlist, true, null, pageCounter.getNumberPages(), folder);
            setSuccess();
            System.out.println("Done converting");
        }
        catch (Exception e)
        {
            System.err.println("Ecnountered Error");
            setError(e);
            e.printStackTrace();
        }

    }

    private void setSuccess()
    {
        _gui.getProgresslabel().setForeground(new Color(0, 100, 0));
        _gui.getProgresslabel().setText("Progress: Finished conversion");
    }

    private void setError(Exception e)
    {
        _gui.getProgresslabel().setForeground(Color.RED);
        _gui.getProgresslabel().setText("Error: " + e.getClass().getSimpleName() + " " + e.getMessage());
    }

    private void registerListeners()
    {
        _gui.getInputselect().addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                inputSelectAction();
            }
        });
        _gui.getStartButton().addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                new SwingWorker()
                {
                    public void inBackground()
                    {
                        convertAction();
                    }
                };
            }
        });
    }
}
