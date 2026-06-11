package cn.zswltech.mithras.third.tianyancha.application.impl;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycConsumptionRestrictionConvert;
import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.TycConsumptionRestrictionMapper;
import cn.zswltech.mithras.customer.externaldata.tianyancha.model.TycConsumptionRestriction;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycConsumptionRestrictionResp;
import cn.zswltech.mithras.third.tianyancha.application.TycAbstractService;
import cn.zswltech.mithras.third.tianyancha.application.TycConsumptionRestrictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 经营异常
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycConsumptionRestrictionServiceImpl extends TycAbstractService<TycConsumptionRestrictionMapper, TycConsumptionRestriction, TycConsumptionRestrictionResp.ItemsDTO> implements TycConsumptionRestrictionService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_CONSUMPTION_RESTRICTION;
    }

    @Override
    public TycConsumptionRestriction convert(TycConsumptionRestrictionResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycConsumptionRestrictionConvert.tycResp2Entity(itemsDTO);
    }

}
