package cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.service.convert.groupcreditestablish.GroupCreditEstablishBaseInfoConverter;
import cn.zswltech.mithras.service.enums.groupcreditestablish.GroupCreditEstablishInfoModule;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler.GroupCreditEstablishLibAbstractHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public GroupCreditEstablishInfoModule getSubModule() {
        return GroupCreditEstablishInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
