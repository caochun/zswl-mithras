package cn.zswltech.mithras.factory.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAreaIndicatorModifyREQ;
import cn.zswltech.mithras.factory.enums.RatingFetchMethodEnum;
import cn.zswltech.mithras.factory.mapper.AreaInfoMapper;
import cn.zswltech.mithras.factory.mapper.RatingClientAreaIndicatorMapper;
import cn.zswltech.mithras.factory.model.AreaInfo;
import cn.zswltech.mithras.factory.model.RatingClientAreaIndicator;
import cn.zswltech.mithras.factory.model.RatingClientAreaIndicatorConfig;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.DataMinerClient;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.req.QueryDmIndicatorReq;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.req.QueryDmRegionScoreReq;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.resp.DataMinerRsp;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.resp.QueryDmIndicatorRsp;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.resp.QueryDmRegionScoreRsp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
@Slf4j
@Service
public class RatingClientAreaIndicatorService extends ServiceImpl<RatingClientAreaIndicatorMapper, RatingClientAreaIndicator> {
    @Resource
    private DataMinerClient dataMinerClient;
    @Resource
    private AreaInfoMapper areaInfoMapper;
    @Resource
    private RatingClientAreaIndicatorConfigService ratingClientAreaIndicatorConfigService;

    @Transactional(rollbackFor = Throwable.class)
    public void modify(Long ratingClientId, List<RatingClientAreaIndicatorModifyREQ> list) {
        // 查询已有
        List<RatingClientAreaIndicator> dbList = this.listByRatingClient(ratingClientId);
        Map<Long, RatingClientAreaIndicator> dbMap = dbList.stream().collect(Collectors.toMap(RatingClientAreaIndicator::getId, e -> e));
        // 比对并更新
        List<RatingClientAreaIndicator> updateList = list.stream()
                .filter(e -> Objects.nonNull(e.getBizId()))
                .map(e -> {
                    RatingClientAreaIndicator ratingClientAreaIndicator = new RatingClientAreaIndicator();
                    ratingClientAreaIndicator.setId(e.getBizId());
                    if (StrUtil.isNotBlank(e.getIndicatorValue())) {
                        ratingClientAreaIndicator.setIndicatorValue(new BigDecimal(e.getIndicatorValue()));
                    }
                    RatingClientAreaIndicator dbData = dbMap.get(e.getBizId());
                    boolean same = Optional.ofNullable(dbData.getIndicatorValueSystem()).orElse(BigDecimal.ZERO).compareTo(Optional.ofNullable(e.getIndicatorValue()).map(BigDecimal::new).orElse(BigDecimal.ZERO)) == 0;
                    ratingClientAreaIndicator.setMode(!same ? RatingFetchMethodEnum.IMPORT.name() : RatingFetchMethodEnum.SYSTEM.name());
                    return ratingClientAreaIndicator;
                }).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
    }

    public List<RatingClientAreaIndicator> listByRatingClientAndCategory(Long ratingClientId, String categoryCode) {
        LambdaQueryWrapper<RatingClientAreaIndicator> query = Wrappers.lambdaQuery();
        query.eq(RatingClientAreaIndicator::getRatingClientId, ratingClientId);
        query.eq(RatingClientAreaIndicator::getCategoryCode, categoryCode);
        return this.list(query);
    }

    public List<RatingClientAreaIndicator> listByRatingClient(Long ratingClientId) {
        LambdaQueryWrapper<RatingClientAreaIndicator> query = Wrappers.lambdaQuery();
        query.eq(RatingClientAreaIndicator::getRatingClientId, ratingClientId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void create(Long ratingClientId, Long areaUniCode, int targetYear) {
        List<RatingClientAreaIndicator> todoList = this.initByConfig(ratingClientId, areaUniCode);
        LocalDate now = LocalDate.now();
        try {
            // 查询区域指标
//            int currentYear = now.getYear();
            QueryDmIndicatorReq indicatorReq = new QueryDmIndicatorReq();
            indicatorReq.setYear(targetYear);
            indicatorReq.setAreaUniCode(areaUniCode);
            DataMinerRsp<QueryDmIndicatorRsp> indicatorResult = dataMinerClient.doRequest(indicatorReq, QueryDmIndicatorRsp.class);
            if (CollectionUtil.isNotEmpty(indicatorResult.getDataList())) {
                // 去掉value为null的，防止当年全是null数据引起的问题
                indicatorResult.getDataList().removeIf(e -> Objects.isNull(e.getIndicatorValue()));
            }
            if (CollectionUtil.isEmpty(indicatorResult.getDataList())) {
                // 调整年份再查询一次
                indicatorReq.setYear(targetYear - 1);
                indicatorResult = dataMinerClient.doRequest(indicatorReq, QueryDmIndicatorRsp.class);
            }
            if (CollectionUtil.isNotEmpty(indicatorResult.getDataList())) {
                Map<String, QueryDmIndicatorRsp> map = indicatorResult.getDataList().stream().collect(Collectors.toMap(QueryDmIndicatorRsp::getIndicatorCode, e -> e, (a, b) -> b));
                todoList.forEach(indicator -> {
                    QueryDmIndicatorRsp rsp = map.get(indicator.getIndicatorCode());
                    if (Objects.isNull(rsp)) {
                        return;
                    }
                    indicator.setYear(rsp.getYear());
                    indicator.setIndicatorValueSystem(rsp.getIndicatorValue());
                    indicator.setIndicatorValue(indicator.getIndicatorValueSystem());
                });
            }
            // 查询所属地级市得分
            AreaInfo areaInfo = areaInfoMapper.selectOne(Wrappers.<AreaInfo>lambdaQuery().eq(AreaInfo::getAreaUniCode, areaUniCode).last("limit 1"));
            if (Objects.nonNull(areaInfo) && Objects.nonNull(areaInfo.getCityUniCode()) && !Objects.equals(areaInfo.getCityUniCode(), areaUniCode)) {
                QueryDmRegionScoreReq scoreReq = new QueryDmRegionScoreReq();
                scoreReq.setAreaUniCode(areaInfo.getCityUniCode());
                DataMinerRsp<QueryDmRegionScoreRsp> scoreResult = dataMinerClient.doRequest(scoreReq, QueryDmRegionScoreRsp.class);
                if (CollectionUtil.isNotEmpty(scoreResult.getDataList())) {
                    QueryDmRegionScoreRsp rsp = scoreResult.getDataList().get(0);
                    todoList.forEach(indicator -> {
                        if (Objects.equals(indicator.getIndicatorCode(), RatingClientAreaIndicatorConfig.DmIndicatorCode.belong_city_score.name())) {
                            indicator.setYear(now.getYear());
                            indicator.setIndicatorValueSystem(rsp.getFinalScore());
                            indicator.setIndicatorValue(indicator.getIndicatorValueSystem());
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("客户评级-查询区域指标数据异常", e);
        }
        if (CollectionUtil.isNotEmpty(todoList)) {
            saveBatch(todoList);
        }
    }

    public List<RatingClientAreaIndicator> initByConfig(Long ratingClientId, Long areaUniCode) {
        List<RatingClientAreaIndicatorConfig> configList = ratingClientAreaIndicatorConfigService.list();
        if (CollectionUtil.isEmpty(configList)) {
            return Collections.emptyList();
        }
        return configList.stream().map(e -> {
            RatingClientAreaIndicator indicator = new RatingClientAreaIndicator();
            indicator.setCategoryCode(e.getCategoryCode());
            indicator.setCategoryName(e.getCategoryName());
            indicator.setRatingClientId(ratingClientId);
            indicator.setAreaUniCode(areaUniCode);
            indicator.setIndicatorCode(e.getIndicatorCode());
            indicator.setIndicatorName(e.getIndicatorName());
            indicator.setIndicatorUnit(e.getIndicatorUnit());
            indicator.setIndicatorSort(e.getIndicatorSort());
            indicator.setIndicatorDataType(e.getIndicatorDataType());
            return indicator;
        }).collect(Collectors.toList());
    }
}
