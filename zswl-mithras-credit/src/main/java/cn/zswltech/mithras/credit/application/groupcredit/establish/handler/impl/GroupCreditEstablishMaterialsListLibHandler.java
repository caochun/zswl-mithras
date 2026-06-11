package cn.zswltech.mithras.credit.application.groupcredit.establish.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishInfoModule;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.document.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.credit.application.groupcredit.establish.handler.GroupCreditEstablishLibAbstractHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 集团授信立项版本处理器
 *
 * @author wangchuanhao
 * @date 2022/11/11 16:49 PM
 */
@Component
public class GroupCreditEstablishMaterialsListLibHandler
        extends GroupCreditEstablishLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

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
    public GroupCreditEstablishInfoModule getSubModule() {
        return GroupCreditEstablishInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
