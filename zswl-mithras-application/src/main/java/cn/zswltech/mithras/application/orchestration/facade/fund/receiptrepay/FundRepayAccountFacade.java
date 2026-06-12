package cn.zswltech.mithras.application.orchestration.facade.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundRepayAccountApplicationService;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.FundReceiptRepayAddSubAuthChecker;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.FundReceiptRepayRemoveSubAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundRepayAccountMapper;
import cn.zswltech.mithras.fund.application.receiptrepay.FundRepayAccountService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-我方付款账户
 * @date 2023-02-22
 */
@Service
public class FundRepayAccountFacade implements FundRepayAccountApplicationService {

    @Resource
    private FundRepayAccountService fundRepayAccountService;

    @Override
    @DataAuthCheck(keyFieldName = "receiptRepayId", checkerClass = FundReceiptRepayAddSubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY")
    public R<Void> add(FundRepayAccountAddREQ req) {
        fundRepayAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundReceiptRepayModifySubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY", mapperClass = FundRepayAccountMapper.class)
    public R<Void> modify(FundRepayAccountModifyREQ req) {
        fundRepayAccountService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundRepayAccountListRSP>> list(FundRepayAccountListREQ req) {
        return R.ok(fundRepayAccountService.list(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundReceiptRepayRemoveSubAuthChecker.class, businessModule = "FUND_RECEIPT_REPAY", mapperClass = FundRepayAccountMapper.class)
    public R<Void> remove(FundRepayAccountRemoveREQ req) {
        fundRepayAccountService.remove(req);
        return R.ok();
    }

}