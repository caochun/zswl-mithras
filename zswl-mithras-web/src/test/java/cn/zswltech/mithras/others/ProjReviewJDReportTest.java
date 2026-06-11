package cn.zswltech.mithras.others;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjReviewEarningsRateRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjReviewJDReportZLRender;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.application.bo.ProjReviewRenderBO;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/8/4
 * @description
 */
public class ProjReviewJDReportTest extends ApplicationTest {
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewJDReportZLRender projReviewJDReportZLRender;
    @Resource
    private ProjReviewEarningsRateRender projReviewEarningsRateRender;

    @Test
    public void render() {
        String output = "/Users/mockorz/Documents/jdbg.docx";
        try {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(116L);
            projReviewJDReportZLRender.render(FileUtil.getOutputStream(output), new ProjReviewRenderBO(true, projReviewBaseInfo, ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void render2() {
        String output = "/Users/mockorz/Documents/xmsyl.docx";
        try {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(3515);
            projReviewEarningsRateRender.render(FileUtil.getOutputStream(output), new ProjReviewRenderBO(false, projReviewBaseInfo, ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
