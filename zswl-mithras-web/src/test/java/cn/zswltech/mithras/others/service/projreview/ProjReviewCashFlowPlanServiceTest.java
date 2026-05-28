package cn.zswltech.mithras.others.service.projreview;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanExportREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowPlanService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
public class ProjReviewCashFlowPlanServiceTest extends ApplicationTest {
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;

    @Test
    public void exportRentTest() throws Exception {
        Long projReviewId = 104L;
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/test.xlsx");
        ProjReviewCashFlowMeetMinutePlanExportREQ req = new ProjReviewCashFlowMeetMinutePlanExportREQ();
        req.setProjReviewId(projReviewId);
        projReviewCashFlowPlanService.exportRent(req, outputStream);
    }

    @Test
    public void exportCashFlowTest() {
        Long projReviewId = 104L;
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/test.xlsx");
        ProjReviewCashFlowMeetMinutePlanExportREQ req = new ProjReviewCashFlowMeetMinutePlanExportREQ();
        req.setProjReviewId(projReviewId);
        projReviewCashFlowPlanService.exportCashFlow(req, outputStream);
    }
}
