package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.zswltech.mithras.creditreport.service.CreditReportMaterialPort;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

@Component
public class CreditReportMaterialPortAdapter implements CreditReportMaterialPort {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public List<MaterialsList> list(String businessType, List<String> materialsTypes, List<Long> belongIds) {
        return materialsListService.list(businessType, materialsTypes, belongIds);
    }

    @Override
    public void removeByIds(Collection<Long> ids) {
        materialsListService.removeByIds(ids);
    }

    @Override
    public Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String businessType) {
        return materialsListService.add(inputStream, fileName, belongId, materialsType, businessType);
    }
}
