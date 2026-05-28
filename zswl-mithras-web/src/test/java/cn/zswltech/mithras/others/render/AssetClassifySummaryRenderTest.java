package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.gendoc.render.AssetClassifySummaryRender;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2023/9/20
 * @description
 */
public class AssetClassifySummaryRenderTest extends ApplicationTest {
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifySummaryRender assetClassifySummaryRender;

    @Test
    public void renderTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/test.docx");
        assetClassifySummaryRender.render(outputStream, assetClassifyService.getById(568L));
    }
}
