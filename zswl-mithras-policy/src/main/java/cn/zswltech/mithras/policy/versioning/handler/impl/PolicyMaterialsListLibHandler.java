package cn.zswltech.mithras.policy.versioning.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.policy.versioning.handler.PolicyAbstractHandler;
import cn.zswltech.mithras.policy.versioning.handler.PolicyInfoModule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class PolicyMaterialsListLibHandler
        extends PolicyAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> {

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
    public PolicyInfoModule getSubModule() {
        return PolicyInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    /**
     * 文件暂不参与比对
     * @param newestVersion
     * @return
     */
    @Override
    public ChangeDTO checkActualChange(CommonVersion newestVersion) {
        ChangeDTO changeDTO = new ChangeDTO();
        changeDTO.setChangeFlag(false);
        changeDTO.setNeedApprovalChangeFlag(false);
        return changeDTO;
    }

}
