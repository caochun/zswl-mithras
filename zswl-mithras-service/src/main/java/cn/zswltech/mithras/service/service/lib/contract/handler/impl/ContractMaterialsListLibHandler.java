package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
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
        return materialsListLibHandlerProxy.listNeedHandleEntity(mainId, businessModuleEnum());
    }

    @Override
    public List<MaterialsListLib> listNeedHandleLib(Long mainId, String version) {
        return materialsListLibHandlerProxy.listNeedHandleLib(mainId, version, businessModuleEnum());
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

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.CONTRACT;
    }

}
