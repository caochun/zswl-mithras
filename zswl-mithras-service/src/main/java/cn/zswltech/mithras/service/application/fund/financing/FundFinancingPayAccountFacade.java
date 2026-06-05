package cn.zswltech.mithras.service.application.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPayAccountApplicationService;
import cn.zswltech.mithras.dto.fund.financing.payaccount.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.fund.application.convert.financing.FundFinancingConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingPayAccountMapper;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FundFinancingPayAccountController
 * @Description 对方还款账户
 * @Author jackerhe
 * @Date 2023/2/21 11:36 上午
 * @Version 1.0
 **/
@Service
public class FundFinancingPayAccountFacade implements FundFinancingPayAccountApplicationService {

    @Resource
    private FundFinancingPayAccountService fundFinancingPayAccountService;
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;
    @Resource
    private FundFinancingConverter fundFinancingConverter;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;

    @Override
    public R<List<FundFinancingPayAccountBankREQ>> bankList(@Valid FundFinancingPayAccountBankREQ req) {
        List<BaseDataBankAccount> bankAccounts = baseDataBankAccountService.list(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .like(ObjectUtil.isNotNull(req.getAccountBank()), BaseDataBankAccount::getAccountBank, req.getAccountBank()));
        List<FundFinancingPayAccountBankREQ> rsps = new ArrayList<>();
        if(ObjectUtil.isEmpty(bankAccounts)){
            return R.ok();
        }
        bankAccounts.forEach(bank->{
            FundFinancingPayAccountBankREQ rsp = new FundFinancingPayAccountBankREQ();
            rsp.setAccountBank(bank.getAccountBank());
            rsps.add(rsp);
        });
        return R.ok(rsps);
    }

    @Override
    public R<List<FundFinancingPayAccountBankRSP>> bankInfo(@Valid FundFinancingPayAccountBankREQ req) {
        List<BaseDataBankAccount> bankAccounts = baseDataBankAccountService.list(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getAccountBank()), BaseDataBankAccount::getAccountBank, req.getAccountBank())
                .like(ObjectUtil.isNotNull(req.getAccountNumber()), BaseDataBankAccount::getAccountNumber, req.getAccountNumber()));
        if(ObjectUtil.isEmpty(bankAccounts)){
            return R.ok();
        }
        List<FundFinancingPayAccountBankRSP> rsps = new ArrayList<>();
        bankAccounts.forEach(base ->{
            rsps.add(fundFinancingConverter.modifyReq2Entity(base));
        });
        return R.ok(rsps);
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING)
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> create(@Valid FundFinancingPayAccountCreateREQ req) {
        fundFinancingPayAccountService.checkExist(req.getBankAccountId(), req.getFinancingId(), null);
        fundFinancingPayAccountService.save(BeanUtil.copyProperties(req, FundFinancingPayAccount.class));
        fundFinancingBaseInfoService.tryUpdateChangeOther(req.getFinancingId());
        return R.ok();
    }

    @Override
    public R<List<FundFinancingPayAccountListRSP>> list(@Valid FundFinancingPayAccountListREQ req) {
        List<FundFinancingPayAccount> payAccounts = fundFinancingPayAccountService.list(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                .eq(FundFinancingPayAccount::getFinancingId, req.getFinancingId()));
        if(ObjectUtil.isEmpty(payAccounts)){
            return R.ok();
        }
        List<FundFinancingPayAccountListRSP> rsps = BeanUtil.copyToList(payAccounts, FundFinancingPayAccountListRSP.class);
        return R.ok(rsps);
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING, mapperClass = FundFinancingPayAccountMapper.class)
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> modify(@Valid FundFinancingPayAccountModifyREQ req) {
        fundFinancingPayAccountService.checkExist(req.getBankAccountId(), req.getFinancingId(), req.getId());
        fundFinancingPayAccountService.updateById(BeanUtil.copyProperties(req, FundFinancingPayAccount.class));
        fundFinancingBaseInfoService.tryUpdateChangeOther(req.getFinancingId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING, mapperClass = FundFinancingPayAccountMapper.class)
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> delete(@Valid FundFinancingPayAccountREQ req) {
        FundFinancingPayAccount financingPayAccount = fundFinancingPayAccountService.getById(req.getId());
        fundFinancingBaseInfoService.tryUpdateChangeOther(financingPayAccount.getFinancingId());
        fundFinancingPayAccountService.removeById(req.getId());
        return R.ok();
    }
}
