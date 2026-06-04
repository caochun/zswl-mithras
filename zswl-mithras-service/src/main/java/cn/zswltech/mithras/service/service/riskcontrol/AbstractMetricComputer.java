package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.riskcontrol.common.AlertState;
import cn.zswltech.mithras.riskcontrol.common.MetricUnit;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategySnapshot;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategySnapshotService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.zswltech.mithras.riskcontrol.common.AlertState.OVER;
import static cn.zswltech.mithras.riskcontrol.common.AlertState.WARNING;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/10 15:00
 */
public abstract class AbstractMetricComputer {
    /**
     * 亿元转换因子
     */
    private static final BigDecimal BILLION_FACTOR = new BigDecimal(100000000L);
    /**
     * 倍转换因子
     */
    private static final BigDecimal TIMES_FACTOR = new BigDecimal(10000L);
    /**
     * %转换因子
     */
    private static final BigDecimal PERCENT_FACTOR = new BigDecimal(10000L);

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetricComputer.class);
    @Resource
    protected MetricComputeEventBus eventBus;
    @Resource
    private RiskControlStrategyService riskControlStrategyService;
    @Resource
    private RiskControlStrategySnapshotService riskControlStrategySnapshotService;
    @Resource
    private MessageService messageService;
    @Resource
    private SysUserService userService;
    @Resource
    private MessageConver messageConver;

    @PostConstruct
    public void init() {
        eventBus.register(this, getMetricCode());
        LOGGER.info("MetricComputer {} register to eventBus", getMetricCode());
    }

    public void compute(MetricComputeEvent metricComputeEvent) {
        if (metricComputeEvent.getSnapshotDate() == null) {
            metricComputeEvent.setSnapshotDate(LocalDate.now());
        }
        // 若需计算的数据时间不等于今天，则执行快照计算
        RiskControlStrategy theOne = riskControlStrategyService.getOne(Wrappers.<RiskControlStrategy>lambdaQuery()
                .eq(RiskControlStrategy::getMetricCode, getMetricCode())
                .last("limit 1"));
        if (theOne == null) {
            LOGGER.error("未找到预警监控指标，metricCode={}", getMetricCode());
            return;
        }
        LocalDate firstDayOfLastMonth = metricComputeEvent.getSnapshotDate()
                .with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        metricComputeEvent.setFactorQueryDate(firstDayOfLastMonth);
        try {
            calculate(metricComputeEvent, theOne);
            if (theOne.getCurrentValueOneDecimal() != null) {
                if (MetricUnit.BILLION_YUAN.display().equals(theOne.getValueUnitOne())) {
                    theOne.setCurrentValueOne(
                            theOne.getCurrentValueOneDecimal()
                                    .divide(BILLION_FACTOR, 0, RoundingMode.HALF_UP).longValue());
                } else if (MetricUnit.PERCENTAGE.display().equals(theOne.getValueUnitOne())) {
                    theOne.setCurrentValueOne(theOne.getCurrentValueOneDecimal().multiply(PERCENT_FACTOR).longValue());
                } else if (MetricUnit.TIMES.display().equals(theOne.getValueUnitOne())) {
                    theOne.setCurrentValueOne(theOne.getCurrentValueOneDecimal().multiply(TIMES_FACTOR).longValue());
                }
            }
            if (theOne.getCurrentValueTwoDecimal() != null) {
                if (MetricUnit.BILLION_YUAN.display().equals(theOne.getValueUnitTwo())) {
                    theOne.setCurrentValueTwo(
                            theOne.getCurrentValueTwoDecimal()
                                    .divide(BILLION_FACTOR, 0, RoundingMode.HALF_UP).longValue());
                }
            }
            if (ObjectUtil.isNotEmpty(theOne.getCurrentValueOne())) {
                theOne.setNullReason(null);
            }
            // 如果预警开关打开，当前值超过预警值，发送消息
            if (theOne.getEarlyWarningState() == 1 && theOne.getCurrentValueOne() != null) {
                AlertState alertState = theOne.currentAlertState();
                switch (alertState) {
                    case WARNING:
                        if (canSendMessage(theOne.getLastEarlyWaringTime())) {
                            sendMessage(WARNING, theOne);
                            theOne.setLastEarlyWaringTime(LocalDate.now());
                        }
                        break;
                    case OVER:
                        if (canSendMessage(theOne.getLastLimitWaringTime())) {
                            sendMessage(OVER, theOne);
                            theOne.setLastLimitWaringTime(LocalDate.now());
                        }
                        break;
                    default:
                        break;
                }
            }
            if (!LocalDate.now().equals(metricComputeEvent.getSnapshotDate())) {
                RiskControlStrategySnapshot snapshot = new RiskControlStrategySnapshot();
                snapshot.setMetricId(theOne.getId());
                snapshot.setDate(metricComputeEvent.getSnapshotDate());
                snapshot.setValueTwo(theOne.getCurrentValueTwo());
                snapshot.setValueOne(theOne.getCurrentValueOne());
                riskControlStrategySnapshotService.saveOrUpdate(snapshot,
                        Wrappers.<RiskControlStrategySnapshot>lambdaQuery()
                                .eq(RiskControlStrategySnapshot::getMetricId, theOne.getId())
                                .eq(RiskControlStrategySnapshot::getDate, metricComputeEvent.getSnapshotDate()));
            } else {
                riskControlStrategyService.updateById(theOne);
            }
        } catch (Throwable t) {
            LOGGER.error("计算风控指标异常，metricCode={}", getMetricCode(), t);
        }
    }

    /**
     * 一个月只能发送一次
     *
     * @param targetDate
     * @return
     */
    private boolean canSendMessage(LocalDate targetDate) {
        LocalDate now = LocalDate.now();
        if (targetDate != null && targetDate.getYear() == now.getYear()
                && targetDate.getMonthValue() == now.getMonthValue()) {
            return false;
        }
        return true;
    }

    private void sendMessage(AlertState alertState, RiskControlStrategy theOne) {
        Set<String> jobCodes = new HashSet<>(Arrays.asList(JobEnum.riskdeptmanager.name(), JobEnum.chiefriskofficer.name(), JobEnum.assetmanagement.name()));
        List<Long> to = userService.jobUsers(jobCodes);
        MessageAddREQ addReq = new MessageAddREQ();
        addReq.setTo(to);
        addReq.setMessageType(MessageTypeEnum.INDICATOR_WARNING.name());
        addReq.setNeedOa(true);
        addReq.setNoticeSource(BusinessModuleEnum.RISK_MONITOR.name());
        addReq.setPcurl(String.format(MessageUrlEnum.INDICATOR_WARNING.pcUrl, theOne.getId()));
        addReq.setFrom("风险监控");
        if (alertState.equals(WARNING)) {
            addReq.setRelation(theOne.getMetricCode() + "指标预警");
            addReq.setContent(theOne.getMetricCode());
        } else {
            addReq.setRelation(theOne.getMetricCode() + "指标超限");
            addReq.setContent(theOne.getMetricCode());
        }
        messageService.sendMessage(messageConver.reqToMessage(addReq));
    }

    protected abstract String getMetricCode();

    protected abstract void calculate(MetricComputeEvent event, RiskControlStrategy strategy);
}
