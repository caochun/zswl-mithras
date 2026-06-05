package cn.zswltech.mithras.assetclassify.controller;

import cn.zswltech.mithras.api.assetclassify.AssetClassifyIndexApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.*;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyIndexApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class AssetClassifyIndexController implements AssetClassifyIndexApi {
    @Resource
    private AssetClassifyIndexApplicationService assetClassifyIndexApplicationService;

    @Override
    public R<List<QuarterDetailRSP>> quarterSelect(@Valid QuarterDetailREQ quarterDetailREQ) {
        return assetClassifyIndexApplicationService.quarterSelect(quarterDetailREQ);
    }

    @Override
    public R<AssetClassifyNodeRSP> gradeProcess(@Valid AssetClassifyNodeREQ req) {
        return assetClassifyIndexApplicationService.gradeProcess(req);
    }

    @Override
    public R<AssetClassifyResidueWorkdayRSP> residueWorkday(@Valid AssetClassifyNodeREQ req) {
        return assetClassifyIndexApplicationService.residueWorkday(req);
    }

    @Override
    public R<PageR<AssetClassifyClientListRSP>> clientPageList(@Valid AssetClassifyClientListREQ req) {
        return assetClassifyIndexApplicationService.clientPageList(req);
    }

    @Override
    public R<AssetClassifyClientDetailRSP> clientDetail(@Valid AssetClassifyClientDetailREQ req) {
        return assetClassifyIndexApplicationService.clientDetail(req);
    }

    @Override
    public R<Void> clientReviewSubmit(SinglePkREQ req) {
        return assetClassifyIndexApplicationService.clientReviewSubmit(req);
    }

    @Override
    public R<AssetClassifyCheckContentPackRSP> checkReport(@Valid AssetClassifyCheckContentREQ req) {
        return assetClassifyIndexApplicationService.checkReport(req);
    }

    @Override
    public R<Void> checkReportUpdate(@Valid AssetClassifyCheckContentModifyREQ req) {
        return assetClassifyIndexApplicationService.checkReportUpdate(req);
    }

    @Override
    public R<List<AssetClassifyHistoryRSP>> history(@Valid AssetClassifyCheckContentREQ req) {
        return assetClassifyIndexApplicationService.history(req);
    }

    @Override
    public R<Void> clientModify(@Valid AssetClassifyClientModifyREQ req) {
        return assetClassifyIndexApplicationService.clientModify(req);
    }

    @Override
    public R<Void> saveClientClassifyResult(@Valid AssetClassifyClientResultSaveREQ req) {
        return assetClassifyIndexApplicationService.saveClientClassifyResult(req);
    }

    @Override
    public R<AssetClassifyClientDetailRSP> clientDetailByReq(@Valid AssetClassifyClientDetailGeneralREQ req) {
        return assetClassifyIndexApplicationService.clientDetailByReq(req);
    }

    @Override
    public R<List<AssetClassifyClientWithdrawalRatioListRsp>> withdrawalRatio(AssetClassifyClientWithdrawalRatioListReq req) {
        return assetClassifyIndexApplicationService.withdrawalRatio(req);
    }

    @Override
    public R<Void> modifyWithdrawalRatio(AssetClassifyClientWithdrawalRatioModifyReq req) {
        return assetClassifyIndexApplicationService.modifyWithdrawalRatio(req);
    }

    @Override
    public R<List<AssetClassifyClientRiskFactorRSP>> riskFactor(SinglePkREQ req) {
        return assetClassifyIndexApplicationService.riskFactor(req);
    }

    @Override
    public R<Void> modifyRiskFactor(AssetClassifyClientRiskFactorModifyREQ req) {
        return assetClassifyIndexApplicationService.modifyRiskFactor(req);
    }

    @Override
    public R<String> lastestVersionCode(SinglePkREQ req) {
        return assetClassifyIndexApplicationService.lastestVersionCode(req);
    }

    @Override
    public R<AssetManualDivisionRSP> manualDivision(@Valid AssetManualDivisionREQ req) {
        return assetClassifyIndexApplicationService.manualDivision(req);
    }

    @Override
    public R<Void> initBalance(AssetBalanceInitREQ req) {
        return assetClassifyIndexApplicationService.initBalance(req);
    }

    @Override
    public void downloadSummaryFile(@Valid SinglePkREQ req) {
        assetClassifyIndexApplicationService.downloadSummaryFile(req);
    }

    @Override
    public R<PageR<MidQuarterClientListRSP>> midQuarterClientList(MidQuarterClientListREQ req) {
        return assetClassifyIndexApplicationService.midQuarterClientList(req);
    }

    @Override
    public R<AssetManualDivisionRSP> midQuarterDivision(MidQuarterClientListREQ req) {
        return assetClassifyIndexApplicationService.midQuarterDivision(req);
    }

    @Override
    public R<Void> remove(@Valid AssetClassifyClientRemoveREQ req) {
        return assetClassifyIndexApplicationService.remove(req);
    }

}
