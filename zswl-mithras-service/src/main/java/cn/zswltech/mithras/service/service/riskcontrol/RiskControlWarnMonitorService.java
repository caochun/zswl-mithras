package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import cn.zswltech.mithras.service.application.riskcontrol.RiskControlOpinionMonitorFacade;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskOpinionWarnCordType;
import cn.zswltech.mithras.service.flow.dynamicform.risk.opinion.RiskOpinionHandleCheckHandler;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorPageWarnDTO;
import cn.zswltech.mithras.riskcontrol.warning.RiskWarnCardDTO;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.riskcontrol.monitor.WarnCountDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus.IGNORED;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static java.util.Objects.isNull;

/**
* @description 风控预警监测
* @author vico
* @date 2024-12-23
*/
@Slf4j
@Service
public class RiskControlWarnMonitorService extends ServiceImpl<RiskControlWarnMonitorMapper, RiskControlWarnMonitor> {

    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Value("${xinsight.ips.primary}")
    private String XinsightIp;

    public List<WarnCountDto> selectCountByClient(Set<String> usccs, String warnLevel) {
        if(ObjectUtil.isEmpty(usccs)){
            return Collections.emptyList();
        }
        return baseMapper.selectCountByClient(usccs,warnLevel);
    }

    public Page<RiskControlWarnMonitor> warnList(RiskWarnMonitorWarnListREQ req) {
        return riskControlWarnMonitorMapper.pageWarn(new Page<>(req.getPage(), req.getPageSize()), BeanUtil.copyProperties(req, RiskControlWarnMonitorPageWarnDTO.class));
    }

    public RiskWarnMonitorWarnDetailRSP warnDetail(SinglePkREQ req) {
        RiskControlWarnMonitor riskControlWarnMonitor = riskControlWarnMonitorMapper.selectById(req.getId());
        RiskWarnMonitorWarnDetailRSP rsp = BeanUtil.copyProperties(riskControlWarnMonitor, RiskWarnMonitorWarnDetailRSP.class);
        //慧眼数据需要拼接IP地址
        if(rsp.getDataSource().equals(RiskDataSourceEnum.XINSIGHT.name())){
            rsp.setLinkAddress(XinsightIp + rsp.getLinkAddress());
        }
        return rsp;
    }

    public void subCheck(Long id) {
        RiskControlWarnMonitor riskControlWarnMonitor = riskControlWarnMonitorMapper.selectById(id);
        if(ObjectUtil.isEmpty(riskControlWarnMonitor)){
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        if(ObjectUtil.isEmpty(riskControlWarnMonitor.getHandleResult()) || ObjectUtil.isEmpty(riskControlWarnMonitor.getAdvisement())) {
            throw new MithrasException("请在详细信息页面右上角点击编辑按钮完成“是否处置”和“处置意见”的填写");
        }
    }

    public RiskWarnMonitorStatisticsRSP statisticsWarn(RiskWarnMonitorStatisticsRSP rsp) {
        if (ObjectUtil.isEmpty(rsp)) {
            rsp = new RiskWarnMonitorStatisticsRSP();
        }
       /* RiskWarnMonitorWarnListREQ warnListREQ = new RiskWarnMonitorWarnListREQ();
        warnListREQ.setPage(1);
        warnListREQ.setPageSize(1);
        R<PageR<RiskWarnMonitorWarnListRSP>> pageRR = riskWarnMonitorController.warnList(warnListREQ);
        if (ObjectUtil.isNotEmpty(pageRR) && ObjectUtil.isNotEmpty(pageRR.getData())) {
            rsp.setTotalWarnClient(pageRR.getData().getTotal());
        }*/
        rsp.setTotalWarnClient(riskControlWarnMonitorMapper.countDealingClient());
        //今日舆情数
        if(ObjectUtil.isEmpty(rsp.getOpinionCard())) {
            rsp.setOpinionCard(new ArrayList<>());
        }
        rsp.getOpinionCard().add(RiskWarnMonitorStatisticsRSP.RiskWarnMonitorStatisticsBody.builder()
                .cardCode(RiskOpinionWarnCordType.RED.name())
                .cardCodeName(RiskOpinionWarnCordType.RED.display())
                .amount(riskControlWarnMonitorMapper.countDealing(3))
                .todayAdd(riskControlWarnMonitorMapper.countNewWarn(3))
                .todayClose(riskControlWarnMonitorMapper.countCloseWarn(3))
                .build());
        rsp.getOpinionCard().add(RiskWarnMonitorStatisticsRSP.RiskWarnMonitorStatisticsBody.builder()
                .cardCode(RiskOpinionWarnCordType.YELLOW.name())
                .cardCodeName(RiskOpinionWarnCordType.YELLOW.display())
                .amount(riskControlWarnMonitorMapper.countDealing(2))
                .todayAdd(riskControlWarnMonitorMapper.countNewWarn(2))
                .todayClose(riskControlWarnMonitorMapper.countCloseWarn(2))
                .build());
        return rsp;
    }

    //统计每天审批中的数据
    public List<RiskWarnMonitorQuantityChangeRSP> quantityChange(LocalDate began) {
        if(ObjectUtil.isEmpty(began)) {
            began = LocalDate.now().minusDays(4);
        }
        //查询流程中数据
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.RiskControlWarnNotPaymentFlow.name(), ProcessModelTypeEnum.RiskControlWarnPaymentFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(5000);
        flowReq.setProcessCreateTimeFrom(Date.from(began.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if(ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return null;
        }
        List<Long> warnIds = flowRespPage.getContents().stream().map(ProcessResp::getBusinessKey).map(Long::parseLong).collect(Collectors.toList());
        Map<Long, Integer> warnId2WarnLevel = riskControlWarnMonitorMapper.selectBatchIds(warnIds).stream().collect(Collectors.toMap(RiskControlWarnMonitor::getId, RiskControlWarnMonitor::getWarnLevel, (a, b) -> a));

        List<RiskWarnMonitorQuantityChangeRSP> rsps = new ArrayList<>();
        //计算每天数量
        RiskWarnMonitorQuantityChangeRSP redRsp = new RiskWarnMonitorQuantityChangeRSP();
        redRsp.setCardCode(RiskOpinionWarnCordType.RED.name());
        redRsp.setCardCodeName(RiskOpinionWarnCordType.RED.display());
        Map<LocalDate, Integer> redRspCardDetail = new HashMap<>();
        redRsp.setCardDetail(redRspCardDetail);

        RiskWarnMonitorQuantityChangeRSP yellowRsp = new RiskWarnMonitorQuantityChangeRSP();
        yellowRsp.setCardCode(RiskOpinionWarnCordType.YELLOW.name());
        yellowRsp.setCardCodeName(RiskOpinionWarnCordType.YELLOW.display());
        Map<LocalDate, Integer> yellowRspCardDetail = new HashMap<>();
        yellowRsp.setCardDetail(yellowRspCardDetail);

        rsps.add(redRsp);
        rsps.add(yellowRsp);

        for (ProcessResp flow : flowRespPage.getContents()) {
            if (ObjectUtil.equals(warnId2WarnLevel.getOrDefault(Long.parseLong(flow.getBusinessKey()), 0), 3)) {
                totalNumEveryDay(redRspCardDetail, flow.getStartTime(), flow.getEndTime(), began);
            }
            if (ObjectUtil.equals(warnId2WarnLevel.getOrDefault(Long.parseLong(flow.getBusinessKey()), 0), 2)) {
                totalNumEveryDay(yellowRspCardDetail, flow.getStartTime(), flow.getEndTime(), began);
            }
        }
        return rsps;
    }

    private void totalNumEveryDay(Map<LocalDate, Integer> cardDetail, Date startTime, Date endTime, LocalDate began) {
        if (ObjectUtil.isEmpty(startTime)) {
            return;
        }
        LocalDate startLocalDate = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate;
        if (ObjectUtil.isEmpty(endTime)) {
            endLocalDate = LocalDate.now();
        } else {
            endLocalDate = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        for(LocalDate temp = startLocalDate; !temp.isAfter(endLocalDate);) {
            if(!temp.isBefore(began)) {
                Integer orDefault = cardDetail.getOrDefault(temp, 0);
                cardDetail.put(temp, ++orDefault);
            }
            temp = temp.plusDays(1);
        }
    }

    public List<RiskWarnMonitorTypeChangeRSP> typeChange(){
        List<RiskWarnCardDTO> riskWarnCardDTOS = riskControlWarnMonitorMapper.totalRiskType();
        if (ObjectUtil.isEmpty(riskWarnCardDTOS)) {
            return null;
        }
        return BeanUtil.copyToList(riskWarnCardDTOS, RiskWarnMonitorTypeChangeRSP.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void warnModify(RiskControlWarnModifyREQ req) {
        RiskControlWarnMonitor record = riskControlWarnMonitorMapper.selectById(req.getId());
        err(isNull(record), RECORD_NOT_EXIST);
        if (ObjectUtil.isNotEmpty(req.getHandleResult()) && ObjectUtil.isNotEmpty(req.getProcessInstanceId())) {
            SpringContextHolder.getBean(RuntimeService.class).setVariable(req.getProcessInstanceId(), RiskOpinionHandleCheckHandler.HANDLE_TYPE, req.getHandleResult());
        }
        riskControlWarnMonitorMapper.updateById(new RiskControlWarnMonitor().setId(req.getId()).setAdvisement(req.getAdvisement()).setHandleResult(req.getHandleResult()));
    }

    //过滤预警
    @Transactional(rollbackFor = Throwable.class)
    public void ignoreWarn() {
        List<RiskControlWarnMonitor> list = this.list(Wrappers.<RiskControlWarnMonitor>lambdaQuery()
                .eq(RiskControlWarnMonitor::getHandleStatus, RiskControlOpinionHandleStatus.PEND_HANDLE.name())
                .ge(RiskControlWarnMonitor::getCreateTime, LocalDate.now()));
        //过滤预警
        List<RiskControlWarnMonitor> updateList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list)) {
            Set<String> focusClientUscCodes = SpringContextHolder.getBean(RiskControlOpinionMonitorFacade.class).focusClientUscCodes();
            list.forEach(
                    e -> {
                        //没有存量合作合同的客户的舆情，也不用处理
                        if (e.getWarnLevel() < 2 || !focusClientUscCodes.contains(e.getCreditCode())) {
                            e.setHandleStatus(IGNORED.name());
                            updateList.add(e);
                        }
                    }
            );
        }
        if (ObjectUtil.isNotEmpty(updateList)) {
            SpringContextHolder.getBean(RiskControlWarnMonitorService.class).updateBatchById(updateList);
        }
    }

    // 与数据库数据作对比并去重
    public List<RiskControlWarnMonitor> existingDate(List<RiskControlWarnMonitor> list){
        List<RiskControlWarnMonitor> results = new ArrayList<>();
        // 1. 查询数据库中已存在的记录
        Set<String> existingKeys = getExistingKeysFromDatabase(list);
        log.info("数据库中已存在的数据量: {}", existingKeys.size());

        // 2. 过滤掉已存在的数据
        results = filterExistingData(list, existingKeys);
        log.info("需要插入的数据量: {}", results.size());

        if (results.isEmpty()) {
            log.info("所有数据在数据库中已存在，无需插入");
            return results;
        }

        return results;
    }

    /**
     * 查询数据库中已存在的记录
     */
    private Set<String> getExistingKeysFromDatabase(List<RiskControlWarnMonitor> data) {
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptySet();
        }

        // 准备查询参数
        List<Map<String, Object>> params = new ArrayList<>();
        for (RiskControlWarnMonitor item : data) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("title", item.getTitle());
            paramMap.put("creditCode", item.getCreditCode());
            paramMap.put("dataTime", item.getDataTime());
            params.add(paramMap);
        }

        // 查询数据库中已存在的记录
        List<RiskControlWarnMonitor> existingRecords = this.baseMapper.selectExistingRecords(params);

        // 转换为唯一键集合
        return existingRecords.stream()
                .map(RiskControlWarnMonitor::getUniqueKey)
                .collect(Collectors.toSet());
    }

    /**
     * 过滤掉已存在的数据
     */
    private List<RiskControlWarnMonitor> filterExistingData(
            List<RiskControlWarnMonitor> allData,
            Set<String> existingKeys) {

        if (CollectionUtils.isEmpty(allData) || CollectionUtils.isEmpty(existingKeys)) {
            return allData;
        }

        List<RiskControlWarnMonitor> filteredData = new ArrayList<>();

        for (RiskControlWarnMonitor item : allData) {
            String uniqueKey = item.getUniqueKey();
            if (!existingKeys.contains(uniqueKey)) {
                filteredData.add(item);
            }
        }

        return filteredData;
    }
}
