package cn.zswltech.mithras.service.service.lib.projreview.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.service.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评审 资料清单
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:53 PM
 */
@Component
public class ProjReviewMaterialsListLibHandlerImpl extends ProjReviewLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {
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
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.PROJ_REVIEW;
    }

}
