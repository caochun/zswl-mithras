package cn.zswltech.mithras.others.pdf;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static cn.zswltech.mithras.third.util.WatermarkUtil.doc2Pdf;
import static cn.zswltech.mithras.third.util.WatermarkUtil.setPDFWaterMark;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/22 14:36
 */
public class doc转pdf {


    @Test
    public void test() throws Exception {
        Document document = new Document(FileUtil.getInputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知.docx"));
        document.acceptAllRevisions();
        document.save(FileUtil.getOutputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知.pdf"), SaveFormat.PDF);
        // 用aspose的转PDF方法会丢失word文件的水印，兜底方案是在pdf中再添加回来，看看是否有更好的方案
        ByteArrayOutputStream byteArrayOutputStream = setPDFWaterMark(FileUtil.getInputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知.pdf"), "浙商租赁");
        byteArrayOutputStream.writeTo(FileUtil.getOutputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知带水印.pdf"));
    }

    @Test
    public void test2() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = doc2Pdf(FileUtil.getInputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知.docx"));
        byteArrayOutputStream.writeTo(FileUtil.getOutputStream("/Users/zhaozhengkang/Desktop/未来科技入职须知带水印.pdf"));
    }
}
