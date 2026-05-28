package cn.zswltech.mithras.api.app;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.app.*;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.file.AppFileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author junke
 */
@Api(value = "融租易APP", tags = "融租易接口")
public interface AppApi {

    @ApiOperation("APP拜访打卡")
    @PostMapping("/app/visit/checkIn")
    R<Void> checkIn(@Valid AppCheckInREQ req);


    @ApiOperation("APP漏打卡")
    @PostMapping("/app/visit/RecheckIn")
    R<Void> reCheckIn(@Valid AppCheckInREQ req);


    @ApiOperation("APP拜访记录")
    @PostMapping("/app/visit/list")
    R<PageR<AppVisitRecordRSP>> list(@RequestBody @Valid AppVisitRecordREQ req);


    @ApiOperation("pc端访客管理-拜访明细")
    @PostMapping("/app/pc/visit/list")
    R<PageR<AppPCVisitRecordRSP>> pcList(@RequestBody @Valid AppPCVisitRecordREQ req);


    @ApiOperation("pc端访客管理-拜访汇总")
    @PostMapping("/app/pc/visit/summary")
    R<Map<String,Object>> pcSummary(@RequestBody @Valid AppPCVisitSummaryREQ req);


    @ApiOperation("APP拜访记录立项")
    @PostMapping("/app/visit/queryProjEstablish")
    R<List<ClientAppProjQueryRSP>> queryProjEstablish(@RequestBody @Valid ClientAppProjQueryREQ req);

    @ApiOperation("APP拜访查询合同信息")
    @PostMapping("/app/visit/contract/list")
    R<List<AppContractQueryRSP>> queryContract(@RequestBody @Valid AppContractQueryREQ req);

    @ApiOperation("APP拜访记录租后检查")
    @PostMapping("/app/visit/queryCheckPlan")
    R<List<ClientAppPlanQueryRSP>> queryCheckPlan(@RequestBody @Valid ClientAppPlanQueryREQ req);


    @ApiOperation("APP作废打卡记录")
    @PostMapping("/app/visit/invalid")
    R<Void> invalid(@RequestBody @Valid AppInvalidREQ req);


    @ApiOperation("APP拜访记录详情")
    @PostMapping("/app/visit/detail")
    R<AppRecordDetailRSP> detail(@RequestBody @Valid AppRecordDetailREQ req);


    @ApiOperation("APP我的客户详情")
    @PostMapping("/app/client/list")
    R<PageR<AppClientListRSP>> list(@RequestBody @Valid AppClientListREQ req);

    @ApiOperation("APP我的客户-合同信息")
    @PostMapping("/app/client/contract/list")
    R<List<AppContractListRSP>> list(@RequestBody @Valid AppContractListREQ req);

    @ApiOperation("APP我的客户-拜访详情")
    @PostMapping("/app/client/detail")
    R<AppClientDetailRSP> detail(@RequestBody @Valid AppClientDetailREQ req);

    @ApiOperation("APP我的客户-拜访客户数量下拉框")
    @PostMapping("/app/client/frequent/list")
    R<List<AppClientFrequentRSP>> list(@RequestBody @Valid AppClientFrequentREQ req);


    @ApiOperation("APP我的客户-照片转pdf")
    @PostMapping("/app/client/imageToPdf")
    R<Void> imageToPdf(@Valid AppClientPdfREQ req);


    @ApiOperation("APP我的项目-我的项目列表")
    @PostMapping("/app/proj/info/list")
    R<List<AppProjListRSP>> list(@RequestBody @Valid AppProjListREQ req);

    @ApiOperation("APP我的项目-我的项目信息")
    @PostMapping("/app/proj/info/detail")
    R<AppProjDetailRSP> projDetail(@RequestBody @Valid AppProjDetailREQ req);

    @ApiOperation("APP我的项目-我的合同信息")
    @PostMapping("/app/proj/contract/detail")
    R<AppProContractDetailRSP> contractDetail(@RequestBody @Valid AppProjContractDetailREQ req);


    @ApiOperation("APP我的项目-我的合同数目")
    @PostMapping("/app/proj/contract/base")
    R<List<AppProContractBaseRSP>> contractBase(@RequestBody @Valid AppProjContractBaseREQ req);


    @ApiOperation("APP报价试算-生产现金流")
    @PostMapping("/app/cashFlow/generation/execute")
    R<AppCashFlowGenerationRSP> generate(@RequestBody @Valid AppCashFlowGenerationREQ req);


    @ApiOperation("APP还款日历-我的日历/团队日历")
    @PostMapping("/app/calendar/daily/list")
    R<AppCalendarDailyRSP> dailyList(@RequestBody @Valid AppCalendarDailyREQ req);

    @ApiOperation("APP还款日历-还款详情")
    @PostMapping("/app/calendar/daily/detail")
    R<AppCalendarDetailRSP> dailyDetail(@RequestBody @Valid AppCalendarDetailREQ req);


    @ApiOperation("APP还款日历-每月汇总")
    @PostMapping("/app/calendar/monthly/list")
    R<AppCalendarMonthlyRSP> monthlyList(@RequestBody @Valid AppCalendarMonthlyREQ req);


    @ApiOperation("APP合同面签-签约列表")
    @PostMapping("/app/contract/sign/list")
    R<List<AppContractSignListRSP>> signList(@RequestBody @Valid AppContractSignListREQ req);


    @ApiOperation("APP合同面签-去签署")
    @PostMapping("/app/contract/sign")
    R<AppContractSignRSP> sign(@RequestBody @Valid AppContractSignREQ req);


    @ApiOperation("APP合同面签-合同是否已有签约照片视频")
    @PostMapping("/app/contract/existed")
    R<List<AppContractSignExistedRSP>> contractExisted(@RequestBody @Valid AppContractSignExistedREQ req);

    @ApiOperation("APP合同面签-同项目其它合同是否已有签约照片视频")
    @PostMapping("/app/contract/proj/signed")
    R<List<AppContractSignProjExistedRSP>> projExisted(@RequestBody @Valid AppContractSignProjExistedREQ req);


    @ApiOperation("APP合同面签-拷贝其它合同视频和图片")
    @PostMapping("/app/contract/sign/copy")
    R<List<AppContractSignCopyRSP>> signedCopy(@RequestBody @Valid AppContractSignCopyREQ req);


    @ApiOperation("APP合同面签-详情")
    @PostMapping("/app/contract/sign/detail")
    R<List<AppContractSignDetailRSP>> signDetail(@RequestBody @Valid AppContractSignDetailREQ req);


    @ApiOperation("APP合同面签-是否已签约")
    @PostMapping("/app/contract/pay/sign")
    R<AppContractPaySignedRSP> isSigned(@RequestBody @Valid AppContractPaySignedREQ req);


    @ApiOperation("APP合同面签-更新签约")
    @PostMapping("/app/contract/sign/update")
    R<Void> updateSign(@RequestBody @Valid AppContractSignUpdateREQ req);


    @ApiOperation("APP功能权限-详情")
    @PostMapping("/app/authority/detail")
    R<List<AppAuthorityDetailRSP>> detail(@RequestBody @Valid AppAuthorityDetailREQ req);


    @ApiOperation("APP通用上传文件")
    @PostMapping("/app/file/upload")
    R<FileUploadRSP> upload(@RequestParam(value = "mainId") Long id, @RequestParam("moduleType") String moduleType, @RequestParam("materialsType") String materialsType,
                            @RequestParam(value = "materialsSubType", required = false) String materialsSubType, @RequestParam(value = "needWatermark", required = false) Integer needWatermark, @RequestParam(value = "batchNo", required = false) String batchNo,
                            @RequestParam(value = "processInstanceId", required = false) String processInstanceId, @RequestParam(value = "sourceBusinessKey", required = false) String sourceBusinessKey, @RequestParam(value = "ext", required = false) Map<String, Object> ext,
                            @RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "createdBy") Long createdBy, @RequestParam(value = "location", required = false) String location, @Valid AppFileUploadREQ appFileUploadREQ);

    @ApiOperation("客户拜访文件批量下载")
    @PostMapping(path = "/app/pc/visit/file/download")
    R<String> batchDownload(@RequestBody @Valid AppVisitFileBatchDownloadREQ req);

    @ApiOperation("下载任务列表")
    @PostMapping(path = "/app/pc/visit/file/download/task/list")
    R<List<VisitDownloadTaskRSP>> listDownloadTask();
}
