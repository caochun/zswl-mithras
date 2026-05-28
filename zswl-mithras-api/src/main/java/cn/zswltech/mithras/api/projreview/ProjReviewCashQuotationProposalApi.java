package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "项目评审-评审会会议纪要-现金流计划相关接口")
public interface ProjReviewCashQuotationProposalApi {

    @ApiOperation("上传现金流计划表")
    @PostMapping("/proj/review/quotation/proposal/cashflowplan/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @RequestParam("meetMinuteId") Long meetMinuteId);

    @ApiOperation("获取现金流计划表")
    @PostMapping("/proj/review/quotation/proposal/cashflowplan/list")
    R<List<ProjReviewCashFlowPlanListRSP>> list(@RequestBody @Valid ProjReviewCashFlowMeetMinutePlanListREQ projReviewCashFlowPlanListREQ);

    @ApiOperation("导出租金表")
    @PostMapping("/proj/review/quotation/proposal/cashflowplan/rent/export")
    void exportRent(@RequestBody @Valid ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ);

    @ApiOperation("导出现金流表")
    @PostMapping("/proj/review/quotation/proposal/cashflowplan/cashflow/export")
    void exportCashFlow(@RequestBody @Valid ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ);

}
