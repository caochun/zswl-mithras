package cn.zswltech.mithras.third.service.tyc.impl;

import cn.zswltech.mithras.third.convert.tyc.TycEquityInfoConvert;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.mapper.TycEquityInfoMapper;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.TycEquityInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.repository.tyc.resp.TycEquityInfoResp;
import cn.zswltech.mithras.third.service.tyc.TycAbstractService;
import cn.zswltech.mithras.third.service.tyc.TycEquityInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 股权出质
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycEquityInfoServiceImpl extends TycAbstractService<TycEquityInfoMapper, TycEquityInfo, TycEquityInfoResp.ItemsDTO> implements TycEquityInfoService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_EQUITY_INFO;
    }

    @Override
    public TycEquityInfo convert(TycEquityInfoResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycEquityInfoConvert.tycResp2Entity(itemsDTO);
    }

}
