package cn.zswltech.mithras.projectprocess.versioning.projpricing.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingInfoModule;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.versioning.handler.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.handler.ProjPricingLibAbstractHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评审 资料清单
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:53 PM
 */
@Component
public class ProjPricingMaterialsListLibHandlerImpl extends ProjPricingLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {
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
    public List<MaterialsList> listNeedHandleEntity(Long mainId) {
        return materialsListLibHandlerProxy.listNeedHandleEntity(mainId, businessModuleName());
    }

    @Override
    public List<MaterialsListLib> listNeedHandleLib(Long mainId, String version) {
        return materialsListLibHandlerProxy.listNeedHandleLib(mainId, version, businessModuleName());
    }

    @Override
    public String entityMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public String libMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public ProjPricingInfoModule getSubModule() {
        return ProjPricingInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

}
