package cn.zswltech.mithras.service.controller.client;

import cn.zswltech.mithras.api.client.lifecycle.ClientLifeCycleApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.*;
import cn.zswltech.mithras.service.service.client.ClientLifeCycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 15:28
 */
@RestController
@Slf4j
public class ClientLifeCycleController implements ClientLifeCycleApi {
    @Resource
    private ClientLifeCycleService clientLifeCycleService;

    @Override
    public R<ClientLifeCycleCardRsp> card() {
        return R.ok(clientLifeCycleService.card());
    }

    @Override
    public R<PageR<ClientListRSP>> clientList(ClientLifeCycleListReq req) {
        return R.ok(clientLifeCycleService.clientList(req));
    }

    @Override
    public R<ClientListRSP> clientDetail(ClientLifeCycleDetailReq req) {
        return R.ok(clientLifeCycleService.clientDetail(req.getClientId()));
    }

    @Override
    public R<List<ClientLifeCycleProjectListRsp>> projectList(ClientLifeCycleDetailReq req) {
        return R.ok(clientLifeCycleService.projectList(req));
    }

    @Override
    public R<String> fiveLevel(ClientLifeCycleDetailReq req) {
        return R.ok(clientLifeCycleService.fiveLevel(req));
    }

    @Override
    public R<ClientLifeCycleReceiptRsp> receipt(ClientLifeCycleDetailReq req) {
        return R.ok(clientLifeCycleService.receipt(req));
    }

    @GetMapping("/client/lifecycle/test")
    public String test() {
        clientLifeCycleService.card();
        return "test";
    }


}
