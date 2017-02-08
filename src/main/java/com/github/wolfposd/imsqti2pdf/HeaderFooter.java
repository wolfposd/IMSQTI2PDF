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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * EventHelper which creates a Header and Footer for every page
 * 
 * @author wolf.posdorfer
 * 
 */
class HeaderFooter extends PdfPageEventHelper
{

    private Phrase _sumSymbol;
    private final int _maximumPageNumber;

    public HeaderFooter(int maximumPageNumber)
    {
        _maximumPageNumber = maximumPageNumber;

        Chunk c = new Chunk("" + (char) 229);
        c.setFont(new Font(FontFamily.SYMBOL, 28));
        _sumSymbol = new Phrase(c);
    }

    public void onEndPage(PdfWriter writer, Document document)
    {
        Rectangle rect = writer.getBoxSize("art");

        if (rect != null)
        {
            sumSymboltoFooter(writer, rect);

            pageNumberFooter(writer, rect);
        }
    }

    private void pageNumberFooter(PdfWriter writer, Rectangle rect)
    {
        Chunk c = new Chunk(String.format(LocaleStrings.getString("page"), writer.getPageNumber(), _maximumPageNumber));
        c.setFont(new Font(FontFamily.HELVETICA, 10));
        Phrase pagephrase = new Phrase(c);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, pagephrase, rect.getRight() - 60,
                rect.getBottom() - 30, 0);
    }

    private void sumSymboltoFooter(PdfWriter writer, Rectangle rect)
    {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, _sumSymbol, rect.getLeft() - 10,
                rect.getBottom() - 30, 0);
    }

    @SuppressWarnings("unused")
    private void headerText(PdfWriter writer, Rectangle rect)
    {
        float center = (rect.getLeft() + rect.getRight()) / 2;

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Header Text"), center, rect.getTop(), 0);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("01.01.1970"), center, rect.getTop() - 16,
                0);
    }
}