package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ProjItemStatus;
import cn.zswltech.mithras.client.vwsync.infrastructure.mapper.ClientVwSyncMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientViewByRiskControl;
import cn.zswltech.mithras.client.vwsync.infrastructure.model.ClientVwSync;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.client.vwsync.application.ClientVwSyncService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contract.*;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import cn.zswltech.mithras.contract.versioning.application.ContractTenantryLibService;

@Slf4j
@Component
public class ClientViewByRiskControlJob {

    @Resource
    private ClientService clientService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;
    @Resource
    private ContractPledgeLibService contractPledgeLibService;
    @Resource
    private ClientVwSyncMapper clientVwSyncMapper;
    @Resource
    private ClientVwSyncService clientVwSyncService;

    /**
     * XXL-Job定时任务：同步监控客户数据
     */
    @XxlJob("syncMonitoringClientsJob") // 0 0 16 * * ?   每天16：00
    public void syncMonitoringClientsJob() {
        try {
            // 记录任务开始
            String jobParam = XxlJobHelper.getJobParam();
            XxlJobHelper.log("开始执行监控客户数据同步任务，参数: {}", jobParam);

            // 执行数据同步
            boolean syncSuccess = syncMonitoringClients(jobParam);

            if (syncSuccess) {
                XxlJobHelper.handleSuccess("监控客户数据同步成功");
                log.info("监控客户数据同步任务执行成功");
            } else {
                XxlJobHelper.handleFail("监控客户数据同步失败");
                log.error("监控客户数据同步任务执行失败");
            }

        } catch (Exception e) {
            XxlJobHelper.log("监控客户数据同步任务异常: {}", e.getMessage());
            XxlJobHelper.handleFail("监控客户数据同步异常: " + e.getMessage());
            log.error("监控客户数据同步任务异常", e);
        }
    }

    /**
     * 同步监控客户数据
     * @return 是否同步成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean syncMonitoringClients(String jobParam) {
        long startTime = System.currentTimeMillis();
        String batchNo = "BATCH_" + System.currentTimeMillis();

        try {
            log.info("开始获取监控客户数据");

            // 1. 获取监控客户数据
            List<ClientViewByRiskControl> monitoringClients = getMonitoringClientsFromService(jobParam);
            if (CollectionUtils.isEmpty(monitoringClients)) {
                log.info("未获取到监控客户数据");
                return true;
            }
            log.info("获取到 {} 条监控客户数据", monitoringClients.size());

            // 2. 清空同步表
            int deletedRows = clientVwSyncMapper.delete(null);
            log.info("已清空同步表,删除记录数: {}", deletedRows);

            // 3. 批量插入数据
            int batchSize = 1000; // 每批插入1000条
            int totalInserted = 0;

            for (int i = 0; i < monitoringClients.size(); i += batchSize) {
                int end = Math.min(i + batchSize, monitoringClients.size());
                List<ClientViewByRiskControl> batch = monitoringClients.subList(i, end);

                int inserted = insertBatch(batch, batchNo);
                totalInserted += inserted;

                log.info("已插入第 {}-{} 条数据，共 {} 条", i + 1, end, totalInserted);
            }

            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;

            // 4. 打印同步日志
            log.info("同步完成，共插入 {} 条数据，耗时 {} 毫秒", totalInserted, costTime);

            return true;

        } catch (Exception e) {
            String errorMsg = "同步监控客户数据失败: " + e.getMessage();
            log.error("同步失败: {}", errorMsg);
            throw e; // 抛出异常让事务回滚
        }
    }

    /**
     * 从业务服务获取监控客户
     */
    private List<ClientViewByRiskControl> getMonitoringClientsFromService(String jobParam) {
        try {
            // 1. 获取所有的企业客户
            List<Client> allClients = getAllClients();
            log.info("获取到 {} 个企业客户", allClients.size());

            if (CollectionUtils.isEmpty(allClients)){
                log.info("未查询到客户数据");
                return null;
            }
            List<Long> clientIds = allClients.stream().map(Client::getId).collect(Collectors.toList());
            // 2. 获取客户敞口映射
            Map<Long, Long> exposureMap = clientService.clientStockRiskExposureMap(clientIds);
            log.info("获取到 {} 个客户的敞口信息", exposureMap.size());

            // 3. 获取有敞口的客户ID
            Set<Long> clientIdsWithExposure = exposureMap.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            log.info("有敞口的客户数量: {}", clientIdsWithExposure.size());

            if (ObjectUtil.isNotEmpty(jobParam)){
                log.info("传参数据: {}", jobParam);
                // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                if (clientIdsWithExposure.contains(safeParseLong(jobParam))){
                    log.info("true：传参数据在敞口>0的数据中");
                }else {
                    log.info("false：传参数据不在敞口>0得数据中");
                }
            }

            // 4. 获取有效的立项,评审,合同的客户ID
            Set<Long> clientIdsWithValidCredit = getEffectiveClients(jobParam);
            log.info("有效的立项,评审,合同的客户数量: {}", clientIdsWithValidCredit.size());

            if (ObjectUtil.isNotEmpty(jobParam)){
                log.info("传参数据: {}", jobParam);
                // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                if (clientIdsWithValidCredit.contains(safeParseLong(jobParam))){
                    log.info("true：传参数据在立项,评审,合同的数据中");
                }else {
                    log.info("false：传参数据不在立项,评审,合同数据中");
                }
            }

            // 5. 筛选监控客户
            return allClients.stream()
                    .filter(client -> {
                        Long clientId = client.getId();
                        boolean hasExposure = clientIdsWithExposure.contains(clientId);
                        boolean hasValidCredit = clientIdsWithValidCredit.contains(clientId);

                        // 满足条件1或条件2
                        return hasExposure || hasValidCredit;
                    })
                    .map(client -> new ClientViewByRiskControl(
                            Long.toString(client.getId()),
                            client.getUscCode(),
                            client.getClientName()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取监控客户数据异常: {}", e.getMessage());
            throw new RuntimeException("获取监控客户数据失败", e);
        }
    }

    /**
     * 获取所有的企业客户
     */
    private List<Client> getAllClients() {

        List<Client> list = clientService.list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientType, ClientType.CORPORATION)
                .isNotNull(Client::getUscCode));
        //log.info("同步客户列表:[{}]", JSONUtil.toJsonStr(list.stream().map(Client::getId).collect(Collectors.toList())));
        return list;
    }

    /**
     * 新方法
     * 获取：客户作为承租人/联合承租人/担保人/抵押人/质押人名下存在有效期的立项,评审,合同 的客户ID集合
     */
    private Set<Long> getEffectiveClients(String jobParam) {
        Set<Long> result = new HashSet<>();
        // 查询全量的项目评审数据
        List<ProjReviewBaseInfo> allProjReviewList = projReviewBaseInfoService.list();
        // 查询全量的合同数据
        List<ContractBaseInfo> allContractList = contractBaseInfoService.list();

        // 1. 项目立项 返回已到达评审阶段的立项数据
        List<ProjEstablishBaseInfo> projEstablishClientIds = getProjEstablishClientIds(result, allProjReviewList, jobParam);

        // 2. 集团授信 返回已到达评审阶段授信项数据
        List<GroupCreditReviewBaseInfo> groupCreditClientIds = getGroupCreditClientIds(result, allProjReviewList);

        // 3. 项目评审 返回已到达合同阶段的评审数据
        List<ProjReviewBaseInfo> projReviewClientIds = getProjReviewClientIds(result, projEstablishClientIds, groupCreditClientIds, allProjReviewList, allContractList, jobParam);

        // 4. 合同
        getContractClientIds(result,projReviewClientIds,allContractList,jobParam);

        return result;
    }

    // 获取项目立项的客户ID
    private List<ProjEstablishBaseInfo> getProjEstablishClientIds(Set<Long> result,List<ProjReviewBaseInfo> allProjReviewList,String jobParam){
        try {
            // 1.1查询有效状态的项目数据
            LambdaQueryWrapper<ProjEstablishBaseInfo> projEstablishQuery = Wrappers.lambdaQuery();
            projEstablishQuery.eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name());
            List<ProjEstablishBaseInfo> projTakeEffectList = projEstablishBaseInfoService.list(projEstablishQuery);

            if (CollectionUtil.isNotEmpty(projTakeEffectList)){
                // 2.2查找生效项目立项在全量评审中的数据
                List<String> projCodes = projTakeEffectList.stream().map(ProjEstablishBaseInfo::getProjCode).collect(Collectors.toList());
                List<ProjReviewBaseInfo> projReviewfiltList = allProjReviewList.stream()
                        .filter(Objects::nonNull)
                        .filter(info -> StringUtils.isNotEmpty(info.getProjCode()) && projCodes.contains(info.getProjCode()))
                        .collect(Collectors.toList());

                if (CollectionUtil.isEmpty(projReviewfiltList)){
                    // 为空说明全都未到项目评审阶段，直接获取立项数据的客户ID
                    Set<Long> projEsClientIds = extractDistinctClientIds(projTakeEffectList);
                    result.addAll(projEsClientIds);
                }else {// 不为空说明有立项数据走到项目评审阶段
                    // 筛选 部分项目立项生效但是未走到评审阶段的数据
                    Set<String> reviewProjCodes = allProjReviewList.stream()
                            .map(ProjReviewBaseInfo::getProjCode)
                            .filter(StringUtils::isNotEmpty)
                            .collect(Collectors.toSet());
                    Map<Boolean, List<ProjEstablishBaseInfo>> ProjEstablishMap = projTakeEffectList.stream()
                            .collect(Collectors.partitioningBy(
                                    proj -> StringUtils.isNotEmpty(proj.getProjCode())
                                            && reviewProjCodes.contains(proj.getProjCode())
                            ));
                    // 未到达
                    List<ProjEstablishBaseInfo> projEstablishInfoFalse = ProjEstablishMap.get(false);
                    // 到达
                    List<ProjEstablishBaseInfo> projEstablishBaseInfoTrue = ProjEstablishMap.get(true);
                    // 获取未到达数据的客户ID
                    if (CollectionUtil.isNotEmpty(projEstablishInfoFalse)){
                        Set<Long> projEsClientIds = extractDistinctClientIds(projEstablishInfoFalse);

                        if (ObjectUtil.isNotEmpty(jobParam)){
                            log.info("传参数据: {}", jobParam);
                            // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                            List<String> paramClientIds = convertStringToList(jobParam);
                            boolean anyMatch = paramClientIds.stream()
                                    .map(this::safeParseLong)
                                    .filter(Objects::nonNull)
                                    .anyMatch(projEsClientIds::contains);
                            if (anyMatch){
                                log.info("true：传参数据在立项关联数据中");
                            }else {
                                log.info("false：传参数据不在立项关联数据中");
                            }
                        }

                        result.addAll(projEsClientIds);
                    }

                    return projEstablishBaseInfoTrue;
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取项目立项的客户数据异常:", e);
            return null;
        }
    }

    // 获取集团授信的客户ID
    private List<GroupCreditReviewBaseInfo> getGroupCreditClientIds(Set<Long> result,List<ProjReviewBaseInfo> allProjReviewList){
        try {
            // 2.1集团授信立项生效数据查询
            LambdaQueryWrapper<GroupCreditEstablishBaseInfo> groupCreditEstablishQuery = Wrappers.lambdaQuery();
            groupCreditEstablishQuery.eq(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, RecordStatus.TAKE_EFFECT.name());
            List<GroupCreditEstablishBaseInfo> groupTakeEffectList = groupCreditEstablishBaseInfoService.list(groupCreditEstablishQuery);
            // 2.2集团授信评审全量数据
            List<GroupCreditReviewBaseInfo> allgroupCreditReviewList = groupCreditReviewBaseInfoService.list();
            if (CollectionUtil.isNotEmpty(groupTakeEffectList)){
                // 2.3通过授信立项生效数据查询对应的授信评审数据
                List<Long> groupCreditIds = groupTakeEffectList.stream().map(GroupCreditEstablishBaseInfo::getId).collect(Collectors.toList());
                List<GroupCreditReviewBaseInfo> groupCreditReviewList = allgroupCreditReviewList.stream()
                        .filter(Objects::nonNull)
                        .filter(info -> groupCreditIds.contains(info.getGroupCreditEstablishId()))
                        .collect(Collectors.toList());
                if (CollectionUtil.isEmpty(groupCreditReviewList)){
                    // 为空说明全部没有走到授信评审阶段，取授信立项数据的客户ID
                    Set<Long> groupClientIds = groupTakeEffectList.stream().map(GroupCreditEstablishBaseInfo::getClientId).collect(Collectors.toSet());
                    result.addAll(groupClientIds);
                }else {// 不为空说明有授信立项数据走到授信评审阶段
                    // 2.4筛选 立项生效但是未走到评审阶段与走到走到授信评审阶段的数据
                    Set<Long> establishIds = allgroupCreditReviewList.stream()
                            .map(GroupCreditReviewBaseInfo::getGroupCreditEstablishId)
                            .filter(ObjectUtil::isNotEmpty)
                            .collect(Collectors.toSet());
                    Map<Boolean, List<GroupCreditEstablishBaseInfo>> GroupCreditEstablishMap = groupTakeEffectList.stream()
                            .collect(Collectors.partitioningBy(proj -> establishIds.contains(proj.getId())
                            ));
                    // 未到达
                    List<GroupCreditEstablishBaseInfo> projEstablishInfoFalse = GroupCreditEstablishMap.get(false);
                    // 到达
                    List<GroupCreditEstablishBaseInfo> projEstablishInfoTrue = GroupCreditEstablishMap.get(true);
                    // 2.5未到达评审取授信立项的客户ID
                    if (CollectionUtil.isNotEmpty(projEstablishInfoFalse)){
                        Set<Long> groupClientIds = projEstablishInfoFalse.stream().map(GroupCreditEstablishBaseInfo::getClientId).collect(Collectors.toSet());
                        result.addAll(groupClientIds);
                    }
                    // 2.6筛选 评审生效的数据与非生效状态的数据
                    Map<Boolean, List<GroupCreditReviewBaseInfo>> groupCreditReviewMap = groupCreditReviewList.stream().collect(Collectors.partitioningBy(
                            info -> ProjItemStatus.TAKE_EFFECT.name().equals(info.getGroupCreditReviewStatus())
                    ));
                    // 获取生效状态的数据
                    List<GroupCreditReviewBaseInfo> groupCreditReviewEffList = groupCreditReviewMap.get(true);
                    // 获取未生效状态的数据
                    List<GroupCreditReviewBaseInfo> groupCreditReviewFalseList = groupCreditReviewMap.get(false);
                    // 2.7 评审未生效的数据 取到达评审阶段的授信立项的数据客户ID
                    if (CollectionUtil.isNotEmpty(groupCreditReviewFalseList)){
                        List<Long> groupCreditEstablishIds = groupCreditReviewFalseList.stream().map(GroupCreditReviewBaseInfo::getGroupCreditEstablishId).collect(Collectors.toList());
                        List<GroupCreditEstablishBaseInfo> projEstablishTrueList = projEstablishInfoTrue.stream()
                                .filter(Objects::nonNull)
                                .filter(info -> groupCreditEstablishIds.contains(info.getId()))
                                .collect(Collectors.toList());
                        Set<Long> groupTrueClientIds = projEstablishTrueList.stream().map(GroupCreditEstablishBaseInfo::getClientId).collect(Collectors.toSet());
                        result.addAll(groupTrueClientIds);
                    }
                    // 2.8授信评审生效的数据再去查对应的项目评审数据
                    if (CollectionUtil.isNotEmpty(groupCreditReviewEffList)){
                        List<Long> ids = groupCreditReviewEffList.stream().map(GroupCreditReviewBaseInfo::getId).collect(Collectors.toList());
                        List<ProjReviewBaseInfo> projReviewByGroupCreditList = allProjReviewList.stream()
                                .filter(Objects::nonNull)
                                .filter(info -> info.getGroupCreditReviewId() != null && ids.contains(info.getGroupCreditReviewId()))
                                .collect(Collectors.toList());
                        if (CollectionUtil.isEmpty(projReviewByGroupCreditList)){
                            // 为空说明全部授信评审未到项目评审阶段，直接获取授信评审生效的客户ID
                            Set<Long> groupClientIds = groupCreditReviewEffList.stream().map(GroupCreditReviewBaseInfo::getClientId).collect(Collectors.toSet());
                            result.addAll(groupClientIds);
                        }else {// 不为空说明有授信评审走到项目评审阶段
                            // 筛选 授信评审生效到达与未到达项目评审阶段的数据
                            Set<Long> ReviewIds = allProjReviewList.stream()
                                    .map(ProjReviewBaseInfo::getGroupCreditReviewId)
                                    .filter(ObjectUtil::isNotEmpty)
                                    .collect(Collectors.toSet());
                            Map<Boolean, List<GroupCreditReviewBaseInfo>> GroupMap = groupCreditReviewEffList.stream()
                                    .collect(Collectors.partitioningBy(proj -> ReviewIds.contains(proj.getId())
                                    ));
                            // 到达
                            List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoTrue = GroupMap.get(true);
                            // 未到达
                            List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoFalse = GroupMap.get(false);
                            if (CollectionUtil.isNotEmpty(groupCreditReviewBaseInfoFalse)){
                                Set<Long> clientIds = groupCreditReviewBaseInfoFalse.stream()
                                        .map(GroupCreditReviewBaseInfo::getClientId).collect(Collectors.toSet());
                                result.addAll(clientIds);
                            }

                            return groupCreditReviewBaseInfoTrue;
                        }
                    }
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取集团授信的客户数据异常:", e);
            return null;
        }
    }

    // 获取项目评审客户ID
    private List<ProjReviewBaseInfo> getProjReviewClientIds(Set<Long> result,
                                                            List<ProjEstablishBaseInfo> projEstablishEffList,
                                                            List<GroupCreditReviewBaseInfo> groupCreditReviewEffList,
                                                            List<ProjReviewBaseInfo> allProjReviewList,
                                                            List<ContractBaseInfo> allContractList,
                                                            String jobParam){
        try {
            // 3.筛选 评审生效的数据与非生效状态的数据
            Map<Boolean, List<ProjReviewBaseInfo>> partitionedMap = allProjReviewList.stream()
                    .collect(Collectors.partitioningBy(
                            info -> ProjItemStatus.TAKE_EFFECT.name().equals(info.getProjReviewStatus())
                    ));
            // 获取生效状态的数据
            List<ProjReviewBaseInfo> projReviewEffList = partitionedMap.get(true);
            // 获取非生效状态的数据
            List<ProjReviewBaseInfo> projReviewNonEffList = partitionedMap.get(false);
            // 3.1根据非生效状态的项目评审数据 取项目立项的客户ID 与 授信立项的客户ID (例如到达项目评审后但是还未新建审批通过的数据)
            if (CollectionUtil.isNotEmpty(projReviewNonEffList)){
                // 项目立项的客户ID
                if (CollectionUtil.isNotEmpty(projEstablishEffList)){
                    List<String> NonEffProjCodes = projReviewNonEffList.stream().map(ProjReviewBaseInfo::getProjCode).collect(Collectors.toList());
                    List<ProjEstablishBaseInfo> NonEffProjEsClientIds = projEstablishEffList.stream()
                            .filter(Objects::nonNull)
                            .filter(info -> StringUtils.isNotEmpty(info.getProjCode()) && NonEffProjCodes.contains(info.getProjCode()))
                            .collect(Collectors.toList());
                    Set<Long> projEsClientIds = extractDistinctClientIds(NonEffProjEsClientIds);

                    if (ObjectUtil.isNotEmpty(jobParam)){
                        log.info("传参数据: {}", jobParam);
                        // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                        if (projEsClientIds.contains(safeParseLong(jobParam))){
                            log.info("true：传参数据在项目立项生效且项目评审非生效状态的数据中");
                        }else {
                            log.info("false：传参数据不在项目立项生效且项目评审非生效状态数据中");
                        }
                    }

                    result.addAll(projEsClientIds);
                }
                // 授信评审的客户ID
                if (CollectionUtil.isNotEmpty(groupCreditReviewEffList)){
                    List<Long> groupCreditReviewIds = projReviewNonEffList.stream().map(ProjReviewBaseInfo::getGroupCreditReviewId).collect(Collectors.toList());
                    List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoList = groupCreditReviewEffList.stream()
                            .filter(Objects::nonNull)
                            .filter(info -> groupCreditReviewIds.contains(info.getId()))
                            .collect(Collectors.toList());
                    List<Long> groupCreditClientIs = groupCreditReviewBaseInfoList.stream().map(GroupCreditReviewBaseInfo::getClientId).collect(Collectors.toList());

                    if (ObjectUtil.isNotEmpty(jobParam)){
                        log.info("传参数据: {}", jobParam);
                        // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                        if (groupCreditClientIs.contains(safeParseLong(jobParam))){
                            log.info("true：传参数据在授信评审生效且项目评审非生效状态的数据中");
                        }else {
                            log.info("false：传参数据不在授信评审生效且项目评审非生效状态数据中");
                        }
                    }
                    result.addAll(groupCreditClientIs);
                }

            }
            // 3.2评审生效的数据再去查对应的合同数据
            if (CollectionUtil.isNotEmpty(projReviewEffList)){
                // 筛选 评审生效未到合同阶段的数据
                Set<Long> projReviewIds = allContractList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
                Map<Boolean, List<ProjReviewBaseInfo>> projReviewEffMap = projReviewEffList.stream()
                        .collect(Collectors.partitioningBy(proj -> projReviewIds.contains(proj.getId())
                        ));
                // 到达
                List<ProjReviewBaseInfo> groupCreditReviewBaseInfoTrue = projReviewEffMap.get(true);
                // 未到达
                List<ProjReviewBaseInfo> groupCreditReviewBaseInfoFalse = projReviewEffMap.get(false);
                if (CollectionUtil.isNotEmpty(groupCreditReviewBaseInfoFalse)){
                    Set<Long> reviewClientIds = reviewDistinctClientIds(groupCreditReviewBaseInfoFalse);

                    if (ObjectUtil.isNotEmpty(jobParam)){
                        log.info("传参数据: {}", jobParam);
                        // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                        List<String> paramClientIds = convertStringToList(jobParam);
                        boolean anyMatch = paramClientIds.stream()
                                .map(this::safeParseLong)
                                .filter(Objects::nonNull)
                                .anyMatch(reviewClientIds::contains);
                        if (anyMatch){
                            log.info("true：传参数据在评审关联数据中");
                        }else {
                            log.info("false：传参数据不在评审关联数据中");
                        }
                    }

                    result.addAll(reviewClientIds);
                }
                return groupCreditReviewBaseInfoTrue;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取项目评审阶段的客户数据异常:", e);
            return null;
        }
    }

    // 获取合同阶段的客户ID
    private void getContractClientIds(Set<Long> result,
                                      List<ProjReviewBaseInfo> projReviewEffList,
                                      List<ContractBaseInfo> allContractList,
                                      String jobParam) {
        try {
            // 4.1筛选 合同生效与起租的数据与其他数据
            Map<Boolean, List<ContractBaseInfo>> contractMap = allContractList.stream()
                    .collect(Collectors.partitioningBy(
                            info -> Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name()).contains(info.getContractStatus())
                    ));
            // 获取生效与起租状态的数据
            List<ContractBaseInfo> contractEffList = contractMap.get(true);
            // 获取非生效与起租状态的数据
            List<ContractBaseInfo> contractNonEffList = contractMap.get(false);

            // 4.2根据非生效与起租状态的合同数据取项目评审的客户ID
            if (CollectionUtil.isNotEmpty(contractNonEffList)){
                List<Long> NonEffProjReviewIds = contractNonEffList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList());
                List<ProjReviewBaseInfo> EffProjReviewList = projReviewEffList.stream()
                        .filter(info -> NonEffProjReviewIds.contains(info.getId()))
                        .collect(Collectors.toList());
                Set<Long> reviewClientIds = reviewDistinctClientIds(EffProjReviewList);

                if (ObjectUtil.isNotEmpty(jobParam)){
                    log.info("传参数据: {}", jobParam);
                    // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                    List<String> paramClientIds = convertStringToList(jobParam);
                    boolean anyMatch = paramClientIds.stream()
                            .map(this::safeParseLong)
                            .filter(Objects::nonNull)
                            .anyMatch(reviewClientIds::contains);
                    if (anyMatch){
                        log.info("true：传参数据在项目评审并且合同状态为非生效与起租的数据中");
                    }else {
                        log.info("false：传参数据不在项目评审并且合同状态为非生效与起租的数据中");
                    }
                }

                result.addAll(reviewClientIds);
            }

            // 4.3生效与起租状态的数据取对应的客户ID
            if (CollectionUtil.isNotEmpty(contractEffList)){
                List<Long> contractIds = contractEffList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                // 获取合同 当前有效版本的关联客户数据
                Set<Long> clientIdSByContractIds = getClientIdSByContractLibs(contractIds);

                if (ObjectUtil.isNotEmpty(jobParam)){
                    log.info("传参数据: {}", jobParam);
                    // 传参不为空时，使用传参数据作为监控客户数据，用来测试获取监控客户数据的逻辑
                    List<String> paramClientIds = convertStringToList(jobParam);
                    boolean anyMatch = paramClientIds.stream()
                            .map(this::safeParseLong)
                            .filter(Objects::nonNull)
                            .anyMatch(clientIdSByContractIds::contains);
                    if (anyMatch){
                        log.info("true：传参数据在生效合同关联数据中");
                    }else {
                        log.info("false：传参数据不在生效合同关联数据中");
                    }
                }

                result.addAll(clientIdSByContractIds);
            }

        } catch (Exception e) {
            log.error("获取合同阶段的客户数据异常:", e);
        }
    }

    /**
     * 批量插入数据
     */
    private int insertBatch(List<ClientViewByRiskControl> clients, String batchNo) {
        // 1. 将 ClientViewByRiskControl 转换为 ClientVwSync
        List<ClientVwSync> clientVwSyncList = clients.stream()
                .map(c -> {
                    ClientVwSync clientVwSync = new ClientVwSync();
                    clientVwSync.setClientId(c.getClientId());
                    clientVwSync.setCertNumber(c.getCertNumber());
                    clientVwSync.setClientName(c.getClientName());
                    clientVwSync.setSyncBatch(batchNo);
                    return clientVwSync;
                })
                .collect(Collectors.toList());

        int result = 0;
        boolean saved = clientVwSyncService.saveBatch(clientVwSyncList);
        if (saved){
            result = clientVwSyncList.size();
            log.info("插入ClientVwSync表成功,插入数据{}条", result);
        }
        return result;
    }

    /**
     * 从立项数据中提取去重后的客户ID
     */
    private Set<Long> extractDistinctClientIds(List<ProjEstablishBaseInfo> projects) {
        Set<Long> clientIds = new HashSet<>();

        List<Long> projEstablishIds = projects.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
        // 查询立项 有效版本的数据
        LambdaQueryWrapper<ProjEstablishBaseInfoLib> EstablishQuery = Wrappers.lambdaQuery();
        EstablishQuery.in(ProjEstablishBaseInfoLib::getOriginId,projEstablishIds);
        EstablishQuery.eq(ProjEstablishBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        List<ProjEstablishBaseInfoLib> libList = projEstablishBaseInfoLibService.list(EstablishQuery);

        // 当originId相同时，保留id最大的记录
        List<ProjEstablishBaseInfoLib> deduplicateLibList = new ArrayList<>(libList.stream()
                .collect(Collectors.toMap(
                        ProjEstablishBaseInfoLib::getOriginId, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ProjEstablishBaseInfoLib::getId))
                ))
                .values());

        for (ProjEstablishBaseInfoLib project : deduplicateLibList) {
            // 解析lessee_info 承租人
            extractClientIdsFromJson(project.getLesseeInfo(), clientIds);
            // 解析guarantee_info 担保人
            extractClientIdsFromJson(project.getGuaranteeInfo(), clientIds);
            // 解析pledgor_info 质押人
            extractClientIdsFromJson(project.getPledgorInfo(), clientIds);
            // 解析mortgagor_info 抵押人
            extractClientIdsFromJson(project.getMortgagorInfo(), clientIds);
        }

        return clientIds;
    }

    /**
     * 从评审数据中提取去重后的客户ID
     */
    private Set<Long> reviewDistinctClientIds(List<ProjReviewBaseInfo> projects) {
        Set<Long> clientIds = new HashSet<>();
        // 查询评审 有效版本的数据
        List<Long> projReviewIds = projects.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ProjReviewBaseInfoLib> ReviewQuery = Wrappers.lambdaQuery();
        ReviewQuery.in(ProjReviewBaseInfoLib::getOriginId,projReviewIds);
        ReviewQuery.eq(ProjReviewBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        List<ProjReviewBaseInfoLib> LibList = projReviewBaseInfoLibService.list(ReviewQuery);

        // 当originId相同时，保留id最大的记录
        List<ProjReviewBaseInfoLib> deduplicateLibList = new ArrayList<>(LibList.stream()
                .collect(Collectors.toMap(
                        ProjReviewBaseInfoLib::getOriginId, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ProjReviewBaseInfoLib::getId))
                ))
                .values());

        for (ProjReviewBaseInfoLib project : deduplicateLibList) {
            // 解析lessee_info 承租人
            extractClientIdsFromJson(project.getLesseeInfo(), clientIds);
            // 解析guarantee_info 担保人
            extractClientIdsFromJson(project.getGuaranteeInfo(), clientIds);
            // 解析pledgor_info 质押人
            extractClientIdsFromJson(project.getPledgorInfo(), clientIds);
            // 解析mortgagor_info 抵押人
            extractClientIdsFromJson(project.getMortgagorInfo(), clientIds);
        }

        return clientIds;
    }

    /**
     * 从JSON字符串中提取clientId
     */
    private void extractClientIdsFromJson(String jsonStr, Set<Long> clientIds) {
        if (jsonStr == null || jsonStr.trim().isEmpty() || "null".equalsIgnoreCase(jsonStr)) {
            return;
        }

        try {
            // JSON解析，提取clientId
            // 这里使用字符串处理，因为JSON可能包含多个对象
            String[] parts = jsonStr.split("\"clientId\"\\s*:");
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                int commaIndex = part.indexOf(',');
                int bracketIndex = part.indexOf('}');
                int endIndex = Math.min(
                        commaIndex > 0 ? commaIndex : Integer.MAX_VALUE,
                        bracketIndex > 0 ? bracketIndex : Integer.MAX_VALUE
                );

                if (endIndex != Integer.MAX_VALUE) {
                    String idStr = part.substring(0, endIndex).trim();
                    try {
                        Long clientId = Long.parseLong(idStr);
                        clientIds.add(clientId);
                    } catch (NumberFormatException e) {
                        // 忽略格式错误的ID
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析JSON失败: {}", jsonStr, e);
        }
    }

    /**
     * 批量获取合同有效版本的承租人/联合承租人/担保人/抵质押人
     */
    private Set<Long> getClientIdSByContractLibs(List<Long> contractIds){
        Set<Long> clientIds = new HashSet<>();
        try {
            // 查找合同 最新有效版本数据
            LambdaQueryWrapper<ContractBaseInfoLib> libQuery = Wrappers.lambdaQuery();
            libQuery.in(ContractBaseInfoLib::getOriginId,contractIds);
            libQuery.eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
            List<ContractBaseInfoLib> libList = contractBaseInfoLibService.list(libQuery);
            // 当OriginId相同时，保留id最大的记录
            List<ContractBaseInfoLib> dedupLibList = new ArrayList<>(libList.stream()
                    .collect(Collectors.toMap(
                            ContractBaseInfoLib::getOriginId, Function.identity(),
                            BinaryOperator.maxBy(Comparator.comparing(ContractBaseInfoLib::getId))
                    ))
                    .values());
            // 构建Map: key 为 contractId (即originId),value 为 version
            Map<Long, String> contractIdToVersionMap = dedupLibList.stream()
                    .collect(Collectors.toMap(
                            ContractBaseInfoLib::getOriginId,
                            ContractBaseInfoLib::getVersion
                    ));

            // 承租人
            LambdaQueryWrapper<ContractTenantryLib> TenantryQuery = Wrappers.lambdaQuery();
            TenantryQuery.in(ContractTenantryLib::getContractId,contractIdToVersionMap.keySet());
            TenantryQuery.eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL);
            List<ContractTenantryLib> allTenantryList = contractTenantryLibService.list(TenantryQuery);
            // 根据map进行筛选
            List<ContractTenantryLib> contractTenantryList = allTenantryList.stream()
                    .filter(item -> {
                        String expectedVersion = contractIdToVersionMap.get(item.getContractId());
                        return expectedVersion != null && expectedVersion.equals(item.getVersion());
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(contractTenantryList)){
                List<Long> lesseeId = contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
                clientIds.addAll(lesseeId);
            }
            // 担保人
            LambdaQueryWrapper<ContractGuarantorLib> GuarantorQuery = Wrappers.lambdaQuery();
            GuarantorQuery.in(ContractGuarantor::getContractId, contractIdToVersionMap.keySet());
            GuarantorQuery.eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL);
            List<ContractGuarantorLib> allGuarantorList = contractGuarantorLibService.list(GuarantorQuery);
            // 根据map进行筛选
            List<ContractGuarantorLib> dedupGuarantorLibList = allGuarantorList.stream()
                    .filter(item -> {
                        String expectedVersion = contractIdToVersionMap.get(item.getContractId());
                        return expectedVersion != null && expectedVersion.equals(item.getVersion());
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(dedupGuarantorLibList)){
                for (ContractGuarantorLib contractGuarantor : dedupGuarantorLibList) {
                    if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
                        continue;
                    }
                    clientIds.addAll(JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class));
                }
            }
            // 抵押人
            LambdaQueryWrapper<ContractMortgageLib> MortgageQuery = Wrappers.lambdaQuery();
            MortgageQuery.in(ContractMortgageLib::getContractId,contractIdToVersionMap.keySet());
            MortgageQuery.eq(ContractMortgageLib::getVersionType, VersionTypeConstants.NORMAL);
            List<ContractMortgageLib> allMortgageList = contractMortgageLibService.list(MortgageQuery);
            // 根据map进行筛选
            List<ContractMortgageLib> dedupMortgageLibList = allMortgageList.stream()
                    .filter(item -> {
                        String expectedVersion = contractIdToVersionMap.get(item.getContractId());
                        return expectedVersion != null && expectedVersion.equals(item.getVersion());
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(dedupMortgageLibList)) {
                for (ContractMortgageLib contractMortgage : dedupMortgageLibList) {
                    if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
                        continue;
                    }
                    clientIds.addAll(JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class));
                }
            }
            // 质押人
            LambdaQueryWrapper<ContractPledgeLib> PledgeQuery = Wrappers.lambdaQuery();
            PledgeQuery.in(ContractPledgeLib::getContractId,contractIdToVersionMap.keySet());
            PledgeQuery.eq(ContractPledgeLib::getVersionType, VersionTypeConstants.NORMAL);
            List<ContractPledgeLib> allPledgeList = contractPledgeLibService.list(PledgeQuery);
            // 根据map进行筛选
            List<ContractPledgeLib> dedupPledgeLibList = allPledgeList.stream()
                    .filter(item -> {
                        String expectedVersion = contractIdToVersionMap.get(item.getContractId());
                        return expectedVersion != null && expectedVersion.equals(item.getVersion());
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(dedupPledgeLibList)) {
                for (ContractPledgeLib contractPledge : dedupPledgeLibList) {
                    if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
                        continue;
                    }
                    clientIds.addAll(JSONUtil.toList(contractPledge.getPledgeIds(), Long.class));
                }
            }
            log.info("获取合同阶段的客户数量: {}", clientIds.size());
            return clientIds;
        } catch (Exception e) {
            log.error("获取合同数据异常: {}", e.getMessage());
            return clientIds;
        }
    }

    private List<String> convertStringToList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Stream.of(input.split(","))
                .map(String::trim)  // 去除空格
                .filter(s -> !s.isEmpty())  // 过滤空字符串
                .collect(Collectors.toList());
    }

    private Long safeParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null; // 处理无效字符串
        }
    }
}
