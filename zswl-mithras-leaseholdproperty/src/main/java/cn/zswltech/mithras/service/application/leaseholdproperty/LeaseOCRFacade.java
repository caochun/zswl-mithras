package cn.zswltech.mithras.service.application.leaseholdproperty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseOCRApplicationService;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseOCRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/15 11:51
 */
@Slf4j
@Service
public class LeaseOCRFacade implements LeaseOCRApplicationService {

    @Resource
    private LeaseOCRService leaseOCRService;

    @Override
    public R<List<String>> fileNameComparison(LeaseFileNameComparisonREQ req) {
        return leaseOCRService.fileNameComparison(req);
    }
}
