package cn.zswltech.mithras.third.financialshare.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.third.financialshare.application.CqApiRecordApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordRSP;
import cn.zswltech.mithras.third.financialshare.application.ExceptionRequestInfoService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/7/17
 * @description
 */
@Service
public class CqApiRecordFacade implements CqApiRecordApplicationService {
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
