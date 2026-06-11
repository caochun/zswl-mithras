package cn.zswltech.mithras.leaseholdproperty.application.lib.appraisalcompanywhitelist;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.document.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Component
public class AppraisalCompanyWhitelistMaterialLibHandler extends LibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> {
    @Resource
    private MaterialsListLibHandlerProxy materialsListLibHandlerProxy;

    @Override
    protected MaterialsListLib entity2Lib(MaterialsList f) {
        return materialsListLibHandlerProxy.entity2Lib(f);
    }

    @Override
    protected MaterialsList lib2Entity(MaterialsListLib t) {
        return materialsListLibHandlerProxy.lib2Entity(t);
    }

    @Override
    protected ListBaseRSP lib2Rsp(MaterialsListLib f) {
        return materialsListLibHandlerProxy.lib2Rsp(f);
    }

    @Override
    public String libMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.emptySet();
    }

    @Override
    protected String businessModuleName() {
        return "APPRAISAL_COMPANY_WHITELIST";
    }
}
