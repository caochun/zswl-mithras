package cn.zswltech.mithras.service.controller.assetclassify;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.api.assetclassify.AssetClassifyIndexApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyInitTypeEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyReviewStatusEnum;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyNodeRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.assetclassify.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName AssetClassifyIndexController
 * @Author jackerhe
 * @Date 2023/1/4 2:10 下午
 * @Version 1.0
 **/
@RestController
@Slf4j
public class AssetClassifyIndexController implements AssetClassifyIndexApi {
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private AssetClassifyCheckContentService assetClassifyCheckContentService;
    @Resource
    private AssetClassifyClientRiskFactorService assetClassifyClientRiskFactorService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private AssetClassifyClientWithdrawalRatioService assetClassifyClientWithdrawalRatioService;

    @Override
    public R<List<QuarterDetailRSP>> quarterSelect(@Valid QuarterDetailREQ quarterDetailREQ) {
        return R.ok(assetClassifyService.quarterSelect(quarterDetailREQ.getYear()));
    }

    @Override
    public R<AssetClassifyNodeRSP> gradeProcess(@Valid AssetClassifyNodeREQ req) {
        return R.ok(assetClassifyNodeRecordService.gradeProcess(req.getAssetClassifyId()));
    }

    @Override
    public R<AssetClassifyResidueWorkdayRSP> residueWorkday(@Valid AssetClassifyNodeREQ req) {
        StopWatch st = new StopWatch("计算当前第几个工作日");
        st.start();
        Integer integer = assetClassifyService.residueWorkday(req.getAssetClassifyId());
        st.stop();
        log.info("AssetClassifyIndexController residueWorkday use time {}ms", st.getLastTaskTimeMillis());
        AssetClassifyResidueWorkdayRSP rsp = new AssetClassifyResidueWorkdayRSP();
        rsp.setResidueWorkday(integer);
        return R.ok(rsp);
    }

    @Override
    public R<PageR<AssetClassifyClientListRSP>> clientPageList(@Valid AssetClassifyClientListREQ req) {
        return R.ok(assetClassifyClientService.pageList(req));
    }

    @Override
    public R<AssetClassifyClientDetailRSP> clientDetail(@Valid AssetClassifyClientDetailREQ req) {
        return R.ok(assetClassifyClientService.clientDetail(req));
    }

    @Override
    public R<Void> clientReviewSubmit(SinglePkREQ req) {
        assetClassifyClientService.clientReviewSubmit(req.getId());
        return R.ok();
    }

    @Override
    public R<AssetClassifyCheckContentPackRSP> checkReport(@Valid AssetClassifyCheckContentREQ req) {
        return R.ok(assetClassifyCheckContentService.getCheckReport(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "assetClassifyClientId", checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.ASSET_CLASSIFY_REVIEW)
    public R<Void> checkReportUpdate(@Valid AssetClassifyCheckContentModifyREQ req) {
        assetClassifyCheckContentService.checkReportUpdate(req);
        return R.ok();
    }

    @Override
    public R<List<AssetClassifyHistoryRSP>> history(@Valid AssetClassifyCheckContentREQ req) {
        return R.ok(assetClassifyClientService.history(req));
    }

    @Override
    /*@DataAuthCheck(keyFieldName = "id", checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.ASSET_CLASSIFY_REVIEW)*/
    public R<Void> clientModify(@Valid AssetClassifyClientModifyREQ req) {
        assetClassifyClientService.clientModify(req);
        return R.ok();
    }

    @Override
    public R<Void> saveClientClassifyResult(@Valid AssetClassifyClientResultSaveREQ req) {
        assetClassifyClientService.saveSuggestResult(req.getId(), req.getClassifyResult());
        return R.ok();
    }

    @Override
    public R<AssetClassifyClientDetailRSP> clientDetailByReq(@Valid AssetClassifyClientDetailGeneralREQ req) {
        return R.ok(assetClassifyClientService.clientDetailByReq(req));
    }

    @Override
    public R<List<AssetClassifyClientWithdrawalRatioListRsp>> withdrawalRatio(AssetClassifyClientWithdrawalRatioListReq req) {
        return R.ok(assetClassifyClientWithdrawalRatioService.listRatios(req));
    }

    @Override
    public R<Void> modifyWithdrawalRatio(AssetClassifyClientWithdrawalRatioModifyReq req) {
        assetClassifyClientWithdrawalRatioService.modifyRatioBatch(req);
        return R.ok();
    }

    @Override
    public R<List<AssetClassifyClientRiskFactorRSP>> riskFactor(SinglePkREQ req) {
        return R.ok(assetClassifyClientRiskFactorService.listRiskFactor(req));
    }

    @Override
    public R<Void> modifyRiskFactor(AssetClassifyClientRiskFactorModifyREQ req) {
        assetClassifyClientRiskFactorService.modifyRiskFactorBatch(req);
        return R.ok();
    }

    @Override
    public R<String> lastestVersionCode(SinglePkREQ req) {
        return R.ok(assetClassifyService.lastestVersionCode(req.getId()));
    }

    @Override
    public R<AssetManualDivisionRSP> manualDivision(@Valid AssetManualDivisionREQ req) {
        return R.ok(assetClassifyService.init(req.getYear(), req.getQuarter()));
    }

    @Override
    public R<Void> initBalance(AssetBalanceInitREQ req) {
        assetClassifyService.initBalance(req.getYear(), req.getQuarter());
        return R.ok();
    }

    @Override
    public void downloadSummaryFile(@Valid SinglePkREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租赁资产五级分类认定汇总审批表" + GlobalConstants.OFFICE_WORD_SUFFIX, StandardCharsets.UTF_8.name()));
            assetClassifyService.downloadSummaryFile(req.getId(), httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载资产五级分类认定汇总审批表发生未知异常", e);
            throw new MithrasException("下载资产五级分类认定汇总审批表发生未知异常");
        }
    }

    @Override
    public R<PageR<MidQuarterClientListRSP>> midQuarterClientList(MidQuarterClientListREQ req) {

        return R.ok(assetClassifyClientService.clientListByMidQuarter(req));
    }

    @Override
    public R<AssetManualDivisionRSP> midQuarterDivision(MidQuarterClientListREQ req) {
        return R.ok(assetClassifyService.midQuarterInit(req));
    }

    @Override
    public R<Void> remove(@Valid AssetClassifyClientRemoveREQ req) {
        assetClassifyService.deleteClient(req);
        return R.ok();
    }

}
