package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingApplicationService;
import cn.zswltech.mithras.dto.fund.financing.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingDeleteAuthChecker;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainCreateAuthChecker;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Slf4j
@Service
public class FundFinancingFacade implements FundFinancingApplicationService {
    @Resource
    private FundFinancingService fundFinancingService;

    @Override
    public R<FundFinancingListRSP> pageList(@Valid FundFinancingListREQ req) {
        return R.ok(fundFinancingService.pageList(req));
    }

    @DataAuthCheck(checkerClass = FundFinancingMainCreateAuthChecker.class, paramType = DataAuthCheck.ParamType.NO, businessModule = "FUND_FINANCING")
    @Override
    public R<Long> create(@Valid FundFinancingCreateREQ req) {
        if(Objects.equals(req.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())){
            return R.ok(fundFinancingService.createSyndication(req.getFinancingAmount()));
        }else {
            return R.ok(fundFinancingService.create(req.getFundCreditId(), req.getFinancingAmount(), req.getBusinessType()));
        }
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> close(@Valid SingleFinancingIdREQ req) {
        fundFinancingService.close(req.getFinancingId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<FundFinancingChangePreCheckRSP> changePreCheck(@Valid SingleFinancingIdREQ req) {
        return R.ok(fundFinancingService.changePreCheck(req.getFinancingId()));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingDeleteAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> delete(@Valid SingleFinancingIdREQ req) {
        fundFinancingService.deleteByFinancingId(req.getFinancingId());
        return R.ok();
    }
}
