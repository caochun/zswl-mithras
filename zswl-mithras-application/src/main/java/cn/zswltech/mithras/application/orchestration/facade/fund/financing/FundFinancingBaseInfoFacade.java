package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingBaseInfoApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.FileDownloadREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingBatchDownloadREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.fund.application.financial.FundFinancialSystemService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Service
public class FundFinancingBaseInfoFacade implements FundFinancingBaseInfoApplicationService {
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancialSystemService fundFinancialSystemService;

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> modify(@Valid FundFinancingBaseInfoModifyREQ req) {
        if(Objects.equals(req.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
            financingBaseInfoService.syndicationModify(req);
        }else{
            financingBaseInfoService.modify(req);
        }
        return R.ok();
    }

    @Override
    public R<FundFinancingBaseInfoDetailRSP> detail(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingBaseInfoService.detail(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> modifyPlanLoanDate(@Valid FundFinancingPlanLoanDateModifyREQ req) {
        financingBaseInfoService.modifyPlanLoanDate(req.getFinancingId(), LocalDateTimeUtil.parseDate(req.getPlanLoanDate(), DatePattern.NORM_DATE_PATTERN));
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> modifyActualLoanDate(@Valid FundFinancingActualLoanDateModifyREQ req) {
        financingBaseInfoService.modifyActualLoanDate(req.getFinancingId(), LocalDateTimeUtil.parseDate(req.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
        return R.ok();
    }

    @Override
    public R<Long> calcRemainingGuaranteeAmount(@Valid FundFinancingCalcRemainingGuaranteeAmountREQ req) {
        return R.ok(financingBaseInfoService.calcRemainingGuaranteeAmount(req));
    }

    @Override
    public R<FundFinancingCarryInterestInfoRSP> getCarryInterestInfo(@Valid SingleFinancingIdREQ req) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(req.getFinancingId());
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资信息不存在"));
        FundFinancingCarryInterestInfoRSP rsp = new FundFinancingCarryInterestInfoRSP();
        rsp.setRepayDay(financingBaseInfo.getRepayDay());
        if (Objects.nonNull(financingBaseInfo.getActualLoanDate())) {
            rsp.setActualLoanDate(LocalDateTimeUtil.format(financingBaseInfo.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
        }
        return R.ok(rsp);
    }

    @Override
    public R<Void> http() {
        fundFinancialSystemService.send();
        return R.ok();
    }

    @Override
    public void batchDownload(@Valid FundFinancingBatchDownloadREQ req){
        SpringContextHolder.getBean(FundDirectFinancingBaseInfoService.class).batchDownload(req);
    }

    @Override
    public void download(@Valid FileDownloadREQ req){
        SpringContextHolder.getBean(FundDirectFinancingBaseInfoService.class).download(req);

    }
}
