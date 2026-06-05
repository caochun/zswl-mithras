package cn.zswltech.mithras.creditreport.service.lib.creditreport;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportClientItem;
import cn.zswltech.mithras.creditreport.service.CreditReportClientItemService;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 * @since
 */
@Service
public class CreditReportMaterialsListLibHandler
        extends CreditReportLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {


    @Resource
    private MaterialsListLibHandlerProxy materialsListLibHandlerProxy;
    @Resource
    private CreditReportClientItemService creditReportClientItemService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private MaterialsListLibMapper materialsListLibMapper;

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
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByBaseInfoId(mainId);
        if (ObjectUtil.isEmpty(clientItems)) {
            return ListUtil.empty();
        }
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .in(MaterialsList::getBelongId, clientItems.stream().map(CreditReportClientItem::getId).collect(Collectors.toList()))
                .eq(MaterialsList::getBusinessType, businessModuleName()));
    }

    @Override
    public List<MaterialsListLib> listNeedHandleLib(Long mainId, String version) {
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByBaseInfoId(mainId);
        if (ObjectUtil.isEmpty(clientItems)) {
            return ListUtil.empty();
        }
        return materialsListLibMapper.selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                .eq(MaterialsListLib::getVersion, version)
                .in(MaterialsListLib::getOriginId, clientItems.stream().map(CreditReportClientItem::getId).collect(Collectors.toList()))
                .eq(MaterialsListLib::getBusinessType, businessModuleName()));
    }

    @Override
    public String entityMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public String libMainIdFieldName() {
        return "belong_id";
    }

}
