package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycMortgageInfoConvert;
import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.TycMortgageInfoMapper;
import cn.zswltech.mithras.customer.externaldata.tianyancha.model.TycMortgageInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycMortgageInfoResp;
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
