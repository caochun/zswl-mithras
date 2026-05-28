package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.FileDownloadREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingBatchDownloadREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Api(tags = "融资管理-基本信息相关接口")
@RequestMapping(path = "/fund/financing/baseinfo")
public interface FundFinancingBaseInfoApi {
    @ApiOperation("修改基本信息")
    @PostMapping(path = "/modify")
    R<Void> modify(@RequestBody @Valid FundFinancingBaseInfoModifyREQ req);

    @ApiOperation("基本信息详情")
    @PostMapping(path = "/detail")
    R<FundFinancingBaseInfoDetailRSP> detail(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("修改计划贷款时间")
    @PostMapping(path = "/planloandate/modify")
    R<Void> modifyPlanLoanDate(@RequestBody @Valid FundFinancingPlanLoanDateModifyREQ req);

    @ApiOperation("修改实际贷款时间")
    @PostMapping(path = "/actualloandate/modify")
    R<Void> modifyActualLoanDate(@RequestBody @Valid FundFinancingActualLoanDateModifyREQ req);

    @ApiOperation("编辑担保金额时计算剩余担保额度")
    @PostMapping(path = "/guaranteeinfo/remainingamount")
    R<Long> calcRemainingGuaranteeAmount(@RequestBody @Valid FundFinancingCalcRemainingGuaranteeAmountREQ req);

    @ApiOperation("获取融资起息信息")
    @PostMapping(path = "/carryinterest/info")
    R<FundFinancingCarryInterestInfoRSP> getCarryInterestInfo(@RequestBody @Valid SingleFinancingIdREQ req);

    @PostMapping(path = "/http")
    R<Void> http();


    @ApiOperation("批量下载")
    @PostMapping("/batchDownload")
    void batchDownload(@RequestBody @Valid FundFinancingBatchDownloadREQ req);

    @ApiOperation("下载文件")
    @GetMapping("/download")
    void download(@Valid FileDownloadREQ fileDownloadREQ);
}
