package cn.zswltech.mithras.third.service.tyc;

import cn.zswltech.mithras.third.enums.TycErrorEnum;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.mapper.TycMockDataMapper;
import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.TycMockData;
import cn.zswltech.mithras.client.externaldata.common.infrastructure.model.ExternalDataBaseModel;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.third.repository.tyc.TycMockDataType;
import cn.zswltech.mithras.third.repository.tyc.req.TycBaseReq;
import cn.zswltech.mithras.third.repository.tyc.resp.TycListBaseResp;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天眼查基类
 *
 * @author wangchuanhao
 * @date 2022/6/21 1:27 PM
 */
@Slf4j
public abstract class TycAbstractService<M extends BaseMapper<T>, T extends ExternalDataBaseModel, F> extends ServiceImpl<M, T> {

    protected static final String NO_DATA_BODY = "{\"result\":null,\"reason\":\"无数据\",\"error_code\":300000}";

    @Autowired
    protected BaseMapper<T> mapper;
    @Resource
    protected PlatformApiHandleFactory platformApiHandleFactory;
    @Value("${mithras.tyc.writeToMockDb}")
    protected Boolean writeToMockDb;
    @Resource
    protected TycMockDataMapper mockDataMapper;


    public List<T> queryAllFromTyc(Long clientId, String clientName, String uscCode) {
        List<F> itemsDTOList = new ArrayList<>();
        PlatformApiHandler<TycBaseReq, TycListBaseResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(getApiEnum());
        int total = Integer.MAX_VALUE, pageSize = 20, pageNum = 1;
        TycBaseReq req = new TycBaseReq();
        req.setKeyword(uscCode);
        req.setPageSize(pageSize);
        while (itemsDTOList.size() < total) {
            req.setPageNum(pageNum);
            TycListBaseResp resp = apiHandler.execute(req);
            if (resp == null || TycErrorEnum.isError(resp.getErrorCode())) {
                log.error("天眼查接口调用失败, api:{}, req:{}, resp:{}", getApiEnum().apiCode, JSON.toJSONString(req), JSON.toJSONString(resp));
                throw new MithrasException("天眼查接口调用失败");
            }
            if (TycErrorEnum.NO_DATA.getCode().equals(resp.getErrorCode())) {
                break;
            }
            pageNum++;
            total = resp.getResult().getTotal();
            itemsDTOList.addAll(resp.getResult().getItems());
            // 如果总条数大于200 就不查了只查200条 省点钱
            if (pageNum * pageSize > 200) {
                break;
            }
        }

        if (Boolean.TRUE.equals(writeToMockDb)) {
            // 为mock数据提供数据源
            String mockDataType = TycMockDataType.convert(getApiEnum());
            if (StringUtils.isNotBlank(mockDataType)) {
                // 删除旧的 新增新的
                mockDataMapper.delete(Wrappers.<TycMockData>lambdaQuery().eq(TycMockData::getDataType, mockDataType).eq(TycMockData::getKeyword, uscCode));
                TycMockData tycMockData = new TycMockData();
                tycMockData.setDataType(mockDataType);
                tycMockData.setKeyword(uscCode);
                if (CollectionUtils.isEmpty(itemsDTOList)) {
                    tycMockData.setJsonData(NO_DATA_BODY);
                } else {
                    TycListBaseResp resp = new TycListBaseResp();
                    resp.setErrorCode(0);
                    TycListBaseResp.Result mockResult = new TycListBaseResp.Result();
                    mockResult.setItems(itemsDTOList);
                    mockResult.setTotal(itemsDTOList.size());
                    resp.setResult(mockResult);
                    tycMockData.setJsonData(JSON.toJSONString(resp));
                }
                mockDataMapper.insert(tycMockData);
            }
        }
        List<T> resultList = itemsDTOList.stream()
                .map(f -> convert(f, clientId, clientName))
                .collect(Collectors.toList());
        resultList.forEach(d -> {d.setClientId(clientId);});
        return resultList;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void flushData(Long clientId, List<T> newDataList) {
        mapper.delete(Wrappers.<T>lambdaQuery().eq(T::getClientId, clientId));
        this.saveBatch(newDataList);
    }

    public abstract PlatformApiEnum getApiEnum();

    public abstract T convert(F f, Long clientId, String clientName);

}
