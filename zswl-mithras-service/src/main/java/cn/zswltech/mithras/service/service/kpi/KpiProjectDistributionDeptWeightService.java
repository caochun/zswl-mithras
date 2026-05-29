package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightSaveREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionPrevREQ;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.kpi.KpiProjectDistributionDeptWeightMapper;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/9 14:35
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionDeptWeightService extends ServiceImpl<KpiProjectDistributionDeptWeightMapper, KpiProjectDistributionDeptWeight> {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionDeptWeightService thisService;
    @Resource
    private KpiProjectDistributionDeptLaunchWeightService kipProjectDistributionDeptLaunchWeightService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private KpiProjectDistributionDeptWeightLibService kpiProjectDistributionDeptWeightLibService;

    public List<KpiProjectDistributionDeptWeight> listByProjectDistributionIds(Collection<Long> projectDistributionIds) {
        LambdaQueryWrapper<KpiProjectDistributionDeptWeight> query = Wrappers.lambdaQuery();
        query.in(KpiProjectDistributionDeptWeight::getProjectDistributionId, projectDistributionIds);
        return this.list(query);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void saveDept(KpiProjectDistributionDeptWeightSaveREQ req) {
        // 部门分润比-数据校验
        Pair<KpiProjectDistribution, List<KpiProjectDistributionDeptWeightInfo>> pair = thisService.deptSaveValid(req);
        // 部门投放分配比-数据校验
        Pair<KpiProjectDistribution, List<KpiProjectDistributionDeptLaunchWeightInfo>> launchPair = kipProjectDistributionDeptLaunchWeightService.deptLaunchSaveValid(req);
        KpiProjectDistribution projectDistribution = pair.getKey();
        List<KpiProjectDistributionDeptWeightInfo> weightInfoList = pair.getValue();

        // 保存或者更新数据, 还要考虑多余的数据需要删除
        // 需要将现在的草稿表的数据全部拿出来
        List<Long> list = weightInfoList.stream().map(KpiProjectDistributionDeptWeightInfo::getId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
        List<KpiProjectDistributionDeptWeight> existList = thisService.list(Wrappers.<KpiProjectDistributionDeptWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, projectDistribution.getId()));
        List<Long> needDeleteList = existList.stream().map(KpiProjectDistributionDeptWeight::getId)
                .filter(Objects::nonNull)
                .filter(id -> !list.contains(id)).collect(Collectors.toList());
        Map<Long, KpiProjectDistributionDeptWeight> existMap = new HashMap<>();
        List<KpiProjectDistributionDeptWeight> insertList = new ArrayList<>();
        List<KpiProjectDistributionDeptWeight> updatetList = new ArrayList<>();
        List<Long> ids = weightInfoList.stream().map(KpiProjectDistributionDeptWeightInfo::getId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)) {
            existMap = listByIds(ids).stream().collect(Collectors.toMap(KpiProjectDistributionDeptWeight::getId, Function.identity(), (a, b) -> a));
        }
        for (KpiProjectDistributionDeptWeightInfo weightInfo : weightInfoList) {
            if (Objects.isNull(weightInfo.getId())) {
                KpiProjectDistributionDeptWeight kpiProjectDistributionDeptWeight = new KpiProjectDistributionDeptWeight();
                kpiProjectDistributionDeptWeight.setProjectDistributionId(projectDistribution.getId());
                // weightType 字段暂时用不上，后续可扩展
                kpiProjectDistributionDeptWeight.setWeightTarget(weightInfo.getWeightTarget());
                kpiProjectDistributionDeptWeight.setWeightValue(weightInfo.getWeightValue());
                insertList.add(kpiProjectDistributionDeptWeight);
            } else {
                KpiProjectDistributionDeptWeight kpiProjectDistributionDeptWeight = existMap.get(weightInfo.getId());
                if (Objects.nonNull(kpiProjectDistributionDeptWeight)) {
                    kpiProjectDistributionDeptWeight.setWeightTarget(weightInfo.getWeightTarget());
                    kpiProjectDistributionDeptWeight.setWeightValue(weightInfo.getWeightValue());
                    updatetList.add(kpiProjectDistributionDeptWeight);
                }
            }
        }

        // 操作更新
        if (CollUtil.isNotEmpty(insertList)) {
            thisService.saveBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updatetList)) {
            thisService.updateBatchById(updatetList);
        }
        if (CollUtil.isNotEmpty(needDeleteList)) {
            thisService.removeByIds(needDeleteList);
        }
        // 部门投放分配比-数据保存
        kipProjectDistributionDeptLaunchWeightService.savaBatch(launchPair);
    }

    public Pair<KpiProjectDistribution, List<KpiProjectDistributionDeptWeightInfo>> deptSaveValid(KpiProjectDistributionDeptWeightSaveREQ req) {
        // 首先需要检查列表数据，如果列表不正确的话就没必要查数据库
        for (KpiProjectDistributionDeptWeightInfo weightInfo : req.getDeptWeightInfoList()) {
            if (Objects.isNull(weightInfo.getWeightValue()) && Objects.nonNull(weightInfo.getWeightTarget())) {
                throw new MithrasException("分配比重部门和分配比重必须同时有值或者无值");
            }
            if (Objects.nonNull(weightInfo.getWeightValue()) && Objects.isNull(weightInfo.getWeightTarget())) {
                throw new MithrasException("分配比重部门和分配比重必须同时有值或者无值");
            }
        }
        // 重新构建数据列表，过滤掉无效数据
        List<KpiProjectDistributionDeptWeightInfo> weightInfoList = req.getDeptWeightInfoList().stream()
                .filter(item -> Objects.nonNull(item.getWeightValue())).collect(Collectors.toList());
        if (CollUtil.isEmpty(weightInfoList)) {
            throw new MithrasException("分配比重有效信息不能为空");
        }
        boolean no = weightInfoList.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightValue)
                .filter(Objects::nonNull).anyMatch(e -> e <= 0);
        if (no) {
            throw new MithrasException("分配比重占比不能小于等于0");
        }
        long counted = weightInfoList.stream().collect(Collectors.groupingBy(KpiProjectDistributionDeptWeightInfo::getWeightTarget))
                .values().stream().map(List::size).filter(e -> e != 1).count();
        if (counted > 0) {
            throw new MithrasException("分配比重部门不能重复");
        }
        // 还需要校验总的分配比重是否为100%
        int sum = weightInfoList.stream().mapToInt(KpiProjectDistributionDeptWeightInfo::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("分配比重加总必须等于100%");
        }
        req.setDeptWeightInfoList(weightInfoList);
        KpiProjectDistribution instance = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(instance)) {
            throw new MithrasException("项目分配信息不存在");
        }
//        // 还需要校验对应合同主办对应的业务部门
//        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(instance.getContractId());
//        if (Objects.isNull(contractBaseInfo)) {
//            throw new MithrasException("合同信息不存在");
//        }
//        if (Objects.isNull(contractBaseInfo.getProjSponsorUserId())) {
//            throw new MithrasException("合同主办为空");
//        }
//        OrgDO deptByUserId = sysUserService.getBizDeptByUserId(contractBaseInfo.getProjSponsorUserId());
//        if (Objects.isNull(deptByUserId)) {
//            throw new MithrasException("合同主办对应的业务部门为空");
//        }
//        long count = weightInfoList.stream().filter(item -> Objects.nonNull(item.getWeightTarget()))
//                .filter(item -> !Objects.equals(item.getWeightTarget(), deptByUserId.getId())).count();
//        if (count == 0) {
//            throw new MithrasException("分配比重部门必须包含合同主办对应的业务部门");
//        }
        return Pair.of(instance, weightInfoList);
    }


    public List<KpiProjectDistributionDeptWeightInfo> queryList(Long projectDistributionId) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionService.getById(projectDistributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        List<KpiProjectDistributionDeptWeight> list = thisService.list(Wrappers.<KpiProjectDistributionDeptWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, distributionServiceById.getId()));

        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 组装数据
        Map<Long, String> map = id2NameService.deptId2Name(list.stream().map(KpiProjectDistributionDeptWeight::getWeightTarget).collect(Collectors.toSet()));
        List<KpiProjectDistributionDeptWeightInfo> collect = list.stream().map(item -> {
            KpiProjectDistributionDeptWeightInfo info = new KpiProjectDistributionDeptWeightInfo();
            info.setId(item.getId());
            info.setIsBusinessDept(false);
            info.setWeightValue(item.getWeightValue());
            info.setWeightTarget(item.getWeightTarget());
            info.setWeightTargetName(map.get(item.getWeightTarget()));
            return info;
        }).collect(Collectors.toList());
        collect.get(0).setIsBusinessDept(true);
        return collect;
    }

    public List<KpiProjectDistributionDeptWeightInfo> prev(KpiProjectDistributionPrevREQ req) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        // 找到上个版本
        KpiProjectDistributionDeptWeightLib selected;
        if (CharSequenceUtil.isNotBlank(req.getVersion())) {
            selected = kpiProjectDistributionDeptWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, distributionServiceById.getId())
                    .eq(KpiProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .lt(KpiProjectDistributionDeptWeightLib::getVersion, req.getVersion())
                    .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
        } else {
            selected = kpiProjectDistributionDeptWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, distributionServiceById.getId())
                    .eq(KpiProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
        }
        if (Objects.isNull(selected)) {
            return Collections.emptyList();
        }
        return kpiProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(req.getProjectDistributionId(), selected.getVersion());
    }
}
