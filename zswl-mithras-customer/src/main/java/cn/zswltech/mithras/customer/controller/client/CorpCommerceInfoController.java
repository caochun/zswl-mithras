package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.*;
import cn.zswltech.mithras.dto.contract.HighSeasCustomersREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.client.CorpCommerceInfoApi;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpCommerceInfoController implements CorpCommerceInfoApi {
    @Resource
    private CorpCommerceInfoApplicationService corpCommerceInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpCommerceInfoAddREQ req) {
        return corpCommerceInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpCommerceInfoModifyREQ req) {
        return corpCommerceInfoApplicationService.modify(req);
    }

    @Override
    public R<CorpCommerceInfoDetailRSP> detail(@RequestBody @Valid CorpCommerceInfoDetailREQ req) {
        return corpCommerceInfoApplicationService.detail(req);
    }

    @Override
    public R<List<SelectRSP>> listHighSegasCustomers(@RequestBody HighSeasCustomersREQ req) {
        return corpCommerceInfoApplicationService.listHighSegasCustomers(req);
    }

    @Override
    public R<ClientCorpCommerceInfoValidRSP> valid(@RequestBody @Valid ClientCorpCommerceInfoValidREQ req) {
        return corpCommerceInfoApplicationService.valid(req);
    }
}
