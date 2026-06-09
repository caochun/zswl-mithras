package cn.zswltech.mithras.service.application.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingCollectAccountApplicationService;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingCollectAccountMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCollectAccount;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCollectAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FundFinancingCollectionAccountController
 * @Description 对方收款账户相关操作
 * @Author jackerhe
 * @Date 2023/2/20 7:31 下午
 * @Version 1.0
 **/
@Service
public class FundFinancingCollectAccountFacade implements FundFinancingCollectAccountApplicationService {

    @Resource
    private FundFinancingCollectAccountService fundFinancingCollectAccountService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> create(@Valid FundFinancingCollectAccountCreateREQ req) {
        fundFinancingCollectAccountService.save(BeanUtil.copyProperties(req, FundFinancingCollectAccount.class));
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        financingBaseInfoService.tryUpdateChangeOther(req.getFinancingId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = "FUND_FINANCING", mapperClass = FundFinancingCollectAccountMapper.class)
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> modify(@Valid FundFinancingCollectAccountModifyREQ req) {
        fundFinancingCollectAccountService.updateById(BeanUtil.copyProperties(req, FundFinancingCollectAccount.class));
        FundFinancingCollectAccount financingCollectAccount = fundFinancingCollectAccountService.getById(req.getId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        financingBaseInfoService.tryUpdateChangeOther(financingCollectAccount.getFinancingId());
        return R.ok();
    }

    @Override
    public R<List<FundFinancingCollectAccountListRSP>> list(@Valid FundFinancingCollectAccountListREQ req) {
        List<FundFinancingCollectAccount> list = fundFinancingCollectAccountService.list(Wrappers.<FundFinancingCollectAccount>lambdaQuery()
                .eq(FundFinancingCollectAccount::getFinancingId, req.getFinancingId())
                .orderByDesc(FundFinancingCollectAccount::getCreateTime));
        return R.ok(BeanUtil.copyToList(list, FundFinancingCollectAccountListRSP.class));
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = "FUND_FINANCING", mapperClass = FundFinancingCollectAccountMapper.class)
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> delete(@Valid FundFinancingCollectAccountDetailREQ req) {
        FundFinancingCollectAccount financingCollectAccount = fundFinancingCollectAccountService.getById(req.getId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        financingBaseInfoService.tryUpdateChangeOther(financingCollectAccount.getFinancingId());
        fundFinancingCollectAccountService.removeById(req.getId());
        return R.ok();
    }
}
