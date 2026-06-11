package cn.zswltech.mithras.third.aliyun.ocr.util;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * pdf工具
 *
 * @author wangchuanhao
 * @date 2022/7/6 10:23 AM
 */
@Slf4j
public class PdfUtil {

    /**
     * 经过测试,dpi为96,100,105,120,150,200中,105显示效果较为清晰,体积稳定,dpi越高图片体积越大,一般电脑显示分辨率为96
     */
    public static final float DEFAULT_DPI = 200;

    /**
     * 默认转换的图片格式为jpg
     */
    public static final String DEFAULT_FORMAT = "jpg";


    /**
     * 页数 从0开始计算
     *
     * @param pdfInputStream
     * @param index
     * @return
     */
    @SneakyThrows
    public static BufferedImage pdf2Image(InputStream pdfInputStream, int index) {
        //加载pdf文件
        PDDocument doc = PDDocument.load(pdfInputStream);
        //读取pdf文件
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageCount = doc.getNumberOfPages();
        if (pageCount <= index) {
            return null;
        }
        BufferedImage pdfImg = renderer.renderImageWithDPI(index, DEFAULT_DPI);
        return pdfImg;
    }

    /**
     * pdf转换成图片
     *
     * @param targetPath 输出的图片路径
     * @return 抽取出来的图片路径数组
     */
    @SneakyThrows
    public static List<String> pdfToManyImage(InputStream pdfInputStream, String targetPath) {
        try {
            //加载pdf文件
            PDDocument doc = PDDocument.load(pdfInputStream);
            //读取pdf文件
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            List<String> stringList = new ArrayList<>(pageCount);
            String filePath = null;
            BufferedImage image;
            for (int i = 0; i < pageCount; i++) {
                //96/144/198
                // Windows native DPI
                image = renderer.renderImageWithDPI(i, DEFAULT_DPI);
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                filePath = targetPath + (i + 1) + "." + DEFAULT_FORMAT;
                //保存图片
                ImageIO.write(image, DEFAULT_FORMAT, new File(filePath));
                stringList.add(filePath);
            }
            return stringList;
        } catch (IOException e) {
            log.error("pdf转图片异常", e);
            return null;
        }
    }

    /**
     * 数字区间转成数字列表
     * ex: 1-3,5,7,8-10
     *
     * @param indexRange
     * @return
     */
    public static List<Integer> range2List(String indexRange) {
        List<Integer> indexList = new ArrayList<>();
        String[] indexRangeArr = indexRange.split(",");
        for (String ir : indexRangeArr) {
            if (NumberUtil.isInteger(ir)) {
                indexList.add(Integer.parseInt(ir));
            } else {
                String[] irArr = ir.split("-");
                if (irArr.length == 2
                        && NumberUtil.isInteger(irArr[0])
                        && NumberUtil.isInteger(irArr[1])
                        && Integer.parseInt(irArr[0]) <= Integer.parseInt(irArr[1])) {
                    indexList.addAll(Stream.iterate(Integer.parseInt(irArr[0]), n -> n + 1)
                            .limit(Integer.parseInt(irArr[1]) - Integer.parseInt(irArr[0]) + 1)
                            .collect(Collectors.toList()));
                }
            }
        }
        indexList = indexList.stream().distinct().sorted().collect(Collectors.toList());
        return indexList;
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(range2List("3-1,2,5,6,7,8-10,虎123")));
    }

}
