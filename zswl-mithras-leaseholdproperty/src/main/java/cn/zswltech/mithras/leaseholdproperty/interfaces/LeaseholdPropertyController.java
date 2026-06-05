package cn.zswltech.mithras.leaseholdproperty.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseholdPropertyApi;
import cn.zswltech.mithras.dto.SingleFileREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyRSP;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseholdPropertyApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LeaseholdPropertyController implements LeaseholdPropertyApi {
    @Resource
    private LeaseholdPropertyApplicationService leaseholdPropertyApplicationService;

    @Override
    public R<PageR<LeaseholdPropertyRSP>> list(LeaseholdPropertyREQ param) {
        return leaseholdPropertyApplicationService.list(param);
    }

    @Override
    public R<Void> excelImport(SingleFileREQ file) {
        return leaseholdPropertyApplicationService.excelImport(file);
    }
}
