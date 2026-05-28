package cn.zswltech.mithras.service.controller.leaseholdproperty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseOCRApi;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseOCRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/15 11:51
 */
@Slf4j
@RestController
public class LeaseOCRController implements LeaseOCRApi {

    @Resource
    private LeaseOCRService leaseOCRService;

    @Override
    public R<List<String>> fileNameComparison(LeaseFileNameComparisonREQ req) {
        return leaseOCRService.fileNameComparison(req);
    }
}
