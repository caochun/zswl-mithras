package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientApplyOccupyREQ;
import cn.zswltech.mithras.dto.client.client.ClientApplyOccupyRSP;
import cn.zswltech.mithras.dto.client.client.ClientApplyStatusREQ;
import cn.zswltech.mithras.dto.client.client.ClientApplyStatusRSP;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.share.DataShareRegisterCustomREQ;
import cn.zswltech.mithras.dto.file.*;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.FileListProviderFactory;
import cn.zswltech.mithras.service.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.service.mapper.client.ClientFileInfoMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.service.service.bo.UserOrgJobInfoBO;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.batchdownload.AbstractFileBatchDownload;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.share.DataShareService;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.WatermarkUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.JobEnum.projmanager;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
@Slf4j
public class ClientAuthorityService extends ServiceImpl<ClientAuthorityMapper, ClientAuthority>  implements FlowEndEventProcessor {

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
    private FlowTaskApiService taskApiService;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private List<FileModuleCheck> fileModuleChecks;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private FileListProviderFactory fileListProviderFactory;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private ClientFileInfoMapper fileInfoMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityUtil authorityUtil;
    @Resource
    private ClientAuthorityApplyRecordService clientAuthorityApplyRecordService;

    public boolean currentUserHasManagerAuth(Long clientId) {
        // 判断客户类型
        Client client = clientService.getById(clientId);
        if (Objects.isNull(client)) {
            throw new MithrasException("客户信息不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            // 自然人都有权限
            return true;
        }
        if (authorityUtil.isIntraGroupCollaboration(clientId)) {
            // 公海客户都有权限
            return true;
        }
        // 非公海的法人客户需要判断管护权
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
        if (Objects.isNull(currentUserId)) {
            throw new MithrasException("获取当前登陆用户信息失败");
        }
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getClientId, clientId);
        query.eq(ClientAuthority::getUserId, currentUserId);
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        return this.count(query) > 0;
    }

    public ClientAuthority getSpecificClientManagerAuthority(Long clientId) {
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getClientId, clientId);
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        List<ClientAuthority> list = this.list(query);
        if (CollectionUtil.isEmpty(list)){
            return null;
        }
        if (list.size() > 1) {
            log.error("客户存在多个管护权数据[clientId:{}]", clientId);
            throw new MithrasException("客户管护权数据异常");
        }
        return list.get(0);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long clientId, String opinion) {
        Client client = clientMapper.selectById(clientId);
        if (isNull(client)) {
            throw new MithrasException("客户为空");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        // 先抄一份数据，确保安全
        authorityUtil.copyFromNewToOld(ClientCopyInfoBO.builder().clientId(clientId).currentUserId(AccountUtil.getLoginInfo().getId()).build());
        // 校验数据
        clientVersionService.validateData(clientId);
        // 公海客户特殊处理
        if (authorityUtil.isIntraGroupCollaboration(clientId)) {
            // 公海客户无需审批，直接生效
            Client updateClient = new Client();
            updateClient.setId(clientId);
            updateClient.setClientStatus(ClientStatus.TAKE_EFFECT.name());
            updateClient.setBelongSponsorId(null);
            updateClient.setBelongDeptId(null);
            // 获取集团客户编号
            if (StrUtil.isBlank(client.getClientCode())) {
                String clientCode = this.createDataShareMerchant(client, startUserId);
                if (StrUtil.isNotBlank(clientCode)) {
                    updateClient.setClientCode(clientCode);
                }
            }
            clientMapper.updateAnnotationIncludeNullById(updateClient);
            // 生成版本
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put("userId", startUserId);
            clientVersionService.recordVersion(clientId, VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL, extraMap);
        } else {
            // 非公海走后续逻辑
            String processModelType;
            StartProcessReq startProcessReq = new StartProcessReq();
            // 判断使用创建流程还是修改流程
            if (authorityUtil.isNewClient(client)) {
                processModelType = ProcessModelTypeEnum.ClientAuthorityCreateFlow.name();
                startProcessReq.setModelKey(processModelType);
            } else if (authorityUtil.isReleasedClient(client)) {
                processModelType = ProcessModelTypeEnum.ClientAuthorityModifyFlow.name();
                startProcessReq.setModelKey(processModelType);
            } else {
                throw new MithrasException("客户状态不为新建或者释放");
            }
            List<String> businessKeys = new ArrayList<>();
            businessKeys.add(String.valueOf(clientId));
            Page<ProcessResp> flowRespPage = getClientAuthorityProcessByIds(businessKeys, processModelType, startUserId);
            if (flowRespPage != null && !flowRespPage.getContents().isEmpty()) {
                throw new MithrasException(String.format("项目经理%s已经提交权限创建/变更流程", id2NameService.sysUserId2NameSingle(startUserId)));
            }
            SpringContextHolder.getBean(ClientService.class).checkClientOccupy(clientId);
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                    .eq(ClientAuthority::getClientId, clientId)
                    .eq(ClientAuthority::getDeleted, 0));
            if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
                for (ClientAuthority clientAuthority : clientAuthorityList) {
                    if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
                        String sponsorUserName = id2NameService.sysUserId2NameSingle(clientAuthority.getUserId());
                        throw new MithrasException(String.format("项目经理%s已经拥有该客户管护权", sponsorUserName));
                    }
                }
            }
            //起始人
            OrgDO bizOrgDO = sysUserService.currentUserBizDept();
            if (isNull(bizOrgDO)) {
                throw new MithrasException(ONLY_BIZ_DEPT_DO);
            }
            //获取业务部门负责人
            Long originDeptLeader = sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name());
            startProcessReq.setStartUserId(String.valueOf(startUserId));
            //增加运营部领导
            Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader("YYGLB", FlowAssistService.YYGLB_DESC, businesshead.name());
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("bizDeptLeader", Objects.nonNull(originDeptLeader) ?
                    ListUtil.toList(String.valueOf(originDeptLeader)) : new ArrayList<>());
            varMap.put("yyglbDeptLeader", Objects.nonNull(yyglbDeptLeader) ?
                    ListUtil.toList(String.valueOf(yyglbDeptLeader)) : new ArrayList<>());
            startProcessReq.setVariables(varMap);

            startProcessReq.setBusinessKey(String.valueOf(clientId));
            startProcessReq.setSubModule(client.getClientType());
            startProcessReq.setProcessInstanceName(client.getClientName());
            startProcessReq.setStartUserDeptId(Optional.ofNullable(bizOrgDO.getId()).map(String::valueOf).orElse(null));
            if(StringUtils.isBlank(opinion)){
                opinion = CommentTypeEnum.QD.getMessage();
            }
            String processInstanceId = processApiService.start(startProcessReq, opinion);
            // 保存权限申请记录
            ClientAuthorityApplyRecord clientAuthorityApplyRecord = new ClientAuthorityApplyRecord();
            clientAuthorityApplyRecord.setBatchNo(clientService.getBatchNumber());
            clientAuthorityApplyRecord.setClientId(clientId);
            clientAuthorityApplyRecord.setDeptId(bizOrgDO.getId());
            clientAuthorityApplyRecord.setUserId(startUserId);
            clientAuthorityApplyRecord.setReason("用户主动申请");
            clientAuthorityApplyRecord.setLevel(ClientLevelEnum.MANAGE.getLevel());
            clientAuthorityApplyRecord.setProcessInstanceId(processInstanceId);
            SpringUtil.getBean(ClientAuthorityApplyRecordService.class).save(clientAuthorityApplyRecord);
            bizProcessDataService.recordBizData(processInstanceId, clientId);
            clientService.recordClientStatus(clientId, null, ClientProcessStatus.EFFECT_BLANK);
        }
    }

    @Transactional
    @Override
    public void processEnd(Long clientId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        Client client = clientMapper.selectById(clientId);
        if (isNull(client)) {
            throw new MithrasException("客户为空");
        }
        // 找到权限申请记录
        ClientAuthorityApplyRecord clientAuthorityApplyRecord = clientAuthorityApplyRecordService.findByProcessInstanceId(processInstanceId);
        if (Objects.isNull(clientAuthorityApplyRecord)) {
            throw new MithrasException("没有找到对应流程的权限申请记录");
        }
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(clientId)
                .currentUserId(startUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
        if (processPass) {
            // 赋予用户权限
            ClientAuthority clientAuthority = new ClientAuthority();
            clientAuthority.setClientId(clientAuthorityApplyRecord.getClientId());
            clientAuthority.setUserId(clientAuthorityApplyRecord.getUserId());
            clientAuthority.setLevel(clientAuthorityApplyRecord.getLevel());
            clientAuthority.setDeptId(clientAuthorityApplyRecord.getDeptId());
            clientAuthority.setSourceBusinessType(BusinessModuleEnum.CLIENT.name());
            clientAuthority.setSourceId(clientId.toString());
            clientAuthorityMapper.insert(clientAuthority);
            // 变更客户数据
            client.setClientStatus(ClientStatus.TAKE_EFFECT.name());
            client.setProcessStatus(ClientProcessStatus.EFFECT_BLANK.name());
            client.setBelongSponsorId(clientAuthorityApplyRecord.getUserId());
            client.setBelongDeptId(clientAuthorityApplyRecord.getDeptId());
            // 获取集团客户编号
            if (StrUtil.isBlank(client.getClientCode())) {
                ProcessPageReq flowReq = new ProcessPageReq();
                flowReq.setProcessInstanceIdList(Collections.singletonList(processInstanceId));
                flowReq.setSortType(1);
                Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
                //传入创建人
                String clientCode = this.createDataShareMerchant(client, ObjectUtil.isEmpty(flowRespPage.getContents()) ? null : Long.valueOf(flowRespPage.getContents().get(0).getStartUserId()));
                if (StrUtil.isNotBlank(clientCode)) {
                    client.setClientCode(clientCode);
                }
            }
            clientMapper.updateById(client);
            // 生成版本数据
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put("userId", startUserId);
            clientVersionService.recordVersion(clientId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL, extraMap);
        } else {
            // 生成无效版本
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put("userId", startUserId);
            clientVersionService.recordVersion(clientId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID, extraMap);
            // 编辑区需要回退到上一个有效版本
            clientVersionService.reset(clientId);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public ClientApplyStatusRSP status(ClientApplyStatusREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        ClientApplyStatusRSP rsp = new ClientApplyStatusRSP();
        if (SpringUtil.getBean(ClientAuthorityUtil.class).isIntraGroupCollaboration(client.getId())) {
            rsp.setClientStatus(client.getClientStatus());
            // 公海客户任何人进入都默认最高权限
            rsp.setAuthorityLevel(ClientLevelEnum.MANAGE.getLevel());
            return rsp;
        }
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, req.getClientId())
                .eq(ClientAuthority::getUserId, userId)
                .eq(ClientAuthority::getDeleted, 0)
                .orderByDesc(ClientAuthority::getLevel)
                .last(StringUtil.mysqlLimitOne())
        );
        int level = isNull(clientAuthority) ? 0 : clientAuthority.getLevel();
        rsp.setClientStatus(client.getClientStatus());
        rsp.setAuthorityLevel(level);
        return rsp;
    }


    @Transactional(rollbackFor = Throwable.class)
    public ClientApplyOccupyRSP checkOccupy(ClientApplyOccupyREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        ClientApplyOccupyRSP rsp = new ClientApplyOccupyRSP();
        if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            return rsp;
        }
        //中后台可以有全部查看权限
        if (!sysUserService.currentUserIsBizDept()) {
            return rsp;
        }
        // 公海、新建、释放不校验权限
        if (authorityUtil.isNewClient(client)
                || authorityUtil.isReleasedClient(client)
                || authorityUtil.isIntraGroupCollaboration(client.getId())) {
            return rsp;
        }
        // 自然人不校验
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            return rsp;
        }
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        // 获取当前登陆用户岗位及其所属部门
        UserOrgJobInfoBO userOrgJobInfoBO = sysUserService.getUserOrgJobInfo(userId);
        if (CollectionUtil.isNotEmpty(userOrgJobInfoBO.getOrgJobMap().get(JobEnum.leaderincharge.name()))) {
            // 说明是分管领导，取出分管部门的部门id，判断部门下是否有人存在权限，有的话就允许进入详情页
            List<Long> deptIds = userOrgJobInfoBO.getOrgJobMap().get(JobEnum.leaderincharge.name()).stream().map(OrgDO::getId).collect(Collectors.toList());
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(
                    Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .in(ClientAuthority::getLevel, ListUtil.of(ClientLevelEnum.MANAGE.getLevel(), ClientLevelEnum.APPLY.getLevel(), ClientLevelEnum.VIEW.getLevel()))
                            .in(ClientAuthority::getDeptId, deptIds)
            );
            if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
                return rsp;
            }
        }
        if (CollectionUtil.isNotEmpty(userOrgJobInfoBO.getOrgJobMap().get(businesshead.name()))) {
            // 说明是业务负责人，取出负责的部门id，判断部门下是否有人存在权限，有的话就允许进入详情页
            List<Long> deptIds = userOrgJobInfoBO.getOrgJobMap().get(JobEnum.businesshead.name()).stream().map(OrgDO::getId).collect(Collectors.toList());
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(
                    Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .in(ClientAuthority::getLevel, ListUtil.of(ClientLevelEnum.MANAGE.getLevel(), ClientLevelEnum.APPLY.getLevel(), ClientLevelEnum.VIEW.getLevel()))
                            .in(ClientAuthority::getDeptId, deptIds)
            );
            if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
                return rsp;
            }
        }
        ClientAuthority managerAuthority = null;
        if (CollectionUtil.isNotEmpty(userOrgJobInfoBO.getOrgJobMap().get(projmanager.name()))) {
            // 说明是项目经理
            // 当前用户是否有客户的权限（只要有一个就允许进入详情页）
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                    .eq(ClientAuthority::getClientId, req.getClientId())
                    .in(ClientAuthority::getLevel, ListUtil.of(ClientLevelEnum.MANAGE.getLevel(), ClientLevelEnum.APPLY.getLevel(), ClientLevelEnum.VIEW.getLevel()))
                    .eq(ClientAuthority::getUserId, userId)
                    .eq(ClientAuthority::getDeleted, 0));
            if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
                return rsp;
            }
            // 没有任何权限的话判断当前登陆用户和管控权用户是否同部门
            Long deptId = 0L;
            List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(userId);
            Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
            if (first.isPresent()) {
                //首选业务部门
                deptId = first.get().getId();
            } else if (!deptList.isEmpty()) {
                deptId = deptList.get(0).getId();
            } else {
                throw new MithrasException("用户部门不存在");
            }
            managerAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(client.getId());
            if (Objects.nonNull(managerAuthority) && Objects.equals(managerAuthority.getDeptId(), deptId)) {
                return rsp;
            }
        }
        // 走到这说明当前用户不能看
        if (Objects.nonNull(managerAuthority)) {
            rsp.setMessage(String.format("该客户%s已被%s-%s占用，当前无查看权限", client.getClientName(),
                    id2NameService.deptId2NameSingle(managerAuthority.getDeptId()), id2NameService.sysUserId2NameSingle(managerAuthority.getUserId())));
        } else {
            // 数据有问题？
            rsp.setMessage("当前无权限查看");
        }
        return rsp;
    }

    public Page<ProcessResp> getClientAuthorityProcessByIds(List<String> businessKeys, String processModelType, Long startUserId) {
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

    @Transactional(rollbackFor = Throwable.class)
    public FileUploadRSP upload(FileUploadREQ fileUploadREQ) {
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(fileUploadREQ.getModuleType());
        //自定义权限校验
        if (StringUtils.isNotBlank(fileUploadREQ.getModuleType())) {
            BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(fileUploadREQ.getModuleType())).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
            dataAuthProcessRule.check(moduleEnum, fileUploadREQ.getMainId());
        }

        try {
            InputStream inputStream = fileUploadREQ.getFile().getInputStream();
            if (YesOrNoNumberEnum.YES.getCode().equals(fileUploadREQ.getNeedWatermark())) {
                try {
                    assert fileCheck != null;
                    Pair<String, InputStream> watermark = WatermarkUtil.watermark(inputStream, fileCheck.getWatermarkSting(), fileUploadREQ.getFile().getOriginalFilename());
                    if (watermark != null) {
//                        throw new MithrasException("生成水印失败, 未知文件类型或无文本内容");
                        inputStream = watermark.getValue();
                    }
                } catch (Exception e) {
                    log.error("文件加水印失败", e);
//                   throw new MithrasException("文件加水印失败");
                }
            }
            Long fileId = materialsListService.add(inputStream, fileUploadREQ.getFile().getOriginalFilename(), fileUploadREQ.getMainId()
                    , fileUploadREQ.getMaterialsType(), fileUploadREQ.getMaterialsSubType(), fileUploadREQ.getModuleType());
            if (fileCheck != null) {
                fileCheck.afterUploadHandle(fileUploadREQ.getModuleType(), fileUploadREQ.getMainId(), fileId, fileUploadREQ.getSourceBusinessKey(), fileUploadREQ.getUserId());
            }
            if (StringUtils.isNotBlank(fileUploadREQ.getBatchNo())) {
                ClientFileInfo clientFileInfo = new ClientFileInfo();
                clientFileInfo.setClientId(fileUploadREQ.getMainId());
                clientFileInfo.setBatchNo(fileUploadREQ.getBatchNo());
                clientFileInfo.setFileId(fileId);
                clientFileInfo.setUserId(AccountUtil.getLoginInfo().getId());
                fileInfoMapper.insert(clientFileInfo);
            } else if (StringUtils.isNotBlank(fileUploadREQ.getProcessInstanceId())) {
                ProcessResp processResp = SpringUtil.getBean(FlowTaskApiService.class).queryProcessById(fileUploadREQ.getProcessInstanceId());
                if (Objects.isNull(processResp)) {
                    throw new MithrasException("流程实例不存在");
                }
                List<ClientFileInfo> clientFileInfoList = fileInfoMapper.selectList(Wrappers.<ClientFileInfo>lambdaQuery()
                        .eq(ClientFileInfo::getProcessInstanceId, fileUploadREQ.getProcessInstanceId())
                        .eq(ClientFileInfo::getDeleted, 0));
                String batchNo = null;
                if (clientFileInfoList != null && !clientFileInfoList.isEmpty()) {
                    batchNo = clientFileInfoList.get(0).getBatchNo();
                }
                ClientFileInfo clientFileInfo = new ClientFileInfo();
                clientFileInfo.setClientId(fileUploadREQ.getMainId());
                clientFileInfo.setBatchNo(batchNo);
                clientFileInfo.setFileId(fileId);
                clientFileInfo.setProcessInstanceId(fileUploadREQ.getProcessInstanceId());
                clientFileInfo.setUserId(Long.valueOf(processResp.getStartUserId()));
                fileInfoMapper.insert(clientFileInfo);
            }
            FileUploadRSP rsp = new FileUploadRSP();
            rsp.setFileId(fileId);
            rsp.setMainId(fileUploadREQ.getMainId());
            rsp.setModuleType(fileUploadREQ.getModuleType());
            rsp.setMaterialsType(fileUploadREQ.getMaterialsType());
            rsp.setMaterialsSubType(fileUploadREQ.getMaterialsSubType());
            return rsp;
        } catch (IOException e) {
            log.warn("FileService upload error ", e);
            throw new MithrasException("上传文件异常");
        }
    }


    public PageR<FileListRSP> list(ClientAuthorityFileListREQ req) {
        AbstractFileListProvider fileListProvider = fileListProviderFactory.getProvider(req.getModuleType());
        if (Objects.isNull(fileListProvider)) {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            List<ClientFileInfo> clientFileInfos = fileInfoMapper.selectList(Wrappers.<ClientFileInfo>lambdaQuery()
                    .eq(ClientFileInfo::getProcessInstanceId, req.getProcessInstanceId())
                    .eq(ClientFileInfo::getDeleted, 0));
            if (!clientFileInfos.isEmpty()) {
                Map<String, Object> ext = new HashMap<>();
                List<Long> fileIdList = clientFileInfos.stream().map(ClientFileInfo::getFileId).collect(Collectors.toList());
                ext.putIfAbsent("fileId", fileIdList);
                req.setExt(ext);
                return fileListProvider.list(req);
            }
        } else if (StrUtil.isNotBlank(req.getBatchNo())) {
            List<ClientFileInfo> clientFileInfos = fileInfoMapper.selectList(Wrappers.<ClientFileInfo>lambdaQuery()
                    .eq(ClientFileInfo::getBatchNo, req.getBatchNo())
            );
            if (!clientFileInfos.isEmpty()) {
                Map<String, Object> ext = new HashMap<>();
                List<Long> fileIdList = clientFileInfos.stream().map(ClientFileInfo::getFileId).collect(Collectors.toList());
                ext.putIfAbsent("fileId", fileIdList);
                req.setExt(ext);
                return fileListProvider.list(req);
            }
        }
        return PageR.empty(req.getPage(), req.getPageSize());
    }


    public FileDownLoadRSP download(FileDownLoadREQ req) {
        if (ObjectUtil.equal(VERSIONED, req.getIdType())) {
            return materialsListService.downloadLib(req.getFileId());
        }
        // 先查询文档信息
        materialsListService.getEntityWithCheck(req.getFileId(), req.getVersion());
        //各模块检查
        /*FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkDownload(req.getModuleType(), req.getMainId(), Collections.singletonList(req.getFileId()));
        }*/
        if (BusinessModuleEnum.ARCHIVES.name().equals(req.getModuleType())) {
            String watermark = null;
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            Response<UserVO> userInfoById = userService.getUserInfoById(loginInfo.getId());
            if (userInfoById.isSuccess()) {
                UserVO data = userInfoById.getData();
                String realPhone = userService.getRealPhone(loginInfo.getId());
                String s = StrUtil.isNotEmpty(realPhone) ? realPhone.substring(realPhone.length() - 4) : "";
                watermark = "浙商租赁" + data.getUserName() + s;
            }
            return materialsListService.download(req.getFileId(), req.getVersion(), watermark);
        }
        return materialsListService.download(req.getFileId(), req.getVersion());
    }


    public void batchRemove(FileBatchRemoveREQ req) {
        // 先查询文档信息
        if (ObjectUtil.isEmpty(req.getFileIds())) {
            return;
        }
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            //fileCheck.checkRemove(req.getModuleType(), req.getFileIds());
            if (StringUtils.isNotBlank(req.getModuleType())) {
                BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(req.getModuleType())).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
                Set<Long> belongSet = materialsListService.getByIds(req.getFileIds()).stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
                for(Long mainId : belongSet){
                    dataAuthProcessRule.check(moduleEnum, mainId);
                }
            }
        }
        // 决议文件同步删除
        fileService.resolutionHandler(req);
        //章程文件同步删除
        if (BusinessModuleEnum.CONTRACT.name().equals(req.getModuleType())) {
            fileService.contractConstitutionFile(req);
        }
        // 先查询一下，给后置处理器用
        List<MaterialsList> fileList = materialsListService.listByIds(req.getFileIds());
        materialsListService.remove(req.getFileIds(), req.getMainId());
        //删除后处理
        if (fileCheck != null) {
            fileCheck.afterRemoveHandle(req.getMainId(), fileList, null);
        }
    }


    public void batchDownload(FileBatchDownLoadREQ req) {
        if (!ObjectUtil.equal(VERSIONED, req.getIdType())) {
            //各模块检查
            /*FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
            if (fileCheck != null) {
                fileCheck.checkDownload(req.getModuleType(), req.getMainId(), req.getFileId());
            }*/
        }
        AbstractFileBatchDownload fileBatchDownload = fileService.getFileBatchDownload(req.getModuleType());
        if (fileBatchDownload != null) {
            fileBatchDownload.batchDownload(req);
        }
        //下载
    }


    private FileModuleCheck getFileCheck(String moduleKey) {
        FileModuleCheck fileModuleCheckRSP = null;
        for (FileModuleCheck fileModuleCheck : fileModuleChecks) {
            if (fileModuleCheck.isCheck(moduleKey)) {
                return fileModuleCheck;
            }
            if (ObjectUtil.isNull(fileModuleCheckRSP) && ObjectUtil.equals(fileModuleCheck.getModuleKey(), BusinessModuleEnum.DEFAULT.name())) {
                fileModuleCheckRSP = fileModuleCheck;
            }
        }
        return fileModuleCheckRSP;
    }

    private void updateClientStatus(Client client) {
        client.setClientStatus(ClientStatus.TAKE_EFFECT.name());
        client.setBelongSponsorId(null);
        client.setBelongDeptId(null);
        clientMapper.updateAnnotationIncludeNullById(client);
    }

    private String createDataShareMerchant(Client client, Long createBy) {
        //去集团创建客户
        DataShareRegisterCustomREQ dataShareRegisterCustomREQ = null;
        try {
            dataShareRegisterCustomREQ = buildRegisterCustom(client.getId());
            return SpringUtil.getBean(DataShareService.class).registerCustom(dataShareRegisterCustomREQ);
        } catch (Exception e) {
            log.error("集团创建客户失败， {}", dataShareRegisterCustomREQ, e);
            sendClientNewMessage(client, createBy);
            return null;
        }
    }

    //构建工商信息
    private DataShareRegisterCustomREQ buildRegisterCustom(Long clientId) {
        DataShareRegisterCustomREQ dataShareRegisterCustomREQ = new DataShareRegisterCustomREQ();
        DataShareRegisterCustomREQ.BaseInfo baseInfo = dataShareRegisterCustomREQ.new BaseInfo();
        DataShareRegisterCustomREQ.BusinessInfo businessInfo = dataShareRegisterCustomREQ.new BusinessInfo();
        dataShareRegisterCustomREQ.setBaseInfo(baseInfo);
        dataShareRegisterCustomREQ.setBusinessInfo(businessInfo);
        Client client = clientMapper.selectById(clientId);
        CorpCommerceInfo commerceInfo = SpringUtil.getBean(CorpCommerceInfoMapper.class).selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId).last(StringUtil.mysqlLimitOne()));
        CorpAddressInfo corpAddressInfo = SpringUtil.getBean(CorpAddressInfoMapper.class).selectOne(Wrappers.<CorpAddressInfo>lambdaQuery().eq(CorpAddressInfo::getClientId, clientId).orderByDesc(CorpAddressInfo::getAddressType).last(StringUtil.mysqlLimitOne()));
        CorpAddressInfo registryAddressInfo = SpringUtil.getBean(CorpAddressInfoMapper.class).selectOne(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(CorpAddressInfo::getClientId, clientId)
                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
                .orderByDesc(CorpAddressInfo::getAddressType)
                .last(StringUtil.mysqlLimitOne()));
        CorpContactInfoListREQ req = new CorpContactInfoListREQ();
        req.setClientId(clientId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CorpContactInfo> pageContactInfo = SpringUtil.getBean(CorpContactInfoService.class).list(req);
        baseInfo.setMerchantName(client.getClientName());
        if (ClientType.CORPORATION.name().equals(client.getClientType())) {
            baseInfo.setMerchantType("QIYE");
            baseInfo.setCreditCode(client.getUscCode());
        } else {
            baseInfo.setMerchantType("GEREN");
            baseInfo.setIdentificationNumber(client.getCertNumber());
        }
        //填充地址
        if (ObjectUtil.isNotEmpty(corpAddressInfo)) {
            //目前只有中国
            List<String> addresses = new ArrayList<>();
            addresses.add("CN");
            addresses.add(corpAddressInfo.getProvince());
            addresses.add(corpAddressInfo.getCity());
            baseInfo.setAreaCodeList(addresses);
        }
        //工商信息
        businessInfo.setRegCapital(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(commerceInfo.getRegisterCapital()))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        businessInfo.setRegCapitalCurrencyType(commerceInfo.getRegisterCurrencyType());
        businessInfo.setActualCapital(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(commerceInfo.getRealCapital()))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        businessInfo.setActualCapitalCurrencyType(commerceInfo.getRealCurrencyType());
        businessInfo.setRegNumber(client.getCertNumber());
        businessInfo.setLegalPersonName(commerceInfo.getCorpRepresent());
        businessInfo.setEstablishTime(commerceInfo.getEstablishDate());
        businessInfo.setApprovalTime(commerceInfo.getApprovalDate());
        businessInfo.setBusinessTime(commerceInfo.getBizLicenseEndDate() == null ? null : commerceInfo.getBizLicenseEndDate().toString());
        businessInfo.setOrgNumber(commerceInfo.getOrgCode());
        businessInfo.setCompanyOrgType(commerceInfo.getOrgType());
        businessInfo.setIndustry(commerceInfo.getIndustryType());
        if (ObjectUtil.isNotEmpty(registryAddressInfo)) {
            businessInfo.setRegLocation(registryAddressInfo.getDetail());
            businessInfo.setRegProvince(registryAddressInfo.getProvince());
            businessInfo.setRegCity(registryAddressInfo.getCity());
            businessInfo.setRegCountry(registryAddressInfo.getCountry());
        }
        //联系人信息
        if (ObjectUtil.isNotEmpty(pageContactInfo) && ObjectUtil.isNotEmpty(pageContactInfo.getRecords())) {
            List<DataShareRegisterCustomREQ.LinkmanInfo> list = new ArrayList<>();
            pageContactInfo.getRecords().forEach(record -> {
                DataShareRegisterCustomREQ.LinkmanInfo linkmanInfo = dataShareRegisterCustomREQ.new LinkmanInfo();
                linkmanInfo.setName(record.getName());
                linkmanInfo.setPhoneNo(record.getTelephone());
                linkmanInfo.setDepartment(record.getPosition());
                linkmanInfo.setDuty(record.getPosition());
                linkmanInfo.setEmail(record.getMail());
                list.add(linkmanInfo);
            });
            dataShareRegisterCustomREQ.setLinkmanInfo(list);
        }
        return dataShareRegisterCustomREQ;
    }

    private void sendClientNewMessage(Client client, Long createBy) {
        //只通知综合管理部的信息岗
        List<Long> jobIds = userService.getUsersByjobcod("Informationpost").stream()
                .map(UserDO::getId).collect(Collectors.toList());
        Set<Long> zhglbSet = sysUserService.getUserByDeptCode("ZHGLB").stream().map(UserDO::getId).collect(Collectors.toSet());
        List<Long> to = new ArrayList<>();
        jobIds.forEach(id -> {
            if (zhglbSet.contains(id)) {
                to.add(id);
            }
        });
        MessageAddREQ addREQ = new MessageAddREQ();
        addREQ.setTo(to);
        addREQ.setMessageType(MessageTypeEnum.CLIENT_NEW.name());
        addREQ.setNeedOa(true);
        addREQ.setNoticeSource(BusinessModuleEnum.CLIENT.name());
        addREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_NEW.pcUrl, client.getId(), client.getClientType()));
        //String s = id2NameService.sysUserId2NameSingle(client.getBelongSponsorId());
        String s = id2NameService.sysUserId2NameSingle(ObjectUtil.isEmpty(createBy) ? AccountUtil.getLoginInfo().getId() : createBy);
        addREQ.setFrom(s);
        addREQ.setRelation(s + "创建了客户且自动创建集团客户失败或为自然人" + client.getClientName() + ", ");
        addREQ.setBusinessId(client.getId().toString());
        addREQ.setContent(client.getClientName());
        SpringUtil.getBean(MessageService.class).sendMessage(SpringUtil.getBean(MessageConver.class).reqToMessage(addREQ));
    }
}
