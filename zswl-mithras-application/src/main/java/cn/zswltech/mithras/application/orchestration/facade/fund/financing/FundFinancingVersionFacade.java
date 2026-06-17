package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingVersionApplicationService;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingEffectREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingSubmitREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingLibVersionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FundFinancingVersionController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/21 3:38 下午
 * @Version 1.0
 **/
@Service
public class FundFinancingVersionFacade implements FundFinancingVersionApplicationService {
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private FundFinancingLibVersionService fundFinancingLibVersionService;

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<String> effect(@Valid FundFinancingSubmitREQ req) {
        return R.ok(fundFinancingService.effect(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> carryInterest(@Valid FundFinancingEffectREQ req) {
        fundFinancingService.carryInterest(req);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> confirmChangeLpr(@Valid SingleFinancingIdREQ req) {
        fundFinancingService.confirmChangeLpr(req.getFinancingId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<String> submitEarlySettle(@Valid SingleFinancingIdREQ req) {
        return R.ok(fundFinancingService.submitEarlySettle(req.getFinancingId()));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> cancelChange(@Valid SingleFinancingIdREQ req) {
        fundFinancingLibVersionService.reset(req.getFinancingId());
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.FUND_FINANCING.name());
        }
        PageR<CommonVersionListRSP> data = fundFinancingLibVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@Valid FundFinancingVersionDiffREQ req) {
        CommonVersionDiffRSP commonVersionDiffRSP = fundFinancingLibVersionService.comparePreVersion(req.getId());
        changeModule(commonVersionDiffRSP);
        return R.ok(commonVersionDiffRSP);
    }

    private void changeModule(CommonVersionDiffRSP commonVersionDiffRSP) {
        //判断基本表数据
//        commonVersionDiffRSP.getModuleChanged().get(FundFinancingLibModelEnum.BASE_INFO.name());
        Map<String, List<Map<String, DiffValue>>> newData = commonVersionDiffRSP.getNewData();
        List<Map<String, DiffValue>> baseList = newData.get(FundFinancingLibModelEnum.BASE_INFO.name());
        if (ObjectUtil.isEmpty(baseList)) {
            return;
        }
        //实际 REPAY_ACTUAL
        DiffValue actualLoanDate = baseList.get(0).get("actualLoanDate");
        //计划
        DiffValue planLoanDate = baseList.get(0).get("planLoanDate");
        if (ObjectUtil.isNotEmpty(actualLoanDate) && actualLoanDate.getIsChange()) {
            commonVersionDiffRSP.getModuleChanged().put(FundFinancingLibModelEnum.REPAY_ACTUAL.name(), true);
        }
        if (ObjectUtil.isNotEmpty(planLoanDate) && planLoanDate.getIsChange()) {
            commonVersionDiffRSP.getModuleChanged().put(FundFinancingLibModelEnum.REPAY_ESTIMATE.name(), true);
        }
        Boolean baseChange = false;
        //处理实际
        if (ObjectUtil.isNotEmpty(actualLoanDate) && actualLoanDate.getIsChange()) {
            //修改基本信息状态
            for (Map<String, DiffValue> map : baseList) {
                for (String key : map.keySet()) {
                    if (ObjectUtil.equals("actualLoanDate", key) || ObjectUtil.equals("planLoanDate", key)) {
                        continue;
                    } else {
                        baseChange = map.get(key).getIsChange();
                    }
                    if (baseChange) {
                        break;
                    }
                }
                if (baseChange) {
                    break;
                }
            }
            //结束状态
            commonVersionDiffRSP.getModuleChanged().put(FundFinancingLibModelEnum.BASE_INFO.name(), baseChange);
        }
    }
}
