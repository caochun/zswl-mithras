package cn.zswltech.mithras.service.controller.third;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.third.CqApiRecordApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordRSP;
import cn.zswltech.mithras.system.service.ExceptionRequestInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/7/17
 * @description
 */
@RestController
public class CqApiRecordController implements CqApiRecordApi {
    @Resource
    private ExceptionRequestInfoService exceptionRequestInfoService;

    @Override
    public R<PageR<CqApiRecordRSP>> pageList(@Valid CqApiRecordREQ req) {
        return R.ok(exceptionRequestInfoService.pageList(req));
    }

    @Override
    public R<Void> push(@Valid SinglePkREQ req) {
        exceptionRequestInfoService.operate(req.getId(), false);
        return R.ok();
    }

    @Override
    public R<Void> ignore(@Valid SinglePkREQ req) {
        exceptionRequestInfoService.operate(req.getId(), true);
        return R.ok();
    }
}
