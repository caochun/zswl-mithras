package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.ExternalSyncREQ;
import cn.zswltech.mithras.dto.client.external.tyc.TycAbnormalRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycConsumptionRestrictionRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycDishonestRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycEquityInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycJudicialRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycLawSuitRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycMortgageInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycPunishmentInfoRSP;
import cn.zswltech.mithras.dto.client.external.tyc.TycZhixingInfoRSP;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.TycApi;
import cn.zswltech.mithras.customer.application.client.TycApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TycController implements TycApi {
    @Resource
    private TycApplicationService tycApplicationService;

    @Override
    public R<PageR<TycMortgageInfoRSP>> mortgageInfoList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.mortgageInfoList(req);
    }

    @Override
    public R<PageR<TycEquityInfoRSP>> equityInfo(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.equityInfo(req);
    }

    @Override
    public R<PageR<TycPunishmentInfoRSP>> punishmentInfoList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.punishmentInfoList(req);
    }

    @Override
    public R<PageR<TycAbnormalRSP>> abnormalList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.abnormalList(req);
    }

    @Override
    public R<PageR<TycJudicialRSP>> judicialList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.judicialList(req);
    }

    @Override
    public R<PageR<TycLawSuitRSP>> lawSuitList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.lawSuitList(req);
    }

    @Override
    public R<PageR<TycConsumptionRestrictionRSP>> consumptionRestrictionList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.consumptionRestrictionList(req);
    }

    @Override
    public R<PageR<TycZhixingInfoRSP>> zhixingInfoList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.zhixingInfoList(req);
    }

    @Override
    public R<PageR<TycDishonestRSP>> dishonestList(@RequestBody @Valid ExternalPageREQ req) {
        return tycApplicationService.dishonestList(req);
    }

    @Override
    public R<Void> externalSync(@RequestBody @Valid ExternalSyncREQ req) {
        return tycApplicationService.externalSync(req);
    }
}
