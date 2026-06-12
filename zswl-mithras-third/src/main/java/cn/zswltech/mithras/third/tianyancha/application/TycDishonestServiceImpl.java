package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.third.tianyancha.application.convert.TycDishonestConvert;
import cn.zswltech.mithras.third.externaldata.tianyancha.mapper.TycDishonestMapper;
import cn.zswltech.mithras.third.externaldata.tianyancha.model.TycDishonest;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycDishonestResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 外部信息 天眼查 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycDishonestServiceImpl extends TycAbstractService<TycDishonestMapper, TycDishonest, TycDishonestResp.ItemsDTO> implements TycDishonestService {

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private TycDishonestMapper tycDishonestMapper;


    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_DISHONEST;
    }

    @Override
    public TycDishonest convert(TycDishonestResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        return TycDishonestConvert.tycResp2Entity(itemsDTO);
    }
}
