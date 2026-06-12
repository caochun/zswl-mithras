package cn.zswltech.mithras.customer.mobile.controller;

import cn.zswltech.mithras.api.app.AppApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.customer.mobile.application.AppApplicationService;
import cn.zswltech.mithras.dto.app.AppAuthorityDetailREQ;
import cn.zswltech.mithras.dto.app.AppAuthorityDetailRSP;
import cn.zswltech.mithras.dto.app.AppCalendarDailyREQ;
import cn.zswltech.mithras.dto.app.AppCalendarDailyRSP;
import cn.zswltech.mithras.dto.app.AppCalendarDetailREQ;
import cn.zswltech.mithras.dto.app.AppCalendarDetailRSP;
import cn.zswltech.mithras.dto.app.AppCalendarMonthlyREQ;
import cn.zswltech.mithras.dto.app.AppCalendarMonthlyRSP;
import cn.zswltech.mithras.dto.app.AppCashFlowGenerationREQ;
import cn.zswltech.mithras.dto.app.AppCashFlowGenerationRSP;
import cn.zswltech.mithras.dto.app.AppCheckInREQ;
import cn.zswltech.mithras.dto.app.AppClientDetailREQ;
import cn.zswltech.mithras.dto.app.AppClientDetailRSP;
import cn.zswltech.mithras.dto.app.AppClientFrequentREQ;
import cn.zswltech.mithras.dto.app.AppClientFrequentRSP;
import cn.zswltech.mithras.dto.app.AppClientListREQ;
import cn.zswltech.mithras.dto.app.AppClientListRSP;
import cn.zswltech.mithras.dto.app.AppClientPdfREQ;
import cn.zswltech.mithras.dto.app.AppContractListREQ;
import cn.zswltech.mithras.dto.app.AppContractListRSP;
import cn.zswltech.mithras.dto.app.AppContractPaySignedREQ;
import cn.zswltech.mithras.dto.app.AppContractPaySignedRSP;
import cn.zswltech.mithras.dto.app.AppContractQueryREQ;
import cn.zswltech.mithras.dto.app.AppContractQueryRSP;
import cn.zswltech.mithras.dto.app.AppContractSignCopyREQ;
import cn.zswltech.mithras.dto.app.AppContractSignCopyRSP;
import cn.zswltech.mithras.dto.app.AppContractSignDetailREQ;
import cn.zswltech.mithras.dto.app.AppContractSignDetailRSP;
import cn.zswltech.mithras.dto.app.AppContractSignExistedREQ;
import cn.zswltech.mithras.dto.app.AppContractSignExistedRSP;
import cn.zswltech.mithras.dto.app.AppContractSignListREQ;
import cn.zswltech.mithras.dto.app.AppContractSignListRSP;
import cn.zswltech.mithras.dto.app.AppContractSignProjExistedREQ;
import cn.zswltech.mithras.dto.app.AppContractSignProjExistedRSP;
import cn.zswltech.mithras.dto.app.AppContractSignREQ;
import cn.zswltech.mithras.dto.app.AppContractSignRSP;
import cn.zswltech.mithras.dto.app.AppContractSignUpdateREQ;
import cn.zswltech.mithras.dto.app.AppInvalidREQ;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordREQ;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordRSP;
import cn.zswltech.mithras.dto.app.AppPCVisitSummaryREQ;
import cn.zswltech.mithras.dto.app.AppProContractBaseRSP;
import cn.zswltech.mithras.dto.app.AppProContractDetailRSP;
import cn.zswltech.mithras.dto.app.AppProjContractBaseREQ;
import cn.zswltech.mithras.dto.app.AppProjContractDetailREQ;
import cn.zswltech.mithras.dto.app.AppProjDetailREQ;
import cn.zswltech.mithras.dto.app.AppProjDetailRSP;
import cn.zswltech.mithras.dto.app.AppProjListREQ;
import cn.zswltech.mithras.dto.app.AppProjListRSP;
import cn.zswltech.mithras.dto.app.AppRecordDetailREQ;
import cn.zswltech.mithras.dto.app.AppRecordDetailRSP;
import cn.zswltech.mithras.dto.app.AppVisitFileBatchDownloadREQ;
import cn.zswltech.mithras.dto.app.AppVisitRecordREQ;
import cn.zswltech.mithras.dto.app.AppVisitRecordRSP;
import cn.zswltech.mithras.dto.app.VisitDownloadTaskRSP;
import cn.zswltech.mithras.dto.client.client.ClientAppPlanQueryREQ;
import cn.zswltech.mithras.dto.client.client.ClientAppPlanQueryRSP;
import cn.zswltech.mithras.dto.client.client.ClientAppProjQueryREQ;
import cn.zswltech.mithras.dto.client.client.ClientAppProjQueryRSP;
import cn.zswltech.mithras.dto.file.AppFileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class AppController implements AppApi {
    @Resource
    private AppApplicationService appApplicationService;

    @Override
    public R<Void> checkIn(@Valid AppCheckInREQ req) {
        return appApplicationService.checkIn(req);
    }

    @Override
    public R<Void> reCheckIn(@Valid AppCheckInREQ req) {
        return appApplicationService.reCheckIn(req);
    }

    @Override
    public R<PageR<AppVisitRecordRSP>> list(@RequestBody @Valid AppVisitRecordREQ req) {
        return appApplicationService.list(req);
    }

    @Override
    public R<PageR<AppPCVisitRecordRSP>> pcList(@RequestBody @Valid AppPCVisitRecordREQ req) {
        return appApplicationService.pcList(req);
    }

    @Override
    public R<Map<String, Object>> pcSummary(@RequestBody @Valid AppPCVisitSummaryREQ req) {
        return appApplicationService.pcSummary(req);
    }

    @Override
    public R<List<ClientAppProjQueryRSP>> queryProjEstablish(@RequestBody @Valid ClientAppProjQueryREQ req) {
        return appApplicationService.queryProjEstablish(req);
    }

    @Override
    public R<List<AppContractQueryRSP>> queryContract(@RequestBody @Valid AppContractQueryREQ req) {
        return appApplicationService.queryContract(req);
    }

    @Override
    public R<List<ClientAppPlanQueryRSP>> queryCheckPlan(@RequestBody @Valid ClientAppPlanQueryREQ req) {
        return appApplicationService.queryCheckPlan(req);
    }

    @Override
    public R<Void> invalid(@RequestBody @Valid AppInvalidREQ req) {
        return appApplicationService.invalid(req);
    }

    @Override
    public R<AppRecordDetailRSP> detail(@RequestBody @Valid AppRecordDetailREQ req) {
        return appApplicationService.detail(req);
    }

    @Override
    public R<PageR<AppClientListRSP>> list(@RequestBody @Valid AppClientListREQ req) {
        return appApplicationService.list(req);
    }

    @Override
    public R<List<AppContractListRSP>> list(@RequestBody @Valid AppContractListREQ req) {
        return appApplicationService.list(req);
    }

    @Override
    public R<AppClientDetailRSP> detail(@RequestBody @Valid AppClientDetailREQ req) {
        return appApplicationService.detail(req);
    }

    @Override
    public R<List<AppClientFrequentRSP>> list(@RequestBody @Valid AppClientFrequentREQ req) {
        return appApplicationService.list(req);
    }

    @Override
    public R<Void> imageToPdf(@Valid AppClientPdfREQ req) {
        return appApplicationService.imageToPdf(req);
    }

    @Override
    public R<List<AppProjListRSP>> list(@RequestBody @Valid AppProjListREQ req) {
        return appApplicationService.list(req);
    }

    @Override
    public R<AppProjDetailRSP> projDetail(@RequestBody @Valid AppProjDetailREQ req) {
        return appApplicationService.projDetail(req);
    }

    @Override
    public R<AppProContractDetailRSP> contractDetail(@RequestBody @Valid AppProjContractDetailREQ req) {
        return appApplicationService.contractDetail(req);
    }

    @Override
    public R<List<AppProContractBaseRSP>> contractBase(@RequestBody @Valid AppProjContractBaseREQ req) {
        return appApplicationService.contractBase(req);
    }

    @Override
    public R<AppCashFlowGenerationRSP> generate(@RequestBody @Valid AppCashFlowGenerationREQ req) {
        return appApplicationService.generate(req);
    }

    @Override
    public R<AppCalendarDailyRSP> dailyList(@RequestBody @Valid AppCalendarDailyREQ req) {
        return appApplicationService.dailyList(req);
    }

    @Override
    public R<AppCalendarDetailRSP> dailyDetail(@RequestBody @Valid AppCalendarDetailREQ req) {
        return appApplicationService.dailyDetail(req);
    }

    @Override
    public R<AppCalendarMonthlyRSP> monthlyList(@RequestBody @Valid AppCalendarMonthlyREQ req) {
        return appApplicationService.monthlyList(req);
    }

    @Override
    public R<List<AppContractSignListRSP>> signList(@RequestBody @Valid AppContractSignListREQ req) {
        return appApplicationService.signList(req);
    }

    @Override
    public R<AppContractSignRSP> sign(@RequestBody @Valid AppContractSignREQ req) {
        return appApplicationService.sign(req);
    }

    @Override
    public R<List<AppContractSignExistedRSP>> contractExisted(@RequestBody @Valid AppContractSignExistedREQ req) {
        return appApplicationService.contractExisted(req);
    }

    @Override
    public R<List<AppContractSignProjExistedRSP>> projExisted(@RequestBody @Valid AppContractSignProjExistedREQ req) {
        return appApplicationService.projExisted(req);
    }

    @Override
    public R<List<AppContractSignCopyRSP>> signedCopy(@RequestBody @Valid AppContractSignCopyREQ req) {
        return appApplicationService.signedCopy(req);
    }

    @Override
    public R<List<AppContractSignDetailRSP>> signDetail(@RequestBody @Valid AppContractSignDetailREQ req) {
        return appApplicationService.signDetail(req);
    }

    @Override
    public R<AppContractPaySignedRSP> isSigned(@RequestBody @Valid AppContractPaySignedREQ req) {
        return appApplicationService.isSigned(req);
    }

    @Override
    public R<Void> updateSign(@RequestBody @Valid AppContractSignUpdateREQ req) {
        return appApplicationService.updateSign(req);
    }

    @Override
    public R<List<AppAuthorityDetailRSP>> detail(@RequestBody @Valid AppAuthorityDetailREQ req) {
        return appApplicationService.detail(req);
    }

    @Override
    public R<FileUploadRSP> upload(@RequestParam(value = "mainId") Long id,
                                   @RequestParam("moduleType") String moduleType,
                                   @RequestParam("materialsType") String materialsType,
                                   @RequestParam(value = "materialsSubType", required = false) String materialsSubType,
                                   @RequestParam(value = "needWatermark", required = false) Integer needWatermark,
                                   @RequestParam(value = "batchNo", required = false) String batchNo,
                                   @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
                                   @RequestParam(value = "sourceBusinessKey", required = false) String sourceBusinessKey,
                                   @RequestParam(value = "ext", required = false) Map<String, Object> ext,
                                   @RequestParam(value = "userId", required = false) Long userId,
                                   @RequestParam(value = "createdBy") Long createdBy,
                                   @RequestParam(value = "location", required = false) String location,
                                   @Valid AppFileUploadREQ appFileUploadREQ) {
        return appApplicationService.upload(id, moduleType, materialsType, materialsSubType, needWatermark, batchNo,
                processInstanceId, sourceBusinessKey, ext, userId, createdBy, location, appFileUploadREQ);
    }

    @Override
    public R<String> batchDownload(@RequestBody @Valid AppVisitFileBatchDownloadREQ req) {
        return appApplicationService.batchDownload(req);
    }

    @Override
    public R<List<VisitDownloadTaskRSP>> listDownloadTask() {
        return appApplicationService.listDownloadTask();
    }
}
