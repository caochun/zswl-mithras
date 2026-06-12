package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycZhixingInfoConvert;
import cn.zswltech.mithras.third.externaldata.tianyancha.persistence.mapper.TycZhixingInfoMapper;
import cn.zswltech.mithras.third.externaldata.tianyancha.persistence.model.TycZhixingInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycZhixingInfoResp;
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
