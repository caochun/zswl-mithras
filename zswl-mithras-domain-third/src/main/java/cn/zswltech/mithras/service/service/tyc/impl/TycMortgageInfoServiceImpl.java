package cn.zswltech.mithras.service.service.tyc.impl;

import cn.zswltech.mithras.service.convert.tyc.TycMortgageInfoConvert;
import cn.zswltech.mithras.service.mapper.corp.TycMortgageInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.TycMortgageInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.tyc.resp.TycMortgageInfoResp;
import cn.zswltech.mithras.service.service.tyc.TycAbstractService;
import cn.zswltech.mithras.service.service.tyc.TycMortgageInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 外部信息 天眼查 动产抵押
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycMortgageInfoServiceImpl extends TycAbstractService<TycMortgageInfoMapper, TycMortgageInfo, TycMortgageInfoResp.ItemsDTO> implements TycMortgageInfoService {

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_MORTGAGE_INFO;
    }

    @Override
    public TycMortgageInfo convert(TycMortgageInfoResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycMortgageInfoConvert.tycResp2Entity(itemsDTO);
    }

}
