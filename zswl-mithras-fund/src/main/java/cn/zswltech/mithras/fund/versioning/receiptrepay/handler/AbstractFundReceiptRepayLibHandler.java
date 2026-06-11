package cn.zswltech.mithras.fund.versioning.receiptrepay.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;

import java.util.Collections;
import java.util.Set;

/**
 * 资金收付款版本处理器
 *
 * @author wangchuanhao
 * @date 2023/2/20 15:26 PM
 */
public abstract class AbstractFundReceiptRepayLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    public String libMainIdFieldName() {
        return "receipt_repay_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "receipt_repay_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    protected String businessModuleName() {
        return "FUND_RECEIPT_REPAY";
    }

    public abstract FundReceiptRepayInfoModule getSubModule();
}
