package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.version.FileCompareDeclaration;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资金收付款版本处理器
 *
 * @author wangchuanhao
 * @date 2022/2/20 16:49 PM
 */
@Component
public class FundReceiptRepayMaterialsListLibHandler
        extends AbstractFundReceiptRepayLibHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

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
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.MATERIALS_LIST;
    }

}
