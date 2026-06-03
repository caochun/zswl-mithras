package cn.zswltech.mithras.fund.application.lib.financing.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;

import java.util.Set;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
public abstract class FundFinancingAbstractLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    public String libMainIdFieldName() {
        return "financing_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "financing_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return null;
    }

    @Override
    protected String businessModuleName() {
        return "FUND_FINANCING";
    }

    public abstract FundFinancingLibModelEnum getSubModule();

    public abstract boolean needHandle(Long mainId);
}
