package cn.zswltech.mithras.application.orchestration.adapter.contract;

import cn.zswltech.mithras.contract.core.ContractMaterialsPort;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ContractMaterialsPortAdapter implements ContractMaterialsPort {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType) throws IOException {
        return materialsListService.add(inputStream, fileName, belongId, materialsType, materialsSubType, businessType);
    }

    @Override
    public void remove(List<Long> ids) {
        materialsListService.remove(ids);
    }
}
