package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyConverter;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategySnapshot;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategySnapshotService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber.MetricComputer29J10000396_FJC47608;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber.RiskControlClassifyMetricComputer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 风控管理-预警监控管理
 * @date 2023-02-08
 */
@Service
public class RiskControlStrategyService extends ServiceImpl<RiskControlStrategyMapper, RiskControlStrategy> {
    @Resource
    private TypeConversionWorker converterWorker;
    @Resource
    private RiskControlStrategyConverter strategyConverter;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RiskControlStrategySnapshotService riskControlStrategySnapshotService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ProjEstablishPriceService projEstablishPriceService;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;
    @Resource
    private IndustryTypeMapper industryTypeMapper;

    /**
     * 更新时，包含null值
     *
     * @param strategy 策略
     */
    public void updateAnnotationIncludeNullById(RiskControlStrategy strategy) {
        baseMapper.updateAnnotationIncludeNullById(strategy);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(RiskControlStrategyModifyReq req) {
        RiskControlStrategy originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RiskControlStrategy toBeModify = strategyConverter.modifyReq2Entity(req);
        toBeModify.setUpdateTime(LocalDateTime.now());
        toBeModify.setUpdateBy(AccountUtil.getLoginInfo().getId());
        baseMapper.updateById(toBeModify);
    }

    public PageR<RiskControlStrategyListRsp> list(RiskControlStrategyListReq req) {
        Page<RiskControlStrategy> strategies = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskControlStrategy>lambdaQuery()
                        .eq(ObjectUtil.isNotEmpty(req.getMetricType()), RiskControlStrategy::getMetricType, req.getMetricType())
                        .eq(ObjectUtil.isNotEmpty(req.getMetricCategory()), RiskControlStrategy::getMetricCategory,
                                req.getMetricCategory())
                        .eq(ObjectUtil.isNotEmpty(req.getEarlyWarningState()),
                                RiskControlStrategy::getEarlyWarningState, req.getEarlyWarningState())
                        .like(ObjectUtil.isNotEmpty(req.getMetricName()), RiskControlStrategy::getMetricName,
                                req.getMetricName())
                        .ge(ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()), RiskControlStrategy::getUpdateTime,
                                converterWorker.startOfDay(req.getUpdateTimeFrom()))
                        .le(ObjectUtil.isNotEmpty(req.getUpdateTimeTo()), RiskControlStrategy::getUpdateTime,
                                converterWorker.endOfDay(req.getUpdateTimeTo())));
        if (strategies.getTotal() == 0) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<RiskControlStrategyListRsp> rspList = new ArrayList<>();
        strategies.getRecords().forEach(riskControlStrategy -> {
            RiskControlStrategyListRsp rsp = strategyConverter.entity2ListRsp(riskControlStrategy);
            rspList.add(rsp);
        });

        // 如果入参的数据时点不为null，说明要区快照数据
        if (req.getDate() != null) {
            List<Long> pageMetricIds = strategies.getRecords().stream().map(RiskControlStrategy::getId).collect(Collectors.toList());
            List<RiskControlStrategySnapshot> snapshots = riskControlStrategySnapshotService.list(
                    Wrappers.<RiskControlStrategySnapshot>lambdaQuery()
                            .eq(RiskControlStrategySnapshot::getDate, req.getDate())
                            .in(RiskControlStrategySnapshot::getMetricId, pageMetricIds));
            Map<Long, RiskControlStrategySnapshot> idMap = snapshots.stream()
                    .collect(Collectors.toMap(RiskControlStrategySnapshot::getMetricId, v -> v));
            for (RiskControlStrategyListRsp rsp : rspList) {
                RiskControlStrategySnapshot snapshot = idMap.get(rsp.getId());
                rsp.setCurrentValueOne(snapshot == null ? null : snapshot.getValueOne());
                rsp.setCurrentValueTwo(snapshot == null ? null : snapshot.getValueTwo());
            }
        }
        return PageR.of(strategies, rspList);
    }

    public RiskControlStrategyDetailRsp detail(RiskControlStrategyDetailReq req) {
        RiskControlStrategy strategy = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(strategy)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (req.getDate() != null) {
            RiskControlStrategySnapshot snapshot = riskControlStrategySnapshotService.getOne(Wrappers.<RiskControlStrategySnapshot>lambdaQuery()
                    .eq(RiskControlStrategySnapshot::getMetricId, req.getId())
                    .eq(RiskControlStrategySnapshot::getDate, req.getDate()).last("limit 1"));
            if (snapshot != null) {
                strategy.setCurrentValueOne(snapshot.getValueOne());
                strategy.setCurrentValueTwo(snapshot.getValueTwo());
                strategy.setQuickContext(snapshot.getQuickContext());
            } else {
                throw new MithrasException("该策略指标在" + req.getDate() + "的快照不存在");
            }
        }
        RiskControlStrategyDetailRsp rsp = strategyConverter.entity2DetailRsp(strategy);

        if ("J10000396_FJC47608".equals(strategy.getMetricCode())) {
            Map<String, String> industryName = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                            .eq(IndustryType::getLevel, 2)).stream()
                    .collect(Collectors.toMap(IndustryType::getCode, IndustryType::getDisplay));
            String quickContext = strategy.getQuickContext();
            if (ObjectUtil.isNotEmpty(quickContext)) {
                MetricComputer29J10000396_FJC47608.MetricComputer29ComputerContext ctx =
                        JSON.parseObject(quickContext, new TypeReference<MetricComputer29J10000396_FJC47608.MetricComputer29ComputerContext>() {
                        });
                List<InnovativeBizRsp> list = new ArrayList<>();
                ctx.getTotalRemaining().forEach((k, v) -> {
                    InnovativeBizRsp innovativeBizRsp = new InnovativeBizRsp();
                    innovativeBizRsp.setTwoLevelIndustryName(industryName.getOrDefault(k, "未分类"));
                    innovativeBizRsp.setRemaining(new BigDecimal(v).divide(new BigDecimal(100000000L), 0, RoundingMode.HALF_UP).longValue());
                    list.add(innovativeBizRsp);
                });
                list.sort(Comparator.comparing(InnovativeBizRsp::getRemaining).reversed());
                rsp.setInnovativeBizRspList(list);
            }
        }

        if (ObjectUtil.isNotEmpty(strategy.getClientDetail())) {
            List<ClientDetail> clientDetail = JSON.parseArray(strategy.getClientDetail(), ClientDetail.class);
            clientDetail.forEach(v -> {
                v.setClientType("CORPORATION");
                v.setDomesticOrAbroad("DOMESTIC");
            });
            rsp.setClientDetailList(clientDetail);
        }
        return rsp;
    }


    public InterceptRsp projEstablishIntercept(ProjEstablishInterceptReq req) {
        InterceptRsp rsp = new InterceptRsp();
        rsp.setMetricNames(new ArrayList<>());
        rsp.setIntercept(true);
        ProjEstablishBaseInfo projEstablish = projEstablishBaseInfoMapper.selectById(req.getProjEstablishId());
        if (ObjectUtil.isNull(projEstablish)) {
            throw new MithrasException("项目立项记录不存在");
        }
        ProjEstablishPriceDetailRSP price = projEstablishPriceService.detail(req.getProjEstablishId());
        // 1. 获取客户的风险控制行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMapper
                .selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                        .eq(CorpCommerceInfoLib::getClientId, projEstablish.getClientId())
                        .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(CorpCommerceInfoLib::getVersion).last("limit 1"));
        if (ObjectUtil.isEmpty(corpCommerceInfoLib)) {
            throw new MithrasException("不存在该客户的生效信息");
        }
        String classify = corpCommerceInfoLib.getRiskControlIndustryClassify();
//        int effetCount = projEstablishBaseInfoMapper.selectList(
//                Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
//                        .eq(ProjEstablishBaseInfo::getClientId, projEstablish.getClientId())
//                        .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())).size();
//        if (effetCount == 0) {
//            // 首次立项且行业不为以下三类时，需判断“16”指标
//            if (!RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(classify) &&
//                    !RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(classify) &&
//                    !RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name().equals(classify)) {
//                RiskControlStrategy strategy_16 = baseMapper.selectOne(
//                        Wrappers.<RiskControlStrategy>lambdaQuery()
//                                .eq(RiskControlStrategy::getMetricCode, "A10000396_ZL044").last("limit 1"));
//                if (Objects.nonNull(strategy_16) && Objects.nonNull(strategy_16.getLimitValueOne())
//                        && price.getDeclaredAmount() > strategy_16.getLimitValueOne() * 100000000L) {
//                    // 预警值可以为空 为空时不做拦截
//                    rsp.setIntercept(false);
//                    rsp.getMetricNames().add(strategy_16.getMetricName());
//                }
//            }
//        }
        // 获取客户的风险控制行业分类对应的指标
        if (!Objects.equals(classify, RiskControlIndustryClassify.INNOVATION_BUSINESS.name())) {
            RiskControlStrategy strategy = baseMapper.selectOne(Wrappers.<RiskControlStrategy>lambdaQuery()
                    .in(RiskControlStrategy::getMetricCode, RiskControlClassifyMetricComputer.RELATED_METRIC_CODES)
                    .eq(RiskControlStrategy::getRemark, classify).last("limit 1"));
            if (ObjectUtil.isNotEmpty(strategy)) {
                if (!strategy.industryClassifyCanPass(price.getDeclaredAmount())) {
                    rsp.getMetricNames().add(strategy.getMetricName());
                    if (rsp.getIntercept()) {
                        rsp.setIntercept(false);
                    }
                }
            }
        }
        return rsp;
    }

    public InterceptRsp paymentApplyIntercept(PaymentApplyInterceptReq req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        if (ObjectUtil.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请记录不存在");
        }
        return compareRelatedMetric(paymentBaseInfo.getClientId(), paymentBaseInfo.getApplyPaymentAmount());
    }

    /**
     * 比较相关指标
     *
     * @param thisAmout 本次拦截申请涉及金额, 若为null，则取评审的金额
     * @return InterceptRsp
     */
    private InterceptRsp compareRelatedMetric(Long clientId, @NotNull Long thisAmout) {
        InterceptRsp rsp = new InterceptRsp();
        rsp.setMetricNames(new ArrayList<>());
        rsp.setIntercept(true);
        // 1. 获取客户的风险控制行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMapper
                .selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                        .eq(CorpCommerceInfoLib::getClientId, clientId)
                        .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(CorpCommerceInfoLib::getVersion).last("limit 1"));
        if (ObjectUtil.isEmpty(corpCommerceInfoLib)) {
            throw new MithrasException("不存在该客户的生效信息");
        }
        String classify = corpCommerceInfoLib.getRiskControlIndustryClassify();
        // 获取客户的风险控制行业分类对应的指标
        RiskControlStrategy strategy = baseMapper.selectOne(Wrappers.<RiskControlStrategy>lambdaQuery()
                .in(RiskControlStrategy::getMetricCode, RiskControlClassifyMetricComputer.RELATED_METRIC_CODES)
                .eq(RiskControlStrategy::getRemark, classify).last("limit 1"));
        if (ObjectUtil.isNotEmpty(strategy)) {
            if (!strategy.industryClassifyCanPass(thisAmout)) {
                rsp.getMetricNames().add(strategy.getMetricName());
                if (rsp.getIntercept()) {
                    rsp.setIntercept(false);
                }
            }
        }
        return rsp;
    }


    private BigDecimal conver = new BigDecimal(100000000L);

    public LineBarChartValueVO stockPrincipalChart(WorkbenchMetricReq req) {
        Map<String, RiskControlStrategy> industryMap = baseMapper.selectList(
                        Wrappers.<RiskControlStrategy>lambdaQuery()
                                .isNotNull(RiskControlStrategy::getRemark))
                .stream().collect(Collectors.toMap(RiskControlStrategy::getRemark, v -> v));

        List<ChartBaseDataVO> red = new ArrayList<>();
        List<ChartBaseDataVO> yellow = new ArrayList<>();
        List<ChartBaseDataVO> blue = new ArrayList<>();
        for (Map.Entry<String, RiskControlStrategy> entry : industryMap.entrySet()) {
            String industryType = entry.getKey();
            RiskControlStrategy strategy = entry.getValue();
            RiskControlIndustryClassify riskControlIndustryClassify = RiskControlIndustryClassify.findByName(industryType);
            if (Objects.isNull(riskControlIndustryClassify)) {
                continue;
            }
            String industryDisplay = riskControlIndustryClassify.display();
            JSONObject parse = (JSONObject) JSON.parse(strategy.getQuickContext());
            String totalRemainingPrincipal = parse.getString("totalRemainingPrincipal");
            if (ObjectUtil.isEmpty(totalRemainingPrincipal)) {
                throw new MithrasException("未获取到行业" + industryDisplay + "的总剩余本金");
            }
            BigDecimal total = new BigDecimal(totalRemainingPrincipal);
            BigDecimal redLine = strategy.getLimitValueOne() != null ?
                    new BigDecimal(strategy.getLimitValueOne()).multiply(conver) :
                    total.add(BigDecimal.valueOf(9999999L));
            BigDecimal yellowLine = strategy.getEarlyWarningValueOne() == null ?
                    redLine : new BigDecimal(strategy.getEarlyWarningValueOne()).multiply(conver);
            if (total.compareTo(redLine) > 0) {
                BigDecimal redValue = total.subtract(redLine).divide(conver, 2, RoundingMode.HALF_UP);
                red.add(new ChartBaseDataVO(industryDisplay, redValue.toString(), null, "万元"));
                yellow.add(new ChartBaseDataVO(industryDisplay, redLine.subtract(yellowLine).divide(conver, 2, RoundingMode.HALF_UP).toString(), null, "万元"));
                blue.add(new ChartBaseDataVO(industryDisplay, yellowLine.divide(conver, 2, RoundingMode.HALF_UP).toString(), null, "万元"));
            } else if (total.compareTo(yellowLine) > 0) {
                red.add(new ChartBaseDataVO(industryDisplay, "0", null, "万元"));
                BigDecimal yellowValue = total.subtract(yellowLine).divide(conver, 2, RoundingMode.HALF_UP);
                yellow.add(new ChartBaseDataVO(industryDisplay, yellowValue.toString(), null, "万元"));
                blue.add(new ChartBaseDataVO(industryDisplay, yellowLine.divide(conver, 2, RoundingMode.HALF_UP).toString(), null, "万元"));
            } else {
                red.add(new ChartBaseDataVO(industryDisplay, "0", null, "万元"));
                yellow.add(new ChartBaseDataVO(industryDisplay, "0", null, "万元"));
                blue.add(new ChartBaseDataVO(industryDisplay, total.divide(conver, 2, RoundingMode.HALF_UP).toString(), null, "万元"));
            }
        }

        List<ChartDataVO> data = new ArrayList<>();
        data.add(new ChartDataVO("行业剩余本金正常值", "bar", blue));
        data.add(new ChartDataVO("行业超预警额本金", "bar", yellow));
        data.add(new ChartDataVO("行业超限额本金", "bar", red));
        return new LineBarChartValueVO("存量本金", data);
    }


    public void calculate(LocalDate date) {
        if (date == null) {
            throw new MithrasException("请选择日期后再计算");
        }
        try {
            MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
            metricComputeEvent.setSnapshotDate(date);
            metricComputeEventBus.post(metricComputeEvent);
        } catch (Throwable t) {
            log.error("fullComputeMetricJobHandler error", t);
        }
    }
}
