package cn.zswltech.mithras.service.flow.file.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 项目定价
 *
 * @author wangchuanhao
 * @date 2022/11/23 11:18 AM
 */
@Component
public class ProjPricingFileHandlerImpl implements IFileHandler {

    @Resource
    private MaterialsListService materialsListService;
    
    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {
        fileTypeCheck(processResp, materialsType);
    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {
        fileTypeCheck(processResp, materialsList.getMaterialsType());
    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListService.list(BusinessModuleEnum.PROJ_PRICING.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PROJ_PRICING;
    }
    
    private void fileTypeCheck(ProcessResp processResp, String materialsType) {
        ProjPricingMaterialsEnum projPricingMaterialsEnum = ProjPricingMaterialsEnum.getByName(materialsType);
        if (Objects.isNull(projPricingMaterialsEnum)) {
            // 不需要校验的类型
            return;
        }
        switch (projPricingMaterialsEnum) {
            case BUSINESS_PRICING_APPROVAL_MEETING_REPORT:
                if (!ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                    throw new MithrasException(ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.getDisplay() + "只能在项目定价—财务主管2节点操作");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(ProjPricingMaterialsEnum.getByName(materialsType)).map(ProjPricingMaterialsEnum::getDisplay).orElse(null);
    }

}
