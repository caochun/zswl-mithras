package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.afterlease.AfterLeaseCheckReportConvert;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckReportBaseMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseClientDataBO;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseListExpandBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportBaseLibService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportMetaLibService;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportBaseLibHandler;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportMetaLibHandler;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.aspose.slides.Collections.ArrayList;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@Service
public class AfterLeaseCheckReportBaseServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportBaseMapper, NewAfterLeaseCheckReportBase> implements AfterLeaseCheckReportBaseService {
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckReportMetaLibService checkProjectReportMetaLibService;
    @Resource
    private AfterLeaseCheckReportMetaLibHandler checkProjectReportMetaLibHandler;
    @Resource
    private AfterLeaseCheckReportBaseLibService checkProjectReportBaseLibService;
    @Resource
    private AfterLeaseCheckReportBaseLibHandler checkProjectReportBaseLibHandler;
    @Resource
    private ClientService clientService;

    @Override
    public NewAfterLeaseCheckReportBase getReportBase(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportBase> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportBase::getCheckPlanClientId, checkPlanClientId);
        return this.getOne(query);
    }

    @Override
    public AfterLeaseCheckReportBaseRSP getCheckReportBaseRSP(Long checkPlanClientId, String version) {
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(checkPlanClientId);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划中没有找到客户记录"));
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(checkPlanClient.getPlanId());
        Assert.notNull(planBase, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        // 查询上一次的检查报告
        NewAfterLeaseCheckPlanClient leaseCheckPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getClientId, checkPlanClient.getClientId())
                .eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .le(NewAfterLeaseCheckPlanClient::getId, checkPlanClient.getId())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getId)
                .last(StringUtil.mysqlLimitOne()));
        NewAfterLeaseCheckReportMeta checkProjectReportMeta;
        NewAfterLeaseCheckReportBase reportBase;
        Map<Long, Long> clientRemainingPrincipalMap = clientService.getClientRemainingPrincipalMap(Collections.singletonList(checkPlanClient.getClientId()));
        Map<Long, Long> clientStockRiskExposureMap = clientService.clientStockRiskExposureMap(Collections.singletonList(checkPlanClient.getClientId()));
        if (StrUtil.isBlank(version)) {
            checkProjectReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClientId);
            Assert.notNull(checkProjectReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
            reportBase = this.getReportBase(checkPlanClientId);
        } else {
            NewAfterLeaseCheckReportMetaLib checkProjectReportMetaLib = checkProjectReportMetaLibService.getByCheckProjectIdAndVersion(checkPlanClientId, version);
            Assert.notNull(checkProjectReportMetaLib, () -> MithrasException.newException("指定数据版本的检查报告元数据不存在"));
            checkProjectReportMeta = checkProjectReportMetaLibHandler.actualLib2Entity(checkProjectReportMetaLib);
            NewAfterLeaseCheckReportBaseLib checkProjectReportBaseLib = checkProjectReportBaseLibService.getByCheckProjectIdAndVersion(checkPlanClientId, version);
            Assert.notNull(checkProjectReportBaseLib, () -> MithrasException.newException("指定数据版本的检查报告基本信息不存在"));
            reportBase = checkProjectReportBaseLibHandler.actualLib2Entity(checkProjectReportBaseLib);
        }
        // 判断当前检查客户状态，如果不是审批中或者审批通过则部门业务数据查询最新数据
        //补充：租后一般检查计划时，部门业务数据查询最新数据
        if (!Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())
                && !Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())
                || AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(planBase.getPlanType())) {
            AfterLeaseClientDataBO afterLeaseClientDataBO = this.getAfterLeaseClientDataBO(checkPlanClient.getClientId(), !Objects.equals(checkProjectReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.PUBLIC.name()));
            if (Objects.isNull(reportBase)) {
                reportBase = new NewAfterLeaseCheckReportBase();
            }
            // 使用最新数据覆盖对应字段
            reportBase.setContractAmount(afterLeaseClientDataBO.getContractTotalAmount());
            reportBase.setRiskExposure(afterLeaseClientDataBO.getRiskExposure());
            if (Objects.nonNull(leaseCheckPlanClient)) {
                if (Objects.nonNull(leaseCheckPlanClient.getNextDeadline())) {
                    reportBase.setDeadline(leaseCheckPlanClient.getNextDeadline());
                }
                if (CharSequenceUtil.isNotBlank(leaseCheckPlanClient.getNextCheckWay())) {
                    reportBase.setCheckWay(leaseCheckPlanClient.getNextCheckWay());
                }
            }
            //修改后不在是合同到期日
            reportBase.setNextRepayDate(afterLeaseClientDataBO.getNextRepayDate());
            reportBase.setNextRepayAmount(afterLeaseClientDataBO.getNextRepayAmount());
            reportBase.setClientId(afterLeaseClientDataBO.getClientId());
            reportBase.setClientName(afterLeaseClientDataBO.getClientName());
            reportBase.setIndustry(afterLeaseClientDataBO.getIndustry());
            if (CharSequenceUtil.isNotBlank(checkPlanClient.getCheckWay())) {
                reportBase.setCheckWay(checkPlanClient.getCheckWay());
            }
            // id转name处理
            List<Long> userIds = new LinkedList<>();
            if (Objects.nonNull(checkPlanClient.getBelongSponsorId())) {
                reportBase.setSponsorUserId(checkPlanClient.getBelongSponsorId());
                userIds.add(checkPlanClient.getBelongSponsorId());
            }
            if (Objects.nonNull(checkPlanClient.getRiskManagerId())) {
                reportBase.setRiskManagerId(checkPlanClient.getRiskManagerId());
                userIds.add(checkPlanClient.getRiskManagerId());
            }
            Map<Long, String> userMap = id2NameService.sysUserId2Name(userIds);
            reportBase.setSponsorUserName(userMap.get(reportBase.getSponsorUserId()));
            if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(planBase.getPlanType())) {
                reportBase.setRiskManagerName(userMap.get(checkPlanClient.getRiskManagerId()));
            } else {
                reportBase.setRiskManagerName(userMap.get(reportBase.getRiskManagerId()));
            }
            reportBase.setRiskManagerName(userMap.get(reportBase.getRiskManagerId()));
            reportBase.setBizDeptId(checkPlanClient.getBelongDeptId());
            reportBase.setBizDeptName(id2NameService.deptId2NameSingle(checkPlanClient.getBelongDeptId()));
        }
        // 模型转换
        AfterLeaseCheckReportBaseRSP rsp = AfterLeaseCheckReportConvert.toAfterLeaseCheckReportBaseRSP(reportBase);
        rsp.setCheckTime(checkPlanClient.getCheckTime());
        rsp.setCheckFillTime(checkPlanClient.getCheckFillTime());
        rsp.setRemainingPrincipal(Optional.ofNullable(checkPlanClient.getRemainingPrincipal()).orElse(0L));
        rsp.setRiskExposure(Optional.ofNullable(clientStockRiskExposureMap.get(checkPlanClient.getClientId())).orElse(0L));
        rsp.setPlanName(planBase.getPlanName());
        if (CharSequenceUtil.isNotBlank(planBase.getCheckWay())) {
            rsp.setCheckWay(planBase.getCheckWay());
        }
        //补充合同信息
        List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, checkPlanClient.getClientId())
                .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())));
        if (CollectionUtil.isNotEmpty(list)) {
            //查询租金表信息
            Map<Long, List<ContractRentActual>> contractRentMap = contractRentActualService.list(Wrappers.<ContractRentActual>lambdaQuery()
                    .in(ContractRentActual::getContractId, list.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()))).stream().collect(Collectors.groupingBy(ContractRentActual::getContractId));
            List<AfterLeaseCheckReportBaseRSP.CheckReportContract> contractList = new ArrayList(list.size());

            //Map<Long, ContractLeasePrice> longContractLeasePriceMap = contractPriceService.listByContractIds(list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
            list.forEach(e -> {
                AfterLeaseCheckReportBaseRSP.CheckReportContract contract = rsp.new CheckReportContract();
                contract.setId(e.getId());
                contract.setContractCode(e.getContractCode());
                contract.setApplyCreditAmount(e.getApplyCreditAmount());
                //contract.setLeaseMonthCount(Optional.ofNullable(longContractLeasePriceMap.get(e.getId())).map(ContractLeasePrice::getLeaseMonthCount).orElse(null));
                List<ContractRentActual> contractRentActuals = contractRentMap.get(e.getId());
                if (ObjectUtil.isNotEmpty(contractRentActuals)) {
                    contractRentActuals.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
                    contract.setStartDate(contractRentActuals.get(0).getCashFlowDate());
                    contract.setEndDate(contractRentActuals.get(contractRentActuals.size() - 1).getCashFlowDate());
                }
                contractList.add(contract);
            });
            rsp.setContractList(contractList);
        }
        //补充风险敞口
        if (ObjectUtil.isEmpty(rsp.getRiskExposure())) {
            rsp.setRiskExposure(contractBaseInfoService.getStockRiskExposure(checkPlanClient.getClientId(), null, null));
        }
        if (Objects.nonNull(planBase.getDeadLine())) {
            rsp.setDeadline(LocalDateTimeUtil.format(planBase.getDeadLine(), DatePattern.NORM_DATE_PATTERN));
        }
        if (CharSequenceUtil.isNotBlank(checkPlanClient.getGuaranteeIds())) {
            rsp.setGuaranteeIds(JSONUtil.toList(checkPlanClient.getGuaranteeIds(), Long.class));
        }
        if (CharSequenceUtil.isNotBlank(checkPlanClient.getGuaranteeNames())) {
            rsp.setGuaranteeNames(JSONUtil.toList(checkPlanClient.getGuaranteeNames(), String.class));
        }
        return rsp;
    }

    @Override
    public Long saveReportBase(AfterLeaseCheckReportBaseREQ req) {
        if (ObjectUtil.isNotEmpty(req.getIsNotCheck()) && !req.getIsNotCheck()) {
            this.preCheck(req);
        }
        NewAfterLeaseCheckReportBase reportBase = AfterLeaseCheckReportConvert.toAfterLeaseCheckReportBase(req);
        // 补充一点数据
        AfterLeaseCheckReportBaseRSP rsp = this.getCheckReportBaseRSP(req.getCheckPlanClientId(), null);
        reportBase.setBizDeptId(rsp.getBizDeptId());
        reportBase.setBizDeptName(rsp.getBizDeptName());
        reportBase.setSponsorUserId(rsp.getSponsorId());
        reportBase.setSponsorUserName(rsp.getSponsorName());
        reportBase.setRiskManagerId(rsp.getRiskManagerId());
        reportBase.setRiskManagerName(rsp.getRiskManagerName());
        reportBase.setCheckWay(rsp.getCheckWay());
        reportBase.setClientId(rsp.getClientId());
        reportBase.setRemainingPrincipal(rsp.getRemainingPrincipal());
        reportBase.setClientName(rsp.getClientName());
        if (ObjectUtil.isNotEmpty(req.getRiskExposure())) {
            reportBase.setRiskExposure(req.getRiskExposure());
        }
        SpringUtil.getBean(this.getClass()).saveOrUpdate(reportBase);

        // 详情页更新检查时间
        NewAfterLeaseCheckPlanClient clientPlan = afterLeaseCheckPlanClientService.getById(reportBase.getCheckPlanClientId());
        clientPlan.setCheckTime(req.getCheckTime());
        clientPlan.setCheckFillTime(req.getCheckFillTime());
        clientPlan.setIsNotify(false);
        afterLeaseCheckPlanClientService.updateById(clientPlan);

        return reportBase.getId();
    }

    @Override
    public void removeByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportBase> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportBase::getCheckPlanClientId, checkPlanClientId);
        this.remove(query);
    }

    @Override
    public AfterLeaseClientDataBO getAfterLeaseClientDataBO(Long clientId, boolean rich) {
        Client client = clientService.getById(clientId);
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        AfterLeaseClientDataBO afterLeaseClientDataBO = new AfterLeaseClientDataBO();
        afterLeaseClientDataBO.setClientId(client.getId());
        afterLeaseClientDataBO.setClientName(id2NameService.clientId2NameSingle(client.getId()));
        if (rich) {
            Optional<Map<Long, String>> optional = corpCommerceInfoService.selectIndustryTypeBatchByIds(Collections.singletonList(client.getId()));
            optional.ifPresent(long2StringMap -> afterLeaseClientDataBO.setIndustry(businessDataRepository.getIndustryTypeNameFromLocalCache(long2StringMap.get(client.getId()))));
        }
        // 风险敞口
        Long riskExposure = contractBaseInfoService.getStockRiskExposure(client.getId(), null, null);
        afterLeaseClientDataBO.setRiskExposure(riskExposure);
        // 合同相关数据
//        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByClients(Collections.singletonList(client.getId()));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listInRentContract(client.getId());
        getContractLibInfo(afterLeaseClientDataBO, contractBaseInfoList, rich);
        return afterLeaseClientDataBO;
    }

    @Override
    public Map<Long, AfterLeaseClientDataBO> getAfterLeaseClientDataBO(Map<Long, Client> clientMap,
                                                                       Map<Long, List<ContractBaseInfo>> contractMap) {
        Map<Long, AfterLeaseClientDataBO> res = new HashMap<>();
        contractMap.forEach((clientId, contractBaseInfos) -> {
            Client client = clientService.getById(clientId);
            if (ObjectUtil.isEmpty(client)) {
                return;
            }
            AfterLeaseClientDataBO afterLeaseClientDataBO = new AfterLeaseClientDataBO();
            afterLeaseClientDataBO.setClientId(client.getId());
            Long riskExposure = contractBaseInfoService.getStockRiskExposure(client.getId(), null, null);
            afterLeaseClientDataBO.setRiskExposure(riskExposure);
            getContractLibInfo(afterLeaseClientDataBO, contractBaseInfos, true);
            res.put(clientId, afterLeaseClientDataBO);
        });
        return res;
    }

    private void getContractLibInfo(AfterLeaseClientDataBO afterLeaseClientDataBO,
                                    List<ContractBaseInfo> contractBaseInfoList, boolean rich) {
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return;
        }
        // 找到这些合同的最新版本
        Set<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibService.listByContractIds(contractIds);
        Map<Long, List<ContractBaseInfoLib>> contractBaseInfoLibMap = contractBaseInfoLibList.stream().collect(Collectors.groupingBy(ContractBaseInfoLib::getOriginId));
        List<ContractBaseInfoLib> latestVersionContractList = new LinkedList<>();
        for (Map.Entry<Long, List<ContractBaseInfoLib>> map : contractBaseInfoLibMap.entrySet()) {
            List<ContractBaseInfoLib> list = map.getValue();
            list.sort(Comparator.comparing(ContractBaseInfo::getId));
            latestVersionContractList.add(list.get(list.size() - 1));
        }
        // 合同合计金额
        long total = latestVersionContractList.stream()
                .mapToLong(ContractBaseInfoLib::getApplyCreditAmount)
                .sum();
        afterLeaseClientDataBO.setContractTotalAmount(total);
        // 找到实际租金表/支付表最晚的那一起日期作为到期日
        // 找到第一个比今天晚的时间作为下次付款时间，对应的款项为下次付款金额
        LambdaQueryWrapper<ContractRentActualLib> queryRentLib = Wrappers.lambdaQuery();
        for (ContractBaseInfoLib contractBaseInfoLib : latestVersionContractList) {
            queryRentLib.or(true, innerQuery -> {
                innerQuery.eq(ContractRentActualLib::getContractId, contractBaseInfoLib.getOriginId());
                innerQuery.eq(ContractRentActualLib::getVersion, contractBaseInfoLib.getVersion());
                innerQuery.eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL);
            });
        }
        List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibService.list(queryRentLib);
        if (CollectionUtil.isNotEmpty(contractRentActualLibList)) {
            contractRentActualLibList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
            ContractRentActualLib last = contractRentActualLibList.get(contractRentActualLibList.size() - 1);
            afterLeaseClientDataBO.setDeadline(last.getCashFlowDate());
            if (rich) {
                ContractRentActualLib next = null;
                LocalDate now = LocalDate.now();
                for (ContractRentActualLib contractRentActualLib : contractRentActualLibList) {
                    if (now.isBefore(contractRentActualLib.getCashFlowDate())) {
                        next = contractRentActualLib;
                        break;
                    }
                }
                if (Objects.nonNull(next)) {
                    afterLeaseClientDataBO.setNextRepayDate(next.getCashFlowDate());
                    afterLeaseClientDataBO.setNextRepayAmount(next.getRent());
                }
            }
        }
    }


    private void preCheck(AfterLeaseCheckReportBaseREQ req) {
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(req.getCheckPlanClientId());
        // 根据不同的类型校验必填项
        if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.PUBLIC.name())) {
            Assert.notBlank(req.getCheckPeriodStart(), () -> MithrasException.newException("检查时段（开始）不能为空"));
            Assert.notBlank(req.getCheckPeriodEnd(), () -> MithrasException.newException("检查时段（结束）不能为空"));
            Assert.notBlank(req.getMainPerson(), () -> MithrasException.newException("主要受访人员不能为空"));
            Assert.notBlank(req.getMainPersonJob(), () -> MithrasException.newException("职务不能为空"));
            Assert.notBlank(req.getMainPersonContactWay(), () -> MithrasException.newException("联系方式不能为空"));
        }
    }

    @Override
    public List<AfterLeaseListExpandBO> getAfterLeaseListExpandBO(List<Long> planIds) {
        return this.getBaseMapper().getAfterLeaseListExpandBO(planIds);
    }
}
