package cn.zswltech.mithras.leaseholdproperty.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseVersionApi;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseReviewEffectREQ;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LeaseVersionController implements LeaseVersionApi {
    @Resource
    private LeaseVersionApplicationService leaseVersionApplicationService;

    @Override
    public R<Void> effect(LeaseReviewEffectREQ param) {
        return leaseVersionApplicationService.effect(param);
    }
}
