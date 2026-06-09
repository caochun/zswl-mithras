package cn.zswltech.mithras.service.application.client;

import cn.zswltech.mithras.customer.application.client.api.ClientLifeCycleApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.*;
import cn.zswltech.mithras.service.service.client.ClientLifeCycleService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 15:28
 */
@Service
@Slf4j
public class ClientLifeCycleFacade implements ClientLifeCycleApplicationService {
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
    public String test() {
        clientLifeCycleService.card();
        return "test";
    }


}
