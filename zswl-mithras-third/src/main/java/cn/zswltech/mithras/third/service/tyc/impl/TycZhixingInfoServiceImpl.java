package cn.zswltech.mithras.third.service.tyc.impl;

import cn.zswltech.mithras.third.convert.tyc.TycZhixingInfoConvert;
import cn.zswltech.mithras.service.mapper.corp.TycZhixingInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.TycZhixingInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.repository.tyc.resp.TycZhixingInfoResp;
import cn.zswltech.mithras.third.service.tyc.TycAbstractService;
import cn.zswltech.mithras.third.service.tyc.TycZhixingInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 被执行人
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycZhixingInfoServiceImpl extends TycAbstractService<TycZhixingInfoMapper, TycZhixingInfo, TycZhixingInfoResp.ItemsDTO> implements TycZhixingInfoService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_ZHIXING_INFO;
    }

    @Override
    public TycZhixingInfo convert(TycZhixingInfoResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycZhixingInfoConvert.tycResp2Entity(itemsDTO);
    }

}
