package cn.zswltech.mithras.service.service.projestablish;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.lib.projestablish.impl.ProjEstablishVersionServiceImpl;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.impl.ProjEstablishStateMachine;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleEventService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.common.enums.ProjectBizType.*;
import static cn.zswltech.mithras.service.others.Util.missRequiredParam;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishService implements FlowEndEventProcessor {
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishVersionServiceImpl projEstablishVersionService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ProjEstablishVersionServiceImpl versionService;

    @Resource
    private ProjEstablishStateMachine stateMachine;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ProjEstablishPriceService priceService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long projEstablishId) {
        //更新部门领导信息
        projEstablishBaseInfoService.renewLeader(projEstablishId);
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(projEstablishId);
        String event;
        // 请求头标记此次生效需要走审批 审批测试
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        if (RecordStatus.NEW.name().equals(baseInfo.getProjEstablishStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ProjEstablishCreateFlow.name());
            event = "立项创建审批";
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ProjEstablishModifyFlow.name());
            event = "立项修改审批";
        }

        // 获取风控经理
        String riskControlManagerId = baseInfo.getRiskControlManagerId();
        List<String> riskControlManagers = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(riskControlManagerId)) {
            riskControlManagers.addAll(JSON.parseObject(riskControlManagerId, new TypeReference<List<String>>() {
            }));
        }

        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of("riskControlManager", riskControlManagers)
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(projEstablishId));
        startProcessReq.setSubModule(baseInfo.getBizType());
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ? new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        projectLifecycleEventService.add(event, ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", baseInfo.getId(), ReviewRelationDataType.PROJ_ESTABLISH.name());
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));
    }

    public ProcessResp findRelatedProcess(Long projEstablishId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projEstablishId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_ESTABLISH.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public ProcessResp findRelatedAndPassProcess(Long projEstablishId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setSortType(1);
        processPageReq.setBusinessKey(String.valueOf(projEstablishId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_ESTABLISH.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType(),ProcessBusinessStatusEnum.PASS.getType(),ProcessBusinessStatusEnum.PASS_ALL.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long projEstablishId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(projEstablishId);
        // 更新风险敞口
        baseInfo.setLesseeInfo(setExposureRisk(baseInfo.getLesseeInfo()));
        baseInfo.setPledgorInfo(setExposureRisk(baseInfo.getPledgorInfo()));
        baseInfo.setGuaranteeInfo(setExposureRisk(baseInfo.getGuaranteeInfo()));
        baseInfo.setMortgagorInfo(setExposureRisk(baseInfo.getMortgagorInfo()));
        baseInfo.setDebtorInfo(setExposureRisk(baseInfo.getDebtorInfo()));
        baseInfo.setCreditorInfo(setExposureRisk(baseInfo.getCreditorInfo()));
        projEstablishBaseInfoMapper.updateById(baseInfo);
        // 记录版本前要先更新状态
        if (processPass) {
            // 审批通过
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
            //触发客户权限修改
            ProjEstablishBaseInfoListRSP projEstablishBaseInfoListRSP = baseInfoConverter.entityToDetailRSP(baseInfo);
            //承租人
            Set<Long> clientIds = new HashSet();
            if(ObjectUtil.isNotEmpty(projEstablishBaseInfoListRSP.getLesseeInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getLesseeInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            //债权人
            if(ObjectUtil.isNotEmpty(projEstablishBaseInfoListRSP.getCreditorInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getCreditorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            //债务人
            if(ObjectUtil.isNotEmpty(projEstablishBaseInfoListRSP.getDebtorInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getDebtorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if(ObjectUtil.isNotEmpty(clientIds)){
                //判断权限,被占用无法继续审批
                clientIds.forEach(clientId -> SpringContextHolder.getBean(ClientService.class).checkClientOccupy(clientId, baseInfo.getProjSponsorUserId()));
                //clientIds.forEach(client -> clientService.updateClientAuth(client));
            }
        } else {
            if (RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getProjEstablishStatus())) {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_REJECT, baseInfo.getProcessStatus()));
                }
            } else {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_REJECT, baseInfo.getProcessStatus()));
                }
            }
            //更新部门领导信息，这里生成版本后更新
            projEstablishBaseInfoService.renewLeader(projEstablishId);
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        projEstablishVersionService.recordVersion(projEstablishId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getProjEstablishStatus())) {
            // 变更审批 且 未审批通过 回滚
            versionService.reset(projEstablishId);
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
            }
        }
    }

    public boolean canSave(Long projEstablishId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (projEstablishId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(projEstablishId);
        if (processResp == null) {
            // 运行中流程为空 可以保存
            return true;
        }
        if (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())) {
            // 有运行中流程 不在发起人节点 不能保存
            return false;
        }
        if (!Objects.equals(String.valueOf(loginUser.getId()), processResp.getStartUserId())) {
            // 在发起人节点 不是发起人 不能保存
            return false;
        }
        return true;
    }

    public String setExposureRisk(String jsonInfo) {
        if (StrUtil.isNotBlank(jsonInfo)) {
            List<ProjEstablishPersonInfo> infos = JSON.parseArray(jsonInfo, ProjEstablishPersonInfo.class);
            for (ProjEstablishPersonInfo info : infos) {
                if (isNotNull(info.getClientId())) {
                    info.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(info.getClientId(), null, null));
                }
            }
            return JSONUtil.toJsonStr(infos);
        }
        return null;
    }

    /**
     * 用于判断客户是否可删除
     * @param clientId
     * @return
     */
    public boolean clientRelatedProjEstablish(Long clientId) {
        int tmpRelatedCount = projEstablishBaseInfoMapper.clientRelatedProjEstablishCount(clientId);
        if (tmpRelatedCount > 0) {
            return true;
        }
        return projEstablishBaseInfoMapper.clientRelatedProjEstablishLibCount(clientId) > 0;
    }

    /**
     * 生效数据校验
     */
    public void effectCheck(Long projEstablishId) {
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(projEstablishId);
        effectCheck(baseInfo);
    }

    /**
     * 生效数据校验
     */
    public void effectCheck(ProjEstablishBaseInfo baseInfo) {
        missRequiredParam((ZL.name().equals(baseInfo.getBizType()) || ZZ.name().equals(baseInfo.getBizType())) && isBlank(baseInfo.getLesseeInfo()), "承租人");
        missRequiredParam((BL.name().equals(baseInfo.getBizType()) || ZR.name().equals(baseInfo.getBizType())) && isBlank(baseInfo.getCreditorInfo()), "债权人信息");
        //检查报价方案
        ProjEstablishPriceDetailRSP priceDetail = priceService.detail(baseInfo.getId());
        if (priceDetail.getAocPriceRSP() == null && priceDetail.getLeasePriceRSP() == null && priceDetail.getFactoringPriceRSP() == null) {
            throw new MithrasException("提交审批前请先填写报价方案");
        }
    }

}
