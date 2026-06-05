package cn.zswltech.mithras.leaseholdproperty.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseOCRApi;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseOCRApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LeaseOCRController implements LeaseOCRApi {
    @Resource
    private LeaseOCRApplicationService leaseOCRApplicationService;

    @Override
    public R<List<String>> fileNameComparison(LeaseFileNameComparisonREQ req) {
        return leaseOCRApplicationService.fileNameComparison(req);
    }
}
