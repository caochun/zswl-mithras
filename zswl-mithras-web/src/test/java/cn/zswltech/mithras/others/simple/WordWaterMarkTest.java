package cn.zswltech.mithras.others.simple;

import com.aspose.words.Shape;
import com.aspose.words.*;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class WordWaterMarkTest {
    //文件路径
    public static String filePath = "C:\\Users\\sunshine\\Desktop\\工作\\waterMark\\";

    static {
        try {
            InputStream is = new ClassPathResource("/license.xml").getInputStream();
            License aposeLic = new License();
            aposeLic.setLicense(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            //1、给docx加水印
            WordWaterMarkTest.setWaterMarkToDocxWps(new File("/Users/luyi/Downloads/aa.docx"), "浙商租赁");
            //2、给doc加水印
//            AsposeWordsTest.setWaterMarkToDoc("sunshine");
            //3、给excel加水印
//            AsposeWordsTest.setWaterMarkToXlsEt("sunshine");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //============================================1、docx、wps添加文字水印==================================================

    public static void setWaterMarkToDocxWps(File docx, String watermarkText) throws Exception {//获取文件输出流
        //获取Doc文档对象模型
        Path path = docx.toPath();
        String name = docx.getName();
        String newName = "Marked-" + name;
        FileOutputStream os = new FileOutputStream(Paths.get(path.getParent().toString(), newName).toFile());
        Document doc = new Document(Files.newInputStream(docx.toPath()));
        Shape watermark = new Shape(doc, ShapeType.TEXT_PLAIN_TEXT);
        //水印内容
        watermark.getTextPath().setText(watermarkText);
        //水印字体
        watermark.getTextPath().setFontFamily("仿宋");
        //水印宽度
        watermark.setWidth(500);
        //水印高度
        watermark.setHeight(100);
        //旋转水印
        watermark.setRotation(-40);
        //水印颜色
        watermark.getFill().setColor(new Color(211, 211, 211, 10));
        watermark.setStrokeColor(new Color(211, 211, 211, 10));
        watermark.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
        watermark.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
        watermark.setWrapType(WrapType.NONE);
        watermark.setVerticalAlignment(VerticalAlignment.CENTER);
        watermark.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Paragraph watermarkPara = new Paragraph(doc);
        watermarkPara.appendChild(watermark);
        for (Section sect : doc.getSections()) {
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_PRIMARY);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_FIRST);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_EVEN);
        }
        System.out.println("Watermark Set");
        doc.save(os, SaveFormat.DOCX);
        //关闭输出流
        if (os != null) {
            os.close();
        }
    }
    //============================================2、doc添加水印==================================================

    /**
     * 给doc加水印
     */
    public static void setWaterMarkToDoc(String watermarkText) throws Exception {
        //获取文件输出流
        FileOutputStream os = new FileOutputStream(WordWaterMarkTest.filePath + "To5.doc");
        //FileOutputStream os = new FileOutputStream(AsposeWordsTest.filePath+"To10.wpt");

        //获取Doc文档对象模型
        Document doc = new Document(WordWaterMarkTest.filePath + "5.doc");
        //Document doc = new Document(AsposeWordsTest.filePath+"10.wpt");

        Shape watermark = new Shape(doc, ShapeType.TEXT_PLAIN_TEXT);
        //水印内容
        watermark.getTextPath().setText(watermarkText);
        //水印字体
        watermark.getTextPath().setFontFamily("宋体");
        //水印宽度
        watermark.setWidth(500);
        //水印高度
        watermark.setHeight(100);
        //旋转水印
        watermark.setRotation(-40);
        //水印颜色
        watermark.getFill().setColor(Color.lightGray);
        watermark.setStrokeColor(Color.lightGray);
        watermark.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
        watermark.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
        watermark.setWrapType(WrapType.NONE);
        watermark.setVerticalAlignment(VerticalAlignment.CENTER);
        watermark.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Paragraph watermarkPara = new Paragraph(doc);
        watermarkPara.appendChild(watermark);
        for (Section sect : doc.getSections()) {
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_PRIMARY);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_FIRST);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_EVEN);
        }
        System.out.println("Watermark Set");
        doc.save(os, SaveFormat.DOC);
        //关闭输出流
        if (os != null) {
            os.close();
        }
    }

    /**
     * 在页眉中插入水印
     *
     * @param watermarkPara
     * @param sect
     * @param headerType
     * @throws Exception
     */
    private static void insertWatermarkIntoHeader(Paragraph watermarkPara, Section sect, int headerType) throws Exception {
        HeaderFooter header = sect.getHeadersFooters().getByHeaderFooterType(headerType);
        if (header == null) {
            header = new HeaderFooter(sect.getDocument(), headerType);
            sect.getHeadersFooters().add(header);
        }
        header.appendChild(watermarkPara.deepClone(true));
    }

    //============================================3、excel添加水印==================================================


}

