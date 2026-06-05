package cn.zswltech.mithras.service.job;


import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorMapper;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionVersionService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlWarnMonitorService;
import cn.zswltech.mithras.riskcontrol.util.WarnCodeGeneratorUtils;
import cn.zswltech.mithras.xinsight.mapper.XinsightWarnMonitorMapper;
import cn.zswltech.mithras.xinsight.model.XinsightWarnMonitor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * XXL-Job定时任务：同步慧眼系统预警数据
 */
@Slf4j
@Component
public class RiskWarnByInsightJob {

    @Resource
    private XinsightWarnMonitorMapper xinsightWarnMonitorMapper;
    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;
    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private WarnCodeGeneratorUtils warnCodeGeneratorUtils;
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;

    @XxlJob("riskWarnDataSyncJob") // 0 0 8,15 * * ?   每天8:00和15:00执行
    public void riskWarnDataSyncJob(){
        log.info("------开始执行慧眼预警数据同步任务------");

        try {
            long startTime = System.currentTimeMillis();

            // 1. 获取上次同步慧眼的最大值
            Long lastSyncTmStamp = getLastSyncTmStamp();
            log.info("上次同步慧眼的tmStamp最大值: {}", lastSyncTmStamp);

            // 2. 执行数据同步
            int totalCount = syncData(lastSyncTmStamp);

            log.info("------数据同步完成，总计同步 {} 条数据，耗时 {} ms------", totalCount, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("------数据同步任务执行失败------", e);
        }
    }

    /**
     * 获取同步慧眼的tmStamp最大值
     */
    private Long getLastSyncTmStamp() {
        try {
            LambdaQueryWrapper<RiskControlWarnMonitor> wrapper = Wrappers.<RiskControlWarnMonitor>lambdaQuery()
                    // 筛选数据来源为慧眼的数据
                    .eq(RiskControlWarnMonitor::getDataSource, RiskDataSourceEnum.XINSIGHT.name())
                    // 按照tmStamp降序排列
                    .orderByDesc(RiskControlWarnMonitor::getSourceXinsightId)
                    // 只取第一条
                    .last("LIMIT 1");

            // 查询单条记录
            RiskControlWarnMonitor lastOne = riskControlWarnMonitorMapper.selectOne(wrapper);

            if (lastOne != null && lastOne.getSourceXinsightId() != null) {
                return lastOne.getSourceXinsightId();
            } else {
                log.info("未找到慧眼数据，从0开始同步");
                return 0L;
            }
        } catch (Exception e) {
            log.warn("获取上次同步的tmStamp最大值失败，从0开始", e);
            return 0L; // 第一次执行时返回0，同步所有数据
        }
    }

    /**
     * 执行数据同步
     */
    @Transactional(rollbackFor = Exception.class)
    private int syncData(Long lastMaxId) {
        int totalCount = 0;
        int batchSize = 1000; // 每批次1000条

        try {
            // 根据lastMaxId查询慧眼预警数据
            List<XinsightWarnMonitor> xinsightWarnList = xinsightWarnMonitorMapper.selectByTmStamp(lastMaxId);
            // 数据去重
            List<XinsightWarnMonitor> dupWarnList = removeDuplicates(xinsightWarnList);
            // 预警数据转换
            List<RiskControlWarnMonitor> warnParams = convertToEntities(dupWarnList);
            // 与数据库数据作对比并去重
            warnParams = riskControlWarnMonitorService.existingDate(warnParams);
            // 分批插入预警表
            List<List<RiskControlWarnMonitor>> warnPartition = Lists.partition(warnParams,batchSize);
            for (List<RiskControlWarnMonitor> warnBatch : warnPartition) {
                // 预警数据批量插入目标表
                List<RiskControlWarnMonitor> warnList = batchInsertData(warnBatch);
                // 预警数据插入后如果要发起预警流程 需要筛选：处理状态为待处理的才发起流程
                //riskControlOpinionVersionService.warnInitiateApproval(warnList);// 不直接走流程，使用另一个定时任务

                totalCount += warnList.size();
                log.info("数据同步完成，本批次同步累计{}条", totalCount);
            }

        } catch (Exception e) {
            log.error("数据同步失败", e);
            throw new RuntimeException("数据同步异常", e);
        }

        return totalCount;
    }

    /**
     * 转换数据为实体列表
     */
    private List<RiskControlWarnMonitor> convertToEntities(List<XinsightWarnMonitor> sourceData) {
        List<RiskControlWarnMonitor> entities = new ArrayList<>();
        Map<Long, Client> clientMap = new HashMap<>();
        if(CollectionUtil.isEmpty(sourceData)){
            return entities;
        }
        // 客户数据相关字段需要取慧眼client_id去客户表中查询，再取客户表中的数据保存（会出现慧眼数据客户信息变动，跟本系统客户表数据不一致）
        // 根据客户ID查询
        List<Long> clientIds = sourceData.stream()
                .map(XinsightWarnMonitor::getClientId)
                .filter(Objects::nonNull)
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(clientIds)) {
            List<Client> clients = clientMapper.selectBatchIds(clientIds);
            clients.forEach(client -> clientMap.put(client.getId(), client));
        }
        log.info("批量查询客户信息，共查询到{}条数据", clientMap.size());
        // 批量生成所需的预警编号
        List<String> warnCodes = warnCodeGeneratorUtils.generateBatchWarnCodes(sourceData.size());
        int idIndex = 0;

        for (XinsightWarnMonitor row : sourceData) {
            RiskControlWarnMonitor entity = new RiskControlWarnMonitor();
            Long clientId = Long.valueOf(row.getClientId());
            Client client = clientMap.get(clientId);
            // 客户相关字段
            if (ObjectUtils.isEmpty(client)){
                entity.setCreditCode(row.getCreditCode());
            }else {
                entity.setChiName(client.getClientName());
                entity.setCreditCode(client.getUscCode());
            }
            // 从慧眼数据映射字段
            entity.setTitle(row.getTitle());
            entity.setDataTime(row.getDataTime());
            entity.setLinkAddress(row.getLinkAddress());
            entity.setWarnLevel(row.getWarnLevel());
            entity.setRiskType(row.getRiskType());
            entity.setNoticeTime(LocalDate.from(row.getNoticeTime()));
            entity.setSourceXinsightId(row.getTmStamp());
            entity.setDataSource(RiskDataSourceEnum.XINSIGHT.name());//FHC-金控, XINSIGHT-慧眼
            //其他字段
            entity.setWarnCode(warnCodes.get(idIndex));
            entity.setHandleStatus(convertHandleStatus(row.getWarnLevel()));

            entities.add(entity);
            idIndex++;
        }

        return entities;
    }

    /**
     * 批量插入数据
     */
    private List<RiskControlWarnMonitor> batchInsertData(List<RiskControlWarnMonitor> batchList) {
        List<RiskControlWarnMonitor> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(batchList)) {
            return resultList;
        }

        // 批量插入
        try {
            boolean success = riskControlWarnMonitorService.saveBatch(batchList);
            if (success){
                resultList.addAll(batchList);
            }
            return resultList;
        } catch (Exception e) {
            log.error("批量插入数据失败，数量：{}", batchList.size(), e);
            return resultList;
        }
    }

    /**
     * 处理状态转换
     */
    private String convertHandleStatus(Integer warnLevel){
        if (warnLevel < 2){
            return RiskControlOpinionHandleStatus.IGNORED.name();
        }else {
            return RiskControlOpinionHandleStatus.PEND_HANDLE.name();
        }
    }

    /**
     * 去重方法：根据 title, creditCode, dataTime 去除重复的 XinsightWarnMonitor 对象
     * @param list 待去重的 XinsightWarnMonitor 列表
     * @return 去重后的列表，保留首次出现的对象
     */
    public static List<XinsightWarnMonitor> removeDuplicates(List<XinsightWarnMonitor> list) {
        // 处理空列表或 null 的情况
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        //  去重
        return list.stream()
                .filter(Objects::nonNull)  // 过滤掉为null的XinsightInfo对象
                .filter(info -> {
                    // 检查所有组成键的字段是否都为null或空
                    String title = info.getTitle();
                    String creditCode = info.getCreditCode();
                    LocalDateTime dataTime = info.getDataTime();
                    // 如果所有字段都为null或空，则过滤掉该对象
                    return !(isEmptyString(title) && isEmptyString(creditCode) && dataTime == null);
                })
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                // 键：由 title, creditCode, dataTime 组成的 List
                                info -> Arrays.asList(info.getTitle(), info.getCreditCode(), info.getDataTime()),
                                // 值：对象本身
                                info -> info,
                                // 合并函数：当键冲突时，保留已存在的对象（首次出现）
                                (existing, replacement) -> existing,
                                LinkedHashMap::new // 使用 LinkedHashMap 保持原始插入顺序
                        ),
                        // 将 Map 的值转换为 List
                        map -> new ArrayList<>(map.values())
                ));
    }

    private static boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

}
