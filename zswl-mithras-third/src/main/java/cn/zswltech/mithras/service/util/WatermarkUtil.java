package cn.zswltech.mithras.service.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.third.qiyuesuo.infrastructure.client.config.QiyuesuoConfig;
import com.aspose.cells.MsoFillFormat;
import com.aspose.cells.MsoPresetTextEffect;
import com.aspose.cells.Shape;
import com.aspose.slides.FillType;
import com.aspose.slides.*;
import com.aspose.words.IWarningCallback;
import com.aspose.words.License;
import com.aspose.words.Paragraph;
import com.aspose.words.SaveFormat;
import com.aspose.words.SaveOptions;
import com.aspose.words.Section;
import com.aspose.words.*;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Font;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @create: 2023-03-29
 **/

@Slf4j
public class WatermarkUtil {
    static String fontName = "Arial Unicode MS";
    private static final List<String> SUPPORT_ADD_WATER_MARK_TYPES = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "png", "jpeg", "jpg");

    //aspose words
    static {
        try {
            InputStream is = new ClassPathResource("/license.xml").getInputStream();
            License aposeLic = new License();
            aposeLic.setLicense(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //aspose cells
    static {
        InputStream is = null;
        try {
            is = new ClassPathResource("/license.xml").getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.aspose.cells.License aposeLic = new com.aspose.cells.License();
        aposeLic.setLicense(is);
    }

    //aspose slides
    static {
        InputStream is = null;
        try {
            is = new ClassPathResource("/license.xml").getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.aspose.slides.License aposeLic = new com.aspose.slides.License();
        aposeLic.setLicense(is);
    }

    /**
     * 文件添加水印
     *
     * @param watermark 水印内容
     * @param fileName  文件mc
     **/
    public static Pair<String, InputStream> watermark(InputStream inputStream, String watermark, String fileName) throws Exception {
        String suffix = FileNameUtil.getSuffix(fileName);
        ByteArrayOutputStream resultOs = null;
        if (!SUPPORT_ADD_WATER_MARK_TYPES.contains(suffix.toLowerCase())) {
            return Pair.of(fileName, inputStream);
        }
        byte[] originalBytes = IoUtil.readBytes(inputStream);
        InputStream copyInputStream = IoUtil.toStream(originalBytes);
        try {
            switch (suffix.toLowerCase(Locale.ROOT)) {
                case "doc":
                    resultOs = WatermarkUtil.setWordWaterMark(copyInputStream, watermark, "doc");
                    break;
                case "docx":
                    resultOs = WatermarkUtil.setWordWaterMark(copyInputStream, watermark, "docx");
                    break;
                case "xls":
                    resultOs = WatermarkUtil.setExcelWaterMark(copyInputStream, watermark, "xls");
                    break;
                case "xlsx":
                    resultOs = WatermarkUtil.setExcelWaterMark(copyInputStream, watermark, "xlsx");
                    break;
                case "ppt":
                    resultOs = WatermarkUtil.setPPTWaterMark(copyInputStream, watermark, "ppt");
                    break;
                case "pptx":
                    resultOs = WatermarkUtil.setPPTWaterMark(copyInputStream, watermark, "pptx");
                    break;
                case "pdf":
                    resultOs = WatermarkUtil.setPDFWaterMark(copyInputStream, watermark);
                    break;
                default:
                    resultOs = WatermarkUtil.setImgWaterMark(copyInputStream, watermark, suffix);
                    break;
            }
            if (resultOs != null) {
                return Pair.of(fileName, new ByteArrayInputStream(resultOs.toByteArray()));
            } else {
                return Pair.of(fileName, IoUtil.toStream(originalBytes));
            }
        } catch (Exception e) {
            log.error("添加水印发生异常", e);
            return Pair.of(fileName, IoUtil.toStream(originalBytes));
        }
    }

    /**
     * word文字水印 (doc,docx)
     */
    @SneakyThrows
    public static ByteArrayOutputStream setWordWaterMark(InputStream inputStream, String markStr, String suffix) throws IOException {
        com.aspose.words.Document doc = new com.aspose.words.Document(inputStream);
        com.aspose.words.Shape watermark = new com.aspose.words.Shape(doc, com.aspose.words.ShapeType.TEXT_PLAIN_TEXT);
        //水印内容
        watermark.getTextPath().setText(markStr);
        //水印字体
        watermark.getTextPath().setFontFamily(fontName);
        //水印宽度
        watermark.setWidth(500);
        //水印高度
        watermark.setHeight(100);
        //旋转水印
        watermark.setRotation(-40);
        //水印颜色
        watermark.getFill().setColor(new Color(211, 211, 211, 100));
        watermark.setStroked(false);
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
        ByteArrayOutputStream resultOs = new ByteArrayOutputStream();
        doc.save(resultOs, "doc".equalsIgnoreCase(suffix) ? SaveFormat.DOC : SaveFormat.DOCX);
        return resultOs;
    }

    private static void insertWatermarkIntoHeader(Paragraph watermarkPara, Section sect, int headerType) throws Exception {
        HeaderFooter header = sect.getHeadersFooters().getByHeaderFooterType(headerType);
        if (header == null) {
            header = new HeaderFooter(sect.getDocument(), headerType);
            sect.getHeadersFooters().add(header);
        }
        header.appendChild(watermarkPara.deepClone(true));
    }

    /**
     * pdf文字水印
     */
    @SneakyThrows
    public static ByteArrayOutputStream setPDFWaterMark(InputStream inputStream, String markStr) {
        //待加水印的文件
        PdfReader pdfReader = new PdfReader(inputStream);
        //存放加水印之后的路径
        ByteArrayOutputStream resultOs = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, resultOs);
        int total = pdfReader.getNumberOfPages() + 1;
        PdfContentByte contentByte = null;
        int i = 1;
        PdfGState gs = new PdfGState();
        //设置水印不透明度
        gs.setFillOpacity(0.1f);
        com.itextpdf.text.Rectangle pageSize;
        int interval;
        try {
            //动态添加水印文字
            String[] waterText = markStr.split("\\|\\|");
            //获取字体高度
            int textH = 0;
            JLabel label = new JLabel();
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
                contentByte.setFontAndSize(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED), 120);
                contentByte.setGState(gs);
                //添加多行水印
                for (int i1 = 0; i1 < waterText.length; i1++) {
                    interval = i1 * textH;
                    //调整水印文字的水平位置及角度
                    contentByte.showTextAligned(Element.ALIGN_CENTER, waterText[i1], pageWidth / 2 - interval, pageHight / 2 + interval, 42);
                }
                contentByte.setColorFill(new BaseColor(211, 211, 211, 100));
                contentByte.endText();
                i++;
            }
            contentByte.stroke();
        } catch (Exception e) {
            throw new Exception(">>>>>>>>>>pdf文件添加水印失败 失败原因:[{}]", e);
        } finally {
            pdfStamper.close();
            pdfReader.close();
        }
        return resultOs;
    }

    /**
     * excel设置水印
     * Excel 水印在正常模式下不可见，仅在页面布局模式或打印预览模式可见。
     */
    @SneakyThrows
    public static ByteArrayOutputStream setExcelWaterMark(InputStream inputStream, String markStr, String suffix) {
        com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook(inputStream);
        com.aspose.cells.Worksheet sheet = workbook.getWorksheets().get(0);
        int i = 0;
        while (i < workbook.getWorksheets().getCount()) {
            int row = sheet.getCells().getMaxDataRow() + 100;
            int col = sheet.getCells().getMaxDataColumn() + 20;
        /*for (int i = 0; i < count; i++) {
            com.aspose.cells.Shape shape = sheet.getShapes().get(i);
            if (shape.isWordArt()) {
                sheet.getShapes().remove(shape);
            }
        }*/

            int count = 0;
            //计算水印的位置，确保有文字的地方都会被水印覆盖
            int j = row / 40;
            if (j <= 0) {
                j = 1;
            }
            int m = col / 6;
            if (m <= 0) {
                m = 1;
            }
            for (int s = 0; s < j; s++) {
                for (int l = 0; l < m; l++) {
                    //艺术字水印
                    Shape shape =
                            sheet.getShapes().addTextEffect(MsoPresetTextEffect.TEXT_EFFECT_1, markStr,
                                    fontName, 22, false, true,
                                    8 + 10 * (s), 0, 1 + 6 * l, 1, 40, 300);
                    MsoFillFormat fillFormat = shape.getFillFormat();
                    fillFormat.setForeColor(com.aspose.cells.Color.fromArgb(100, 211, 211, 211));
                    shape.getLineFormat().setVisible(false);
                    shape.setRotationAngle(330);
                }
            }
            i++;
        }
        ByteArrayOutputStream resultOs = new ByteArrayOutputStream();
        workbook.save(resultOs, com.aspose.cells.SaveFormat.XLSX);
        return resultOs;
    }

    /**
     * PPT设置水印
     * 限制，10页以内可用
     */
    public static ByteArrayOutputStream setPPTWaterMark(InputStream inputStream, String markStr, String suffix) throws Exception {
        com.aspose.slides.Presentation pres = new Presentation(inputStream);
        try {
            IMasterSlide master = pres.getMasters().get_Item(0);
            // 添加矩形类型的自选图形
            IAutoShape ashp = master.getShapes().addAutoShape(com.aspose.slides.ShapeType.Rectangle, 200, 225, 450, 150);
            ashp.setRotation(-40f);
            // 将 ITextFrame 添加到矩形
            ITextFrame textFrame = ashp.addTextFrame(markStr);
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().setFontHeight(100);
            //
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().setEastAsianFont(new FontData(fontName));
            // 将文本颜色更改为黑色（默认为白色）
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().getFillFormat()
                    .setFillType(FillType.Solid);
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().getFillFormat()
                    .getSolidFillColor().setColor(new Color(211, 211, 211, 100));

            // 将矩形的线条颜色更改为透明
            ashp.getShapeStyle().getLineColor().setColor(new Color(211, 211, 211, 0));
            // 删除形状中的任何填充格式
            ashp.getFillFormat().setFillType(FillType.NoFill);
            ByteArrayOutputStream resultOs = new ByteArrayOutputStream();
            pres.save(resultOs, "ppt".equalsIgnoreCase(suffix) ? com.aspose.slides.SaveFormat.Ppt : com.aspose.slides.SaveFormat.Pptx);
            return resultOs;
        } finally {
            pres.dispose();
        }
    }

    public static ByteArrayOutputStream setImgWaterMark(InputStream inputStream, String markStr, String suffix) throws Exception {
        if (StringUtil.isEmpty(markStr)) {
            return null;
        }
        Graphics2D g = null;
        try {
            // 读取原图片
            BufferedImage originalImage = ImageIO.read(inputStream);
            // 获取图形绘制对象
            g = originalImage.createGraphics();
            // 设置抗锯齿
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置水印文字颜色和透明度
            g.setColor(new Color(211, 211, 211, 100)); // 半透明白色
            // 计算文字大小
            int imgSize = Math.min(originalImage.getWidth(), originalImage.getHeight());
            int size = imgSize / Math.max(markStr.length(), 4);
            // 设置字体
            Font font = null;
            InputStream fontStream = WatermarkUtil.class.getResourceAsStream("/ttf/ArialUnicodeMS.ttf"); // 替换为实际路径
            if (fontStream != null) {
                font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, size);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font); // 注册字体，使得系统能识别和使用它
            } else {
                font = new Font(fontName, Font.BOLD, 200);
            }
            g.setFont(font); // 字体、样式和大小
            // 计算水印文字的边界框
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D bounds = fm.getStringBounds(markStr, g);

            // 计算水印文字的位置（居中显示）
            int x = (originalImage.getWidth() - getWatermarkLength(markStr, g)) / 2;
            int y = originalImage.getHeight() / 2;
            // 绘制水印文字
            AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
            transform.rotate(Math.toRadians(-40), bounds.getWidth() / 2, 0);
            g.transform(transform);
            g.drawString(markStr, 0, 0);

            BufferedImage thumbnail = Thumbnails.of(originalImage)
                    //必须设置大小，否则有size not set的ERROR
                    .size(600, 400)
                    //var3表示透明度
                    .watermark(Positions.BOTTOM_RIGHT, originalImage, 1.0f)
                    //缓存输出
                    .asBufferedImage();
            //从HttpServletResponse中获取输出流
            ByteArrayOutputStream resultOs = new ByteArrayOutputStream();
            //将BufferedImage转换为InputStream
            inputStream = bufferedImageToInputStream(thumbnail, markStr);
            //直接COPY输出
            ImageIO.write(originalImage, suffix, resultOs);
            //清理缓存
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(resultOs);
            return resultOs;
        } finally {
            // 清除原图片的图形绘制对象，避免内存泄漏
            if (g != null) {
                g.dispose();
            }
        }

    }

    public static ByteArrayOutputStream doc2Pdf(InputStream inputStream) throws Exception {
        Document document = new Document(inputStream);
        document.acceptAllRevisions();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        document.save(pdfOutputStream, SaveFormat.PDF);
        return pdfOutputStream;
    }


    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

    /**
     * 将BufferedImage转换为InputStream
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image, String markStr) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, markStr, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
