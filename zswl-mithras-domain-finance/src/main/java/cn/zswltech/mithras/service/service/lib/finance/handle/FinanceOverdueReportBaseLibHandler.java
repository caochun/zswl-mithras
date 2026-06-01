package cn.zswltech.mithras.service.service.lib.finance.handle;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueReportBaseDetailRSP;
import cn.zswltech.mithras.service.enums.projreview.FinanceOverdueModule;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBaseLib;
import cn.zswltech.mithras.service.service.lib.finance.FinanceOverdueAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class FinanceOverdueReportBaseLibHandler extends FinanceOverdueAbstractHandler<FinanceOverdueReportBaseLib, FinanceOverdueReportBase, FinanceOverdueReportBaseDetailRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("approvalStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected FinanceOverdueReportBaseLib entity2Lib(FinanceOverdueReportBase f) {
        return BeanUtil.copyProperties(f, FinanceOverdueReportBaseLib.class);
    }

    @Override
    protected FinanceOverdueReportBase lib2Entity(FinanceOverdueReportBaseLib t) {
        return BeanUtil.copyProperties(t, FinanceOverdueReportBase.class);
    }

    @Override
    protected FinanceOverdueReportBaseDetailRSP lib2Rsp(FinanceOverdueReportBaseLib f) {
        return BeanUtil.copyProperties(f, FinanceOverdueReportBaseDetailRSP.class);
    }

    @Override
    public FinanceOverdueModule getSubModule() {
        return FinanceOverdueModule.BASE_INFO;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }
}
