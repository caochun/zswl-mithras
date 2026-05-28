package cn.zswltech.mithras.service.service.tyc.impl;

import cn.zswltech.mithras.service.convert.tyc.TycLawSuitConvert;
import cn.zswltech.mithras.service.enums.TycErrorEnum;
import cn.zswltech.mithras.service.mapper.TycMockDataMapper;
import cn.zswltech.mithras.service.mapper.corp.TycLawSuitMapper;
import cn.zswltech.mithras.service.mapper.model.TycMockData;
import cn.zswltech.mithras.service.mapper.model.client.TycLawSuit;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.repository.tyc.TycMockDataType;
import cn.zswltech.mithras.service.repository.tyc.req.TycLawSuitDetailReq;
import cn.zswltech.mithras.service.repository.tyc.resp.TycLawSuitDetailResp;
import cn.zswltech.mithras.service.repository.tyc.resp.TycLawSuitResp;
import cn.zswltech.mithras.service.service.tyc.TycAbstractService;
import cn.zswltech.mithras.service.service.tyc.TycLawSuitService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 外部信息 天眼查 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:29 PM
 */
@Slf4j
@Service
public class TycLawSuitServiceImpl extends TycAbstractService<TycLawSuitMapper, TycLawSuit, TycLawSuitResp.ItemsDTO> implements TycLawSuitService {

    @Value("${mithras.tyc.writeToMockDb}")
    protected Boolean writeToMockDb;
    @Resource
    protected TycMockDataMapper mockDataMapper;

    @Override
    public PlatformApiEnum getApiEnum() {
        return PlatformApiEnum.TYC_LAW_SUIT;
    }

    @Override
    public TycLawSuit convert(TycLawSuitResp.ItemsDTO itemsDTO, Long clientId, String clientName) {
        TycLawSuit tycLawSuit = TycLawSuitConvert.tycResp2Entity(itemsDTO);
        // 天眼查法律诉讼详情接口 可能存在效率问题 后期优化下
        return tycLawSuit;
    }

    @Override
    public TycLawSuit fillDetail(TycLawSuit tycLawSuit) {
        if (StringUtils.isNotBlank(tycLawSuit.getUuid())) {
            TycLawSuitDetailReq req = new TycLawSuitDetailReq();
            req.setUuid(tycLawSuit.getUuid());
            PlatformApiHandler<TycLawSuitDetailReq, TycLawSuitDetailResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_LAW_SUIT_DETAIL);
            TycLawSuitDetailResp detailResp = apiHandler.execute(req);
            if (Objects.isNull(detailResp) || TycErrorEnum.isError(detailResp.getErrorCode())) {
                log.error("天眼查接口调用失败, api:{}, req:{}, resp:{}", getApiEnum().apiCode, JSON.toJSONString(req), JSON.toJSONString(detailResp));
                throw new MithrasException("天眼查接口调用失败");
            }

            if (Boolean.TRUE.equals(writeToMockDb)) {
                // 为mock数据提供数据源
                mockDataMapper.delete(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, TycMockDataType.LAW_SUIT_DETAIL.name()).eq(TycMockData::getKeyword, tycLawSuit.getUuid()));
                TycMockData tycMockData = new TycMockData();
                tycMockData.setDataType(TycMockDataType.LAW_SUIT_DETAIL.name());
                tycMockData.setKeyword(tycLawSuit.getUuid());
                tycMockData.setJsonData(JSON.toJSONString(detailResp));
                mockDataMapper.insert(tycMockData);
            }

            tycLawSuit.setDetailJson(JSON.toJSONString(detailResp.getResult()));
        }
        return tycLawSuit;
    }

}
