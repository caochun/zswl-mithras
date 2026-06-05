package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.PostMapping;
import cn.zswltech.mithras.api.contract.ContractStartRentNoticeApi;
import cn.zswltech.mithras.contract.application.contract.ContractStartRentNoticeApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractStartRentNoticeController implements ContractStartRentNoticeApi {
    @Resource
    private ContractStartRentNoticeApplicationService contractStartRentNoticeApplicationService;

    @Override
    public R<Void> savePlanNormal() {
        return contractStartRentNoticeApplicationService.savePlanNormal();
    }
}
