package cn.zswltech.mithras.finance.service.lib.finance.handle;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementDetailRSP;
import cn.zswltech.mithras.finance.enums.projreview.FinanceOverdueModule;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueSettlementLib;
import cn.zswltech.mithras.finance.service.lib.finance.FinanceOverdueAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class FinanceOverdueSettlementLibHandler extends FinanceOverdueAbstractHandler<FinanceOverdueSettlementLib, FinanceOverdueSettlement, FinanceOverdueSettlementDetailRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("approvalStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected FinanceOverdueSettlementLib entity2Lib(FinanceOverdueSettlement f) {
        return BeanUtil.copyProperties(f, FinanceOverdueSettlementLib.class);
    }

    @Override
    protected FinanceOverdueSettlement lib2Entity(FinanceOverdueSettlementLib t) {
        return BeanUtil.copyProperties(t, FinanceOverdueSettlement.class);
    }

    @Override
    protected FinanceOverdueSettlementDetailRSP lib2Rsp(FinanceOverdueSettlementLib f) {
        return BeanUtil.copyProperties(f, FinanceOverdueSettlementDetailRSP.class);
    }

    @Override
    public FinanceOverdueModule getSubModule() {
        return FinanceOverdueModule.OVERDUE_SETTLEMENT;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }


}
