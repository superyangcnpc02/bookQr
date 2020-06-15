package com.yxtech.utils.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.service.FileResService;
import com.yxtech.utils.file.PathUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/30.
 */
public class Pdf {

    public static void createQrPdf(List<BookQr> list, String pdfPath, String imgRootPath, FileResService fileResService) throws IOException, DocumentException {
        //Step 1—Create a Document.
        Document document = new Document(PageSize.A4,20,20,20,20);
        //Step 2—Get a PdfWriter instance.
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        //Step 3—Open the Document.
        document.open();
        //Step 4—Add content.
        List<BookQr> bookList = new ArrayList<>();
        List<BookQr> pageList = new ArrayList<>();
        for (BookQr bookQr : list) {
            int qrType = bookQr.getQrType();
            if (qrType == 1) {
                bookList.add(bookQr);
            } else if(qrType == 2){
                pageList.add(bookQr);
            }
        }
        switch (bookList.size() + pageList.size()) {
            case 0:
                FontSelector selector = new FontSelector();
                selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
                selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
                Phrase phrase = selector.process("本书暂无二维码");
                document.add(phrase);
                break;
            default:
                addTable(document, bookList, fileResService, imgRootPath,"课件二维码");
                addTable(document, pageList, fileResService, imgRootPath,"扩展资源二维码");

        }


        //Step 5—Close the Document.
        document.close();
    }


    public static void addTable(Document document,List<BookQr> list,FileResService fileResService,String imgRootPath,String title) throws DocumentException, IOException {
        FontSelector selector = new FontSelector();
        selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
        selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
        Phrase phrase = selector.process(title);
        int size = list.size();
        if (size > 0) {
            document.add(phrase);
        }

        PdfPTable table = new PdfPTable(4);
        if(list.size()%4!=0){
            size=list.size()+4;
        }

        for (int i = 0; i < size; i++) {
            if(i>=list.size()){
                //去掉边框
                PdfPCell cellMain = new PdfPCell();
                cellMain.setBorderWidth(0f);
                table.addCell(cellMain);
                continue;
            }
            BookQr bookQr = list.get(i);
            PdfPTable qr = new PdfPTable(1);
            //获取二维码
            FileRes fileRes= fileResService.select4UUID(bookQr.getUrl());
            Image image = Image.getInstance(imgRootPath +fileRes.getPath());
            qr.addCell(image);
            //处理中文
//            FontSelector selector = new FontSelector();
//            selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
//            selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
             phrase = selector.process(bookQr.getName());
            //将文字添加到表格中
            qr.addCell(phrase);
            table.addCell(qr);
        }
        document.add(table);
    }


    public static void createGuaQrPdf(List<BookQr> list, String pdfPath, String imgRootPath, Book book) throws IOException, DocumentException {
        //Step 1—Create a Document.
//        Document document = new Document(new RectangleReadOnly(2500.0F, 2400.0F),5,5,10,10);
        Document document = new Document(PageSize.B3,110,110,30,720);
        //Step 2—Get a PdfWriter instance.
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        //添加页尾
        String message = "书名 : "+book.getName()+"    isbn : "+book.getIsbn()+"    产品号 : "+book.getCode();
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                BaseFont.NOT_EMBEDDED);
        Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
        HeaderFooter header=new HeaderFooter(message,FontChinese);
        writer.setPageEvent(header);
        //Step 3—Open the Document.
        document.open();
        //Step 4—Add content.
        List<BookQr> guaList = new ArrayList<>();
        for (BookQr bookQr : list) {
            int qrType = bookQr.getQrType();
            if(qrType == 3){
                guaList.add(bookQr);
            }
        }
        switch (guaList.size()) {
            case 0:
                FontSelector selector = new FontSelector();
                selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
                selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
                Phrase phrase = selector.process("本书暂无二维码");
                document.add(phrase);
                break;
            default:

                addSerialTable(document,writer, guaList, imgRootPath,"刮刮乐二维码");

        }


        //Step 5—Close the Document.
        document.close();
    }

    public static void addSerialTable(Document document,PdfWriter writer ,List<BookQr> list,String imgRootPath,String title) throws DocumentException, IOException {

        PdfPTable table = new PdfPTable(10);

        int size = list.size();
        for (int i = 1; i <= size; i++) {
            BookQr bookQr = list.get(i-1);
            //获取二维码
            Image image = Image.getInstance(imgRootPath + File.separator +bookQr.getName());
//            image.scaleAbsolute(49,49 );
            image.scalePercent(28f);//屏幕的分辨率是72dip,一般打印机的是300dip,在生成pdf时需要把分辨率提高。 72dip/300dip=0.24
            PdfPCell pdfPCell = new PdfPCell(image);
            pdfPCell.setPaddingTop(3.5f);
            pdfPCell.setPaddingBottom(3.5f);
            pdfPCell.setBorder(0);

            table.addCell(pdfPCell);

            if(i%100 == 0){
                //每1页pdf重新生成一个PdfPTable
                if (size > 0) {
                    document.add(table);
                    table= new PdfPTable(10);
                }
            }

        }
    }


    public static void createAuthQrPdf(List<BookQr> list, String pdfPath, String imgRootPath, Book book) throws IOException, DocumentException {
        //Step 1—Create a Document.
        Document document = new Document(new RectangleReadOnly(3250.0F, 2850.0F),5,5,10,10);
        //Step 2—Get a PdfWriter instance.
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        //Step 3—Open the Document.
        document.open();
        //Step 4—Add content.

        List<BookQr> authList = new ArrayList<>();
        for (BookQr bookQr : list) {
            int qrType = bookQr.getQrType();
            if(qrType == 4){
                authList.add(bookQr);
            }
        }
        switch (authList.size()) {
            case 0:
                FontSelector selector = new FontSelector();
                selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
                selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
                Phrase phrase = selector.process("本书暂无二维码");
                document.add(phrase);
                break;
            default:
                addAuthTable(document, authList, book, imgRootPath);

        }

        //Step 5—Close the Document.
        document.close();
    }


    public static void addAuthTable(Document document,List<BookQr> list,Book book,String imgRootPath) throws DocumentException, IOException {
        String message = "书名 : "+book.getName()+"    isbn : "+book.getIsbn()+"    产品号 : "+book.getCode();
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                BaseFont.NOT_EMBEDDED);
        Font FontChinese = new Font(bfChinese, 24, Font.NORMAL);
        Paragraph elements = new Paragraph(message,FontChinese);
        elements.setAlignment(Element.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(10);

        int size = list.size();
        for (int i = 1; i <= size; i++) {
            BookQr bookQr = list.get(i-1);
            PdfPTable qr = new PdfPTable(1);
            qr.setSpacingBefore(10f);
            qr.setSpacingAfter(10f);
            //获取二维码
            Image image = Image.getInstance(imgRootPath + File.separator +bookQr.getName());
            //image.scaleAbsolute(190,190);
            PdfPCell pdfPCell = new PdfPCell(image);
            pdfPCell.setPadding(5f);
            pdfPCell.setBorder(0);

            qr.addCell(pdfPCell);

            table.addCell(qr);

            if(i%100 == 0){
                //每1页pdf重新生成一个PdfPTable
                if (size > 0) {
                    document.add(table);
                    table= new PdfPTable(10);
                    //添加页尾
                    document.add(elements);
                }
            }

        }

    }

}
