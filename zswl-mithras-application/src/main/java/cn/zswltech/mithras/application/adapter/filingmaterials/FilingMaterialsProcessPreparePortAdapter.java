package cn.zswltech.mithras.application.adapter.filingmaterials;

import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.filingmaterials.application.process.prepare.FilingMaterialsProcessPreparePort;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsFactory;
import org.springframework.stereotype.Component;

@Component
public class FilingMaterialsProcessPreparePortAdapter implements FilingMaterialsProcessPreparePort {

    @Override
    public String startProcess(String processType, Long businessId) {
        return FilingMaterialsFactory.getProcessor(processType).startProcess(new FilingBaseREQ(businessId));
    }

    @Override
    public void close(String processType, Long businessId) {
        FilingMaterialsFactory.getProcessor(processType).close(new FilingBaseREQ(businessId));
    }
}
