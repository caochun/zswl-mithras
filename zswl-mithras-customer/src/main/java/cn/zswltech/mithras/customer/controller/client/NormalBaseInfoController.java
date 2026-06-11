package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoModifyREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.NormalBaseInfoApi;
import cn.zswltech.mithras.customer.application.client.NormalBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class NormalBaseInfoController implements NormalBaseInfoApi {
    @Resource
    private NormalBaseInfoApplicationService normalBaseInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid NormalBaseInfoAddREQ req) {
        return normalBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid NormalBaseInfoModifyREQ req) {
        return normalBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<NormalBaseInfoDetailRSP> detail(@RequestBody @Valid NormalBaseInfoDetailREQ req) {
        return normalBaseInfoApplicationService.detail(req);
    }
}
