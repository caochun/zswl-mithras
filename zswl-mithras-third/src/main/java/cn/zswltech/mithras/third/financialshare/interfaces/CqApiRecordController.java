package cn.zswltech.mithras.third.financialshare.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.third.CqApiRecordApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordRSP;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.third.financialshare.application.api.CqApiRecordApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CqApiRecordController implements CqApiRecordApi {
    @Resource
    private CqApiRecordApplicationService cqApiRecordApplicationService;

    @Override
    public R<PageR<CqApiRecordRSP>> pageList(@Valid CqApiRecordREQ req) {
        return cqApiRecordApplicationService.pageList(req);
    }

    @Override
    public R<Void> push(@Valid SinglePkREQ req) {
        return cqApiRecordApplicationService.push(req);
    }

    @Override
    public R<Void> ignore(@Valid SinglePkREQ req) {
        return cqApiRecordApplicationService.ignore(req);
    }
}
