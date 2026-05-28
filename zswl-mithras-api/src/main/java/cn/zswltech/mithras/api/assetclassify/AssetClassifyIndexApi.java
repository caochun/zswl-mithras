package cn.zswltech.mithras.api.assetclassify;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName AssetClassifyIndexApi
 * @Description 五级分类首页
 * @Author jackerhe
 * @Date 2023/1/4 1:56 下午
 * @Version 1.0
 **/
@Api(tags = "五级分类-首页")
public interface AssetClassifyIndexApi {

    @ApiOperation("季度选择")
    @PostMapping("/assetclassify/quarter/select")
    R<List<QuarterDetailRSP>> quarterSelect(@RequestBody @Valid QuarterDetailREQ quarterDetailREQ);

    @ApiOperation("定级流程")
    @PostMapping("/assetclassify/grade/process")
    R<AssetClassifyNodeRSP> gradeProcess(@RequestBody @Valid AssetClassifyNodeREQ req);

    @ApiOperation("流程剩余工作日")
    @PostMapping("/assetclassify/residue/workday")
    R<AssetClassifyResidueWorkdayRSP> residueWorkday(@RequestBody @Valid AssetClassifyNodeREQ req);

    @ApiOperation("资产五级分类客户列表")
    @PostMapping("/assetclassify/client/pagelist")
    R<PageR<AssetClassifyClientListRSP>> clientPageList(@RequestBody @Valid AssetClassifyClientListREQ req);

    @ApiOperation("客户详情")
    @PostMapping("/assetclassify/client/detail")
    R<AssetClassifyClientDetailRSP> clientDetail(@RequestBody @Valid AssetClassifyClientDetailREQ req);

    @ApiOperation("客户详情复合提交")
    @PostMapping("/assetclassify/client/review/submit")
    R<Void> clientReviewSubmit(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("检查内容")
    @PostMapping("/assetclassify/client/check/report")
    R<AssetClassifyCheckContentPackRSP> checkReport(@RequestBody @Valid AssetClassifyCheckContentREQ req);

    @ApiOperation("检查内容修改")
    @PostMapping("/assetclassify/client/check/report/modify")
    R<Void> checkReportUpdate(@RequestBody @Valid AssetClassifyCheckContentModifyREQ req);

    @ApiOperation("客户分类历史")
    @PostMapping("/assetclassify/client/history")
    R<List<AssetClassifyHistoryRSP>> history(@RequestBody @Valid AssetClassifyCheckContentREQ req);

    @ApiOperation("修改客户信息")
    @PostMapping("/assetclassify/client/modify")
    R<Void> clientModify(@RequestBody @Valid AssetClassifyClientModifyREQ req);

    @ApiOperation("保存客户分类结果")
    @PostMapping("/assetclassify/client/classifyresult/save")
    R<Void> saveClientClassifyResult(@RequestBody @Valid AssetClassifyClientResultSaveREQ req);

    @ApiOperation("查询客户详情-自定义查询条件")
    @PostMapping("/assetclassify/client/general/list")
    R<AssetClassifyClientDetailRSP> clientDetailByReq(@RequestBody @Valid AssetClassifyClientDetailGeneralREQ req);


    @ApiOperation("查询拨备计提")
    @PostMapping("/assetclassify/client/withdrawal/ratio")
    R<List<AssetClassifyClientWithdrawalRatioListRsp>> withdrawalRatio(@RequestBody @Valid AssetClassifyClientWithdrawalRatioListReq req);

    @ApiOperation("修改拨备计提")
    @PostMapping("/assetclassify/client/withdrawal/ratio/modify")
    R<Void> modifyWithdrawalRatio(@RequestBody @Valid AssetClassifyClientWithdrawalRatioModifyReq req);

    @ApiOperation("查询风险因子")
    @PostMapping("/assetclassify/client/risk/factor")
    R<List<AssetClassifyClientRiskFactorRSP>> riskFactor(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("修改风险因子")
    @PostMapping("/assetclassify/client/risk/factor/modify")
    R<Void> modifyRiskFactor(@RequestBody @Valid AssetClassifyClientRiskFactorModifyREQ req);

    @ApiOperation("编辑区查询最新的文件版本数据")
    @PostMapping("/assetclassify/last/version")
    R<String> lastestVersionCode(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("手动初分")
    @PostMapping("/assetclassify/manual/initial/division")
    R<AssetManualDivisionRSP> manualDivision(@RequestBody @Valid AssetManualDivisionREQ req);

    @ApiOperation("删除客户")
    @PostMapping("/assetclassify/client/remove")
    R<Void> remove(@RequestBody @Valid AssetClassifyClientRemoveREQ req);


    @ApiOperation("手动初始化季度资产余额")
    @PostMapping("/assetclassify/manual/initBalance")
    R<Void> initBalance(@RequestBody @Valid AssetBalanceInitREQ req);

    @ApiOperation("下载汇总审批表")
    @PostMapping("/assetclassify/summaryfile/download")
    void downloadSummaryFile(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("季中初分客户选择列表")
    @PostMapping("/assetclassify/client/clientList")
    R<PageR<MidQuarterClientListRSP>> midQuarterClientList(@RequestBody @Valid MidQuarterClientListREQ req);

    @ApiOperation("季中初分")
    @PostMapping("/assetclassify/manual/initial/midQuarterDivision")
    R<AssetManualDivisionRSP> midQuarterDivision(@RequestBody @Valid MidQuarterClientListREQ req);
}
