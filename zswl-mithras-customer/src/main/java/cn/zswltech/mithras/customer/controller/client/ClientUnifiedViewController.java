package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.afterlease.ClientUnifiedViewOverdueRentListRSP;
import cn.zswltech.mithras.dto.client.client.*;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import cn.zswltech.mithras.api.client.ClientUnifiedViewApi;
import cn.zswltech.mithras.customer.application.client.ClientUnifiedViewApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ClientUnifiedViewController implements ClientUnifiedViewApi {
    @Resource
    private ClientUnifiedViewApplicationService clientUnifiedViewApplicationService;

    @Override
    public R<List<SelectRSP>> orgList() {
        return clientUnifiedViewApplicationService.orgList();
    }

    @Override
    public R<PageR<ClientUnifiedViewListRSP>> unifiedViewList(@RequestBody @Valid ClientUnifiedViewListREQ req) {
        return clientUnifiedViewApplicationService.unifiedViewList(req);
    }

    @Override
    public R<ClientUnifiedViewDetailRSP> unifiedViewDetail(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.unifiedViewDetail(req);
    }

    @Override
    public R<ClientUnifiedApplyCreditRSP> clientApplyCredit(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.clientApplyCredit(req);
    }

    @Override
    public R<ClientClassificationRSP> classification(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.classification(req);
    }

    @Override
    public R<Map<String, Long>> clientApplyCreditHistory(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.clientApplyCreditHistory(req);
    }

    @Override
    public R<List<ClientUnifiedProjListRSP>> clientProjList(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.clientProjList(req);
    }

    @Override
    public R<List<ClientUnifiedContractListRSP>> clientContractList(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.clientContractList(req);
    }

    @Override
    public R<List<ClientUnifiedCustomerTrendsRSP>> customerTrends() {
        return clientUnifiedViewApplicationService.customerTrends();
    }

    @Override
    public R<List<ClientUnifiedRatingHistoryRSP>> ratingHistory(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.ratingHistory(req);
    }

    @Override
    public R<List<ClientUnifiedViewOverdueRentListRSP>> overdueRent(@RequestBody @Valid ClientUnifiedApplyCreditREQ req) {
        return clientUnifiedViewApplicationService.overdueRent(req);
    }
}
