package cn.zswltech.mithras.third.tianyancha.application.impl;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycAbnormalConvert;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.mapper.TycAbnormalMapper;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.TycAbnormal;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycAbnormalResp;
import cn.zswltech.mithras.third.tianyancha.application.TycAbnormalService;
import cn.zswltech.mithras.third.tianyancha.application.TycAbstractService;
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
public class TycAbnormalServiceImpl extends TycAbstractService<TycAbnormalMapper, TycAbnormal, TycAbnormalResp.ItemsDTO> implements TycAbnormalService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_ABNORMAL;
    }

    @Override
    public TycAbnormal convert(TycAbnormalResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycAbnormalConvert.tycResp2Entity(itemsDTO);
    }

}
