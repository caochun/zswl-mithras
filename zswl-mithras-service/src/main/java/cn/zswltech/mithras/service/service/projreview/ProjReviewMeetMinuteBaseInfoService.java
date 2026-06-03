package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projreview.meet.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.trackEvent.TrackEventListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.flow.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.afterlease.ClientRole;
import cn.zswltech.mithras.service.enums.app.AppProjStageStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projreview.*;
import cn.zswltech.mithras.service.flow.dynamicform.projreview.SetMeetingPlanDateHandler;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewMeetMinuteBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.trackevent.TrackEventInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewMeetMinuteBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 项目评审会议纪要表
 * @date 2025-03-18
 */
@Service
@Slf4j
public class ProjReviewMeetMinuteBaseInfoService extends ServiceImpl<ProjReviewMeetMinuteBaseInfoMapper, ProjReviewMeetMinuteBaseInfo> {

    @Resource
    private ProjReviewMeetMinuteBaseInfoMapper projReviewMeetMinuteBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private TrackEventService trackEventService;
    @Resource
    private ProcessService processService;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private ClientService clientService;
    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(ProjReviewMeetMinuteBaseInfoDetailRSP req) {
        ProjReviewMeetMinuteBaseInfo info = BeanUtil.copyProperties(req, ProjReviewMeetMinuteBaseInfo.class);
        projReviewMeetMinuteBaseInfoMapper.insert(info);
    }

    /**
     * 初始化项目评审纪要
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void initProjReviewMeetMinute(Long projReviewId, String processInstanceId) {
        Map<String, Object> variables = flowVariableApiService.getVariables(processInstanceId, Collections.singletonList(SetMeetingPlanDateHandler.FLOW_PARAMETER_NAME));
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        ProjReviewPriceDetailRSP priceDetailRSP = projReviewPriceService.detail(projReviewId);
        if (ObjectUtil.isEmpty(projReviewId) || ObjectUtil.isEmpty(processInstanceId)) {
            return;
        }
        ProjReviewMeetMinuteBaseInfo oldMeetMinuteBaseInfo = baseMapper.selectOne(Wrappers.<ProjReviewMeetMinuteBaseInfo>lambdaQuery()
                .eq(ProjReviewMeetMinuteBaseInfo::getProjReviewId, projReviewId)
                .eq(ProjReviewMeetMinuteBaseInfo::getProjFlowId, processInstanceId)
                .last(StringUtil.mysqlLimitOne()));
        ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo = new ProjReviewMeetMinuteBaseInfo();
        if (ObjectUtil.isNotEmpty(oldMeetMinuteBaseInfo)) {
            meetMinuteBaseInfo.setId(oldMeetMinuteBaseInfo.getId());
        }
        meetMinuteBaseInfo.setProjReviewId(projReviewBaseInfo.getId());
        meetMinuteBaseInfo.setProjName(projReviewBaseInfo.getProjName());
        meetMinuteBaseInfo.setProjFlowId(processInstanceId);
        meetMinuteBaseInfo.setMeetMinuteCode("[]评审字第[]号");
        //meetMinuteBaseInfo.setMeetMinuteSequence(sequence);
        meetMinuteBaseInfo.setMeetMinuteStatus(MeetMinuteStatuesEnum.NEW.name());
        if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getLeaseTypes())) {
            List<String> leaseTypeCodeList = JSONUtil.toList(projReviewBaseInfo.getLeaseTypes(), String.class);
            if (ObjectUtil.isNotEmpty(leaseTypeCodeList)) {
                meetMinuteBaseInfo.setLeaseType(leaseTypeCodeList.get(0));
            }
            if (leaseTypeCodeList.contains(LeaseType.zhi_zu.name())) {
                meetMinuteBaseInfo.setPreLeasePeriodFlag(YesOrNoNumberEnum.YES.getCode());
            }
        }
        List<ProjReviewMeetMinuteResolutionDTO> resolutionDTOS = new ArrayList<>();
        //承租人列表
        meetMinuteBaseInfo.setLesseeInfo(projReviewBaseInfo.getLesseeInfo());
        meetMinuteBaseInfo.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
        meetMinuteBaseInfo.setRentalStartMethod(RentalStartMethodEnum.START_RENT_ONCE.name());
        meetMinuteBaseInfo.setNominalPrice(priceDetailRSP.getNominalPrice());
        meetMinuteBaseInfo.setSupplierInfo(projReviewBaseInfo.getSupplierInfo());
        //决议文件
        if (StrUtil.isNotBlank(projReviewBaseInfo.getLesseeInfo())) {
            List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(projReviewBaseInfo.getLesseeInfo(), ProjEstablishPersonInfo.class);
            if (ObjectUtil.isNotEmpty(personInfos)) {
                resolutionDTOS.addAll(personInfos.stream().map(e -> ProjReviewMeetMinuteResolutionDTO.builder().clientId(e.getClientId())
                        .clientName(e.getClientName()).clientType(e.getClientType()).clientRole(ClientRole.LESSEE.name())
                        .resolutionTypeRateEnum(ResolutionTypeRateEnum.FINANCIAL_LEASING_BUSINESS.name()).build()).collect(Collectors.toList()));
            }
        }
        if (StrUtil.isNotBlank(projReviewBaseInfo.getCreditorInfo())) {
            List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(projReviewBaseInfo.getCreditorInfo(), ProjEstablishPersonInfo.class);
            if (ObjectUtil.isNotEmpty(personInfos)) {
                resolutionDTOS.addAll(personInfos.stream().map(e -> ProjReviewMeetMinuteResolutionDTO.builder().clientId(e.getClientId())
                        .clientName(e.getClientName()).clientType(e.getClientType()).clientRole(ClientRole.CREDITOR.name()).build()).collect(Collectors.toList()));
            }
        }
        //担保人信息
        if (StrUtil.isNotBlank(projReviewBaseInfo.getGuaranteeInfo())) {
            List<ProjReviewMeetMinuteGuaranteeMeasuresDTO> guaranteeList = new ArrayList<>();
            List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(projReviewBaseInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class);
            if (ObjectUtil.isNotEmpty(personInfos)) {
                resolutionDTOS.addAll(personInfos.stream().map(e -> ProjReviewMeetMinuteResolutionDTO.builder().clientId(e.getClientId())
                        .clientName(e.getClientName()).clientType(e.getClientType()).clientRole(ClientRole.GUARANTEE.name()).resolutionTypeRateEnum(ResolutionTypeRateEnum.JOINT_AND_SEVERAL_LIABILITY_GUARANTEE.name()).build()).collect(Collectors.toList()));
                for (ProjEstablishPersonInfo e : personInfos) {
                    ProjReviewMeetMinuteGuaranteeMeasuresDTO dto = new ProjReviewMeetMinuteGuaranteeMeasuresDTO();
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setClientId(e.getClientId());
                    clientInfo.setClientName(e.getClientName());
                    clientInfo.setClientType(e.getClientType());
                    dto.setClientInfoList(Collections.singletonList(clientInfo));
                    if (ClientType.CORPORATION.name().equals(e.getClientType())) {
                        dto.setClientRole(GuaranteeMeasuresTypeEnum.LEGAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE.name());
                    } else {
                        dto.setClientRole(GuaranteeMeasuresTypeEnum.NATURAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE.name());
                    }
                    dto.setGuaranteeRate(1000000);
                    guaranteeList.add(dto);
                }
                meetMinuteBaseInfo.setGuaranteeMeasures(JSON.toJSONString(guaranteeList));
            }
        }
        //质押人信息
        if (StrUtil.isNotBlank(projReviewBaseInfo.getPledgorInfo())) {
            List<ProjReviewMeetMinutePledgeMeasuresDTO> pledgeMeasuresDTOS = new ArrayList<>();
            List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(projReviewBaseInfo.getPledgorInfo(), ProjEstablishPersonInfo.class);
            if (ObjectUtil.isNotEmpty(personInfos)) {
                resolutionDTOS.addAll(personInfos.stream().map(e -> ProjReviewMeetMinuteResolutionDTO.builder().clientId(e.getClientId())
                        .clientName(e.getClientName()).clientType(e.getClientType()).clientRole(ClientRole.PLEDGE.name()).resolutionTypeRateEnum(ResolutionTypeRateEnum.GUARANTEE_PLEDGE.name()).build()).collect(Collectors.toList()));
                for (ProjEstablishPersonInfo e : personInfos) {
                    ProjReviewMeetMinutePledgeMeasuresDTO dto = new ProjReviewMeetMinutePledgeMeasuresDTO();
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setClientId(e.getClientId());
                    clientInfo.setClientName(e.getClientName());
                    clientInfo.setClientType(e.getClientType());
                    dto.setClientInfoList(Collections.singletonList(clientInfo));
                    if (ClientType.CORPORATION.name().equals(e.getClientType())) {
                        dto.setClientRole(GuaranteeMeasuresTypeEnum.LEGAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE.name());
                    } else {
                        dto.setClientRole(GuaranteeMeasuresTypeEnum.NATURAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE.name());
                    }
                    pledgeMeasuresDTOS.add(dto);
                }
            }
            meetMinuteBaseInfo.setPledgeMeasures(JSON.toJSONString(pledgeMeasuresDTOS));
        }
        //抵押人信息
        if (StrUtil.isNotBlank(projReviewBaseInfo.getMortgagorInfo())) {
            List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(projReviewBaseInfo.getMortgagorInfo(), ProjEstablishPersonInfo.class);
            if (ObjectUtil.isNotEmpty(personInfos)) {
                resolutionDTOS.addAll(personInfos.stream().map(e -> ProjReviewMeetMinuteResolutionDTO.builder().clientId(e.getClientId())
                        .clientName(e.getClientName()).clientType(e.getClientType()).clientRole(ClientRole.MORTGAGE.name()).resolutionTypeRateEnum(ResolutionTypeRateEnum.GUARANTEE_MORTGAGE.name()).build()).collect(Collectors.toList()));
            }
        }
        meetMinuteBaseInfo.setResolutionInfo(JSON.toJSONString(resolutionDTOS));
        meetMinuteBaseInfo.setReportIssuanceTime(LocalDate.parse(String.valueOf(variables.get(SetMeetingPlanDateHandler.FLOW_PARAMETER_NAME))));
        if (ObjectUtil.isNotEmpty(meetMinuteBaseInfo.getReportIssuanceTime())) {
            meetMinuteBaseInfo.setReportIssuanceYear(meetMinuteBaseInfo.getReportIssuanceTime().getYear());
        }
        //填充评审会评审人员
        List<String> juryVoteUser = getJuryVoteUser(processInstanceId, meetMinuteBaseInfo);
        if (ObjectUtil.isNotEmpty(juryVoteUser)) {
            meetMinuteBaseInfo.setVotingCommittee(JSONUtil.toJsonStr(juryVoteUser));
        }
        meetMinuteBaseInfo.setProjReviewType(AppProjStageStatus.PROJ_REVIEW_BASE.name());
        if (ObjectUtil.isEmpty(meetMinuteBaseInfo.getId())) {
            projReviewMeetMinuteBaseInfoMapper.insert(meetMinuteBaseInfo);
        } else {
            projReviewMeetMinuteBaseInfoMapper.updateById(meetMinuteBaseInfo);
        }
    }

    /**
     * 初始化变更项目评审纪要 从最新版本项目评审会会议纪要里复制，其中评审会表决结果模块重新取值
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void initChangeReviewMeetMinute(Long projReviewId, String processInstanceId) {
        //1.找到最新生效的评审会会议纪要
        ProjReviewMeetMinuteBaseInfo oldMeetMinuteBaseInfo = projReviewMeetMinuteBaseInfoMapper.selectOne(Wrappers.<ProjReviewMeetMinuteBaseInfo>lambdaQuery()
                .eq(ProjReviewMeetMinuteBaseInfo::getProjReviewId, projReviewId)
                .eq(ProjReviewMeetMinuteBaseInfo::getMeetMinuteStatus, MeetMinuteStatuesEnum.EFFECT.name())
                .orderByDesc(ProjReviewMeetMinuteBaseInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(oldMeetMinuteBaseInfo)) {
            log.info("无最新生效的评审会会议纪要 projReviewId = {}, processInstanceId = {}", projReviewId, processInstanceId);
            return;
        }
        //2.copy原数据
        ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo = BeanUtil.copyProperties(oldMeetMinuteBaseInfo, ProjReviewMeetMinuteBaseInfo.class, "id", "votingResult", "processEndTime", "projFlowId");
        //3.补充评审会投票信息
        getJuryVoteUser(processInstanceId, meetMinuteBaseInfo);
        meetMinuteBaseInfo.setProjFlowId(processInstanceId);
        //保存
        projReviewMeetMinuteBaseInfoMapper.insert(meetMinuteBaseInfo);
    }

    /**
     * 初始化授信评审纪要
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void initGroupProjReviewMeetMinute(Long groupCreditProjReviewId, String processInstanceId) {
        GroupCreditReviewBaseInfo projReviewBaseInfo = groupCreditReviewBaseInfoService.getById(groupCreditProjReviewId);
        if (ObjectUtil.isEmpty(projReviewBaseInfo) || ObjectUtil.isEmpty(processInstanceId)) {
            return;
        }
        ProjReviewMeetMinuteBaseInfo oldMeetMinuteBaseInfo = baseMapper.selectOne(Wrappers.<ProjReviewMeetMinuteBaseInfo>lambdaQuery()
                .eq(ProjReviewMeetMinuteBaseInfo::getProjReviewId, groupCreditProjReviewId)
                .eq(ProjReviewMeetMinuteBaseInfo::getProjFlowId, processInstanceId)
                .last(StringUtil.mysqlLimitOne()));
        ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo = new ProjReviewMeetMinuteBaseInfo();
        if (ObjectUtil.isNotEmpty(oldMeetMinuteBaseInfo)) {
            meetMinuteBaseInfo.setId(oldMeetMinuteBaseInfo.getId());
        }
        if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getClientId())) {
            ClientInfo clientInfo = new ClientInfo();
            Client client = clientService.getById(projReviewBaseInfo.getClientId());
            if (ObjectUtil.isNotEmpty(client)) {
                clientInfo.setClientId(client.getId());
                clientInfo.setClientName(client.getClientName());
                clientInfo.setClientType(client.getClientType());
            }
            meetMinuteBaseInfo.setLesseeInfo(JSONUtil.toJsonStr(ListUtil.toList(clientInfo)));
        }
        meetMinuteBaseInfo.setProjReviewId(projReviewBaseInfo.getId());
        meetMinuteBaseInfo.setProjName(projReviewBaseInfo.getProjName());
        meetMinuteBaseInfo.setProjFlowId(processInstanceId);
        meetMinuteBaseInfo.setMeetMinuteCode("[]评审字第[]号");
        //meetMinuteBaseInfo.setMeetMinuteSequence(sequence);
        meetMinuteBaseInfo.setMeetMinuteStatus(MeetMinuteStatuesEnum.NEW.name());
        /*if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getLeaseTypes())) {
            List<String> leaseTypeCodeList = JSONUtil.toList(projReviewBaseInfo.getLeaseTypes(), String.class);
            if (ObjectUtil.isNotEmpty(leaseTypeCodeList)) {
                meetMinuteBaseInfo.setLeaseType(leaseTypeCodeList.get(0));
            }
            if (leaseTypeCodeList.contains(LeaseType.zhi_zu.name())) {
                meetMinuteBaseInfo.setPreLeasePeriodFlag(YesOrNoNumberEnum.YES.getCode());
            }
        }*/
        List<ProjReviewMeetMinuteResolutionDTO> resolutionDTOS = new ArrayList<>();
        //承租人列表
        //meetMinuteBaseInfo.setLesseeInfo(projReviewBaseInfo.getLesseeInfo());
        //meetMinuteBaseInfo.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
        meetMinuteBaseInfo.setRentalStartMethod(RentalStartMethodEnum.START_RENT_ONCE.name());
        //meetMinuteBaseInfo.setNominalPrice(priceDetailRSP.getNominalPrice());
        //meetMinuteBaseInfo.setSupplierInfo(projReviewBaseInfo.getSupplierInfo());
        meetMinuteBaseInfo.setResolutionInfo(JSON.toJSONString(resolutionDTOS));
        meetMinuteBaseInfo.setReportIssuanceTime(LocalDate.now());
        meetMinuteBaseInfo.setReportIssuanceYear(meetMinuteBaseInfo.getReportIssuanceTime().getYear());
        //填充评审会评审人员
        List<String> juryVoteUser = getJuryVoteUser(processInstanceId, meetMinuteBaseInfo);
        if (ObjectUtil.isNotEmpty(juryVoteUser)) {
            meetMinuteBaseInfo.setVotingCommittee(JSONUtil.toJsonStr(juryVoteUser));
        }
        meetMinuteBaseInfo.setProjReviewType(AppProjStageStatus.GROUP_CREDIT_REVIEW.name());
        if (ObjectUtil.isEmpty(meetMinuteBaseInfo.getId())) {
            projReviewMeetMinuteBaseInfoMapper.insert(meetMinuteBaseInfo);
        } else {
            projReviewMeetMinuteBaseInfoMapper.updateById(meetMinuteBaseInfo);
        }
    }

    //获取评审团投票人员
    private List<String> getJuryVoteUser(String processInstanceId, ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo) {
        if (ObjectUtil.isEmpty(processInstanceId)) {
            return null;
        }
        ProcessHistoryReq flowReq = new ProcessHistoryReq();
        flowReq.setProcessInstanceId(processInstanceId);
        flowReq.setPageSize(5000);
        flowReq.setPageIndex(1);
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> historyRespPage = processApiService.history(flowReq);
        List<ProcessHistoryRSP> rspList = historyRespPage.getContents().stream()
                .map(flowProcessConvert::flowHistoryResp2RSP).collect(Collectors.toList());
        flowProcessConvert.historyRSPFillName(rspList);
        //评审会
        List<ProcessHistoryRSP> juryVote = rspList.stream().filter(e -> "userTask_juryVote".equals(e.getTaskActivityId())).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(juryVote)) {
            return null;
        }
        //初始化
        meetMinuteBaseInfo.setReportNumberAgree(0);
        meetMinuteBaseInfo.setReportNumberConditionalAgree(0);
        meetMinuteBaseInfo.setReportNumberAgainst(0);
        meetMinuteBaseInfo.setReportNumberVoters(0);
        juryVote.forEach(e -> {
            if (CommentTypeEnum.VOTE_AGREE.name().equals(e.getType())) {
                meetMinuteBaseInfo.setReportNumberAgree(meetMinuteBaseInfo.getReportNumberAgree() == null ? 1 : meetMinuteBaseInfo.getReportNumberAgree() + 1);
            } else if (CommentTypeEnum.VOTE_CONDITION_AGREE.name().equals(e.getType())) {
                meetMinuteBaseInfo.setReportNumberConditionalAgree(meetMinuteBaseInfo.getReportNumberConditionalAgree() == null ? 1 : meetMinuteBaseInfo.getReportNumberConditionalAgree() + 1);
            } else if (CommentTypeEnum.VOTE_DISAGREE.name().equals(e.getType())) {
                meetMinuteBaseInfo.setReportNumberAgainst(meetMinuteBaseInfo.getReportNumberAgainst() == null ? 1 : meetMinuteBaseInfo.getReportNumberAgainst() + 1);
            }
            meetMinuteBaseInfo.setReportNumberVoters(meetMinuteBaseInfo.getReportNumberVoters() == null ? 1 : meetMinuteBaseInfo.getReportNumberVoters() + 1);
        });
        return juryVote.stream().map(ProcessHistoryRSP::getOperatorName).collect(Collectors.toList());
    }

    public ProjReviewMeetMinuteBaseInfoDetailRSP detail(ProjReviewMeetMinuteBaseInfoDetailREQ req) {
        if (ObjectUtil.isAllEmpty(req.getProjReviewId(), req.getProjReviewType(), req.getProjFlowId(), req.getIsEffect(), req.getPaymentId())) {
            return null;
        }
        if (ObjectUtil.isNotEmpty(req.getContractId())) {
            ContractBaseInfo contractBaseInfo = SpringContextHolder.getBean(ContractBaseInfoService.class).getById(req.getContractId());
            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                req.setProjReviewId(contractBaseInfo.getProjReviewId());
                req.setProjReviewType(AppProjStageStatus.PROJ_REVIEW_BASE.name());
            }
        }
        ProjReviewMeetMinuteBaseInfo projReviewMeetMinuteBaseInfo = projReviewMeetMinuteBaseInfoMapper.selectOne(Wrappers.<ProjReviewMeetMinuteBaseInfo>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getId()), ProjReviewMeetMinuteBaseInfo::getId, req.getId())
                .eq(ObjectUtil.isNotEmpty(req.getProjReviewId()), ProjReviewMeetMinuteBaseInfo::getProjReviewId, req.getProjReviewId())
                .eq(ObjectUtil.isNotEmpty(req.getProjFlowId()), ProjReviewMeetMinuteBaseInfo::getProjFlowId, req.getProjFlowId())
                .eq(ObjectUtil.isNotEmpty(req.getProjReviewType()), ProjReviewMeetMinuteBaseInfo::getProjReviewType, req.getProjReviewType())
                .eq(ObjectUtil.isNotEmpty(req.getIsEffect()) && YesOrNoNumberEnum.YES.getCode().equals(req.getIsEffect()), ProjReviewMeetMinuteBaseInfo::getMeetMinuteStatus, MeetMinuteStatuesEnum.EFFECT.name())
                .orderByDesc(ProjReviewMeetMinuteBaseInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        return buildProjReviewMeetMinuteDetail(projReviewMeetMinuteBaseInfo);
    }

    private ProjReviewMeetMinuteBaseInfoDetailRSP buildProjReviewMeetMinuteDetail (ProjReviewMeetMinuteBaseInfo projReviewMeetMinuteBaseInfo) {
        if (ObjectUtil.isEmpty(projReviewMeetMinuteBaseInfo)) {
            return null;
        }
        ProjReviewMeetMinuteBaseInfoDetailRSP rsp = BeanUtil.copyProperties(projReviewMeetMinuteBaseInfo, ProjReviewMeetMinuteBaseInfoDetailRSP.class);
        rsp.setLesseeInfoDetail(JSONArray.parseArray(rsp.getLesseeInfo(), ClientInfo.class));
        rsp.setResolutionInfoDetails(JSONArray.parseArray(rsp.getResolutionInfo(), ProjReviewMeetMinuteResolutionDTO.class));
        rsp.setGuaranteeMeasureDetails(JSONArray.parseArray(rsp.getGuaranteeMeasures(), ProjReviewMeetMinuteGuaranteeMeasuresDTO.class));
        rsp.setPledgeMeasuresDetails(JSONArray.parseArray(rsp.getPledgeMeasures(), ProjReviewMeetMinutePledgeMeasuresDTO.class));
        if (ObjectUtil.isNotEmpty(rsp.getProcessEndTime()) && ObjectUtil.isNotEmpty(rsp.getVotingPeriodValidity())) {
            rsp.setCreditExpirationDate(rsp.getProcessEndTime().plusMonths(Optional.ofNullable(VotingPeriodValidityEnum.of(rsp.getVotingPeriodValidity())).map(VotingPeriodValidityEnum::getMonth).orElse(6)));
        }
        if (ObjectUtil.isNotEmpty(rsp.getSpecialContractTerms())) {
            rsp.setSpecialContractTermsList(JSONUtil.toList(rsp.getSpecialContractTerms(), String.class));
        }
        if (ObjectUtil.isNotEmpty(rsp.getConditionsBeforeDisbursement())) {
            rsp.setConditionsBeforeDisbursementList(JSONUtil.toList(rsp.getConditionsBeforeDisbursement(), String.class));
        }
        //补充跟踪事项
        List<TrackEventInfo> trackEventInfos = trackEventService.list(Wrappers.<TrackEventInfo>lambdaQuery()
                .eq(TrackEventInfo::getProjReviewMeetMinuteId, projReviewMeetMinuteBaseInfo.getId())
                .ne(TrackEventInfo::getTaskStatus, YesOrNoNumberEnum.NO.getCode()));
        if (ObjectUtil.isNotEmpty(trackEventInfos)) {
            rsp.setTrackEventListRSPS(BeanUtil.copyToList(trackEventInfos, TrackEventListRSP.class));
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(ProjReviewMeetMinuteBaseInfoModifyREQ req) {
        ProjReviewMeetMinuteBaseInfo originalInfo = projReviewMeetMinuteBaseInfoMapper.selectById(req.getId());
        ProjReviewMeetMinuteBaseInfo info = BeanUtil.copyProperties(req, ProjReviewMeetMinuteBaseInfo.class);
        info.setLesseeInfo(JSON.toJSONString(req.getLesseeInfoDetail()));
        info.setResolutionInfo(JSON.toJSONString(req.getResolutionInfoDetails()));
        info.setGuaranteeMeasures(JSON.toJSONString(req.getGuaranteeMeasureDetails()));
        info.setPledgeMeasures(JSON.toJSONString(req.getPledgeMeasuresDetails()));
        if (ObjectUtil.isNotEmpty(req.getProjectApprovalAmount())) {
            //修改项目批复金额
            if (ObjectUtil.equals(originalInfo.getProjReviewType(), AppProjStageStatus.GROUP_CREDIT_REVIEW.name())) {
                groupCreditReviewBaseInfoService.doModifyProjectApprovalAmount(info.getProjReviewId(), req.getProjectApprovalAmount());
            } else {
                projReviewPriceService.doModifyProjectApprovalAmount(info.getProjReviewId(), req.getProjectApprovalAmount());
            }
        }
        //兼容历史
        if (ObjectUtil.isNotEmpty(originalInfo)) {
            projReviewMeetMinuteBaseInfoMapper.updateAnnotationIncludeNullById(info);
        } else {
            projReviewMeetMinuteBaseInfoMapper.insert(info);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyStatus(String processInstanceId, Long projReviewId, String meetMinuteStatus) {
        LambdaUpdateWrapper<ProjReviewMeetMinuteBaseInfo> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ProjReviewMeetMinuteBaseInfo::getMeetMinuteStatus, meetMinuteStatus);
        updateWrapper.eq(ProjReviewMeetMinuteBaseInfo::getProjFlowId, processInstanceId)
                .eq(ProjReviewMeetMinuteBaseInfo::getProjReviewId, projReviewId);
        if (ObjectUtil.equals(MeetMinuteStatuesEnum.EFFECT.name(), meetMinuteStatus)) {
            updateWrapper.set(ProjReviewMeetMinuteBaseInfo::getProcessEndTime, LocalDate.now());
        }
        projReviewMeetMinuteBaseInfoMapper.update(null, updateWrapper);
    }

    public List<ClientInfo> getRelatedCustomers(ProjReviewMeetMinuteRelatedCustomersREQ req) {
        if (ObjectUtil.isEmpty(req.getMeetMinuteId())) {
            return getProjReviewCustomers(req.getProjReviewId());
        }
        ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo = projReviewMeetMinuteBaseInfoMapper.selectById(req.getMeetMinuteId());
        if (ObjectUtil.isEmpty(meetMinuteBaseInfo)) {
            return getProjReviewCustomers(req.getProjReviewId());
        }
        if (ObjectUtil.equals(meetMinuteBaseInfo.getProjReviewType(), AppProjStageStatus.GROUP_CREDIT_REVIEW.name())) {
            return getGroupProjReviewCustomers(meetMinuteBaseInfo.getProjReviewId());
        } else {
            return getProjReviewCustomers(meetMinuteBaseInfo.getProjReviewId());
        }
    }

    public List<ClientInfo> getProjReviewCustomers(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        if (ObjectUtil.isEmpty(projReviewBaseInfo)) {
            return null;
        }
        List<ClientInfo> rsps = new ArrayList<>();
        List<ClientInfo> lesseeInfos = JSON.parseArray(projReviewBaseInfo.getLesseeInfo(), ClientInfo.class);
        List<ClientInfo> creditorInfos = JSON.parseArray(projReviewBaseInfo.getCreditorInfo(), ClientInfo.class);
        List<ClientInfo> guaranteeInfos = JSON.parseArray(projReviewBaseInfo.getGuaranteeInfo(), ClientInfo.class);
        List<ClientInfo> pledgorInfos = JSON.parseArray(projReviewBaseInfo.getPledgorInfo(), ClientInfo.class);
        List<ClientInfo> mortgagorInfos = JSON.parseArray(projReviewBaseInfo.getMortgagorInfo(), ClientInfo.class);
        if (ObjectUtil.isNotEmpty(lesseeInfos)) {
            rsps.addAll(lesseeInfos);
        }
        if (ObjectUtil.isNotEmpty(creditorInfos)) {
            rsps.addAll(creditorInfos);
        }
        if (ObjectUtil.isNotEmpty(guaranteeInfos)) {
            rsps.addAll(guaranteeInfos);
        }
        if (ObjectUtil.isNotEmpty(pledgorInfos)) {
            rsps.addAll(pledgorInfos);
        }
        if (ObjectUtil.isNotEmpty(mortgagorInfos)) {
            rsps.addAll(mortgagorInfos);
        }
        return ListUtil.toList(rsps.stream().collect(Collectors.toMap(ClientInfo::getClientId, e -> e, (a, b) -> a)).values());
    }

    public List<ClientInfo> getGroupProjReviewCustomers(Long projReviewId) {
        GroupCreditReviewBaseInfo projReviewBaseInfo = groupCreditReviewBaseInfoService.getById(projReviewId);
        if (ObjectUtil.isEmpty(projReviewBaseInfo)) {
            return null;
        }
        if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getClientId())) {
            ClientInfo clientInfo = new ClientInfo();
            Client client = clientService.getById(projReviewBaseInfo.getClientId());
            if (ObjectUtil.isNotEmpty(client)) {
                clientInfo.setClientId(client.getId());
                clientInfo.setClientName(client.getClientName());
                clientInfo.setClientType(client.getClientType());
            }
            return ListUtil.toList(clientInfo);
        }
        return null;
    }


    public void subPass(String flowId) {
        ProjReviewMeetMinuteBaseInfoDetailREQ meetMinuteBaseInfoDetailREQ = new ProjReviewMeetMinuteBaseInfoDetailREQ();
        meetMinuteBaseInfoDetailREQ.setProjFlowId(flowId);
        ProjReviewMeetMinuteBaseInfoDetailRSP reviewMeetMinuteBaseInfoDetailRSP = this.detail(meetMinuteBaseInfoDetailREQ);
        if (ObjectUtil.isEmpty(reviewMeetMinuteBaseInfoDetailRSP)) {
            return;
        }
        if (!MeetMinuteStatuesEnum.SUBMIT.name().equals(reviewMeetMinuteBaseInfoDetailRSP.getMeetMinuteStatus())) {
            throw new MithrasException("请先提交评审会纪要后再操作");
        }
        this.subCheck(BeanUtil.copyProperties(reviewMeetMinuteBaseInfoDetailRSP, ProjReviewMeetMinuteBaseInfoModifyREQ.class));
    }

    public void checkCashFlowPlan(String flowId, Long projReviewId) {
        ProjReviewMeetMinuteBaseInfoDetailREQ meetMinuteBaseInfoDetailREQ = new ProjReviewMeetMinuteBaseInfoDetailREQ();
        meetMinuteBaseInfoDetailREQ.setProjFlowId(flowId);
        meetMinuteBaseInfoDetailREQ.setProjReviewId(projReviewId);
        ProjReviewMeetMinuteBaseInfoDetailRSP reviewMeetMinuteBaseInfoDetailRSP = this.detail(meetMinuteBaseInfoDetailREQ);
        if (ObjectUtil.isEmpty(reviewMeetMinuteBaseInfoDetailRSP)) {
            return;
        }
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlans = projReviewCashFlowPlanService.listByProjReviewMeetMinuteId(reviewMeetMinuteBaseInfoDetailRSP.getId());
        if (ObjectUtil.isEmpty(projReviewCashFlowPlans)) {
            throw new MithrasException("评审会纪要-租金概算表为空");
        }
    }

    public void subCheck(ProjReviewMeetMinuteBaseInfoModifyREQ req) {
        if (ObjectUtil.isEmpty(req.getLeaseType())) {
            throw new MithrasException("租赁类型为空");
        }
        if (ObjectUtil.isEmpty(req.getLesseeInfoDetail())) {
            throw new MithrasException("承租人为空");
        }
        if (ObjectUtil.isEmpty(req.getFixedValueBasis()) && ObjectUtil.isEmpty(req.getFixedValueBasisValue())) {
            throw new MithrasException("定值依据为空");
        }
        if (ObjectUtil.isEmpty(req.getInsurancePurchaser())) {
            throw new MithrasException("保险安排-购买方为空");
        } else if (!ProjectInsurancePurchaserEnum.NOT.name().equals(req.getInsurancePurchaser())) {
            if (ObjectUtil.isEmpty(req.getPolicyType()) && ObjectUtil.isEmpty(req.getPolicyTypeValue())) {
                throw new MithrasException("保险安排-险种为空");
            }
            if (ObjectUtil.isEmpty(req.getPolicyRequire()) && ObjectUtil.isEmpty(req.getPolicyRequireValue())) {
                throw new MithrasException("保险安排-保险要求为空");
            }
            if(ObjectUtil.isEmpty(req.getInsurancePurchaseTime()) && ObjectUtil.isEmpty(req.getInsurancePurchaseTimeValue())){
                throw new MithrasException("保险购买时间为空");
            }
        }
        if (ObjectUtil.isEmpty(req.getProjectApprovalAmount())) {
            throw new MithrasException("项目批复金额为空");
        }
        if (ObjectUtil.isEmpty(req.getRentPaymentMethodRate()) || ObjectUtil.isEmpty(req.getRentPaymentMethodType())) {
            throw new MithrasException("租金支付方式为空");
        }
        if (ObjectUtil.isEmpty(req.getFinancingFundMethodType())) {
            throw new MithrasException("融资款支付方式为空");
        }
        if (ObjectUtil.isEmpty(req.getFundsPurpose())) {
            throw new MithrasException("资金用途为空");
        }
        if (ObjectUtil.isEmpty(req.getRentalStartMethod())) {
            throw new MithrasException("起租方式为空");
        }
        /*if (ObjectUtil.isEmpty(req.getRentalStartCondition())) {
            throw new MithrasException("起租条件为空");
        }*/
        if (ObjectUtil.isEmpty(req.getNominalPrice())) {
            throw new MithrasException("名义价款为空");
        }
        if (ObjectUtil.isEmpty(req.getResolutionInfoDetails())) {
            throw new MithrasException("决议批准文件为空");
        }
    }

    public Page<ProjReviewMeetMinuteBaseInfo> list(ProjReviewMeetMinuteBaseInfoListREQ req) {
        return null;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void remove(ProjReviewMeetMinuteBaseInfoRemoveREQ req) {
        ProjReviewMeetMinuteBaseInfo originalInfo = projReviewMeetMinuteBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        projReviewMeetMinuteBaseInfoMapper.deleteById(req.getId());
    }

}