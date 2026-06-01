package cn.zswltech.mithras.service.service.lib.finance.handle;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationDetailRSP;
import cn.zswltech.mithras.service.enums.projreview.FinanceOverdueModule;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegrationLib;
import cn.zswltech.mithras.service.service.lib.finance.FinanceOverdueAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class FinanceOverdueIntegrationLibHandler extends FinanceOverdueAbstractHandler<FinanceOverdueIntegrationLib, FinanceOverdueIntegration, FinanceOverdueIntegrationDetailRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("approvalStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected FinanceOverdueIntegrationLib entity2Lib(FinanceOverdueIntegration f) {
        return BeanUtil.copyProperties(f, FinanceOverdueIntegrationLib.class);
    }

    @Override
    protected FinanceOverdueIntegration lib2Entity(FinanceOverdueIntegrationLib t) {
        return BeanUtil.copyProperties(t, FinanceOverdueIntegration.class);
    }

    @Override
    protected FinanceOverdueIntegrationDetailRSP lib2Rsp(FinanceOverdueIntegrationLib f) {
        return BeanUtil.copyProperties(f, FinanceOverdueIntegrationDetailRSP.class);
    }

    @Override
    public FinanceOverdueModule getSubModule() {
        return FinanceOverdueModule.OVERDUE_INTEGRATION;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }


}
