package cn.zswltech.mithras.service.application.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptAccountApplicationService;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptAccountMapper;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptAccountService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-对方收款账户
 * @date 2023-02-22
 */
@Service
public class FundReceiptAccountFacade implements FundReceiptAccountApplicationService {

    @Resource
    private FundReceiptAccountService fundReceiptAccountService;

    @Override
    @DataAuthCheck(keyFieldName = "receiptRepayId", checkerClass = FundReceiptRepayAddSubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY")
    public R<Void> add(FundReceiptAccountAddREQ req) {
        fundReceiptAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundReceiptRepayModifySubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY", mapperClass = FundReceiptAccountMapper.class)
    public R<Void> modify(FundReceiptAccountModifyREQ req) {
        fundReceiptAccountService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundReceiptAccountListRSP>> list(FundReceiptAccountListREQ req) {
        return R.ok(fundReceiptAccountService.list(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundReceiptRepayRemoveSubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY", mapperClass = FundReceiptAccountMapper.class)
    public R<Void> remove(FundReceiptAccountRemoveREQ req) {
        fundReceiptAccountService.remove(req);
        return R.ok();
    }

}