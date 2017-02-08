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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Gui for the MainWindow
 * 
 * @author wolf.posdorfer
 */
public class MainWindowGui
{

    private JFrame _frame;
    private JTextField _inputfield;
    private JTextField _outputfield;
    private JButton _inputselect;
    private JLabel _progresslabel;

    private JButton _startButton;

    public MainWindowGui()
    {
        _frame = new JFrame("QTI to PDF");

        _inputfield = new JTextField(40);
        _outputfield = new JTextField(10);
        _outputfield.setText("QTItoPDF.pdf");

        _inputselect = new JButton("Select");

        _progresslabel = new JLabel("Progress: ");

        JPanel panel = new JPanel(new BorderLayout());
        _frame.add(panel);

        JPanel inputpanel = new JPanel();
        inputpanel.setBorder(new TitledBorder("Select Input-Folder"));
        inputpanel.add(_inputfield);
        inputpanel.add(_inputselect);

        JPanel outputpanel = new JPanel();
        outputpanel.setBorder(new TitledBorder("Output-File"));
        outputpanel.add(_outputfield);

        _startButton = new JButton("Convert");
        JPanel southpanel = new JPanel(new BorderLayout());
        southpanel.add(_progresslabel, BorderLayout.CENTER);
        southpanel.add(_startButton, BorderLayout.EAST);

        _frame.add(inputpanel, BorderLayout.NORTH);
        _frame.add(outputpanel, BorderLayout.CENTER);
        _frame.add(southpanel, BorderLayout.SOUTH);

        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void show()
    {
        _frame.pack();
        _frame.setLocationRelativeTo(null);
        _frame.setVisible(true);
    }

    public JFrame getFrame()
    {
        return _frame;
    }

    public JTextField getInputfield()
    {
        return _inputfield;
    }

    public JButton getInputselect()
    {
        return _inputselect;
    }

    public JTextField getOutputfield()
    {
        return _outputfield;
    }

    public JLabel getProgresslabel()
    {
        return _progresslabel;
    }

    public JButton getStartButton()
    {
        return _startButton;
    }

}
