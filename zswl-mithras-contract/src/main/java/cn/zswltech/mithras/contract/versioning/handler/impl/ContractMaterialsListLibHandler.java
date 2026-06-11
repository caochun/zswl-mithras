package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractMaterialsListLibHandler
        extends ContractLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {


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
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
