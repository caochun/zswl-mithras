package cn.zswltech.mithras.application.orchestration.adapter.customer.versioning;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.document.persistence.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.versioning.handler.MaterialsListLibHandlerProxy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 客户 自然人 资料清单
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:37 PM
 */
@Component
public class NormalMaterialsListLibHandlerImpl extends ClientLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

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
    public InfoModule getSubModule() {
        return InfoModule.NORMAL_MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.NORMAL.equals(clientType);
    }

    /**
     * 过滤出需要处理的编辑区数据 有过滤条件的自实现
     *
     * @param mainId
     * @return
     */
    @Override
    public List<MaterialsList> listNeedHandleEntity(Long mainId) {
        return materialsListLibHandlerProxy.listNeedHandleEntity(mainId, businessModuleName());
    }

    /**
     * 过滤出需要处理的版本区数据 有过滤条件的自实现
     *
     * @param mainId
     * @return
     */
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
    public List<MaterialsList> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }
}
