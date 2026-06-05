package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.kpi.convert.KpiProjectDistributionConvert;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionWeightMapper;
import cn.zswltech.mithras.kpi.mapper.query.KpiProjectDistributionQuery;
import cn.zswltech.mithras.kpi.mapper.model.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.kpi.service.lib.handler.impl.KpiProjectDistributionBaseInfoLibHandler;
import cn.zswltech.mithras.kpi.service.lib.handler.impl.KpiProjectDistributionWeightLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionWeightService extends ServiceImpl<KpiProjectDistributionWeightMapper, KpiProjectDistributionWeight> {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;
    @Resource
    private KpiProjectDistributionBaseInfoLibHandler kpiProjectDistributionBaseInfoLibHandler;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;
    @Resource
    private KpiProjectDistributionWeightLibHandler kpiProjectDistributionWeightLibHandler;
    @Resource
    private KpiProjectDistributionDeptWeightService kpiProjectDistributionDeptWeightService;
    @Resource
    private KpiProjectDistributionDeptWeightLibService kpiProjectDistributionDeptWeightLibService;
    @Resource
    private KpiProjectDistributionDeptLaunchWeightService kpiProjectDistributionDeptLaunchWeightService;
    @Resource
    private KpiProjectDistributionDeptLaunchWeightLibService kpiProjectDistributionDeptWeightLaunchLibService;

    public List<KpiProjectDistributionWeight> listByProjectDistributionId(Long projectDistributionId) {
        LambdaQueryWrapper<KpiProjectDistributionWeight> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionWeight::getProjectDistributionId, projectDistributionId);
        return this.list(query);
    }

    public List<KpiProjectDistributionWeight> listByProjectDistributionIds(Collection<Long> projectDistributionIds) {
        LambdaQueryWrapper<KpiProjectDistributionWeight> query = Wrappers.lambdaQuery();
        query.in(KpiProjectDistributionWeight::getProjectDistributionId, projectDistributionIds);
        return this.list(query);
    }

    public KpiProjectDistributionWeightRSP detail(KpiProjectDistributionWeightREQ req) {
        boolean isHistory = StrUtil.isNotBlank(req.getVersion());
        KpiProjectDistribution projectDistribution = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        KpiProjectDistributionBaseInfo baseInfo;
        List<KpiProjectDistributionWeight> dbList;
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoList;
        List<KpiProjectDistributionDeptLaunchWeightInfo> deptLaunchWeightInfoList;
        if (isHistory) {
            KpiProjectDistributionBaseInfoLib baseInfoLib = kpiProjectDistributionBaseInfoLibService.getSpecificByMainIdAndVersion(req.getProjectDistributionId(), req.getVersion());
            baseInfo = kpiProjectDistributionBaseInfoLibHandler.actualLib2Entity(baseInfoLib);
            List<KpiProjectDistributionWeightLib> weightLibList = kpiProjectDistributionWeightLibService.listByMainIdAndVersion(req.getProjectDistributionId(), req.getVersion());
            if (CollectionUtil.isEmpty(weightLibList)) {
                dbList = Collections.emptyList();
            } else {
                dbList = weightLibList.stream().map(kpiProjectDistributionWeightLibHandler::actualLib2Entity).collect(Collectors.toList());
            }

            // 构建部门的比重信息
            deptWeightInfoList = kpiProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(projectDistribution.getId(), req.getVersion());
            //项目投放分配比
            deptLaunchWeightInfoList = kpiProjectDistributionDeptWeightLaunchLibService.buildDeptLaunchWeightInfoByDistributionId(projectDistribution.getId(), req.getVersion());
        } else {
            baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(req.getProjectDistributionId());
            dbList = this.listByProjectDistributionId(req.getProjectDistributionId());

            deptWeightInfoList = kpiProjectDistributionDeptWeightService.queryList(projectDistribution.getId());
            //项目投放分配比
            deptLaunchWeightInfoList = kpiProjectDistributionDeptLaunchWeightService.queryList(projectDistribution.getId());
        }
        KpiProjectDistributionWeightRSP rsp = new KpiProjectDistributionWeightRSP();
        rsp.setProjectDistributionId(baseInfo.getProjectDistributionId());
        rsp.setEffectYear(baseInfo.getEffectYear());
        rsp.setEffectMonth(baseInfo.getEffectMonth());
        if (CollectionUtil.isEmpty(dbList)) {
            rsp.setWeightInfoList(Collections.emptyList());
            return rsp;
        }
        Set<Long> deptIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (KpiProjectDistributionWeight weight : dbList) {
            if (StrUtil.isBlank(weight.getWeightTarget())) {
                continue;
            }
            if (Objects.equals(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name(), weight.getWeightType())) {
                deptIds.add(Long.valueOf(weight.getWeightTarget()));
            } else {
                userIds.add(Long.valueOf(weight.getWeightTarget()));
            }
        }
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIds);
        List<KpiProjectDistributionWeightInfo> list = new ArrayList<>(dbList.size());
        for (KpiProjectDistributionWeight weight : dbList) {
            list.add(KpiProjectDistributionConvert.toKpiProjectDistributionWeightInfo(weight, userNameMap, deptNameMap));
        }
        rsp.setWeightInfoList(list);
        rsp.setDeptWeightInfoList(deptWeightInfoList);
        rsp.setDeptLaunchWeightInfoList(deptLaunchWeightInfoList);
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void save(KpiProjectDistributionWeightSaveREQ req) {
        if (CollectionUtil.isEmpty(req.getWeightInfoList())) {
            throw new MithrasException("分配比重不能为空");
        }
        // 单条数据校验
        for (KpiProjectDistributionWeightInfo weightInfo : req.getWeightInfoList()) {
            if (Objects.nonNull(weightInfo.getWeightTarget()) && Objects.nonNull(weightInfo.getWeightValue())) {
                continue;
            }
            if (Objects.isNull(weightInfo.getWeightTarget()) && Objects.isNull(weightInfo.getWeightValue())) {
                continue;
            }
            throw new MithrasException("分配比重部门/人员和分配比重必须同时有值或者无值");
        }
        // 比重和必须等于100%
        int sum = req.getWeightInfoList().stream().filter(item -> Objects.nonNull(item.getWeightValue())).mapToInt(KpiProjectDistributionWeightInfo::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("分配比重加总必须等于100%");
        }
        KpiProjectDistribution projectDistribution = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        if (!Objects.equals(projectDistribution.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
            // 非"审批中"的需要变更审批状态
            projectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            kpiProjectDistributionService.updateById(projectDistribution);
        }
        KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(req.getProjectDistributionId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("项目分配-基本信息不存在");
        }
        baseInfo.setEffectYear(req.getYear());
        baseInfo.setEffectMonth(req.getMonth());
        kpiProjectDistributionBaseInfoService.updateById(baseInfo);
        // 处理分配比重信息
        List<KpiProjectDistributionWeight> existWeightList = this.listByProjectDistributionId(req.getProjectDistributionId());
        // 流程中需要增加校验，不允许修改部门/人员
        if (Objects.equals(projectDistribution.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name()) && CollectionUtil.isNotEmpty(existWeightList)) {
            List<String> list1 = req.getWeightInfoList().stream().map(item -> item.getWeightType() + "-" + item.getWeightTarget()).sorted(Comparator.comparing(e -> e)).collect(Collectors.toList());
            List<String> list2 = existWeightList.stream().map(item -> item.getWeightType() + "-" + item.getWeightTarget()).sorted(Comparator.comparing(e -> e)).collect(Collectors.toList());
            if (list1.size() != list2.size()) {
                throw new MithrasException("审批流程中不允许变动部门/人员");
            }
            for (int i = 0; i < list1.size(); i++) {
                String s1 = list1.get(i);
                String s2 = list2.get(i);
                if (!Objects.equals(s1, s2)) {
                    throw new MithrasException("审批流程中不允许变动部门/人员");
                }
            }
        }
        Map<Long, KpiProjectDistributionWeight> existWeightMap = existWeightList.stream().collect(Collectors.toMap(KpiProjectDistributionWeight::getId, e -> e));
        List<KpiProjectDistributionWeight> toInsertList = new LinkedList<>();
        List<KpiProjectDistributionWeight> toUpdateList = new LinkedList<>();
        List<Long> toDeleteList = new LinkedList<>();
        for (KpiProjectDistributionWeightInfo reqInfo : req.getWeightInfoList()) {
            KpiProjectDistributionWeight dbModel = KpiProjectDistributionConvert.toKpiProjectDistributionWeight(reqInfo);
            if (Objects.nonNull(reqInfo.getId())) {
                // 更新
                KpiProjectDistributionWeight exist = existWeightMap.get(reqInfo.getId());
                if (Objects.nonNull(exist)) {
                    dbModel.setProjectDistributionId(exist.getProjectDistributionId());
                    toUpdateList.add(dbModel);
                    existWeightMap.remove(reqInfo.getId());
                }
            } else {
                // 新增
                dbModel.setProjectDistributionId(req.getProjectDistributionId());
                toInsertList.add(dbModel);
            }
        }
        if (CollectionUtil.isNotEmpty(existWeightMap)) {
            // 此时map里还有值说明是需要删除的
            toDeleteList.addAll(existWeightMap.keySet());
        }
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            this.saveBatch(toInsertList);
        }
        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            this.updateBatchById(toUpdateList);
        }
        if (CollectionUtil.isNotEmpty(toDeleteList)) {
            this.removeByIds(toDeleteList);
        }
    }

    public List<Long> listProjectDistributionIdsByCondition(KpiProjectDistributionQuery dbQuery) {
        return this.getBaseMapper().listProjectDistributionIdsByCondition(dbQuery);
    }
}
