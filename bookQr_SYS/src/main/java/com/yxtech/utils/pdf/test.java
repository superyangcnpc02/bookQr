package com.yxtech.utils.pdf;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author cuihao
 * @create 2017-08-09-17:34
 */

public class test {
    public static void main(String[] args) throws IOException, DocumentException{

        System.out.println("ImageSequence");

        //Step 1—Create a Document.
        Document document = new Document(PageSize.A4,20,20,20,20);

        try {

            //Step 2—Get a PdfWriter instance.
            PdfWriter.getInstance(document, new FileOutputStream("F:\\media\\cmm\\test.pdf"));

            // step 3: we open the document
            document.open();

//            // step 4:
//            document.add(new Paragraph("1st image"));
//            Image jpg = Image.getInstance("F:\\media\\cmm\\erweima.png");
//
//            jpg.scaleAbsolute(220,220);//自定义大小,设置220,实际生成是240,中间倍数是1.09
//            //jpg.scalePercent(50);//依照比例缩放
//
//            document.add(jpg);
//            document.add(new Paragraph("2nd image"));
//            Image gif= Image.getInstance("F:\\media\\cmm\\snow.jpg");
//
//            gif.scaleAbsolute(200,100);//自定义大小
//
//            document.add(gif);
            PdfPTable table = new PdfPTable(3);
            table.addCell("1");
            table.addCell("2");
            table.addCell("3");
            table.addCell("4");
            table.addCell("5");
            table.addCell("6");
            document.add(table);

            table = new PdfPTable(3);
            table.addCell("7");
            table.addCell("8");
            table.addCell("9");



            document.add(table);

        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch(IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // step 5: we close the document
        document.close();
    }
}
