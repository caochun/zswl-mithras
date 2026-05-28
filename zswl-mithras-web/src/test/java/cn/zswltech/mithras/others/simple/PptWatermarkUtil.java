package cn.zswltech.mithras.others.simple;

import com.aspose.slides.*;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author luyi
 */
public class PptWatermarkUtil {

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

    @SneakyThrows
    public static void main(String[] args) {

        Presentation pres = new Presentation(new FileInputStream(new File("/Users/luyi/Desktop/GPT解密.pptx")));
        try {
            IMasterSlide master = pres.getMasters().get_Item(0);

            // 添加矩形类型的自选图形
            IAutoShape ashp = master.getShapes().addAutoShape(ShapeType.Rectangle, 300, 225, 450, 150);
            ashp.setRotation(-30f);
            // 将 ITextFrame 添加到矩形
            ITextFrame textFrame = ashp.addTextFrame("浙商租赁");
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().setFontHeight(70);
            // 将文本颜色更改为黑色（默认为白色）
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().getFillFormat()
                    .setFillType(FillType.Solid);
            textFrame.getParagraphs().get_Item(0).getPortions().get_Item(0).getPortionFormat().getFillFormat()
                    .getSolidFillColor().setColor(new Color(211, 211, 211, 100));

            // 将矩形的线条颜色更改为透明
            ashp.getShapeStyle().getLineColor().setColor(new Color(211, 211, 211, 0));
            // 删除形状中的任何填充格式
            ashp.getFillFormat().setFillType(FillType.NoFill);

            pres.save("/Users/luyi/Desktop/XX-GPT解密.pptx", SaveFormat.Ppt);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }
}
