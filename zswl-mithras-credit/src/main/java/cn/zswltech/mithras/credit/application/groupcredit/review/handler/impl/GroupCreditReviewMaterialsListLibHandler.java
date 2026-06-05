package cn.zswltech.mithras.credit.application.groupcredit.review.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewInfoModule;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.credit.application.groupcredit.review.handler.GroupCreditReviewLibAbstractHandler;
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
public class GroupCreditReviewMaterialsListLibHandler
        extends GroupCreditReviewLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

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
    public GroupCreditReviewInfoModule getSubModule() {
        return GroupCreditReviewInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
