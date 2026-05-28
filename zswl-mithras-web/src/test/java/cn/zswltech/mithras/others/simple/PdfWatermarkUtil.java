/*
package cn.zswltech.mithras.others.simple;

import com.aspose.pdf.Document;
import com.aspose.pdf.HorizontalAlignment;
import com.aspose.pdf.VerticalAlignment;
import com.aspose.pdf.WatermarkArtifact;
import com.aspose.pdf.facades.EncodingType;
import com.aspose.pdf.facades.FormattedText;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfWatermarkUtil {

    static {
        InputStream is = null;
        try {
            is = new ClassPathResource("/license.xml").getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.aspose.pdf.License aposeLic = new com.aspose.pdf.License();
        try {
            aposeLic.setLicense(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Path DIR = Paths.get(System.getProperty("user.home"), "Downloads");

    Logger logger = LoggerFactory.getLogger(PdfWatermarkUtil.class);


    @SneakyThrows
    public static void addWatermark2() {
        String filename = "88.pdf";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream = new FileOutputStream(target.toFile());
        Document doc = new Document(path.toString());
        FormattedText formattedText = new FormattedText("浙商租赁", Color.lightGray, "Arial Unicode MS", EncodingType.Winansi, false, 110);

// Add watermark to the first page of PDF
        for (int i = 1; i <= doc.getPages().size(); i++) {
            WatermarkArtifact artifact = new WatermarkArtifact();
            artifact.setText(formattedText);
            artifact.setArtifactHorizontalAlignment(HorizontalAlignment.Center);
            artifact.setArtifactVerticalAlignment(VerticalAlignment.Center);
            artifact.setRotation(25);
            artifact.setOpacity(0.2);
            artifact.setBackground(false);
            doc.getPages().get_Item(i).getArtifacts().add(artifact);
        }
        doc.save(fileOutputStream);
    }

    */
/**
 * 给pdf加水印--最后一步加水印
 *//*

    private void pdfAddWatermark(String filePath, OutputStream outputStream) throws Exception {
        //待加水印的文件
        PdfReader pdfReader = new PdfReader(filePath);
        PdfStamper pdfStamper = null;
        //存放加水印之后的路径
        pdfStamper = new PdfStamper(pdfReader, outputStream);
        int total = pdfReader.getNumberOfPages() + 1;
        PdfContentByte contentByte = null;
        int i = 1;
        PdfGState gs = new PdfGState();
        //设置水印不透明度
        gs.setFillOpacity(0.2f);
        com.itextpdf.text.Rectangle pageSize = null;
        int interval = 40;
        try {
            //动态添加水印文字
            String text = "浙商租赁";
            String[] waterText = text.split("\\|\\|");
            logger.info(">>>>>>>>生成水印内容：[{}]", waterText);
            //获取字体高度
            int textH = 0;
            JLabel label = new JLabel();
            //label.setText(waterText[0]);
            FontMetrics metrics = label.getFontMetrics(label.getFont());
            textH = metrics.getHeight();
            //逐页添加水印
            while (i < total) {
                //水印置于最上层(无论多少分层)
                contentByte = pdfStamper.getOverContent(i);
                pageSize = pdfReader.getPageSizeWithRotation(i);
                float pageHight = pageSize.getHeight();
                float pageWidth = pageSize.getWidth();
                contentByte.beginText();
                //设置字体及字号
                contentByte.setFontAndSize(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED), 100);
                contentByte.setGState(gs);
                //添加多行水印
                for (int i1 = 0; i1 < waterText.length; i1++) {
                    interval = i1 * textH;
                    //调整水印文字的水平位置及角度
                    contentByte.showTextAligned(Element.ALIGN_CENTER, waterText[i1], pageWidth / 2 - interval, pageHight / 2 + interval, 42);
                }
                contentByte.setColorFill(BaseColor.GRAY);
                contentByte.endText();
                i++;
            }
            contentByte.stroke();
            logger.info(">>>>>>>>>> pdf文件添加水印成功!");
        } catch (Exception e) {
            throw new Exception(">>>>>>>>>>pdf文件添加水印失败 失败原因:[{}]", e);
        } finally {
            contentByte = null;
            gs = null;
            pdfStamper.close();
            pdfStamper = null;
        }
        pdfReader.close();
    }

    @SneakyThrows
    public static void main(String[] args) {
//        addWatermark2();
        PdfWatermarkUtil pdfWatermarkUtil = new PdfWatermarkUtil();
        pdfWatermarkUtil.pdfAddWatermark("/Users/luyi/Downloads/租赁物权属2.pdf", Files.newOutputStream(new File("/Users/luyi/Downloads/XX-租赁物权属2.pdf").toPath()));
    }
}

*/
