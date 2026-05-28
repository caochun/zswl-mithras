package cn.zswltech.mithras.others.simple;

import com.aspose.cells.*;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author luyi
 */
public class ExcelWatermarkUtil {
    static {
           /* String s = "<License>\n" +
                    "  <Data>\n" +
                    "    <Products>\n" +
                    "      <Product>Aspose.Total for Java</Product>\n" +
                    "      <Product>Aspose.Words for Java</Product>\n" +
                    "    </Products>\n" +
                    "    <EditionType>Enterprise</EditionType>\n" +
                    "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
                    "    <LicenseExpiry>20991231</LicenseExpiry>\n" +
                    "    <SerialNumber>23dcc79f-44ec-4a23-be3a-03c1632404e9</SerialNumber>\n" +
                    "  </Data>\n" +
                    "  <Signature>2sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
                    "</License>\n";*/
//            ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
//            InputStream is = getClass().getClassLoader().getResourceAsStream("license.xml");
        InputStream is = null;
        try {
            is = new ClassPathResource("/license.xml").getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        License aposeLic = new License();
        aposeLic.setLicense(is);
    }

    @SneakyThrows
    public static void main(String[] args) {
        Workbook workbook = new Workbook("/Users/luyi/Downloads/新机房VPN申请.xlsx");
        Worksheet sheet = workbook.getWorksheets().get(0);
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
                            sheet.getShapes().addTextEffect(MsoPresetTextEffect.TEXT_EFFECT_1, "浙商租赁",
                                    "仿宋", 22, false, true,
                                    8 + 10 * (s), 0, 1 + 6 * l, 1, 40, 300);
                    MsoFillFormat fillFormat = shape.getFillFormat();
                    fillFormat.setForeColor(com.aspose.cells.Color.fromArgb(100, 100, 100));
                    fillFormat.setTransparency(0.2);
                    shape.getLineFormat().setVisible(false);
                    shape.setRotationAngle(330);
                }
            }
            i++;
        }
        workbook.save("/Users/luyi/Downloads/XX-新机房VPN申请.xlsx", SaveFormat.XLSX);

    }
}
