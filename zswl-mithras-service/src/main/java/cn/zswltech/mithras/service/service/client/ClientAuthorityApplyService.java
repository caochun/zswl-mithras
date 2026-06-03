package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.customer.authorityrecord.application.ClientAuthorityApplyRecordService;
import cn.zswltech.mithras.customer.authorityrecord.infrastructure.mapper.ClientAuthorityApplyRecordMapper;
import cn.zswltech.mithras.customer.authorityrecord.infrastructure.model.ClientAuthorityApplyRecord;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.*;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.NewCorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.customer.application.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.customer.application.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.JobEnum.leaderincharge;
import static cn.zswltech.mithras.customer.domain.enums.client.ClientTransferStatus.timed_approved;
import static cn.zswltech.mithras.customer.domain.enums.client.ClientTransferStatus.to_be_approved;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ClientAuthorityApplyService extends ServiceImpl<ClientAuthorityMapper, ClientAuthority>  implements FlowEndEventProcessor {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientTransferMapper clientTransferMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private ClientFileInfoMapper fileInfoMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private NewCorpCommerceInfoMapper newCorpCommerceInfoMapper;
    @Resource
    private ClientAuthorityUtil clientAuthorityUtil;
    @Resource
    private ClientCreateRecordService clientCreateRecordService;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ClientAuthorityApplyRecordMapper applyRecordMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void applyEffect(ClientApplyEffectREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        if (StringUtils.isBlank(req.getApplyReason())) {
            throw new MithrasException("请填写申请原因");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        if (clientAuthorityUtil.isIntraGroupCollaboration(client.getId())) {
            throw new MithrasException("所选择客户为公海客户，无需申请相关权限！");
        }
        if (!ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())) {
            throw new MithrasException("请选择生效状态客户");
        }
        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, req.getClientId())
                .eq(ClientAuthority::getLevel, ClientLevelEnum.APPLY.getLevel())
                .eq(ClientAuthority::getUserId, startUserId)
                .eq(ClientAuthority::getDeleted, 0));
        if (Objects.nonNull(clientAuthority)) {
            throw new MithrasException("您已拥有该客户的申办权限，无需再次申请!");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ClientApplyAuthorityFlow.name());
        List<String> businessKeys = new ArrayList<>();
        businessKeys.add(String.valueOf(client.getId()));
        Long deptId = 0L;
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(startUserId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        } else {
            throw new MithrasException("用户部门不存在");
        }

        Page<ProcessResp> flowRespPage = getClientAuthorityApplyProcessByIds(businessKeys, ProcessModelTypeEnum.ClientApplyAuthorityFlow.name(), startUserId);
        if (flowRespPage != null && !flowRespPage.getContents().isEmpty()) {
            throw new MithrasException(String.format("项目经理%s已经提交申办权流程", id2NameService.sysUserId2NameSingle(startUserId)));
        }
        //起始人
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        //申办权限业务部门负责人
        Long originDeptLeader = sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name());
        startProcessReq.setStartUserId(String.valueOf(startUserId));
        //权限管护人（即所属主办）
        Long belongSponsorId = 0L;
        if (req.getBelongSponsorId() != null) {
            belongSponsorId = req.getBelongSponsorId();
        } else {
            belongSponsorId = client.getBelongSponsorId();
        }
        //权限管护人(所属部门)
        Long belongDeptId = 0L;
        if (req.getBelongDeptId() != null) {
            belongDeptId = req.getBelongDeptId();
        } else {
            belongDeptId = client.getBelongDeptId();
        }
        //权限管护人业务部门负责人
        Long sponsorDeptLeader = sysUserService.getUserIdByOrgJob(belongDeptId, JobEnum.businesshead.name());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(originDeptLeader) ? ListUtil.toList(String.valueOf(originDeptLeader)) : new ArrayList<>()),
                Pair.of("manageUserId", Objects.nonNull(belongSponsorId) ? ListUtil.toList(String.valueOf(belongSponsorId)) : new ArrayList<>()),
                Pair.of("manageDeptLeader", Objects.nonNull(sponsorDeptLeader) ? ListUtil.toList(String.valueOf(sponsorDeptLeader)) : new ArrayList<>()))
        );
        startProcessReq.setBusinessKey(String.valueOf(req.getClientId()));
        startProcessReq.setSubModule(client.getClientType());
        StringBuilder stringBuilder = new StringBuilder();
        String currentUserName = sysUserService.getUserName(startUserId);
        stringBuilder.append(currentUserName).append("发起的申办权流程-").append(client.getClientName());
        startProcessReq.setProcessInstanceName(stringBuilder.toString());
        startProcessReq.setStartUserDeptId(Optional.ofNullable(bizOrgDO.getId()).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, req.getClientId());
        // 创建权限申请记录
        ClientAuthorityApplyRecord clientAuthorityApplyRecord = applyRecordMapper.selectOne(Wrappers.<ClientAuthorityApplyRecord>lambdaQuery()
                .eq(ClientAuthorityApplyRecord::getClientId, req.getClientId())
                .eq(ClientAuthorityApplyRecord::getBatchNo, req.getBatchNo())
                .eq(ClientAuthorityApplyRecord::getUserId, startUserId)
                .eq(ClientAuthorityApplyRecord::getDeleted, 0));
        if (clientAuthorityApplyRecord == null) {
            clientAuthorityApplyRecord = new ClientAuthorityApplyRecord();
            clientAuthorityApplyRecord.setBatchNo(req.getBatchNo());
            clientAuthorityApplyRecord.setClientId(client.getId());
            clientAuthorityApplyRecord.setDeptId(bizOrgDO.getId());
            clientAuthorityApplyRecord.setUserId(startUserId);
            clientAuthorityApplyRecord.setLevel(ClientLevelEnum.APPLY.getLevel());
            clientAuthorityApplyRecord.setReason(req.getApplyReason());
            clientAuthorityApplyRecord.setProcessInstanceId(processInstanceId);
            SpringUtil.getBean(ClientAuthorityApplyRecordService.class).save(clientAuthorityApplyRecord);
        } else {
            clientAuthorityApplyRecord.setLevel(ClientLevelEnum.APPLY.getLevel());
            clientAuthorityApplyRecord.setReason(req.getApplyReason());
            clientAuthorityApplyRecord.setProcessInstanceId(processInstanceId);
            applyRecordMapper.updateAnnotationIncludeNullById(clientAuthorityApplyRecord);
        }

        // 批次文件绑定
        if (StringUtils.isNotBlank(req.getBatchNo())) {
            List<ClientFileInfo> clientFileInfoList = fileInfoMapper.selectList(Wrappers.<ClientFileInfo>lambdaQuery()
                    .eq(ClientFileInfo::getBatchNo, req.getBatchNo())
                    .eq(ClientFileInfo::getDeleted, 0));
            if (!clientFileInfoList.isEmpty()) {
                for (ClientFileInfo clientFileInfo : clientFileInfoList) {
                    clientFileInfo.setProcessInstanceId(processInstanceId);
                    fileInfoMapper.updateAnnotationIncludeNullById(clientFileInfo);
                }
            } else {
                ClientFileInfo clientFileInfo = new ClientFileInfo();
                clientFileInfo.setBatchNo(req.getBatchNo());
                clientFileInfo.setProcessInstanceId(processInstanceId);
                clientFileInfo.setClientId(req.getClientId());
                clientFileInfo.setUserId(startUserId);
                fileInfoMapper.insert(clientFileInfo);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void applyModify(ClientApplyModifyREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        if (StringUtils.isBlank(req.getApplyReason())) {
            throw new MithrasException("请填写申请原因");
        }
        //起始人
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        ClientAuthorityApplyRecord clientAuthorityApplyRecord = null;
        if (StringUtils.isNotBlank(req.getBatchNo())) {
            clientAuthorityApplyRecord = applyRecordMapper.selectOne(Wrappers.<ClientAuthorityApplyRecord>lambdaQuery()
                    .eq(ClientAuthorityApplyRecord::getClientId, req.getClientId())
                    .eq(ClientAuthorityApplyRecord::getBatchNo, req.getBatchNo())
                    .eq(ClientAuthorityApplyRecord::getUserId, startUserId)
                    .eq(ClientAuthorityApplyRecord::getDeleted, 0));
        } else if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            ProcessResp processResp = SpringUtil.getBean(FlowTaskApiService.class).queryProcessById(req.getProcessInstanceId());
            if (Objects.isNull(processResp)) {
                throw new MithrasException("流程实例不存在");
            }
            clientAuthorityApplyRecord = applyRecordMapper.selectOne(Wrappers.<ClientAuthorityApplyRecord>lambdaQuery()
                    .eq(ClientAuthorityApplyRecord::getClientId, req.getClientId())
                    .eq(ClientAuthorityApplyRecord::getProcessInstanceId, req.getProcessInstanceId())
                    .eq(ClientAuthorityApplyRecord::getUserId, processResp.getStartUserId())
                    .eq(ClientAuthorityApplyRecord::getDeleted, 0));
        }
        if (clientAuthorityApplyRecord == null) {
            clientAuthorityApplyRecord = new ClientAuthorityApplyRecord();
            clientAuthorityApplyRecord.setBatchNo(req.getBatchNo());
            clientAuthorityApplyRecord.setClientId(client.getId());
            clientAuthorityApplyRecord.setDeptId(bizOrgDO.getId());
            clientAuthorityApplyRecord.setUserId(startUserId);
            clientAuthorityApplyRecord.setLevel(ClientLevelEnum.APPLY.getLevel());
            clientAuthorityApplyRecord.setReason(req.getApplyReason());
            SpringUtil.getBean(ClientAuthorityApplyRecordService.class).save(clientAuthorityApplyRecord);
        } else {
            clientAuthorityApplyRecord.setReason(req.getApplyReason());
            applyRecordMapper.updateAnnotationIncludeNullById(clientAuthorityApplyRecord);
        }
    }


    @Transactional(rollbackFor = Throwable.class)
    public ClientApplyDetailRSP applyDetail(ClientApplyDetailREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        ClientApplyDetailRSP rsp = new ClientApplyDetailRSP();
        if (clientAuthorityUtil.isIntraGroupCollaboration(client.getId())) {
            rsp.setClientId(client.getId());
            rsp.setClientName(client.getClientName());
            rsp.setBelongSponsorName("-");
            rsp.setBelongDeptName("-");
            return rsp;
        }
        Long deptId = 0L;
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(startUserId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        } else {
            throw new MithrasException("用户部门不存在");
        }
        ClientAuthorityApplyRecord clientAuthorityApplyRecord = null;
        if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            clientAuthorityApplyRecord = SpringUtil.getBean(ClientAuthorityApplyRecordService.class).findByProcessInstanceId(req.getProcessInstanceId());
            if (Objects.nonNull(clientAuthorityApplyRecord)) {
                rsp.setApplyReason(clientAuthorityApplyRecord.getReason());
                rsp.setApplyDeptId(clientAuthorityApplyRecord.getDeptId());
                rsp.setApplyDeptName(id2NameService.deptId2NameSingle(clientAuthorityApplyRecord.getDeptId()));
                rsp.setApplyId(clientAuthorityApplyRecord.getUserId());
                rsp.setApplyName(id2NameService.sysUserId2NameSingle(clientAuthorityApplyRecord.getUserId()));
            } else {
                throw new MithrasException("权限申请记录不存在");
            }
        } else if (StringUtils.isNotBlank(req.getBatchNo())) {
            clientAuthorityApplyRecord = applyRecordMapper.selectOne(Wrappers.<ClientAuthorityApplyRecord>lambdaQuery()
                    .eq(ClientAuthorityApplyRecord::getClientId, req.getClientId())
                    .eq(ClientAuthorityApplyRecord::getBatchNo, req.getBatchNo())
                    .eq(ClientAuthorityApplyRecord::getUserId, startUserId)
                    .eq(ClientAuthorityApplyRecord::getDeleted, 0));
            if (Objects.nonNull(clientAuthorityApplyRecord)) {
                rsp.setApplyReason(clientAuthorityApplyRecord.getReason());
                rsp.setApplyDeptId(clientAuthorityApplyRecord.getDeptId());
                rsp.setApplyDeptName(id2NameService.deptId2NameSingle(clientAuthorityApplyRecord.getDeptId()));
                rsp.setApplyId(clientAuthorityApplyRecord.getUserId());
                rsp.setApplyName(id2NameService.sysUserId2NameSingle(clientAuthorityApplyRecord.getUserId()));
            } else {
                rsp.setApplyId(startUserId);
                rsp.setApplyName(id2NameService.sysUserId2NameSingle(startUserId));
                rsp.setApplyDeptId(deptId);
                rsp.setApplyDeptName(id2NameService.deptId2NameSingle(deptId));
            }
        } else {
            rsp.setApplyId(startUserId);
            rsp.setApplyName(id2NameService.sysUserId2NameSingle(startUserId));
            rsp.setApplyDeptId(deptId);
            rsp.setApplyDeptName(id2NameService.deptId2NameSingle(deptId));
        }
        rsp.setClientName(client.getClientName());
        rsp.setClientId(client.getId());
        rsp.setAuthorityLevel(ClientLevelEnum.APPLY.name());
        rsp.setBelongDeptId(client.getBelongDeptId());
        rsp.setBelongDeptName(id2NameService.deptId2NameSingle(client.getBelongDeptId()));
        rsp.setBelongSponsorId(client.getBelongSponsorId());
        rsp.setBelongSponsorName(id2NameService.sysUserId2NameSingle(client.getBelongSponsorId()));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void applyValidate(ClientApplyDetailREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        if (clientAuthorityUtil.isIntraGroupCollaboration(client.getId())) {
            throw new MithrasException("所选择客户为公海客户，无需申请相关权限!");
        }
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, req.getClientId())
                .eq(ClientAuthority::getUserId, Optional.ofNullable(AccountUtil.getLoginInfo())
                        .map(AccountVO::getId)
                        .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)))
                .eq(ClientAuthority::getDeleted, 0));
        if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
            clientAuthorityList.forEach(e -> {
                if (ClientLevelEnum.APPLY.getLevel() == e.getLevel()) {
                    throw new MithrasException("您已拥有该客户的申办权限，无需再次申请!");
                } else if (ClientLevelEnum.MANAGE.getLevel() == e.getLevel()) {
                    throw new MithrasException("您已拥有该客户的管护权限，不能申请申办权!");
                }
            });
        }
    }

    //    @Transactional(rollbackFor = Throwable.class)
    public ClientOwnApplyDetailRSP applyOwn(ClientOwnApplyDetailREQ req) {
        ClientOwnApplyDetailRSP rsp = new ClientOwnApplyDetailRSP();
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        // 非项目经理不需要编辑
        if (!sysUserService.userIsSpecificJob(userId, JobEnum.projmanager.name())) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        // 流程详情页进入
        if (StrUtil.isNotBlank(req.getProcessInstanceId())) {
            ProcessResp processResp = SpringUtil.getBean(FlowTaskApiService.class).queryProcessById(req.getProcessInstanceId());
            if (Objects.equals(processResp.getProcessStatus(), ProcessBusinessStatusEnum.RUNNING.getType())) {
                boolean startUserNode = Objects.equals(processResp.getCurTaskActivityIds(), FlowConstants.START_USER_TASK);
                if (startUserNode) {
                    // 发起人节点需要进一步判断客户状态
                    if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                        // 生效客户需判断权限
                        rsp.setCanEdit(clientAuthorityService.currentUserHasManagerAuth(client.getId()));
                    } else {
                        rsp.setCanEdit(true);
                    }
                } else {
                    rsp.setCanEdit(false);
                    rsp.setShowCommerceInfo(false);
                    return rsp;
                }
            } else {
                // 流程结束不允许编辑
                rsp.setCanEdit(false);
                rsp.setShowCommerceInfo(false);
                return rsp;
            }
        }
        // 客户详情页进入
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(client.getId().toString());
        processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType()));
        processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.ClientAuthorityCreateFlow.name(), ProcessModelTypeEnum.ClientAuthorityModifyFlow.name(), ProcessModelTypeEnum.ClientModifyFlow.name()));
        processPageReq.setStartUserId(userId.toString());
        Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
        if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            ProcessResp processResp = processRespPage.getContents().get(0);
            // 判断是否发起人节点
            boolean startUserNode = Objects.equals(processResp.getCurTaskActivityIds(), FlowConstants.START_USER_TASK);
            if (startUserNode) {
                // 发起人节点需要进一步判断客户状态
                if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                    // 生效客户需判断权限
                    rsp.setCanEdit(clientAuthorityService.currentUserHasManagerAuth(client.getId()));
                } else {
                    rsp.setCanEdit(true);
                }
            } else {
                rsp.setCanEdit(false);
            }
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        // 非流程走后续逻辑
        // 自然人放开
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            rsp.setCanEdit(true);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        // 公海客户放开
        if (clientAuthorityUtil.isIntraGroupCollaboration(client.getId())) {
            // 尝试初始化一份数据
            clientService.tryInitNewClientInfoOrIgnore(client.getId(), userId, null);
            rsp.setCanEdit(true);
            rsp.setShowCommerceInfo(false);
            return rsp;
        } else {
            clientService.tryInitNewClientInfoOrIgnore(client.getId(), userId, ListUtil.of(InfoModule.CORP_COMMERCE, InfoModule.CORP_ADDRESS, InfoModule.CORP_BOND, InfoModule.CORP_SHAREHOLDER, InfoModule.CORP_RELATED_ENTERPRISE));
        }
        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, req.getClientId())
                .eq(ClientAuthority::getUserId, userId)
                .orderByDesc(ClientAuthority::getLevel)
                .last(StringUtil.mysqlLimitOne())
        );
        boolean isToCosponsorIds = false;
        List<ClientTransfer> inProcessClient = clientTransferMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                .eq(ClientTransfer::getClientId, req.getClientId())
                .in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name())
        );
        if (inProcessClient != null && !inProcessClient.isEmpty()) {
            for (ClientTransfer clientTransfer : inProcessClient) {
                if (clientTransfer.getToCosponsorIds() != null
                        && !clientTransfer.getToCosponsorIds().isEmpty()
                        && clientTransfer.getToCosponsorIds().contains(userId)) {
                    isToCosponsorIds = true;
                }
            }
        }
        if (clientAuthorityUtil.isNewClient(client)) {
            // 新建非释放客户，需要有关联才能编辑
            int count = SpringUtil.getBean(ClientUserRefService.class).countByClientUser(client.getId(), userId);
            rsp.setCanEdit(count > 0);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (clientAuthorityUtil.isReleasedClient(client)) {
            // 释放客户
            rsp.setCanEdit(true);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (Objects.isNull(clientAuthority)) {
            // 查一下管护权
            ClientAuthority managerAuth = clientAuthorityService.getSpecificClientManagerAuthority(client.getId());
            if (Objects.isNull(managerAuth)) {
                throw new MithrasException("管护权不存在");
            }
            // 判断是否同部门
            OrgDO org = sysUserService.getBizDeptByUserId(userId);
            if (Objects.nonNull(org) && Objects.equals(org.getId(), managerAuth.getDeptId())) {
                rsp.setCanEdit(false);
                rsp.setShowCommerceInfo(true);
                return rsp;
            } else {
                throw new MithrasException("无权查看");
            }
        }
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                && clientAuthority != null
                && ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
            rsp.setCanEdit(true);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                && clientAuthority != null
                && ClientLevelEnum.APPLY.getLevel() == clientAuthority.getLevel()) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                && clientAuthority != null
                && ClientLevelEnum.VIEW.getLevel() == clientAuthority.getLevel()) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (clientAuthorityUtil.isReleasedClient(client) && !isToCosponsorIds) {
            rsp.setCanEdit(true);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        if (clientAuthorityUtil.isReleasedClient(client) && isToCosponsorIds) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(true);
            return rsp;
        }
        // 为了兼容即是项目经理又是负责人的情况
        if (sysUserService.userIsSpecificJob(userId, businesshead.name())) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        // 为了兼容即是项目经理又是分管领导的情况
        if (sysUserService.userIsSpecificJob(userId, leaderincharge.name())) {
            rsp.setCanEdit(false);
            rsp.setShowCommerceInfo(false);
            return rsp;
        }
        rsp.setShowCommerceInfo(true);
        rsp.setCanEdit(false);
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public ClientApplyStatusRSP status(ClientApplyStatusREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, req.getClientId())
                .eq(ClientAuthority::getUserId, userId)
                .eq(ClientAuthority::getDeleted, 0));
        int level = isNull(clientAuthority) ? 0 : clientAuthority.getLevel();
        ClientApplyStatusRSP rsp = new ClientApplyStatusRSP();
        rsp.setClientStatus(client.getClientStatus());
        rsp.setAuthorityLevel(level);
        return rsp;
    }

    public Page<ProcessResp> getClientAuthorityApplyProcessByIds(List<String> businessKeys, String processModelType, Long startUserId) {
        ProcessPageReq processReq = new ProcessPageReq();
        processReq.setModelKeyList(Collections.singletonList(processModelType));
        processReq.setBusinessKeyList(businessKeys);
        processReq.setPageIndex(1);
        processReq.setStartUserId(String.valueOf(startUserId));
        processReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        processReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processReq);
        return processRespPage;
    }

    @Override
    public void processEnd(Long clientId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        Client client = clientMapper.selectById(clientId);
        if (isNull(client)) {
            throw new MithrasException("客户为空");
        }
        // 寻找权限申请记录
        ClientAuthorityApplyRecord clientAuthorityApplyRecord = SpringUtil.getBean(ClientAuthorityApplyRecordService.class).findByProcessInstanceId(processInstanceId);
        if (Objects.isNull(clientAuthorityApplyRecord)) {
            throw new MithrasException("权限申请记录不存在");
        }
        if (processPass) {
            // 赋予权限
            ClientAuthority clientAuthority = new ClientAuthority();
            clientAuthority.setClientId(clientAuthorityApplyRecord.getClientId());
            clientAuthority.setUserId(clientAuthorityApplyRecord.getUserId());
            clientAuthority.setDeptId(clientAuthorityApplyRecord.getDeptId());
            clientAuthority.setLevel(clientAuthorityApplyRecord.getLevel());
            clientAuthority.setSourceBusinessType("CLIENT_AUTHORITY_APPLY");
            clientAuthority.setSourceId(clientAuthorityApplyRecord.getBatchNo());
            clientAuthorityService.save(clientAuthority);
        }
    }
}
