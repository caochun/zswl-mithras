package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycPunishmentInfoConvert;
import cn.zswltech.mithras.third.externaldata.tianyancha.mapper.TycPunishmentInfoMapper;
import cn.zswltech.mithras.third.externaldata.tianyancha.model.TycPunishmentInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycPunishmentInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 行政处罚
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycPunishmentInfoServiceImpl extends TycAbstractService<TycPunishmentInfoMapper, TycPunishmentInfo, TycPunishmentInfoResp.ItemsDTO> implements TycPunishmentInfoService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_PUNISHMENT_INFO;
    }

    @Override
    public TycPunishmentInfo convert(TycPunishmentInfoResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycPunishmentInfoConvert.tycResp2Entity(itemsDTO);
    }

}
