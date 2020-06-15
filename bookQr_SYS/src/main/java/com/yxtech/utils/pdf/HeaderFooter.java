package com.yxtech.utils.pdf;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

/**
 * @author cuihao
 * @create 2017-08-22-14:08
 */
public class HeaderFooter extends PdfPageEventHelper{
    private String footRemark;
    private Font FontChinese;
    public HeaderFooter(){}
    public HeaderFooter(String footRemark,Font FontChinese){
        this.footRemark = footRemark;
        this.FontChinese = FontChinese;
    }
    public void onEndPage (PdfWriter writer, Document document) {
//        Rectangle rect = writer.getBoxSize("art");
//        switch(writer.getPageNumber() % 2) {
//            case 0:
//                ColumnText.showTextAligned(writer.getDirectContent(),
//                        Element.ALIGN_RIGHT, new Phrase("even header"),
//                        rect.getRight(), rect.getTop(), 0);
//                break;
//            case 1:
//                ColumnText.showTextAligned(writer.getDirectContent(),
//                        Element.ALIGN_LEFT, new Phrase("odd header"),
//                        rect.getLeft(), rect.getTop(), 0);
//                break;
//        }

        //添加页尾
        float X = (document.right() + document.left())/2;//水平居中
        float y = 20;//距离页底距离10
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(footRemark+"  "+String.format("  第 %d 页", writer.getPageNumber()),FontChinese),
                X, y, 0);

    }
}
