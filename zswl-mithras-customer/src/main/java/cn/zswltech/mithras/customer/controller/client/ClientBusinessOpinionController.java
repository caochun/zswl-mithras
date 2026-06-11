package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionAddREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListRSP;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.ClientBusinessOpinionApi;
import cn.zswltech.mithras.customer.application.client.ClientBusinessOpinionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ClientBusinessOpinionController implements ClientBusinessOpinionApi {
    @Resource
    private ClientBusinessOpinionApplicationService clientBusinessOpinionApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid ClientBusinessOpinionAddREQ req) {
        return clientBusinessOpinionApplicationService.add(req);
    }

    @Override
    public R<PageR<ClientBusinessOpinionListRSP>> list(@RequestBody @Valid ClientBusinessOpinionListREQ req) {
        return clientBusinessOpinionApplicationService.list(req);
    }
}
