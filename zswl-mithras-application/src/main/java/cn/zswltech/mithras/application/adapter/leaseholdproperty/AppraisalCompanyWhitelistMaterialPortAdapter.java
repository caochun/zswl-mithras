package cn.zswltech.mithras.application.adapter.leaseholdproperty;

import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistMaterialPort;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AppraisalCompanyWhitelistMaterialPortAdapter implements AppraisalCompanyWhitelistMaterialPort {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public int countMaterials(String businessType, Long belongId) {
        return materialsListService.count(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, businessType)
                .eq(MaterialsList::getBelongId, belongId));
    }
}
