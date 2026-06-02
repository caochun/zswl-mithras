package cn.zswltech.mithras.third.service.tyc.impl;

import cn.zswltech.mithras.third.convert.tyc.TycJudicialConvert;
import cn.zswltech.mithras.service.mapper.corp.TycJudicialMapper;
import cn.zswltech.mithras.service.mapper.model.client.TycJudicial;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.repository.tyc.resp.TycJudicialResp;
import cn.zswltech.mithras.third.service.tyc.TycAbstractService;
import cn.zswltech.mithras.third.service.tyc.TycJudicialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 司法协助
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycJudicialServiceImpl extends TycAbstractService<TycJudicialMapper, TycJudicial, TycJudicialResp.ItemsDTO> implements TycJudicialService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_JUDICIAL;
    }

    @Override
    public TycJudicial convert(TycJudicialResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycJudicialConvert.tycResp2Entity(itemsDTO);
    }

}
