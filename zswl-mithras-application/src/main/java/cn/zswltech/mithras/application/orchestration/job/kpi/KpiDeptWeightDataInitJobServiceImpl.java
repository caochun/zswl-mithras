package cn.zswltech.mithras.application.orchestration.job.kpi;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.job.service.KpiDeptWeightDataInitJobService;
import cn.zswltech.mithras.kpi.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.kpi.application.distribution.lib.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightLibService;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/16 10:04
 * @description
 */
@Slf4j
@Component
public class KpiDeptWeightDataInitJobServiceImpl implements KpiDeptWeightDataInitJobService {

    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionDeptWeightService service;
    @Resource
    private KpiProjectDistributionDeptWeightLibService libService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void initDeptWeightData() {
        log.info("kpiDeptWeightDataInitJob start ......");
        List<KpiProjectDistribution> distributions = kpiProjectDistributionService.list();
        Map<Integer, List<KpiProjectDistribution>> listMap = distributions.stream().collect(Collectors.groupingBy(KpiProjectDistribution::getDistributionStatus));

        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoMapper.selectBatchIds(distributions.stream().map(KpiProjectDistribution::getContractId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        // 1. 获取所有已提交的项目分配信息
        List<KpiProjectDistribution> kpiProjectDistributions = listMap.get(YesOrNoNumberEnum.YES.getCode());
        if (!CollectionUtils.isEmpty(kpiProjectDistributions)) {
            for (KpiProjectDistribution distribution : kpiProjectDistributions) {
                try {
                    // 拿到合同主办对应的业务部门
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(distribution.getContractId());
                    if (Objects.isNull(contractBaseInfo)) {
                        log.error("项目分配对应合同为空");
                        continue;
                    }
                    OrgDO deptByUserId = sysUserService.getBizDeptByUserId(contractBaseInfo.getProjSponsorUserId());
                    if (Objects.isNull(deptByUserId)) {
                        log.error("合同主办对应的业务部门为空");
                        continue;
                    }
                    String contractCode = contractBaseInfo.getContractCode();
                    // 浙商租【2024】租字第（A-0019）号
                    // 浙商租【2024】租字第(A-0019)号
                    List<KpiProjectDistributionDeptWeight> deptWeightList = new ArrayList<>();
                    if ("浙商租【2024】租字第(A-0019)号".equals(contractCode)) {
                        KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                        deptWeight.setProjectDistributionId(distribution.getId());
                        deptWeight.setWeightTarget(6L);
                        deptWeight.setWeightValue(500000);
                        service.save(deptWeight);
                        deptWeightList.add(deptWeight);

                        KpiProjectDistributionDeptWeight deptWeight1 = new KpiProjectDistributionDeptWeight();
                        deptWeight1.setProjectDistributionId(distribution.getId());
                        deptWeight1.setWeightTarget(9L);
                        deptWeight1.setWeightValue(500000);
                        service.save(deptWeight1);
                        deptWeightList.add(deptWeight1);
                    } else
                        //浙商租【2024】租字第（A-0209）号
                        if ("浙商租【2024】租字第(A-0209)号".equals(contractCode)) {
                            KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                            deptWeight.setProjectDistributionId(distribution.getId());
                            deptWeight.setWeightTarget(27L);
                            deptWeight.setWeightValue(500000);
                            service.save(deptWeight);
                            deptWeightList.add(deptWeight);

                            KpiProjectDistributionDeptWeight deptWeight1 = new KpiProjectDistributionDeptWeight();
                            deptWeight1.setProjectDistributionId(distribution.getId());
                            deptWeight1.setWeightTarget(9L);
                            deptWeight1.setWeightValue(500000);
                            service.save(deptWeight1);
                            deptWeightList.add(deptWeight1);
                        } else
                            //浙商租【2025】租字第（A-0003）号
                            if ("浙商租【2025】租字第(A-0003)号".equals(contractCode)) {
                                KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                                deptWeight.setProjectDistributionId(distribution.getId());
                                deptWeight.setWeightTarget(27L);
                                deptWeight.setWeightValue(500000);
                                service.save(deptWeight);
                                deptWeightList.add(deptWeight);

                                KpiProjectDistributionDeptWeight deptWeight1 = new KpiProjectDistributionDeptWeight();
                                deptWeight1.setProjectDistributionId(distribution.getId());
                                deptWeight1.setWeightTarget(9L);
                                deptWeight1.setWeightValue(500000);
                                service.save(deptWeight1);
                                deptWeightList.add(deptWeight1);
                            } else if ("浙商租【2021】租字第(A-0006)号".equals(contractCode)) {
                                KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                                deptWeight.setProjectDistributionId(distribution.getId());
                                deptWeight.setWeightTarget(11L);
                                deptWeight.setWeightValue(1000000);
                                service.save(deptWeight);
                                deptWeightList.add(deptWeight);
                            }else if ("浙商租【2021】租字第(A-0043)号".equals(contractCode)) {
                                KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                                deptWeight.setProjectDistributionId(distribution.getId());
                                deptWeight.setWeightTarget(6L);
                                deptWeight.setWeightValue(1000000);
                                service.save(deptWeight);
                                deptWeightList.add(deptWeight);
                            }else if ("浙商租【2021】租字第(A-0044)号".equals(contractCode)) {
                                KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                                deptWeight.setProjectDistributionId(distribution.getId());
                                deptWeight.setWeightTarget(6L);
                                deptWeight.setWeightValue(1000000);
                                service.save(deptWeight);
                                deptWeightList.add(deptWeight);
                            } else {
                                KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                                deptWeight.setProjectDistributionId(distribution.getId());
                                deptWeight.setWeightTarget(deptByUserId.getId());
                                deptWeight.setWeightValue(1000000);
                                service.save(deptWeight);
                                deptWeightList.add(deptWeight);
                            }

                    for (KpiProjectDistributionDeptWeight deptWeight : deptWeightList) {
                        // 处理版本表
                        List<KpiProjectDistributionBaseInfoLib> kpiProjectDistributionBaseInfoLibs = kpiProjectDistributionBaseInfoLibService.list(Wrappers.<KpiProjectDistributionBaseInfoLib>lambdaQuery()
                                .in(KpiProjectDistributionBaseInfoLib::getContractCode, contractCode)
                                .eq(KpiProjectDistributionBaseInfoLib::getProjectDistributionId, distribution.getId()));
                        if (!CollectionUtils.isEmpty(kpiProjectDistributionBaseInfoLibs)) {
                            for (KpiProjectDistributionBaseInfoLib kpiProjectDistributionBaseInfoLib : kpiProjectDistributionBaseInfoLibs) {
                                KpiProjectDistributionDeptWeightLib deptWeightLib = new KpiProjectDistributionDeptWeightLib();
                                deptWeightLib.setProjectDistributionId(kpiProjectDistributionBaseInfoLib.getProjectDistributionId());
                                deptWeightLib.setWeightTarget(deptWeight.getWeightTarget());
                                deptWeightLib.setWeightValue(deptWeight.getWeightValue());
                                deptWeightLib.setVersionType(kpiProjectDistributionBaseInfoLib.getVersionType());
                                deptWeightLib.setVersion(kpiProjectDistributionBaseInfoLib.getVersion());
                                deptWeightLib.setOriginId(deptWeight.getId());
                                deptWeightLib.setDataCreateTime(deptWeight.getCreateTime());
                                deptWeightLib.setDataCreateBy(deptWeight.getCreateBy());
                                deptWeightLib.setDataUpdateTime(deptWeight.getUpdateTime());
                                deptWeightLib.setDataUpdateBy(deptWeight.getUpdateBy());
                                deptWeightLib.setCreateTime(deptWeight.getCreateTime());
                                deptWeightLib.setCreateBy(deptWeight.getCreateBy());
                                deptWeightLib.setUpdateTime(deptWeight.getUpdateTime());
                                deptWeightLib.setUpdateBy(deptWeight.getUpdateBy());
                                libService.save(deptWeightLib);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("kpiDeptWeightDataInitJob error", e);
                }
            }
        }

        // 2、处理所有的未分配信息
        List<KpiProjectDistribution> projectDistributionList = listMap.get(YesOrNoNumberEnum.NO.getCode());
        if (!CollectionUtils.isEmpty(projectDistributionList)) {
            for (KpiProjectDistribution projectDistribution : projectDistributionList) {
                try {
                    // 拿到合同主办对应的业务部门
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(projectDistribution.getContractId());
                    if (Objects.isNull(contractBaseInfo)) {
                        log.error("项目分配对应合同为空");
                        continue;
                    }
                    OrgDO deptByUserId = sysUserService.getBizDeptByUserId(contractBaseInfo.getProjSponsorUserId());
                    if (Objects.isNull(deptByUserId)) {
                        log.error("合同主办对应的业务部门为空");
                        continue;
                    }
                    KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
                    deptWeight.setProjectDistributionId(projectDistribution.getId());
                    deptWeight.setWeightTarget(deptByUserId.getId());
                    deptWeight.setWeightValue(1000000);
                    service.save(deptWeight);
                } catch (Exception e) {
                    log.error("kpiDeptWeightDataInitJob error", e);
                }
            }
        }

        log.info("kpiDeptWeightDataInitJob end ......");
    }
}
