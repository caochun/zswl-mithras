package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.afterlease.AfterLeaseCheckPlanProjectConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.afterlease.*;
import cn.zswltech.mithras.service.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.service.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.service.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.afterlease.*;
import cn.zswltech.mithras.service.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.*;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanBaseLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanClientLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckReportMetaLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.impl.AfterLeaseCheckReportMetaLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
@Service
public class AfterLeaseCheckPlanClientServiceImpl extends ServiceImpl<NewAfterLeaseCheckPlanClientMapper, NewAfterLeaseCheckPlanClient> implements AfterLeaseCheckPlanClientService {
    @Resource
    private OrgService orgService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanBaseLibService afterLeaseCheckPlanBaseLibService;
    @Resource
    private AfterLeaseCheckReportBaseService afterLeaseCheckReportBaseService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private UserService userService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckReportMetaLibService afterLeaseCheckReportMetaLibService;
    @Resource
    private AfterLeaseCheckReportMetaLibHandler afterLeaseCheckReportMetaLibHandler;
    @Resource
    private AfterLeaseCheckPlanClientLibService afterLeaseCheckPlanClientLibService;
    @Resource
    private ClientService clientService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private NewAfterLeaseCheckReportDetailService afterLeaseCheckReportDetailService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private AfterLeaseCheckReportFinanceService afterLeaseCheckReportFinanceService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Autowired
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private OrgDOMapper orgDOMapper;

    @Override
    public AfterLeaseCheckClientInfoRSP getInfoById(Long checkPlanClientId, String version) {
        NewAfterLeaseCheckPlanClient checkPlanClient = this.getById(checkPlanClientId);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划客户记录不存在"));
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(checkPlanClient.getPlanId());
        NewAfterLeaseCheckReportMeta checkReportMeta;
        if (StrUtil.isBlank(version)) {
            checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClientId);
        } else {
            NewAfterLeaseCheckReportMetaLib checkReportMetaLib = afterLeaseCheckReportMetaLibService.getByCheckProjectIdAndVersion(checkPlanClientId, version);
            checkReportMeta = afterLeaseCheckReportMetaLibHandler.actualLib2Entity(checkReportMetaLib);
        }
        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
        Client client = clientService.getById(checkPlanClient.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        //修改为查询客户下生效合同
        //List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listInRentContract(checkPlanClient.getClientId());
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, checkPlanClient.getClientId())
                .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())));
        List<Long> startRentContractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        Assert.notEmpty(startRentContractIdList, () -> MithrasException.newException("在租合同信息不存在"));
        AfterLeaseCheckClientInfoRSP rsp = AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckClientInfoRSP(checkPlanClient);
        rsp.setClientName(id2NameService.clientId2NameSingle(checkPlanClient.getClientId()));
        // 填充报告类型
        rsp.setReportType(checkReportMeta.getReportType());
        // 填充报告模板类型
        rsp.setReportTemplateType(checkReportMeta.getReportTemplateVersion());
        // 承租人/债权人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractIds(startRentContractIdList);
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            Set<Long> clientIds = contractTenantryList.stream()
                    .filter(e -> !Objects.equals(e.getLesseeType(), CreditorDebtorTypeEnum.DEBTOR.name()))
                    .map(ContractTenantry::getLesseeId)
                    .collect(Collectors.toSet());
            List<Client> clientList = clientService.listByClientIds(clientIds);
            if (CollectionUtil.isNotEmpty(clientList)) {
                List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoService.list(clientIds);
                Map<Long, CorpCommerceInfo> corpCommerceInfoMap;
                if (CollectionUtil.isEmpty(corpCommerceInfoList)) {
                    corpCommerceInfoMap = Collections.emptyMap();
                } else {
                    corpCommerceInfoMap = corpCommerceInfoList.stream().collect(Collectors.toMap(ClientBaseModel::getClientId, e -> e));
                }
                rsp.setLesseeList(clientList.stream().map(c -> {
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setClientId(c.getId());
                    clientInfo.setClientName(c.getClientName());
                    clientInfo.setClientType(c.getClientType());
                    clientInfo.setBelongSponsorId(c.getBelongSponsorId());
                    clientInfo.setOrgType(Optional.ofNullable(corpCommerceInfoMap.get(c.getId())).map(CorpCommerceInfo::getOrgType).orElse(null));
                    return clientInfo;
                }).collect(Collectors.toList()));
            }
        }
        // 担保人
        Optional<List<Long>> optional = contractGuarantorService.getGuaranteeIdByContractIds(startRentContractIdList);
        if (optional.isPresent() && CollectionUtil.isNotEmpty(optional.get())) {
            List<Client> clientList = clientService.listByClientIds(optional.get());
            if (CollectionUtil.isNotEmpty(clientList)) {
                rsp.setGuarantorList(clientList.stream().map(c -> {
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setClientId(c.getId());
                    clientInfo.setClientName(c.getClientName());
                    clientInfo.setClientType(c.getClientType());
                    clientInfo.setBelongSponsorId(c.getBelongSponsorId());
                    return clientInfo;
                }).collect(Collectors.toList()));
            }
        }
        ProcessResp relatedProcess = afterLeaseAdjustInfoService.findRelatedProcess(checkPlanClientId);
        rsp.setCurAssigneeIds(relatedProcess != null ? relatedProcess.getCurAssigneeIds() : null);
        //添加计划信息
        if (ObjectUtil.isNotEmpty(planBase)) {
            rsp.setPlanType(planBase.getPlanType());
        }
        return rsp;
    }

    @Override
    public List<AfterLeaseCheckClientListRSP> listBy(AfterLeaseCheckClientListREQ req) {
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(req.getPlanId());
        Assert.notNull(planBase, () -> MithrasException.newException("检查计划不存在"));
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = this.listBy(req.getPlanId(), req.getClientIdList());
        Map<Long, NewAfterLeaseCheckPlanClient> checkPlanClientMap;
        if (CollectionUtil.isEmpty(checkPlanClientList)) {
            checkPlanClientMap = Collections.emptyMap();
        } else {
            checkPlanClientMap = checkPlanClientList.stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanClient::getClientId, e -> e));
        }
        // 根据类型确定需要展示的客户信息
        List<Client> clientList;
        if (Objects.equals(planBase.getPlanType(), AfterLeaseCheckPlanTypeEnum.QUARTER.name())) {
            Assert.notEmpty(req.getClientIdList(), () -> MithrasException.newException("客户id列表不能为空"));
            clientList = clientService.listByIds(req.getClientIdList());
        } else {
            if (CollectionUtil.isEmpty(checkPlanClientList)) {
                return Collections.emptyList();
            }
            // 其他类型计划展示已被人工选择的
            List<Long> clientIds = checkPlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toList());
            clientList = clientService.listByIds(clientIds);
        }
        List<Long> clientIds = checkPlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(clientList)) {
            return Collections.emptyList();
        }
        // 查询前端所需的额外参数
        List<Long> sponsorIdList = new LinkedList<>();
        List<Long> bizDeptIdList = new LinkedList<>();
        for (Client client : clientList) {
            sponsorIdList.add(client.getBelongSponsorId());
            bizDeptIdList.add(client.getBelongDeptId());
        }
        Map<Long, String> sponsorMap = id2NameService.sysUserId2Name(sponsorIdList);
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(bizDeptIdList);
        // 查询检查报告模板类型
        Map<Long, String> reportTypeMap;
        if (CollectionUtil.isNotEmpty(checkPlanClientMap)) {
            reportTypeMap = afterLeaseCheckReportMetaService.listByCheckPlanClientIds(checkPlanClientMap.keySet()).stream().collect(Collectors.toMap(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, e -> e.getReportType()));
        } else {
            reportTypeMap = Collections.emptyMap();
        }
        // 拼装返回参数
        List<AfterLeaseCheckClientListRSP> result = new ArrayList<>(clientList.size());
        for (Client client : clientList) {
            NewAfterLeaseCheckPlanClient checkPlanClient = checkPlanClientMap.get(client.getId());
            AfterLeaseCheckClientListRSP rsp = AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckPlanClientListRSP(client, checkPlanClient);
            rsp.setSponsorName(sponsorMap.get(rsp.getSponsorId()));
            rsp.setBizDeptName(deptNameMap.get(rsp.getBizDeptId()));
            if (Objects.nonNull(checkPlanClient)) {
                rsp.setReportType(reportTypeMap.get(checkPlanClient.getId()));
            }
            result.add(rsp);
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(AfterLeaseCheckClientSaveREQ req) {
        NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getById(req.getPlanId());
        Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
        if (Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.CHECKING.name())
                || Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.FINISH.name())
                || Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.CLOSE.name())) {
            throw new MithrasException("当前计划状态不允许操作");
        }
        Client client = clientService.getById(req.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        NewAfterLeaseCheckPlanClient checkPlanClient = AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckPlanClient(req, client);
        // 根据项目主办填充所属部门
        List<OrgDO> orgList = sysUserService.listOrgByJob(req.getSponsorUserId(), JobEnum.projmanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            throw new MithrasException("没有找到项目主办作为项目经理的部门信息，保存失败");
        }
        checkPlanClient.setBelongDeptId(orgList.get(0).getId());
        // 找到担保人信息
        List<ContractTenantry> tenantryList = SpringUtil.getBean(ContractTenantryService.class).list(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getLesseeId, req.getClientId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name()));
        if (CollUtil.isNotEmpty(tenantryList)) {
            List<ContractGuarantor> contractGuarantors = SpringUtil.getBean(ContractGuarantorService.class).list(Wrappers.<ContractGuarantor>lambdaQuery()
                    .in(ContractGuarantor::getContractId, tenantryList.stream().map(ContractTenantry::getContractId).collect(Collectors.toList())));
            Set<Long> collect = contractGuarantors.stream().map(e -> JSONUtil.toList(e.getGuarantorIds(), Long.class))
                    .flatMap(Collection::stream).collect(Collectors.toSet());
            Map<Long, String> clientedId2Name = id2NameService.clientId2Name(collect);
            checkPlanClient.setGuaranteeIds(JSONUtil.toJsonStr(clientedId2Name.keySet()));
            checkPlanClient.setGuaranteeNames(JSONUtil.toJsonStr(clientedId2Name.values()));
        }
        // 保存剩余本季
        Map<Long, Long> principalMap = clientService.getClientRemainingPrincipalMap(Collections.singletonList(req.getClientId()));
        if (CollectionUtil.isNotEmpty(principalMap)) {
            checkPlanClient.setRemainingPrincipal(principalMap.get(req.getClientId()));
        }
        if (Objects.nonNull(req.getId())) {
            NewAfterLeaseCheckPlanClient exist = this.getById(req.getId());
            boolean canUpdate = !Objects.equals(exist.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name()) && !Objects.equals(exist.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name());
            Assert.isTrue(canUpdate, () -> MithrasException.newException("当前状态不允许修改检查客户信息"));
//            this.updateById(checkPlanClient);
            this.getBaseMapper().updateAnnotationIncludeNullById(checkPlanClient);
        } else {
            // 校验是否已经添加
            List<NewAfterLeaseCheckPlanClient> list = this.listBy(req.getPlanId(), Collections.singletonList(req.getClientId()));
            if (CollectionUtil.isNotEmpty(list)) {
                throw new MithrasException("该客户已经添加到本次计划中");
            }
            checkPlanClient.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            this.save(checkPlanClient);
        }
        // 变更计划状态
        afterLeaseCheckPlanBaseService.changeModifyStatus(req.getPlanId());
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        // 修改元数据信息
        if (CharSequenceUtil.isNotBlank(req.getReportType()) && Objects.nonNull(checkReportMeta)) {
            afterLeaseCheckReportMetaService.lambdaUpdate()
                    .set(NewAfterLeaseCheckReportMeta::getReportType, req.getReportType())
                    .eq(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, checkPlanClient.getId())
                    .update();
        } else if (Objects.isNull(checkReportMeta) && CharSequenceUtil.isNotBlank(req.getReportType())) {
            NewAfterLeaseCheckReportMeta newAfterLeaseCheckReportMeta = new NewAfterLeaseCheckReportMeta();
            newAfterLeaseCheckReportMeta.setCheckPlanClientId(checkPlanClient.getId());
            newAfterLeaseCheckReportMeta.setReportType(req.getReportType());
            afterLeaseCheckReportMetaService.save(newAfterLeaseCheckReportMeta);
        }
    }

    @Override
    public List<NewAfterLeaseCheckPlanClient> listBy(Long planId) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        return this.list(query);
    }

    @Override
    public List<NewAfterLeaseCheckPlanClient> listBy(Long planId, Collection<Long> clientIds) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        if (CollectionUtil.isNotEmpty(clientIds)) {
            query.in(NewAfterLeaseCheckPlanClient::getClientId, clientIds);
        }
        return this.list(query);
    }

    @Override
    public List<NewAfterLeaseCheckPlanClient> listByClientIds(Collection<Long> clientIds) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.in(NewAfterLeaseCheckPlanClient::getClientId, clientIds);
        return this.list(query);
    }

    @Override
    public List<AfterLeaseCheckClientDeptInfoRSP> listAfterLeaseCanCheckGroupByDept(Long planId) {
        NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getById(planId);
        Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
        // 如果是季度计划需要默认添加符合要求的客户 只有新建状态需要添加
        if (Objects.equals(plan.getPlanType(), AfterLeaseCheckPlanTypeEnum.QUARTER.name())
                && Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.NEW.name())
                && this.checkProcessStatusBeforeAdd(plan)) {
            List<NewAfterLeaseCheckPlanClient> exist = this.listBy(planId);
            Set<Long> existClientId = exist.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toSet());
            // 找到所有起租状态的合同
            LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
            contractQuery.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
            Set<Long> candidateClientId = contractBaseInfoList.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());
            // 如果已经添加过则移除
            candidateClientId.removeIf(existClientId::contains);
            if (CollectionUtil.isNotEmpty(candidateClientId)) {
                // 如果有不存在的则加到计划中
                List<Client> clientList = clientService.listByIds(candidateClientId);
                List<NewAfterLeaseCheckPlanClient> toInsertList = clientList.stream().map(item -> AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckPlanClient(planId, item)).collect(Collectors.toList());
                this.saveBatch(toInsertList);
            }
        }
        // 找到所有业务部门
        Example example = new Example(OrgDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", OrgConstants.BUSINESS_DEPT);
        OrgDO currentUserBizDept = sysUserService.currentUserBizDept();
        if (Objects.nonNull(currentUserBizDept)) {
            // 如果是业务部门，则只取自己部门的数据
            criteria.andEqualTo("id", currentUserBizDept.getId());
        }
        List<OrgDO> orgList = orgService.selectByExample(example);
        if (CollectionUtil.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        Map<Long, OrgDO> orgMap = orgList.stream().collect(Collectors.toMap(OrgDO::getId, e -> e));
        // 拼装返回参数
        List<NewAfterLeaseCheckPlanClient> all = this.listBy(planId);
        Map<Long, List<NewAfterLeaseCheckPlanClient>> map = all.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanClient::getBelongDeptId));
        List<AfterLeaseCheckClientDeptInfoRSP> result = new ArrayList<>(orgMap.size());
        for (Map.Entry<Long, OrgDO> entry : orgMap.entrySet()) {
            OrgDO orgDO = entry.getValue();
            AfterLeaseCheckClientDeptInfoRSP rsp = new AfterLeaseCheckClientDeptInfoRSP();
            rsp.setDeptId(orgDO.getId());
            rsp.setDeptName(orgDO.getName());
            List<NewAfterLeaseCheckPlanClient> list = map.get(orgDO.getId());
            if (CollectionUtil.isEmpty(list)) {
                rsp.setClientIdList(Collections.emptyList());
                rsp.setToCheckCount(0);
            } else {
                List<Long> clientIdList = new ArrayList<>(list.size());
                int toCheckCount = 0;
                for (NewAfterLeaseCheckPlanClient checkPlanClient : list) {
                    clientIdList.add(checkPlanClient.getClientId());
                    if (Objects.equals(checkPlanClient.getIsCheck(), YesOrNoNumberEnum.YES.getCode())) {
                        toCheckCount++;
                    }
                }
                rsp.setToCheckCount(toCheckCount);
                rsp.setClientIdList(clientIdList);
            }
            result.add(rsp);
        }
        return result;
    }

    @Override
    public List<NewAfterLeaseCheckPlanClient> listToCheck(Long planId) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        query.eq(NewAfterLeaseCheckPlanClient::getIsCheck, YesOrNoNumberEnum.YES.getCode());
        query.in(NewAfterLeaseCheckPlanClient::getApprovalStatus, Arrays.asList(ProcessStatus.UN_SUBMIT.name(), ProcessStatus.APPROVAL_REJECT.name(), ProcessStatus.CANCELED.name()));
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public AfterLeaseCheckReportVersionRSP changeReportType(Long id, String reportType) {
        NewAfterLeaseCheckPlanClient checkPlanClient = this.getById(id);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("没有找到检查计划中的客户信息"));
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(id);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查报告元数据不存在"));
        boolean canChange = !Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name()) && !Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name());
        Assert.isTrue(canChange, () -> MithrasException.newException("当前状态不允许变更模板"));
        // 清空已有数据
        NewAfterLeaseCheckReportDetail reportDetail = afterLeaseCheckReportDetailService.getOneByCheckPlanClientId(checkPlanClient.getId());
        reportDetail.setReportContent(null);
        reportDetail.setReportSummary(null);
        afterLeaseCheckReportDetailService.getBaseMapper().updateAnnotationIncludeNullById(reportDetail);
        // 执行变更
        NewAfterLeaseCheckReportMeta toUpdate = new NewAfterLeaseCheckReportMeta();
        toUpdate.setId(checkReportMeta.getId());
        toUpdate.setReportType(reportType);
        toUpdate.setReportTemplateVersion(AfterLeaseCheckReportTemplateVersionEnum.getLatestVersion(reportType));
        afterLeaseCheckReportMetaService.updateById(toUpdate);
        AfterLeaseCheckReportVersionRSP rsp = new AfterLeaseCheckReportVersionRSP();
        rsp.setType(toUpdate.getReportType());
        rsp.setVersion(toUpdate.getReportTemplateVersion());
        return rsp;
    }

    @Override
    public List<AfterLeaseCheckClientSelectRSP> queryClient(AfterLeaseCheckClientSelectREQ req) {
//        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
//        query.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
//        query.orderByDesc(ContractBaseInfo::getId);
//        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(query);
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listInRentContract(null);
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyList();
        }
        List<Long> clientIds = contractBaseInfoList.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList());
        LambdaQueryWrapper<Client> clientQuery = Wrappers.lambdaQuery();
        clientQuery.in(Client::getId, clientIds);
        if (StrUtil.isNotBlank(req.getClientName())) {
            clientQuery.like(Client::getClientName, req.getClientName());
        }
        clientQuery.last(StringUtil.mysqlLimit(0, 10));
        List<Client> clientList = clientService.list(clientQuery);
        List<Long> clientIdList = new ArrayList<>(clientList.size());
        List<Long> sponsorIdList = new ArrayList<>(clientList.size());
        List<Long> bizDeptIdList = new ArrayList<>(clientList.size());
        for (Client client : clientList) {
            clientIdList.add(client.getId());
            sponsorIdList.add(client.getBelongSponsorId());
            bizDeptIdList.add(client.getBelongDeptId());
        }
        // id转名称
        Map<Long, String> userMap = id2NameService.sysUserId2Name(sponsorIdList);
        Map<Long, String> bizDeptMap = id2NameService.deptId2Name(bizDeptIdList);
        // 查询计划项目确定是否已被选择
        List<NewAfterLeaseCheckPlanClient> selectedProjectList = this.listBy(req.getPlanId(), clientIdList);
        // 按照项目id分组
        Map<Long, NewAfterLeaseCheckPlanClient> selectedProjectMap = selectedProjectList.stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanClient::getClientId, e -> e));
        // 拼装返回参数
        List<AfterLeaseCheckClientSelectRSP> result = new ArrayList<>(clientList.size());
        for (Client client : clientList) {
            AfterLeaseCheckClientSelectRSP rsp = AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckClientSelectRSP(client, selectedProjectMap.get(client.getId()));
            // 补充一些信息
            rsp.setBizDeptName(bizDeptMap.get(client.getBelongDeptId()));
            rsp.setSponsorName(userMap.get(client.getBelongSponsorId()));
            result.add(rsp);
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void submitApproval(Long checkClientId) {
        submitApproval(checkClientId, true);
    }

    public void submitApproval(Long checkClientId, boolean isCheck) {
        // 检查是否存在暂存的信息
        NewAfterLeaseCheckReportDetail reportDetail = afterLeaseCheckReportDetailService.getOneByCheckPlanClientId(checkClientId);
        if (Objects.isNull(reportDetail) || CharSequenceUtil.equalsAny(SaveStatusEnum.NO_SAVE.name(), reportDetail.getReportContentSaveStatus(), reportDetail.getReportSummarySaveStatus())) {
            throw new MithrasException("存在暂存的检查报告，请先保存后提交");
        }
        NewAfterLeaseCheckPlanClient checkPlanClient = this.getById(checkClientId);
        if (isCheck) {
            startCheck(checkPlanClient);
        }
        checkPlanClient.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        // 计划台账展示使用
        if (isCheck) {
            checkPlanClient.setCommitTime(LocalDateTime.now());
            this.updateById(checkPlanClient);
        }
        // 提交审批
        String processInstanceId = startFlow(checkPlanClient);
        bizProcessDataService.recordBizData(processInstanceId, checkPlanClient.getClientId());
        //回调代发起
        commonProcessPrepareService.commitCallback(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name(), String.valueOf(checkClientId));
    }

    private void startCheck(NewAfterLeaseCheckPlanClient checkPlanClient) {
        if (checkPlanClient.getCheckTime() == null) {
            if (checkPlanClient.getCheckWay().equals(AfterLeaseCheckWayEnum.SITE.name())) {
                throw new MithrasException("检查日期为空，请联系协查风控经理维护相关信息");
            } else {
                throw new MithrasException("检查日期为空");
            }
        }
        this.submitApprovalCheck(checkPlanClient);
        if (Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
            throw new MithrasException("审批中，不允许重复提交审批");
        }
        if (Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
            throw new MithrasException("审批通过，不允许再提交审批");
        }
    }

    private String startFlow(NewAfterLeaseCheckPlanClient checkPlanClient) {
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(checkPlanClient.getPlanId());
        Client client = clientService.getById(checkPlanClient.getClientId());
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(checkPlanClient.getId().toString());
        if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(planBase.getPlanType())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name());
        }
        if (Objects.nonNull(checkPlanClient.getBelongDeptId())) {
            startProcessReq.setStartUserDeptId(checkPlanClient.getBelongDeptId().toString());
        }
        AccountVO accountVO = AccountUtil.getLoginInfo();
        startProcessReq.setStartUserId(Optional.ofNullable(accountVO).map(AccountVO::getId).map(String::valueOf).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setProcessInstanceName(client.getClientName() + "的租后检查报告审批流程");
        // 设置启动参数
        Map<String, Object> initParamMap = new HashMap<>();
        List<String> riskControlManagerList = new LinkedList<>();
        List<String> bizDeptLeaderList = new LinkedList<>();
        List<String> bizDivisionLeaderList = new LinkedList<>();
        if (Objects.nonNull(checkPlanClient.getRiskManagerId())) {
            riskControlManagerList.add(checkPlanClient.getRiskManagerId().toString());
        }
        // 查询业务部门负责人
        List<UserDO> businessHeadList = userService.jobUsers(checkPlanClient.getBelongDeptId(), JobEnum.businesshead.name());
        if (CollectionUtil.isNotEmpty(businessHeadList)) {
            for (UserDO userDO : businessHeadList) {
                bizDeptLeaderList.add(userDO.getId().toString());
            }
        }
        // 查询业务分管领导
        List<UserDO> leaderList = userService.jobUsers(checkPlanClient.getBelongDeptId(), JobEnum.leaderincharge.name());
        if (CollectionUtil.isNotEmpty(leaderList)) {
            for (UserDO userDO : leaderList) {
                bizDivisionLeaderList.add(userDO.getId().toString());
            }
        }
        if (AfterLeaseCheckWayEnum.SITE.name().equals(planBase.getCheckWay())) {
            initParamMap.put("isSite", Boolean.TRUE);
        } else {
            initParamMap.put("isSite", Boolean.FALSE);
        }
        initParamMap.put("projectManager", ListUtil.toList(String.valueOf(checkPlanClient.getBelongSponsorId())));
        initParamMap.put("riskControlManager", riskControlManagerList);
        initParamMap.put("bizDeptLeader", bizDeptLeaderList);
        initParamMap.put("bizDivisionLeader", bizDivisionLeaderList);
        startProcessReq.setVariables(initParamMap);
        return flowProcessApiService.start(startProcessReq);
    }

    @Override
    public Map<Long, List<NewAfterLeaseCheckPlanClient>> getClientMapByPlan(Collection<Long> planIds) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.in(NewAfterLeaseCheckPlanClient::getPlanId, planIds);

        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        query.and(isBizUser, e -> e.eq(NewAfterLeaseCheckPlanClient::getBelongSponsorId, AccountUtil.getLoginInfo().getId())
                .or().in(NewAfterLeaseCheckPlanClient::getBelongDeptId, canViewDeptIds)
        );
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = this.list(query);
        if (CollectionUtil.isEmpty(checkPlanClientList)) {
            return Collections.emptyMap();
        }
        return checkPlanClientList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanClient::getPlanId));
    }

    @Override
    public List<AfterLeaseCheckClientListRSP> listByLoginUser(Long planId, String version) {
        NewAfterLeaseCheckPlanBase plan;
        if (ObjectUtil.isNull(version)) {
            plan = afterLeaseCheckPlanBaseService.getById(planId);
        } else {
            plan = afterLeaseCheckPlanBaseLibService.getByOriginIdAndVersion(planId, version);
        }
        Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        // 判断是否是业务部门，如果是业务部门只能看自己部门和主办是自己的
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        query.and(isBizUser, e -> e.eq(NewAfterLeaseCheckPlanClient::getBelongSponsorId, AccountUtil.getLoginInfo().getId())
                .or().in(NewAfterLeaseCheckPlanClient::getBelongDeptId, canViewDeptIds)
        );
        // 如果计划已经处于检查中或者计划完结，则过滤无需检查的项目
        if (Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.CHECKING.name()) || Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.FINISH.name())) {
            query.eq(NewAfterLeaseCheckPlanClient::getIsCheck, YesOrNoNumberEnum.YES.getCode());
        }
        List<NewAfterLeaseCheckPlanClient> todoList;
        if (ObjectUtil.isNull(version)) {
            todoList = this.list(query);
        } else {
            //todo 我理解审批流中应该可以看到计划的全部内容，而不是根据每个人的权限来看到不同的，待确认
            todoList = afterLeaseCheckPlanClientLibService.listByPlanIdAndVersion(planId, version);
        }

        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        // 额外信息查询
        List<NewAfterLeaseCheckReportMetaLib> metaLibs = afterLeaseCheckReportMetaLibService.getBaseMapper().selectLatestListByPlanClientId(todoList.stream().map(NewAfterLeaseCheckPlanClient::getId).collect(Collectors.toList()));
        Map<Long, String> metaLibMap;
        if (CollUtil.isEmpty(metaLibs)) {
            metaLibMap = afterLeaseCheckReportMetaService.listByCheckPlanClientIds(todoList.stream().map(NewAfterLeaseCheckPlanClient::getId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toMap(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, NewAfterLeaseCheckReportMeta::getReportType));
        } else {
            metaLibMap = metaLibs.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportMetaLib::getCheckPlanClientId, NewAfterLeaseCheckReportMeta::getReportType));
        }
        List<Long> clientIdList = new LinkedList<>();
        List<Long> bizDeptIdList = new LinkedList<>();
        List<Long> sponsorIdList = new LinkedList<>();
        for (NewAfterLeaseCheckPlanClient checkPlanProject : todoList) {
            clientIdList.add(checkPlanProject.getClientId());
            bizDeptIdList.add(checkPlanProject.getBelongDeptId());
            sponsorIdList.add(checkPlanProject.getBelongSponsorId());
            sponsorIdList.add(checkPlanProject.getRiskManagerId());
        }
        List<Client> clientList = clientService.listByIds(clientIdList);
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, e -> e));
        Map<Long, String> bizDeptNameMap = id2NameService.deptId2Name(bizDeptIdList);
        Map<Long, String> sponsorNameMap = id2NameService.sysUserId2Name(sponsorIdList);
        Map<Long, String> finalMetaLibMap = metaLibMap;
        return todoList.stream().map(item -> {
            AfterLeaseCheckClientListRSP rsp = AfterLeaseCheckPlanProjectConvert.toAfterLeaseCheckPlanClientListRSP(clientMap.get(item.getClientId()), item);
            rsp.setReportType(finalMetaLibMap.get(item.getId()));
            rsp.setBizDeptName(bizDeptNameMap.get(rsp.getBizDeptId()));
            rsp.setSponsorName(sponsorNameMap.get(rsp.getSponsorId()));
            rsp.setRiskManagerName(sponsorNameMap.get(rsp.getRiskManagerId()));
            return rsp;
        }).collect(Collectors.toList());
    }

    @Override
    public void submitApprovalCheck(Long checkClientId) {
        NewAfterLeaseCheckPlanClient checkPlanClient = this.getById(checkClientId);
        this.submitApprovalCheck(checkPlanClient);
    }

    @Override
    public void submitApprovalCheck(NewAfterLeaseCheckPlanClient checkPlanClient) {
        //区分版本
        NewAfterLeaseCheckReportMeta afterLeaseCheckReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        if (ObjectUtil.isNotEmpty(afterLeaseCheckReportMeta)) {
            switch (afterLeaseCheckReportMeta.getReportTemplateVersion()) {
                //todo 版本加个枚举控制
                case "V1":
                case "V2":
                    this.submitApprovalCheckV1(checkPlanClient);
                    break;
                case "V3":
                    this.submitApprovalCheckV3(checkPlanClient);
                default:
                    this.submitApprovalCheckV1(checkPlanClient);
            }
        } else {
            this.submitApprovalCheckV1(checkPlanClient);
        }
    }

    private void submitApprovalCheckV1(NewAfterLeaseCheckPlanClient checkPlanClient) {
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划待检客户数据不存在"));
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
        Client client = clientService.getById(checkPlanClient.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        NewAfterLeaseCheckReportBase reportBase = afterLeaseCheckReportBaseService.getReportBase(checkPlanClient.getId());
        Assert.notNull(reportBase, () -> MithrasException.newException("客户租后检查报告模版未维护完整，请检查并维护完整后再提交！"));
        NewAfterLeaseCheckReportDetail reportDetail = afterLeaseCheckReportDetailService.getOneByCheckPlanClientId(checkPlanClient.getId());
        Assert.notBlank(reportDetail.getReportContent(), () -> MithrasException.newException("客户租后检查报告模版未维护完整，请检查并维护完整后再提交！"));
        //Assert.notBlank(reportDetail.getReportSummary(), () -> MithrasException.newException("检查报告-检查总结未保存"));
        // 如果是公用事业类和产业类则客户指定类型的财报必填
        if (CharSequenceUtil.equalsAny(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.BUS.name(), AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(),
                AfterLeaseCheckReportTypeEnum.PUBLIC.name(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
            AfterLeaseCheckClientInfoRSP clientInfoRSP = this.getInfoById(checkPlanClient.getId(), null);
            if (CollectionUtil.isNotEmpty(clientInfoRSP.getLesseeList())) {
                // 查询当前报告的财务报表快照
                List<NewAfterLeaseCheckReportFinance> checkReportFinanceList = afterLeaseCheckReportFinanceService.listByCheckPlanClientId(checkPlanClient.getId());
                if (CollectionUtil.isEmpty(checkReportFinanceList)) {
                    throw new MithrasException("承租人财务报表中的<资产负债表>和<利润表>必须保存");
                }
                Set<String> keySet = checkReportFinanceList.stream().map(e -> e.getClientId() + "-" + e.getSubjectType()).collect(Collectors.toSet());
                for (ClientInfo clientInfo : clientInfoRSP.getLesseeList()) {
                    // 根据客户的组织机构类型区分处理
                    if (Objects.equals(clientInfo.getOrgType(), "1")) {
                        // 企业
                        if (!keySet.contains(clientInfo.getClientId() + "-" + SubjectItemType.CAPITAL_BALANCE.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), SubjectItemType.CAPITAL_BALANCE.display()));
                        }
                        if (!keySet.contains(clientInfo.getClientId() + "-" + SubjectItemType.PROFIT.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), SubjectItemType.PROFIT.display()));
                        }
                    } else {
                        // 非企业
                        if (!keySet.contains(clientInfo.getClientId() + "-" + GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.display()));
                        }
                        if (!keySet.contains(clientInfo.getClientId() + "-" + GovernmentSubjectItemType.INCOME_EXPEND.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), GovernmentSubjectItemType.INCOME_EXPEND.display()));
                        }
                    }
                }
            }
        }
        // 文件类型校验
        // 若为现场检查则《最新一期财务报表》、《法人企业征信报告》、《租赁物照片、与客户工作人员厂区（办公区）合影》为必须上传项
        // 若为非现场检查则《最新一期财务报表》、《法人企业征信报告》为必须上传项
        if (!Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.LOW_RISK.name())) {
            List<MaterialsList> materialsListList = materialsListService.listBy(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(), checkPlanClient.getId());
            Set<String> types = materialsListList.stream().map(MaterialsList::getMaterialSubType).collect(Collectors.toSet());
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ZXYQCWBB.name())) {
                throw new MithrasException("<最新一期财务报表>必须上传");
            }
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.FRQYZXBG.name())) {
                throw new MithrasException("<法人企业征信报告>必须上传");
            }
            if (Objects.equals(checkPlanClient.getCheckWay(), AfterLeaseCheckWayEnum.SITE.name())) {
                if (!types.contains(AfterLeaseCheckReportMaterialsEnum.KHHY.name())  && noVisitRecord(checkPlanClient)) {
                    throw new MithrasException("<与客户工作人员厂区（办公区）合影>必须上传");
                }
                if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                    if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ZLWZP.name())) {
                        throw new MithrasException("<租赁物照片>必须上传");
                    }
                }
            }
            // XMX-40 调整租后检查流程校验逻辑
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.RZKSYPZ_MXB_JHB.name())) {
                throw new MithrasException("<融资款使用凭证或相关银行流水、融资明细表/还款计划表>必须上传");
            }
            if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ANALYSIS_REPORT.name())) {
                    throw new MithrasException("租后检查分析报告必须上传");
                }
            }
        }
    }

    private void submitApprovalCheckV3(NewAfterLeaseCheckPlanClient checkPlanClient) {
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划待检客户数据不存在"));
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
        Client client = clientService.getById(checkPlanClient.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        NewAfterLeaseCheckReportBase reportBase = afterLeaseCheckReportBaseService.getReportBase(checkPlanClient.getId());
        Assert.notNull(reportBase, () -> MithrasException.newException("检查报告-基本信息未保存"));
        NewAfterLeaseCheckReportDetail reportDetail = afterLeaseCheckReportDetailService.getOneByCheckPlanClientId(checkPlanClient.getId());
        Assert.notBlank(reportDetail.getReportContent(), () -> MithrasException.newException("客户租后检查报告模版未维护完整，请检查并维护完整后再提交！"));
        //Assert.notBlank(reportDetail.getReportSummary(), () -> MithrasException.newException("检查报告-检查总结未保存"));
        // 如果是公用事业类和产业类则客户指定类型的财报必填
        if (CharSequenceUtil.equalsAny(checkReportMeta.getReportType(),
                AfterLeaseCheckReportTypeEnum.BUS.name(), AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(),
                AfterLeaseCheckReportTypeEnum.PUBLIC.name(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
            AfterLeaseCheckClientInfoRSP clientInfoRSP = this.getInfoById(checkPlanClient.getId(), null);
            if (CollectionUtil.isNotEmpty(clientInfoRSP.getLesseeList())) {
                // 查询当前报告的财务报表快照
                List<NewAfterLeaseCheckReportFinance> checkReportFinanceList = afterLeaseCheckReportFinanceService.listByCheckPlanClientId(checkPlanClient.getId());
                if (CollectionUtil.isEmpty(checkReportFinanceList)) {
                    throw new MithrasException("承租人财务报表中的<资产负债表>和<利润表>必须保存");
                }
                Set<String> keySet = checkReportFinanceList.stream().map(e -> e.getClientId() + "-" + e.getSubjectType()).collect(Collectors.toSet());
                for (ClientInfo clientInfo : clientInfoRSP.getLesseeList()) {
                    // 根据客户的组织机构类型区分处理
                    if (Objects.equals(clientInfo.getOrgType(), "1")) {
                        // 企业
                        if (!keySet.contains(clientInfo.getClientId() + "-" + SubjectItemType.CAPITAL_BALANCE.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), SubjectItemType.CAPITAL_BALANCE.display()));
                        }
                        if (!keySet.contains(clientInfo.getClientId() + "-" + SubjectItemType.PROFIT.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), SubjectItemType.PROFIT.display()));
                        }
                    } else {
                        // 非企业
                        if (!keySet.contains(clientInfo.getClientId() + "-" + GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.display()));
                        }
                        if (!keySet.contains(clientInfo.getClientId() + "-" + GovernmentSubjectItemType.INCOME_EXPEND.name())) {
                            throw new MithrasException(String.format("<%s>的<%s>不能为空", clientInfo.getClientName(), GovernmentSubjectItemType.INCOME_EXPEND.display()));
                        }
                    }
                }
            }
        }
        // 文件类型校验
        // 若为现场检查则《最新一期财务报表》、《法人企业征信报告》、《租赁物照片、与客户工作人员厂区（办公区）合影》为必须上传项
        // 若为非现场检查则《最新一期财务报表》、《法人企业征信报告》为必须上传项
        if (!Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.LOW_RISK.name())) {
            List<MaterialsList> materialsListList = materialsListService.listBy(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(), checkPlanClient.getId());
            Set<String> types = materialsListList.stream().map(MaterialsList::getMaterialSubType).collect(Collectors.toSet());
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ZXYQCWBB.name())) {
                throw new MithrasException("<最新一期财务报表>必须上传");
            }
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.FRQYZXBG.name())) {
                throw new MithrasException("<法人企业征信报告>必须上传");
            }
            if (Objects.equals(checkPlanClient.getCheckWay(), AfterLeaseCheckWayEnum.SITE.name())) {
                if (!types.contains(AfterLeaseCheckReportMaterialsEnum.KHHY.name()) && noVisitRecord(checkPlanClient)) {
                    throw new MithrasException("<与客户工作人员厂区（办公区）合影>必须上传");
                }
                if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                    if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ZLWZP.name())) {
                        throw new MithrasException("<租赁物照片>必须上传");
                    }
                }
            }
            // XMX-40 调整租后检查流程校验逻辑
            if (!types.contains(AfterLeaseCheckReportMaterialsEnum.RZKSYPZ_MXB_JHB.name())) {
                throw new MithrasException("<融资款使用凭证或相关银行流水、融资明细表/还款计划表>必须上传");
            }
            if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                if (!types.contains(AfterLeaseCheckReportMaterialsEnum.ANALYSIS_REPORT.name())) {
                    throw new MithrasException("租后检查分析报告必须上传");
                }
            }
        }
    }


    @Override
    public void updateAnnotationIncludeNullById(NewAfterLeaseCheckPlanClient checkPlanClient) {
        this.getBaseMapper().updateAnnotationIncludeNullById(checkPlanClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void removeProjectById(Long id) {
        NewAfterLeaseCheckPlanClient checkPlanClient = this.getById(id);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划中的客户数据不存在"));
        this.removeById(id);
        // 变更计划状态
        afterLeaseCheckPlanBaseService.changeModifyStatus(checkPlanClient.getPlanId());
    }

    private boolean checkProcessStatusBeforeAdd(NewAfterLeaseCheckPlanBase plan) {
        if (StrUtil.isBlank(plan.getApprovalStatus())) {
            return true;
        }
        if (Objects.equals(plan.getApprovalStatus(), AfterLeaseCheckPlanProcessStatusEnum.NEW_REJECT.name())) {
            return true;
        }
        if (Objects.equals(plan.getApprovalStatus(), AfterLeaseCheckPlanProcessStatusEnum.NEW_CANCEL.name())) {
            return true;
        }
        return false;
    }

    // XMX-36 是否存在打卡照片
    private boolean noVisitRecord(NewAfterLeaseCheckPlanClient checkPlanClient) {
        if (checkPlanClient != null && checkPlanClient.getPlanId() != null) {
            List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                    Wrappers.<VisitRecord>lambdaQuery()
                            .in(VisitRecord::getCheckPlanId, checkPlanClient.getPlanId())
                            .eq(VisitRecord::getClientId, checkPlanClient.getClientId())
                            .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                            .eq(VisitRecord::getDeleted, 0)
                            .orderByDesc(VisitRecord::getCheckInDate));
            if (!visitRecordList.isEmpty()) {
                Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
                List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                        .in(MaterialsList::getBelongId, visitIds)
                        .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name())
                );
                return dataList.isEmpty();
            }
        }
        return true;
    }
    @Override
    public PageR<AfterLeaseCheckLedgerListRSP> queryLedgerList(AfterLeaseCheckLedgerListREQ req) {
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();

        // 是否为业务部门负责人
        boolean bizDeptFlag = userDeptList.stream().allMatch(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType()));
        if (bizDeptFlag) {
            boolean containId = Objects.nonNull(req.getBelongDeptId()) && new HashSet<>(userDeptList.stream().filter(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType())).map(OrgDO::getId).collect(Collectors.toList())).containsAll(req.getBelongDeptId());
            if(!containId){
                // 业务部门负责人只能查看自己部门下的计划
                req.setBelongDeptId(userDeptList.stream().filter(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType())).map(OrgDO::getId).collect(Collectors.toList()));
            }
        }

        Page<AfterLeaseCheckLedgerListRSP> pageResult = newAfterLeaseCheckPlanClientMapper.queryAfterLeaseCheckPlanLedgerList(new Page<>(req.getPage(), req.getPageSize()), req);

        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<AfterLeaseCheckLedgerListRSP> ledgerList = pageResult.getRecords();

        // 业务部门和客户名称
        Map<Long, String> userIdNameMap = userDOMapper.selectByIds(ledgerList.stream().map(AfterLeaseCheckLedgerListRSP::getBelongSponsorId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUserName, (a, b) -> a));
        Map<Long, String> deptIdNameMap = orgDOMapper.selectByIds(ledgerList.stream().map(AfterLeaseCheckLedgerListRSP::getBelongDeptId).collect(Collectors.toList()), 1)
                .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (a, b) -> a));

        // 补充字段
        ledgerList.forEach(item -> {
            item.setBelongSponsorUserName(userIdNameMap.get(item.getBelongSponsorId()));
            item.setBelongDeptName(deptIdNameMap.get(item.getBelongDeptId()));
            item.setCheckWay(StrUtil.isEmpty(item.getCheckWay()) ? "" : AfterLeaseCheckWayEnum.find(item.getCheckWay()).display());
            item.setReportType(StrUtil.isEmpty(item.getReportType()) ? "" : AfterLeaseCheckReportTypeEnum.ofName(item.getReportType()).display());
            item.setCheckTime(StrUtil.isEmpty(item.getCheckTime()) ? "" : item.getCheckTime());
            // 手动检查直接启动流程，状态需要特殊处理一下
            AfterLeaseCheckStatusEnum afterLeaseCheckStatusEnum = AfterLeaseCheckStatusEnum.find(item.getCheckStatus());
            item.setCheckStatus(Objects.isNull(afterLeaseCheckStatusEnum)? "0": afterLeaseCheckStatusEnum.display());
            item.setOverdueDays(StrUtil.isEmpty(item.getOverdueDays()) ? "0" : item.getOverdueDays());
            item.setPlanType(StrUtil.isEmpty(item.getPlanType()) ? "" : AfterLeaseCheckPlanTypeEnum.of(item.getPlanType()).display());
            item.setCommitTime(StrUtil.isEmpty(item.getCommitTime()) ? "" : item.getCommitTime().substring(0, 10) );
            if("0".equals(item.getCheckStatus())){
                item.setCheckStatus(AfterLeaseCheckStatusEnum.UN_P_UN_S.display());
                item.setCommitTime("");
            }
        });
        PageR<AfterLeaseCheckLedgerListRSP> result = PageR.of(ledgerList, pageResult.getTotal(), req.getPage(), req.getPageSize());
        if(bizDeptFlag){
            // 返回一个列表给前端下拉筛选
            result.setOthers(MapUtil.of("deptIdList", userDeptList.stream().map(OrgDO::getId).collect(Collectors.toList())));
        }
        return result;
    }
}
