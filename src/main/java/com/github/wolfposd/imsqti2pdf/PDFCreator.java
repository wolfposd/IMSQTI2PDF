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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.github.wolfposd.imsqti2pdf.Question.BaseType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Class to convert a folder containing QTI-XML-Files into a single PDF
 * 
 * @author wolf.posdorfer
 */
@SuppressWarnings("deprecation")
public class PDFCreator
{

    public static final int OVERALLFONTSIZE = 10;
    public static final int INDENTATION = 25;
    public static final int SPACING = 5;
    private static final Chunk SQUARE_CHUNK = new Chunk("" + (char) 111, new Font(FontFamily.ZAPFDINGBATS, 16));
    private String _inputFolder;

    public void createPDF(String outputFile, ArrayList<Question> qlist, boolean showCorrectAnswer,
            PageCounter pagecounter, int maximumPageNumber, String inputFolder) throws DocumentException, IOException
    {
        _inputFolder = inputFolder;
        Document document = new Document(PageSize.A4, 50, 50, 70, 50);
        PdfWriter pdfwriter = PdfWriter.getInstance(document, new FileOutputStream(outputFile));

        pdfwriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));

        pdfwriter.setPageEvent(new HeaderFooter(maximumPageNumber));

        if (pagecounter != null)
        {
            pdfwriter.setPageEvent(pagecounter);
        }

        document.open();

        Paragraph p = new Paragraph();
        // p.setSpacingBefore(SPACING);
        p.setSpacingAfter(SPACING);
        p.setIndentationLeft(INDENTATION);

        writeQuestions(p, document, showCorrectAnswer, qlist);

        document.close();

    }

    private void writeQuestions(Paragraph paragraph, Document document, boolean showCorrectAnswer,
            ArrayList<Question> qlist) throws DocumentException, IOException
    {
        for (int i = 0; i < qlist.size(); i++)
        {
            Question question = qlist.get(i);
            paragraph.clear();

            // addQuestionNumber(paragraph, i, qlist.size());

            addQuestionText(paragraph, question, i);

            addAnswerTexts(paragraph, showCorrectAnswer, question);

            fixFonts(paragraph);

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setKeepTogether(true);

            PdfPCell cell = new PdfPCell();
            cell.addElement(paragraph);
            cell.setBorderColor(BaseColor.WHITE);
            cell.setBorder(PdfPCell.NO_BORDER);

            table.addCell(cell);

            document.add(table);
        }

    }

    private Chunk getQuestionNumberChunk(Font f, int number)
    {
        Chunk c = new Chunk((number + 1) + ". ");
        Font ff = new Font(f);
        ff.setStyle(Font.BOLD);
        c.setFont(ff);
        return c;
    }

    private Chunk getPointsChunk(Font f, Question question)
    {
        int points = (int) question.getPointsPerAnswer();
        String punkteText = LocaleStrings.getString(points == 1 ?"point":"points");

        String text;
        if (question.basetype == BaseType.STRING)
        {
            text = " " + LocaleStrings.getString("maximum"); //(maximal %d %s)
            text = String.format(text, points, punkteText);
        }
        else
        {
            if (question.type.equalsIgnoreCase("single"))
            {
                text = " "+ LocaleStrings.getString("correctAnswer"); //" (1 richtige Antwort";

                if (points == 1)
                {
                    text += ")";
                }
                else
                {
                    text += ", %d %s)";
                    text = String.format(text, points, punkteText);
                }
            }
            else
            { // MUTLIPLE
                text = " "+ LocaleStrings.getString("perCorrectAnswer"); //(%d %s pro richtige Antwort)";
                text = String.format(text, points, punkteText);
            }
        }

        Chunk c = new Chunk(text);
        Font ff = new Font(f);
        ff.setSize(OVERALLFONTSIZE);
        c.setFont(ff);
        return c;
    }

    private void addQuestionText(Paragraph paragraph, Question question, int questionnumber) throws IOException
    {
        String fixedFonts = question.questiontext.replace("font-size: 12pt", "font-size: 10pt");

        fixedFonts = fixedFonts.replace("face=\"courier new\"", "face=\"Courier\"");

        fixedFonts = fixedFonts.replace("src=\"media/", getPathToMedia());

        ArrayList<Element> htmllist = (ArrayList<Element>) HTMLWorker.parseToList(new StringReader(fixedFonts), null);

        ArrayList<Paragraph> codeParagraphs = new ArrayList<Paragraph>();

        for (int i = 0; i < htmllist.size(); i++)
        {
            Element e = htmllist.get(i);
            if (e instanceof Paragraph)
            {
                Paragraph p = (Paragraph) e;
                if (i == 0)
                {
                    p.setIndentationLeft(INDENTATION);
                    p.getFont().setSize(OVERALLFONTSIZE);
                    p.add(0, getQuestionNumberChunk(p.getFont(), questionnumber));
                    p.add(getPointsChunk(p.getFont(), question));
                    paragraph.add(p);
                }
                else
                {
                    codeParagraphs.add(p);
                }
            }
        }
        if (codeParagraphs.size() > 0)
        {
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(90);
            table.setKeepTogether(true);

            Paragraph codeParagraph = new Paragraph();

            for (Paragraph p : codeParagraphs)
            {
                p.setIndentationLeft(INDENTATION);
                p.getFont().setSize(OVERALLFONTSIZE);
                codeParagraph.add(p);
                fixFonts(p);
            }
            codeParagraph.add(Chunk.NEWLINE);

            PdfPCell cell = new PdfPCell();
            cell.addElement(codeParagraph);
            cell.setBorderColor(BaseColor.BLACK);
            table.addCell(cell);

            paragraph.add(Chunk.NEWLINE);
            paragraph.add(table);
        }
    }

    private void addAnswerTexts(Paragraph paragraph, boolean showCorrectAnswer, Question question)
    {
        if (question.basetype == BaseType.IDENTIFIER)
        {
            List list = new List(false, 20);

            list.setListSymbol(SQUARE_CHUNK);
            for (String key : question.answers.keySet())
            {
                String answer = question.answers.get(key);

                Double points = question.points.get(key);
                points = points == null ? 0 : points;
                if (showCorrectAnswer)
                {
                    ListItem item = new ListItem(answer);
                    item.getFont().setSize(OVERALLFONTSIZE);

                    if (points > 0)
                    {
                        item.getFont().setColor(39, 158, 35);
                    }
                    else
                    {
                        // item.getFont().setColor(255, 0, 0);
                    }
                    list.add(item);
                }
                else
                {
                    ListItem item = new ListItem(answer);
                    item.getFont().setSize(OVERALLFONTSIZE);
                    list.add(item);
                }
            }

            paragraph.add(Chunk.NEWLINE);
            paragraph.add(list);
        }
        else if (question.basetype == BaseType.STRING)
        {
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(Chunk.NEWLINE);

            if (showCorrectAnswer)
            {
                String key = question.points.keySet().iterator().next();
                Double points = question.points.get(key);

                paragraph.add(new Phrase(getXes(points) + " " + LocaleStrings.getString("answer") + " " + key));
            }
            else
            {
                paragraph.add(new Phrase(LocaleStrings.getString("answerLine")));
            }
        }
    }

    /**
     * Returns a String containung points * "X"
     * 
     * @param points
     *            amount of "X"
     * @return
     */
    private String getXes(Double points)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < points; i++)
        {
            builder.append("X");
        }

        return builder.toString();
    }

    /**
     * @return src="/Users/..../questionfolder/media/
     */
    private String getPathToMedia()
    {
        return "src=\"" + _inputFolder + "/media/";
    }

    private void fixFonts(Paragraph para)
    {
        for (Element element : para)
        {
            if (element instanceof Paragraph)
            {
                Paragraph p = (Paragraph) element;
                p.getFont().setSize(OVERALLFONTSIZE);
                fixFonts(p);
            }
            else if (element instanceof Chunk)
            {
                Chunk c = (Chunk) element;
                c.getFont().setSize(OVERALLFONTSIZE);
            }
            else if (element instanceof List)
            {
            }
            else if (element instanceof PdfPTable)
            {
                PdfPTable table = (PdfPTable) element;
                for (Chunk c : table.getChunks())
                {
                    c.getFont().setSize(OVERALLFONTSIZE);
                }
            }
            else
            {
                // System.out.println(element);
            }
        }
    }

}
