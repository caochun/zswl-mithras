package cn.zswltech.mithras.third.service.tyc.impl;

import cn.zswltech.mithras.third.convert.tyc.TycPunishmentInfoConvert;
import cn.zswltech.mithras.service.mapper.corp.TycPunishmentInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.TycPunishmentInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.repository.tyc.resp.TycPunishmentInfoResp;
import cn.zswltech.mithras.third.service.tyc.TycAbstractService;
import cn.zswltech.mithras.third.service.tyc.TycPunishmentInfoService;
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
