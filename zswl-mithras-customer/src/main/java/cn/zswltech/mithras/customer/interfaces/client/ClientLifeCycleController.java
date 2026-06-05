package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.client.lifecycle.ClientLifeCycleApi;
import cn.zswltech.mithras.customer.application.client.api.ClientLifeCycleApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ClientLifeCycleController implements ClientLifeCycleApi {
    @Resource
    private ClientLifeCycleApplicationService clientLifeCycleApplicationService;

    @Override
    public R<ClientLifeCycleCardRsp> card() {
        return clientLifeCycleApplicationService.card();
    }

    @Override
    public R<PageR<ClientListRSP>> clientList(@RequestBody @Valid ClientLifeCycleListReq req) {
        return clientLifeCycleApplicationService.clientList(req);
    }

    @Override
    public R<ClientListRSP> clientDetail(@RequestBody @Valid ClientLifeCycleDetailReq req) {
        return clientLifeCycleApplicationService.clientDetail(req);
    }

    @Override
    public R<List<ClientLifeCycleProjectListRsp>> projectList(@RequestBody @Valid ClientLifeCycleDetailReq req) {
        return clientLifeCycleApplicationService.projectList(req);
    }

    @Override
    public R<String> fiveLevel(@RequestBody @Valid ClientLifeCycleDetailReq req) {
        return clientLifeCycleApplicationService.fiveLevel(req);
    }

    @Override
    public R<ClientLifeCycleReceiptRsp> receipt(@RequestBody @Valid ClientLifeCycleDetailReq req) {
        return clientLifeCycleApplicationService.receipt(req);
    }

    @org.springframework.web.bind.annotation.GetMapping("/client/lifecycle/test")
    public String test() {
        return clientLifeCycleApplicationService.test();
    }
}
