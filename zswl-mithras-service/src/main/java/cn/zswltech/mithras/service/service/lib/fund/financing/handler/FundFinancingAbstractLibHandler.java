package cn.zswltech.mithras.service.service.lib.fund.financing.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingLibModelEnum;
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
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.FUND_FINANCING;
    }

    public abstract FundFinancingLibModelEnum getSubModule();

    public abstract boolean needHandle(Long mainId);
}
