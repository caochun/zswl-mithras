package cn.zswltech.mithras.service.service.lib.creditreport;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;

import java.util.Collections;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class CreditReportLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    public String libMainIdFieldName() {
        return "asset_classify_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "asset_classify_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    protected String businessModuleName() {
        return "CREDIT_REPORT_SELECT";
    }
}
