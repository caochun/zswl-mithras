package cn.zswltech.mithras.application.orchestration.job.riskcontrol;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.riskcontrol.application.job.RiskOpinionByInsightJobService;
import cn.zswltech.mithras.riskcontrol.common.RiskControlEmotionEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.riskcontrol.util.IdGeneratorUtils;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionVersionService;
import cn.zswltech.mithras.third.xinsight.persistence.mapper.XinsightInfoMapper;
import cn.zswltech.mithras.third.xinsight.persistence.model.XinsightInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * XXL-Job定时任务：同步慧眼系统舆情数据
 */
@Slf4j
@Component
public class RiskOpinionByInsightJobServiceImpl implements RiskOpinionByInsightJobService {


    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;
    @Resource
    private IdGeneratorUtils idGenerator;
    @Resource
    private XinsightInfoMapper xinsightInfoMapper;

    private static final String RISK_TYPE_1 = "news";//新闻舆情(一般舆情)
    private static final String RISK_TYPE_2 = "business";//工商舆情


    @Override
    public void syncOpinionData() {
        log.info("------开始执行慧眼舆情数据同步任务------");

        try {
            long startTime = System.currentTimeMillis();

            // 1. 获取上次同步慧眼的最大TMSTAMP字段（从新增的字段获取）
            Long lastSyncTmStamp = getLastSyncTmStamp();
            log.info("上次同步慧眼的最大TmStamp: {}", lastSyncTmStamp);

            // 2. 执行数据同步
            int totalCount = syncData(lastSyncTmStamp);

            log.info("------数据同步完成，总计同步 {} 条数据，耗时 {} ms------",
                    totalCount, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("------数据同步任务执行失败------", e);
        }
    }

    /**
     * 获取同步慧眼的最大TMSTAMP
     */
    private Long getLastSyncTmStamp() {
        try {
            LambdaQueryWrapper<RiskControlOpinionMonitor> wrapper = Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                    // 筛选数据来源为慧眼的数据
                    .eq(RiskControlOpinionMonitor::getDataSource, RiskDataSourceEnum.XINSIGHT.name())
                    // 按照TMSTAMP降序排列
                    .orderByDesc(RiskControlOpinionMonitor::getSourceXinsightId)
                    // 只取第一条
                    .last("LIMIT 1");

            // 查询单条记录
            RiskControlOpinionMonitor lastRecord = riskControlOpinionMonitorMapper.selectOne(wrapper);

            if (lastRecord != null && lastRecord.getSourceXinsightId() != null) {
                return lastRecord.getSourceXinsightId();
            } else {
                log.info("未找到慧眼数据，从0开始同步");
                return 0L;
            }
        } catch (Exception e) {
            log.warn("获取上次同步的最大TMSTAMP失败，从0开始", e);
            return 0L; // 第一次执行时返回0，同步所有数据
        }
    }

    /**
     * 执行数据同步
     */
    @Transactional(rollbackFor = Exception.class)
    private int syncData(Long lastMaxId) {
        int totalCount = 0;
        int batchSize = 1000; // 每批1000条

        try {
        // 根据lastMaxId查询全量慧眼数据
        List<XinsightInfo> xinsightInfos = xinsightInfoMapper.selectByTmStamp(lastMaxId);
        // 按照新闻舆情与工商舆情的规则进行筛选
        // 新闻舆情
        List<Integer> newsModuleIds = Arrays.asList(40, 41, 42);
        List<XinsightInfo> newsList = xinsightInfos.stream()
                .filter(Objects::nonNull)  // 过滤空对象
                .filter(info -> info.getConfigId() != null && info.getConfigId() == 3)
                .filter(info -> info.getModuleId() != null && newsModuleIds.contains(info.getModuleId()))
                .collect(Collectors.toList());
        // 数据去重
        List<XinsightInfo> dupNewsList = removeDuplicates(newsList);
        // 工商舆情
        List<Integer> businessModuleIds = Arrays.asList(52,55,56,58,60,61,62,63,66,70,71);
        List<XinsightInfo> businessList = xinsightInfos.stream()
                .filter(Objects::nonNull)  // 过滤空对象
                .filter(info -> info.getConfigId() != null && info.getConfigId() == 4)
                .filter(info -> info.getModuleId() != null && businessModuleIds.contains(info.getModuleId()))
                .collect(Collectors.toList());
        // 数据去重
        List<XinsightInfo> dupBusinessList = removeDuplicates(businessList);
        // 新闻舆情数据转换
        List<RiskControlOpinionMonitor> newsParams = convertToEntities(dupNewsList,RISK_TYPE_1);
        // 与数据库数据作对比并去重
        newsParams = riskControlOpinionMonitorService.existingDate(newsParams);
        // 分批插入
        List<List<RiskControlOpinionMonitor>> newsPartition = Lists.partition(newsParams,batchSize);
        for (List<RiskControlOpinionMonitor> newsBatch : newsPartition) {
            // 新闻舆情批量插入目标表
            List<RiskControlOpinionMonitor> newsRiskList = batchInsertData(newsBatch);
            // 新闻舆情插入数据后还要发起舆情流程
            riskControlOpinionVersionService.opinionInitiateApproval(newsRiskList);
        }

        // 工商舆情数据转换
        List<RiskControlOpinionMonitor> businessParams = convertToEntities(dupBusinessList,RISK_TYPE_2);
        // 与数据库数据作对比并去重
        businessParams = riskControlOpinionMonitorService.existingDate(businessParams);
        // 分批插入
        List<List<RiskControlOpinionMonitor>> businessPartition = Lists.partition(businessParams,batchSize);
        for (List<RiskControlOpinionMonitor> businessBatch : businessPartition) {
            // 工商舆情批量插入目标表
            batchInsertData(businessBatch);
        }

        totalCount = newsParams.size() + businessParams.size();

        log.info("数据同步完成，本次同步累计{}条", totalCount);

        } catch (Exception e) {
            log.error("数据同步失败", e);
            throw new RuntimeException("数据同步异常", e);
        }

        return totalCount;
    }

    /**
     * 转换数据为实体列表
     */
    private List<RiskControlOpinionMonitor> convertToEntities(List<XinsightInfo> sourceData,String riskTypeFlag) {
        List<RiskControlOpinionMonitor> entities = new ArrayList<>();
        Map<Long, Client> clientMap = new HashMap<>();
        if(CollectionUtil.isEmpty(sourceData)){
            return entities;
        }
        // 客户数据相关字段需要取慧眼CLIENTID去客户表中查询，再取客户表中的数据保存（会出现慧眼数据客户信息变动，跟本系统客户表数据不一致）
        // 根据客户ID查询
        List<Long> clientIds = sourceData.stream()
                .map(XinsightInfo::getClientId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(clientIds)) {
            List<Client> clients = clientMapper.selectBatchIds(clientIds);
            clients.forEach(client -> clientMap.put(client.getId(), client));
        }
        log.info("批量查询客户信息，共查询到{}条数据", clientMap.size());
        // 批量生成所需的所有ID（提高性能）
        List<Long> generatedIds = idGenerator.generateBatchIds(sourceData.size());
        int idIndex = 0;

        for (XinsightInfo row : sourceData) {
            RiskControlOpinionMonitor entity = new RiskControlOpinionMonitor();
            Long clientId = row.getClientId();
            Client client = clientMap.get(clientId);
            // 客户相关字段
            if (ObjectUtils.isEmpty(client)){
                entity.setChiName(row.getSjitName());
                entity.setCreditCode(row.getSymbol());
            }else {
                entity.setChiName(client.getClientName());
                entity.setCreditCode(client.getUscCode());
            }
            // 从慧眼数据映射字段
            entity.setSourceXinsightId(row.getTmStamp());
            entity.setDataSource(RiskDataSourceEnum.XINSIGHT.name());//FHC-金控, XINSIGHT-慧眼
            entity.setTitle(row.getNewsTitle());
            entity.setNewsUrl(row.getXnewsUrl());
            entity.setInfoPublDate(convertStandardFormat(row.getPublishTime()));
            entity.setRelationType(row.getReleTypeCode());
            entity.setRelationTypeName(row.getReleTypeCn());
            entity.setRelateCompanyName(row.getSymbolComp());
            entity.setNoticeTime(convertStandardFormat(row.getXdfwDate()));
            entity.setSourceName(row.getNewsSource());
            entity.setRiskType(convertToRiskType(riskTypeFlag));
            Integer level = convertModToInt(row.getModuleId());
            // 工商舆情字段逻辑
            if (riskTypeFlag.equals(RISK_TYPE_2)){
                entity.setNewTypeOpinion(convertToBusinessType(row.getModuleId()));
                entity.setWarnLevel(1);// 工商舆情预警信号默认绿色
                entity.setWarnStar(1);// 工商舆情默认为一星
            }else {
                // 新闻舆情字段逻辑
                entity.setEmotion(convertToEmotion(row.getEmotionName()));
                entity.setWarnStar(level);
                entity.setWarnLevel(level);
            }
            //其他字段
            entity.setId(generatedIds.get(idIndex++));// 使用生成的ID，防止与表中存量数据ID冲突
            entity.setHandleStatus(convertHandleStatus(level,riskTypeFlag));

            entities.add(entity);
        }

        return entities;
    }

    /**
     * 批量插入数据
     */
    private List<RiskControlOpinionMonitor> batchInsertData(List<RiskControlOpinionMonitor> batchList) {
        List<RiskControlOpinionMonitor> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(batchList)) {
            return resultList;
        }

        // 批量插入
        try {
            boolean success = riskControlOpinionMonitorService.saveBatch(batchList);
            if (success){
                resultList.addAll(batchList);
            }
            return resultList;
        } catch (Exception e) {
            log.error("批量插入数据失败，数量：{}", batchList.size(), e);
            return resultList;
        }
    }

    private LocalDateTime convertStandardFormat(Object value) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (value == null) {
            return null;
        }

        // 如果是字符串格式
        if (value instanceof String) {
            String str = ((String) value).trim();
            try {
                return LocalDateTime.parse(str, FORMATTER);
            } catch (Exception e) {
                // 尝试处理可能的时区信息
                str = str.replace("T", " ").replace("Z", "");
                return LocalDateTime.parse(str, FORMATTER);
            }
        }

        // 如果是 Timestamp
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }

        // 如果是其他日期类型
        if (value instanceof Date) {
            return ((Date) value).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        throw new IllegalArgumentException("不支持的日期类型: " + value.getClass());
    }

    /**
     * MODULEID转换预警星级/信号
     * MODULEID=40 三星/红色 MODULEID=41 二星/黄色 MODULEID=42 一星/绿色
     */
    private Integer convertModToInt(Integer value) {
        if (value == null) {return null;}
        if (value == 40){
            return 3;
        }else if (value == 41){
            return 2;
        } else if (value == 42) {
            return 1;
        }else {
            return 1;
        }
    }

    /**
     * 舆情类型转换：1 基础舆情  2 工商舆情
     */
    private Integer convertToRiskType(String riskTypeFlag){
        if (riskTypeFlag.equals(RISK_TYPE_1)){
            return 1;
        }else {
            return 2;
        }
    }

    /**
     * 开庭公告： MODULEID=70
     * 企业变更： MODULEID=66
     * 法院公告： MODULEID=71
     * 立案信息： MODULEID=56
     * 其他：    MODULEID IN（'52','55','58','60','61','62','63'）
     */
    private String convertToBusinessType(Integer moduleid){
        if (moduleid == 70){
            return RiskControlOpinionEnum.COURT_SESSION.value;
        }else if (moduleid == 66){
            return RiskControlOpinionEnum.CHANGE_INFO.value;
        }else if (moduleid == 71){
            return RiskControlOpinionEnum.COURT_ANNOUNCE.value;
        }else if (moduleid == 56){
            return RiskControlOpinionEnum.CASE_INFO.value;
        }else {
            return RiskControlOpinionEnum.OTHER.value;
        }
    }

    private String convertHandleStatus(Integer level,String riskTypeFlag){
        if (riskTypeFlag.equals(RISK_TYPE_1)){
            if (level < 2){
                return RiskControlOpinionHandleStatus.IGNORED.name();
            }else {
                return RiskControlOpinionHandleStatus.PEND_HANDLE.name();
            }
        }else {
            return RiskControlOpinionHandleStatus.IGNORED.name();
        }
    }

    /**
     * 情感方向字段转换
     * 负面---预警
     * 中性---一般
     * 正面---正面
     */
    private String convertToEmotion(Object value){
        if (ObjectUtils.isEmpty(value)){
            return null;
        }
        String emotionname = (String) value;
        if (emotionname.equals(RiskDataSourceEnum.EMOTIONNAME_YJ.display)){
            return RiskControlEmotionEnum.FCC0000002QA.name();
        }else if (emotionname.equals(RiskDataSourceEnum.EMOTIONNAME_YB.display)){
            return RiskControlEmotionEnum.FCC0000002QF.name();
        }else if (emotionname.equals(RiskDataSourceEnum.EMOTIONNAME_ZM.display)){
            return RiskControlEmotionEnum.FCC0000002Q9.name();
        }else {
            return null;
        }

    }

    /**
     * 去重方法：根据 newsTitle, symbol, publishTime 去除重复的 XinsightInfo 对象
     * @param list 待去重的 XinsightInfo 列表
     * @return 去重后的列表，保留首次出现的对象
     */
    public static List<XinsightInfo> removeDuplicates(List<XinsightInfo> list) {
        // 处理空列表或 null 的情况
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        //  去重
        return list.stream()
                .filter(Objects::nonNull)  // 过滤掉为null的XinsightInfo对象
                .filter(info -> {
                    // 检查所有组成键的字段是否都为null或空
                    String newsTitle = info.getNewsTitle();
                    String symbol = info.getSymbol();
                    Date publishTime = info.getPublishTime();
                    // 如果所有字段都为null或空，则过滤掉该对象
                    return !(isEmptyString(newsTitle) && isEmptyString(symbol) && publishTime == null);
                })
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                // 键：由 newsTitle, symbol, publishTime 组成的 List
                                info -> Arrays.asList(info.getNewsTitle(), info.getSymbol(), info.getPublishTime()),
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
