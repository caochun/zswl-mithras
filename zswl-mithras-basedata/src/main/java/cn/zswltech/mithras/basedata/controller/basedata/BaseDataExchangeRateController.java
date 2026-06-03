package cn.zswltech.mithras.basedata.controller;

import cn.zswltech.mithras.api.basedata.BaseDataExchangeRateApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRateQueryREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRSP;
import cn.zswltech.mithras.basedata.service.BaseDataExchangeRateService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@RestController
public class BaseDataExchangeRateController implements BaseDataExchangeRateApi {
    @Resource
    private BaseDataExchangeRateService baseDataExchangeRateService;

    @Override
    public R<PageR<BaseDataExchangeRSP>> pageList(BaseDataExchangeRateQueryREQ req) {
        return R.ok(baseDataExchangeRateService.pageList(req));
    }

    @Override
    public R<Long> add(BaseDataExchangeREQ req) {
        return R.ok(baseDataExchangeRateService.add(req));
    }

    @Override
    public R<Long> modify(BaseDataExchangeREQ req) {
        return R.ok(baseDataExchangeRateService.modify(req));
    }

    @Override
    public R<Void> delete(SinglePkREQ req) {
        baseDataExchangeRateService.deleteById(req.getId());
        return R.ok();
    }
}
