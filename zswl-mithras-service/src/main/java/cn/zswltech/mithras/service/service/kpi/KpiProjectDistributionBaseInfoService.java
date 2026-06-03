package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.service.enums.projestablish.ProjSourceType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.kpi.KpiProjectDistributionBaseInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.kpi.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.kpi.handler.impl.KpiProjectDistributionBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionBaseInfoService extends ServiceImpl<KpiProjectDistributionBaseInfoMapper, KpiProjectDistributionBaseInfo> {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;
    @Resource
    private KpiProjectDistributionBaseInfoLibHandler kpiProjectDistributionBaseInfoLibHandler;
    @Resource
    private ProcessService processService;

    public KpiProjectDistributionBaseInfo getOneByProjectDistributionId(Long projectDistributionId) {
        LambdaQueryWrapper<KpiProjectDistributionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public KpiProjectDistributionBaseInfoRSP detail(KpiProjectDistributionBaseInfoREQ req) {
        KpiProjectDistribution projectDistribution = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        boolean isHistory = StrUtil.isNotBlank(req.getVersion());
        KpiProjectDistributionBaseInfo baseInfo;
        if (isHistory) {
            KpiProjectDistributionBaseInfoLib baseInfoLib = kpiProjectDistributionBaseInfoLibService.getSpecificByMainIdAndVersion(req.getProjectDistributionId(), req.getVersion());
            baseInfo = kpiProjectDistributionBaseInfoLibHandler.actualLib2Entity(baseInfoLib);
        } else {
            baseInfo = this.getOneByProjectDistributionId(req.getProjectDistributionId());
        }
        KpiProjectDistributionBaseInfoRSP rsp = new KpiProjectDistributionBaseInfoRSP();
        rsp.setId(baseInfo.getId());
        rsp.setProjectDistributionId(baseInfo.getProjectDistributionId());
        rsp.setContractCode(baseInfo.getContractCode());
        rsp.setProjName(baseInfo.getProjName());
        if (Objects.nonNull(baseInfo.getContractStartDate())) {
            rsp.setContractStartDate(LocalDateTimeUtil.format(baseInfo.getContractStartDate(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(baseInfo.getContractEndDate())) {
            rsp.setContractEndDate(LocalDateTimeUtil.format(baseInfo.getContractEndDate(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setBelongDeptId(baseInfo.getProfitBelongDeptId());
        if (Objects.nonNull(rsp.getBelongDeptId())) {
            rsp.setBelongDeptName(id2NameService.deptId2NameSingle(rsp.getBelongDeptId()));
        }
        rsp.setTeamLeaderId(baseInfo.getTeamLeaderId());
        if (Objects.nonNull(rsp.getTeamLeaderId())) {
            rsp.setTeamLeaderName(id2NameService.sysUserId2NameSingle(rsp.getTeamLeaderId()));
        }
        rsp.setRemark(baseInfo.getRemark());
        rsp.setSuppleDescribe(baseInfo.getSuppleDescribe());
        // 查询合同信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(projectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        rsp.setContractBelongDeptId(contractBaseInfo.getBizDeptId());
        // 动态字段区分是否历史信息
        if (isHistory) {
            rsp.setProjClassify(baseInfo.getProjClassify());
            rsp.setProjSource(baseInfo.getProjSource());
        } else {
            // 项目评审信息
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(contractBaseInfo.getProjReviewId());
            if (Objects.isNull(projReviewBaseInfo)) {
                throw new MithrasException("项目评审信息不存在");
            }
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
            if (Objects.nonNull(corpCommerceInfoLib)) {
                rsp.setProjClassify(this.ensureProjClassify(corpCommerceInfoLib.getRiskControlIndustryClassify()).name());
            }
            rsp.setProjSource(this.ensureProjSource(projReviewBaseInfo.getProjSource()).name());
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiProjectDistributionBaseInfoModifyREQ req) {
        KpiProjectDistribution projectDistribution = kpiProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        KpiProjectDistributionBaseInfo existBaseInfo = this.getOneByProjectDistributionId(req.getProjectDistributionId());
        if (Objects.isNull(existBaseInfo)) {
            throw new MithrasException("项目分配-基本信息不存在");
        }
        if (processService.isInProcess(projectDistribution.getId().toString(), BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.getModelKeyList())) {
            if (!Objects.equals(existBaseInfo.getTeamLeaderId(), req.getTeamLeaderId())) {
                throw new MithrasException("流程中不允许修改团队长，若要修改请关闭流程后进行修改");
            }
            if (!Objects.equals(existBaseInfo.getProfitBelongDeptId(), req.getProfitBelongDeptId())) {
                throw new MithrasException("流程中不允许修改利润归属部门，若要修改请关闭流程后进行修改");
            }
        } else {
            projectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            kpiProjectDistributionService.updateById(projectDistribution);
            existBaseInfo.setProfitBelongDeptId(req.getProfitBelongDeptId());
            existBaseInfo.setTeamLeaderId(req.getTeamLeaderId());
        }
        existBaseInfo.setRemark(req.getRemark());
        existBaseInfo.setSuppleDescribe(req.getSuppleDescribe());
        this.updateById(existBaseInfo);
    }

    public KpiProjectClassifyEnum ensureProjClassify(String clientRiskControlIndustryClassify) {
//        if (RiskControlIndustryClassify.ENGINEERING_MACHINERY.name().equals(clientRiskControlIndustryClassify)) {
//            return KpiProjectClassifyEnum.FACTORY;
//        } else
        if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(clientRiskControlIndustryClassify)) {
            return KpiProjectClassifyEnum.PUBLIC;
        } else if (RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(clientRiskControlIndustryClassify)) {
            return KpiProjectClassifyEnum.PUBLIC;
        } else if (RiskControlIndustryClassify.TRAVEL.name().equals(clientRiskControlIndustryClassify)) {
            return KpiProjectClassifyEnum.PUBLIC;
        } else {
            return KpiProjectClassifyEnum.INDUSTRY;
        }
    }

    public KpiProjectSourceDistributionEnum ensureProjSource(String projReviewSource) {
        if (ProjSourceType.clfd.name().equals(projReviewSource)) {
            return KpiProjectSourceDistributionEnum.HISTORY;
        } else {
            return KpiProjectSourceDistributionEnum.NEW;
        }
    }
}
