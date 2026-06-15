package cn.zswltech.mithras.application.orchestration.adapter.ftp.versioning.monthly;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpMonthlyInfoModule;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.document.persistence.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.versioning.handler.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.AbstractFtpMonthlyLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件版本处理
 *
 * @author wangchuanhao
 * @date 2023/2/7 11:44 AM
 */
@Service
public class FtpMonthlyMaterialsListLibHandler
        extends AbstractFtpMonthlyLibHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

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
    public FtpMonthlyInfoModule getSubModule() {
        return FtpMonthlyInfoModule.MATERIALS_LIST;
    }

}
