package cn.zswltech.mithras.service.application.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPledgeApplicationService;
import cn.zswltech.mithras.dto.fund.financing.pledge.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FinancingPledgeController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/20 2:45 下午
 * @Version 1.0
 **/
@Service
public class FundFinancingPledgeFacade implements FundFinancingPledgeApplicationService {

    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;

    @Override
    public R<List<FundFinancingPledgeProjListRSP>> projList(@Valid FundFinancingPledgeProjListREQ req) {
        return R.ok(fundFinancingPledgeInfoService.projList(req));
    }

    @Override
    public R<List<FundFinancingPledgeContractListRSP>> contractList(@Valid FundFinancingPledgeContractListREQ req) {
        return R.ok(fundFinancingPledgeInfoService.contractList(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING)
    @Override
    public R<Void> create(@Valid FundFinancingPledgeCreateREQ req) {
        fundFinancingPledgeInfoService.create(req);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING, mapperClass = FundFinancingPledgeInfoMapper.class)
    @Override
    public R<Void> modify(@Valid FundFinancingPledgeModifyREQ req) {
        fundFinancingPledgeInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<FundFinancingPledgeListRSP>> list(@Valid FundFinancingPledgeListREQ req) {
        return R.ok(fundFinancingPledgeInfoService.list(req));
    }

    @Override
    public R<FundFinancingPledgeDetailRSP> detail(@Valid FundFinancingPledgeDetailREQ req) {
        return R.ok(fundFinancingPledgeInfoService.detail(req));
    }

    @DataAuthCheck(keyFieldName = "pledgeId", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING, mapperClass = FundFinancingPledgeInfoMapper.class)
    @Override
    public R<Void> delete(@Valid FundFinancingPledgeDetailREQ req) {
        fundFinancingPledgeInfoService.delete(req.getPledgeId());
        return R.ok();
    }

    @Override
    public R<List<FundFinancingContractInfoListRSP>> contractSearch(FundFinancingContractInfoListREQ req) {
        return R.ok(fundFinancingPledgeInfoService.contractSearch(req));
    }
}
