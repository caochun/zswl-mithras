package cn.zswltech.mithras.application.orchestration.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.TaskBusinessStatusEnum;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.OrgJobVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.EmailType;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.customer.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.enums.client.ClientRemindContentEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.customer.application.client.dto.ClientReleaseRemindDTO;
import cn.zswltech.mithras.customer.application.client.ClientReleaseRemindJobService;
import cn.zswltech.mithras.customer.application.client.dto.ReleaseRemindEmailInfoDTO;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.application.orchestration.client.dto.ClientAsLesseeInfoDTO;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientAuthority;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTradeStructureService;
import cn.zswltech.mithras.message.service.email.AbstractSendEmailHandler;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewTradeStructureService;
import cn.zswltech.mithras.foundation.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 客户实发提醒
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Slf4j
@Service
public class ClientReleaseRemindService implements ClientReleaseRemindJobService {

    private static final String USERTASK_BIZDIVISIONLEADER = "userTask_bizDivisionLeader";

    private static final String MESSAGE = "MESSAGE";


    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private List<AbstractSendEmailHandler> abstractSendEmailHandlers;

    /**
     * 客户释放前提醒
     *
     * @param clientId 客户Id
     */
    public void clientReleaseRemind(String clientId) {
        log.info("clientReleaseRemind Start  clientId:{}", clientId);
        //取全量的管护数据
        List<ClientAuthority> clientAuthorityList = this.querryClientAuthorityList(clientId);
        List<ClientReleaseRemindDTO> totalList = summaryClientRmind(clientAuthorityList);
        if (CollectionUtil.isEmpty(totalList)) {
            log.info("clientReleaseRemind 无需发送提醒");
            return;
        }
        log.info("clientReleaseRemind 需要发送的客户  totalList:{}", JSON.toJSONString(totalList));
        sendMsgBfRelease(totalList);
        sendEmailBfRelease(totalList);
    }


    //客户释放通知
    public void sendEmailBfRelease(List<ClientReleaseRemindDTO> remindDtoList) {
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(remindDtoList.stream().map(ClientReleaseRemindDTO::getClientId).collect(Collectors.toList()));
        Map<Long, List<ClientReleaseRemindDTO>> userIdMap = remindDtoList.stream().collect(Collectors.groupingBy(ClientReleaseRemindDTO::getUserId));
        // 将合并
        for (Map.Entry<Long, List<ClientReleaseRemindDTO>> entry : userIdMap.entrySet()) {
            Long userId = entry.getKey();
            List<ClientReleaseRemindDTO> releaseRemindDtoList = entry.getValue();
            Set<Long> toSet = new HashSet<>();
            toSet.add(userId);
            Set<Long> ccSet = new HashSet<>();
            //抄送部门负责人
            UserVO userVO = userServiceAPI.getUserInfoById(userId).getData();
            List<OrgJobVO> jobsNames = userVO.getJobsName();
            if (CollectionUtils.isNotEmpty(jobsNames)) {
                List<Long> orgIds = jobsNames.stream()
                        .map(OrgJobVO::getOrgId)
                        .filter(orgId -> orgId != null)
                        .collect(Collectors.toList());
                // 2. 查询部门负责人（前置判空，避免空集合查询）
                if (CollectionUtils.isNotEmpty(orgIds)) {
                    List<UserDO> deptManagerList = userServiceAPI.jobUsers(orgIds, JobEnum.businesshead.name());
                    // 3. 提取有效邮箱到抄送集合（流式操作简化+过滤空邮箱）
                    if (CollectionUtils.isNotEmpty(deptManagerList)) {
                        Set<Long> managerIds = deptManagerList.stream()
                                .map(UserDO::getId)
                                .collect(Collectors.toSet());
                        ccSet.addAll(managerIds);
                    }
                }
            }
            ReleaseRemindEmailInfoDTO remindEmailInfoDTO = new ReleaseRemindEmailInfoDTO();
            remindEmailInfoDTO.setClientReleaseRemindDTOList(releaseRemindDtoList);
            remindEmailInfoDTO.setClientId2Name(clientId2Name);
            for (AbstractSendEmailHandler abstractSendEmailHandler : abstractSendEmailHandlers) {
                if (abstractSendEmailHandler.needHandle(EmailType.BF_RELEASE_REMIND_EMAIL)) {
                    abstractSendEmailHandler.newSendEmail(toSet, ccSet, null, remindEmailInfoDTO);
                }
            }
        }
    }


    //客户释放通知
    public void sendMsgBfRelease(List<ClientReleaseRemindDTO> remindDtoList) {
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(remindDtoList.stream().map(ClientReleaseRemindDTO::getClientId).collect(Collectors.toList()));
        remindDtoList.forEach(remindDto -> {
            MessageAddREQ messageAddREQ = buildMessage(clientId2Name, remindDto);
            messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        });
    }

    private MessageAddREQ buildMessage(Map<Long, String> clientId2Name, ClientReleaseRemindDTO remindDto) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.CLIENT_RELEASE.name());
        StringBuffer relationgBuff = new StringBuffer(clientId2Name.get(remindDto.getClientId()));
        List<String> sourceList = remindDto.getSourceList();
        sourceList.sort(Comparator.naturalOrder());
        int i = 0;
        //使用&拼接
        for (String source : sourceList) {
            ClientRemindContentEnum contentEnum = ClientRemindContentEnum.getContentEnum(MESSAGE, source);
            if (Objects.nonNull(contentEnum)) {
                relationgBuff.append(contentEnum.getDisplay());
                if (i != sourceList.size() - 1) {
                    relationgBuff.append("&");
                }
            }
            i++;
        }
        //默认客户
        messageAddREQ.setContent(String.valueOf(remindDto.getClientId()));
        messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_NEW.pcUrl, remindDto.getClientId(), remindDto.getClientType()));
        if (Objects.nonNull(remindDto.getProjEstablishId())){
            messageAddREQ.setContent(String.valueOf(remindDto.getProjEstablishId()));
            messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_RELEASE_REMIND_ESTABLISH.pcUrl, remindDto.getProjEstablishId()));
        }
//        if (sourceList.contains(ClientRemindContentEnum.MESSAGE_02.getSource())) {
//            messageAddREQ.setContent(String.valueOf(remindDto.getProjEstablishId()));
//            messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_RELEASE_REMIND_ESTABLISH.pcUrl, remindDto.getProjEstablishId()));
//        }
        //无需跳评审
//        if (sourceList.contains(ClientRemindContentEnum.MESSAGE_03.getSource())) {
//            messageAddREQ.setContent(String.valueOf(remindDto.getProjReviewId()));
//            messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_RELEASE_REMIND_REVIEW.pcUrl, remindDto.getProjReviewId()));
//        }
        messageAddREQ.setRelation(relationgBuff.toString());
        messageAddREQ.setTo(Collections.singletonList(remindDto.getUserId()));
        messageAddREQ.setNeedOa(true);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.CLIENT_RELEASE.name());
        return messageAddREQ;
    }


    /**
     * 客户释放前提醒
     *
     * @param clientAuthorityList 权限集合
     */
    private List<ClientReleaseRemindDTO> summaryClientRmind(List<ClientAuthority> clientAuthorityList) {
        if (CollectionUtils.isEmpty(clientAuthorityList)) {
            log.info("clientReleaseRemind 无管护权的权限数据！！");
            return Collections.emptyList();
        }
        List<ClientReleaseRemindDTO> resultList = new ArrayList<>();
        for (ClientAuthority clientAuthority : clientAuthorityList) {
            ClientReleaseRemindDTO remindDto = singleClientRemind(clientAuthority);
            log.info("singleClientRemind 客户{} -- 获取结果：{}", remindDto.getClientId(), JSON.toJSONString(remindDto));
            List<String> sourceList = remindDto.getSourceList();
            if (CollectionUtil.isNotEmpty(sourceList)) {
                resultList.add(remindDto);
            }
        }
        return resultList;
    }

    /**
     * 判断当前客户是否满足即将释放的条件
     *
     * @param clientAuthority 权限数据
     */
    private ClientReleaseRemindDTO singleClientRemind(ClientAuthority clientAuthority) {
        Long clientId = clientAuthority.getClientId();
        ClientReleaseRemindDTO clientReleaseRemindDto = new ClientReleaseRemindDTO();
        clientReleaseRemindDto.setClientId(clientId);
        clientReleaseRemindDto.setUserId(clientAuthority.getUserId());
        Client client = clientService.getById(clientId);
        clientReleaseRemindDto.setClientType(client.getClientType());
        if (Objects.isNull(client)) {
            log.error("没有找到{}的客户信息", clientId);
            return clientReleaseRemindDto;
        }
        //判断一下当前时间和权限获取时间是否小于获取管护权时间限制  建议从配置中获取  因为释放是写死60天 这里暂时也写死60-7
        int beforeDays = 7;
        int criticalDays1 = 60;
        int criticalDays2 = 90;
        int criticalDays3 = 365;
        int criticalDays4 = 90;
        int remindDays = criticalDays1 - beforeDays;
        long acquireAuthDays = LocalDateTimeUtil.between(clientAuthority.getCreateTime().toLocalDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
        if (acquireAuthDays < remindDays) {
            log.info("客户：{}无需提前{}天进行提醒-获取管护权至今仅{}天，未超过释放条件的要求天数：{}天,", clientId, beforeDays, acquireAuthDays, criticalDays1);
            return clientReleaseRemindDto;
        }
        //大条件先写，节约性能，无需后续判断
        // 兜底逻辑
        Map<Long, Long> map = clientService.clientStockRiskExposureMap(Collections.singletonList(clientId));
        //剩余未还金额
        Long balance = Optional.ofNullable(map.get(clientId)).orElse(0L);
        if (balance > 0) {
            log.info("客户：{}无需提前{}天进行提醒-风险淌口{}> 0", clientId, beforeDays, balance);
            return clientReleaseRemindDto;
        }
        // 收集各阶段业务信息(这里查询的 项目、评审、合同 的主办人 = 客户管护人)
        ClientAsLesseeInfoDTO clientAsLesseeInfoDTO = clientService.collectInfo(clientId, clientAuthority);
        // 立项阶段条件 - 目前未有立项审批通过的项目，系统自动释放该客户权限(结合前置大条件 获取)
        boolean establishFlag = this.checkProjEstablish(clientAuthority, clientAsLesseeInfoDTO, beforeDays, criticalDays1, acquireAuthDays);
        // 评审阶段条件1 - 项目立项流程审批通过生效后，超过90天项目评审流程中部门分管领导未审批通过，则系统系统自动释放该客户权限
        Long projEstablishId = this.checkProjReview1(clientAsLesseeInfoDTO, beforeDays, criticalDays2);
        // 评审阶段条件2 - 项目评审流程审批通过的客户，如超过365天未实际投放，则系统系统自动释放该客户权限
        Long projReviewId = this.checkProjReview2(clientAsLesseeInfoDTO, beforeDays, criticalDays3);
        //合同阶段条件 - 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
        boolean contractFlag = this.checkContract(clientAsLesseeInfoDTO, beforeDays, criticalDays4);
        boolean reviewFlag1 = Objects.nonNull(projEstablishId);
        boolean reviewFlag2 = Objects.nonNull(projReviewId);
        // 满足一个条件即释放客户
        log.info("客户第一次释放判断结果[立项:{}, 评审1:{}, 评审2:{}, 合同:{}]", establishFlag, reviewFlag1, reviewFlag2, contractFlag);
        if (establishFlag || reviewFlag1 || reviewFlag2 || contractFlag) {
            //二次释放判断 这里不是很理解，目前就是立项、评审取值来源发生改变，其他没区别
            clientReleaseRemindDto = this.releaseClientDoubleCheck(clientReleaseRemindDto, client, clientAuthority, acquireAuthDays, beforeDays, criticalDays1, criticalDays2, criticalDays3, criticalDays4);
        }
        return clientReleaseRemindDto;
    }

    /**
     * 客户释放前进行二次判断（二次判断作为逻辑漏洞的补丁，是后续补充的，为了不改动原有已经覆盖绝大多数场景的逻辑，故不直接在原逻辑上改动）
     * 补丁用于解决在后一阶段才被纳入交易结构的场景（比如说立项没有，评审才有）
     *
     */
    private ClientReleaseRemindDTO releaseClientDoubleCheck(ClientReleaseRemindDTO clientReleaseRemindDto, Client client, ClientAuthority clientAuthority, long acquireAuthDays,
                                                            int beforeDays, int criticalDays1, int criticalDays2, int criticalDays3, int criticalDays4) {
        Set<Long> projEstablishIds = new HashSet<>();
        Set<Long> projReviewIds = new HashSet<>();
        // 合同
        Set<Long> contractIds = SpringUtil.getBean(ContractTradeStructureService.class).listContractIdsByClientId(client.getId());
        List<ContractBaseInfo> contractBaseInfoList;
        if (CollectionUtil.isNotEmpty(contractIds)) {
            // contractBaseInfoList = contractBaseInfoService.listByIds(contractIds);
            // 查询项目主办为管护权人的合同信息
            contractBaseInfoList = contractBaseInfoService.list(
                    Wrappers.<ContractBaseInfo>lambdaQuery()
                            .in(ContractBaseInfo::getId, contractIds)
                            .eq(ContractBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
            projReviewIds.addAll(contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet()));
        } else {
            contractBaseInfoList = Collections.emptyList();
        }
        // 评审
        List<ProjReviewBaseInfo> projReviewBaseInfoList;
        projReviewIds.addAll(SpringUtil.getBean(ProjReviewTradeStructureService.class).listProjReviewIdsByClientId(client.getId()));
        if (CollectionUtil.isNotEmpty(projReviewIds)) {
            //            projReviewBaseInfoList = projReviewBaseInfoService.listByIds(projReviewIds);
            // 查询项目主办为管护权人的评审信息
            projReviewBaseInfoList = projReviewBaseInfoService.list(
                    Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                            .in(ProjReviewBaseInfo::getId, projReviewIds)
                            .eq(ProjReviewBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
            projEstablishIds.addAll(projReviewBaseInfoList.stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet()));
        } else {
            projReviewBaseInfoList = Collections.emptyList();
        }
        // 立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
        projEstablishIds.addAll(SpringUtil.getBean(ProjEstablishTradeStructureService.class).listProjEstablishIdsByClientId(client.getId()));
        if (CollectionUtil.isNotEmpty(projEstablishIds)) {
            //            projEstablishBaseInfoList = projEstablishBaseInfoService.listByIds(projEstablishIds);
            // 查询项目主办为管护权人的立项信息
            projEstablishBaseInfoList = projEstablishBaseInfoService.list(
                    Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                            .in(ProjEstablishBaseInfo::getId, projEstablishIds)
                            .eq(ProjEstablishBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
        } else {
            projEstablishBaseInfoList = Collections.emptyList();
        }
        // 重新过一遍释放逻辑
        ClientAsLesseeInfoDTO clientAsLesseeInfoDTO = new ClientAsLesseeInfoDTO();
        clientAsLesseeInfoDTO.setProjEstablishBaseInfoList(projEstablishBaseInfoList);
        clientAsLesseeInfoDTO.setProjReviewBaseInfoList(projReviewBaseInfoList);
        clientAsLesseeInfoDTO.setContractBaseInfoList(contractBaseInfoList);
        // 立项阶段条件 - 目前未有立项审批通过的项目，系统自动释放该客户权限(结合前置大条件 获取)
        boolean establishFlag = this.checkProjEstablish(clientAuthority, clientAsLesseeInfoDTO, beforeDays, criticalDays1, acquireAuthDays);
        // 评审阶段条件1 - 项目立项流程审批通过生效后，超过90天项目评审流程中部门分管领导未审批通过，则系统系统自动释放该客户权限
        Long projEstablishId = this.checkProjReview1(clientAsLesseeInfoDTO, beforeDays, criticalDays2);
        // 评审阶段条件2 - 项目评审流程审批通过的客户，如超过365天未实际投放，则系统系统自动释放该客户权限
        Long projReviewId = this.checkProjReview2(clientAsLesseeInfoDTO, beforeDays, criticalDays3);
        //合同阶段条件 - 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
        boolean contractFlag = this.checkContract(clientAsLesseeInfoDTO, beforeDays, criticalDays4);
        boolean reviewFlag1 = Objects.nonNull(projEstablishId);
        boolean reviewFlag2 = Objects.nonNull(projReviewId);
        // 满足一个条件即释放客户
        log.info("客户二次释放判断结果[立项:{}, 评审1:{}, 评审2:{}, 合同:{}]", establishFlag, reviewFlag1, reviewFlag2, contractFlag);
        List<String> sourceList = new ArrayList<>();
        if (establishFlag) {
            sourceList.add(ClientRemindContentEnum.EMAIL_01.getSource());
        }
        if (reviewFlag1) {
            sourceList.add(ClientRemindContentEnum.EMAIL_02.getSource());
        }
        if (reviewFlag2) {
            sourceList.add(ClientRemindContentEnum.EMAIL_03.getSource());
        }
        if (contractFlag) {
            sourceList.add(ClientRemindContentEnum.EMAIL_04.getSource());
        }
        if (reviewFlag1||reviewFlag2||contractFlag){
            if (CollectionUtil.isNotEmpty(projEstablishBaseInfoList)) {
                Optional<ProjEstablishBaseInfo> latestProj = projEstablishBaseInfoList.stream()
                        .filter(proj -> proj.getCreateTime() != null &&  Objects.equals(proj.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name()))
                        .max(Comparator.comparing(ProjEstablishBaseInfo::getCreateTime));
                clientReleaseRemindDto.setProjEstablishId(latestProj.orElse(new ProjEstablishBaseInfo()).getId());
            }
        }

        clientReleaseRemindDto.setSourceList(sourceList);
        return clientReleaseRemindDto;
    }


    /**
     * 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
     *
     * @param clientAsLesseeInfoDTO
     * @param beforeDays            提前天数提醒
     * @param criticalDays          释放天数
     */
    private boolean checkContract(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO, int beforeDays, int criticalDays) {
        int limitDays = criticalDays - beforeDays;
        List<ContractBaseInfo> contractBaseInfoList = clientAsLesseeInfoDTO.getContractBaseInfoList();
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            log.info("checkContract-不满足{}天后释放客户，-不存在合同", beforeDays);
            return false;
        }
        List<ContractBaseInfo> filterList = contractBaseInfoList.stream()
                .filter(e -> (Objects.equals(e.getContractStatus(), ContractStatus.SETTLE.name())
                        || Objects.equals(e.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())))
                .collect(Collectors.toList());
        // 存在在途流程
        if (contractBaseInfoList.size() != filterList.size()) {
            log.info("checkContract-不满足{}天后释放客户，-存在非结清的合同", beforeDays);
            return false;
        }
        //寻找最晚结清的合同-的结清时间
        long lastSettleTimestamp = 0L;
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            // 找结清流程
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(contractBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.ContractEarlySettleFlow.name(), ProcessModelTypeEnum.ContractNormalSettleFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.nonNull(processResult) && CollectionUtil.isNotEmpty(processResult.getContents())) {
                List<ProcessResp> list = processResult.getContents();
                list.sort(Comparator.comparing(ProcessResp::getEndTime, Comparator.nullsLast(Comparator.reverseOrder())));
                lastSettleTimestamp = Math.max(lastSettleTimestamp, list.get(0).getEndTime().getTime());
            }
        }
        // 判断一下最晚的结清时间到现在是否已经超过limitDays天
        long days = LocalDateTimeUtil.between(LocalDateTimeUtil.of(lastSettleTimestamp).toLocalDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
        if (days < limitDays) {
            log.info("checkContract-不满足{}天后释放客户，-合同结清审批通过仅{}天", limitDays, days);
            return false;
        }
        // 这里满足结清天数，然后看有没有判断一下除结清合同对应的项目之外是否有生效的立项（一般翻单会在结清当前合同之前做）
        List<Long> projReviewIds = contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList());
        List<ProjReviewBaseInfo> prbiList = projReviewBaseInfoService.listByIds(projReviewIds);
        if (CollectionUtil.isNotEmpty(prbiList)) {
            Set<Long> candidateIdList = prbiList.stream().filter(e -> Objects.nonNull(e.getProjEstablishId())).map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(clientAsLesseeInfoDTO.getProjEstablishBaseInfoList())) {
                Set<Long> ids = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList().stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toSet());
                ids.removeIf(candidateIdList::contains);
                if (CollectionUtil.isNotEmpty(ids)) {
                    log.info("checkContract-不满足{}天后释放客户，-存在结清合同对应项目之外其他生效的立项", limitDays);
                    return false;
                }
            }
        }
        log.info("contractStage-释放客户-合同结清审批通过后{}天不存在审批通过的立项", limitDays);
        return days == limitDays;
    }


    /**
     * 项目评审流程审批通过的客户，如超过365天未实际投放，则系统系统自动释放该客户权限
     *
     * @param clientAsLesseeInfoDTO
     * @param beforeDays            提前天数提醒
     */
    private Long checkProjReview2(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO, int beforeDays, int criticalDays) {
        int limitDays = criticalDays - beforeDays;
        List<ProjReviewBaseInfo> projReviewBaseInfoList = clientAsLesseeInfoDTO.getProjReviewBaseInfoList();
        if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
            log.info("checkProjReview2-不满足{}天后释放客户，不存在评审信息", beforeDays);
            return null;
        }
        // 过滤出生效评审
        List<ProjReviewBaseInfo> filterList = projReviewBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name())).collect(Collectors.toList());
        //存在新建/在途的 直接不满足
        if (filterList.size() != projReviewBaseInfoList.size()) {
            log.info("checkProjReview2-不满足{}天后释放客户，存在在途评审流程", beforeDays);
            return null;
        }
        // 剔除已结清的合同
        filterList.removeIf(e -> {
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(e.getId()));
            if (CollectionUtil.isEmpty(contractBaseInfos)) {
                return false;
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                if (!Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && !Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())) {
                    return false;
                }
            }
            return true;
        });
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("checkProjReview2-不满足{}天后释放客户，-不存在非结清合同的生效评审", beforeDays);
            return null;
        }
        Long projReviewId = null;
        // 遍历判断生效评审
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            // 生效评审找流程通过时间
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(projReviewBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                //找不到流程--可能存量数据？
                log.info("checkProjReview2-不满足不释放条件-评审生效但找不到评审对应的流程[projReviewId:{}]", projReviewBaseInfo.getId());
                return null;
            }
            //按结束时间排序
            processResult.getContents().sort(Comparator.comparing(ProcessResp::getEndTime, Comparator.nullsLast(Comparator.reverseOrder())));
            ProcessResp processResp = processResult.getContents().get(0);
            if (Objects.isNull(processResp.getEndTime())) {
                //有流程但是
                log.info("checkProjReview2-不满足不释放条件-评审生效但找不到评审对应的流程[projReviewId:{}]", projReviewBaseInfo.getId());
                return null;
            }
            LocalDate reviewPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
            // 存在一条评审生效时间<365-7 无需提醒
            long days = LocalDateTimeUtil.between(reviewPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
            if (days < limitDays) {
                log.info("checkProjReview2-不满足{}天后释放客户，-存在生效未超过{}天的评审[projReviewId:{}]", limitDays, projReviewBaseInfo.getId());
                return null;
            }
            boolean remindFalg = days == limitDays;
            // 找评审对应的合同
            List<ContractBaseInfo> cbiList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(projReviewBaseInfo.getId()));
            if (CollectionUtil.isEmpty(cbiList)) {
                if (remindFalg) {
                    projReviewId = projReviewBaseInfo.getId();
                }
                continue;
            }
            //找实际付款  没有投放就下一轮
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByContractIds(cbiList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));
            if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                if (remindFalg) {
                    projReviewId = projReviewBaseInfo.getId();
                }
                continue;
            }
            //找第一笔付款，理论上说存在付款那么久已经投放了  但是取了实际支付日期，保持同释放逻辑。
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            PaymentActualDetail paymentActualDetail = paymentActualDetailList.get(0);
            LocalDate earliestPaidDate = paymentActualDetail.getPaidInDate();
            //评审通过后-实际支付日期
            long actPayDays = LocalDateTimeUtil.between(reviewPassDate.atStartOfDay(), earliestPaidDate.atStartOfDay(), ChronoUnit.DAYS);
            if (actPayDays < limitDays) {
                log.info("checkProjReview2-不满足{}天后释放客户，-存在生效的评审在{}天内发生了实际投放projReviewId:{}, paymentActualDetailId:{}", limitDays, actPayDays, projReviewBaseInfo.getId(), paymentActualDetail.getId());
                return null;
            }
            //能进入都是满足了
            if (remindFalg) {
                projReviewId = projReviewBaseInfo.getId();
            }
        }
        log.info("checkProjReview2-释放客户-所有生效评审超过{}天时都没有发生实际投放，", limitDays);
        return projReviewId;
    }


    /**
     * 项目立项流程审批通过生效后，超过90天项目评审流程中部门分管领导未审批通过，则系统系统自动释放该客户权限
     *
     * @param beforeDays            提前天数提醒
     * @param clientAsLesseeInfoDTO
     * @return 符合条件立项id--找到其中一条用户超链接使用
     */

    private Long checkProjReview1(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO, int beforeDays, int criticalDays) {
        int limitDays = criticalDays - beforeDays;
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList();
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            log.info("checkProjReview1-不存在任何立项-无需提醒");
            return null;
        }
        // 过滤出生效的立项-- 这里去掉 项目主办= 客户主办 [前人未交待，不知道为啥]
        List<ProjEstablishBaseInfo> filterList = projEstablishBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("checkProjReview1-不存在任何生效的立项-无需提醒");
            return null;
        }
        // 把结清的剔除
        filterList.removeIf(e -> {
            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(e.getId()));
            if (CollectionUtil.isEmpty(projReviewBaseInfos)) {
                return false;
            }
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            if (CollectionUtil.isEmpty(contractBaseInfos)) {
                return false;
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                if (!Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && !Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())) {
                    return false;
                }
            }
            return true;
        });
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("checkProjReview1-关联生效立项对应的合同均已结清-无需提醒");
            return null;
        }
        Long projEstablishId = null;
        // 遍历判断
        for (ProjEstablishBaseInfo projEstablishBaseInfo : filterList) {
            // 生效立项找流程通过时间
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(projEstablishBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name(),
                    ProcessModelTypeEnum.ProjEstablishModifyFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                log.info("checkProjReview1-生效立项无流程数据[projEstablishId:{}] 无需提醒", projEstablishBaseInfo.getId());
                return null;
            }
            // 释放那边结束时间倒排 这里其实如果endtime 为空就空指针了！！ 如果为空异常数据，放最后面吧
            processResult.getContents().sort(Comparator.comparing(ProcessResp::getEndTime, Comparator.nullsLast(Comparator.reverseOrder())));
            ProcessResp processResp = processResult.getContents().get(0);
            if (Objects.isNull(processResp.getEndTime())) {
                log.info("checkProjReview1-不释放客户-生效立项对应流程通过时间为空[projEstablishId:{}]，无需提醒！", projEstablishBaseInfo.getId());
                return null;
            }
            LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
            // 判断立项生效到现在多少天了，只要一条不超过额 那么就是无需提醒
            long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
            if (days < limitDays) {
                log.info("checkProjReview1-存在生效仅{}天的立项[projEstablishId:{}],无需提醒！", days, projEstablishBaseInfo.getId());
                return null;
            }
            // 找评审  不存在就继续找寻下一立项
            List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(projEstablishBaseInfo.getId()));
            if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
                //没有评审记录一下
                if (days == limitDays) {
                    projEstablishId = projEstablishBaseInfo.getId();
                }
                continue;
            }
            ProjReviewBaseInfo review = projReviewBaseInfoList.get(0);
            boolean reviewEffect = Objects.equals(review.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name());
            // 找评审创建流程
            ProcessPageReq reviewProcessReq = new ProcessPageReq();
            reviewProcessReq.setBusinessKey(review.getId().toString());
            reviewProcessReq.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
            reviewProcessReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> reviewResult = flowTaskApiService.queryProcess(reviewProcessReq);
            //如果找不到流程
            if (Objects.isNull(reviewResult) || CollectionUtil.isEmpty(reviewResult.getContents())) {
                if (reviewEffect) {
                    log.info("checkProjReview1-不释放客户-虽然没找到评审对应的在途流程，但是评审已经生效，考虑历史数据不进行释放");
                    return null;
                }
                //异常数据又不是生效记录一下
                if (days == limitDays) {
                    projEstablishId = projEstablishBaseInfo.getId();
                }
                continue;
            }
            ProcessResp pr = reviewResult.getContents().get(0);
            // 找流程实例中部门分管领导最早一次通过时间
            TaskSystemPageReq taskSystemPageReq = new TaskSystemPageReq();
            taskSystemPageReq.setProcessInstanceId(pr.getProcessInstanceId());
            taskSystemPageReq.setActivityId(USERTASK_BIZDIVISIONLEADER);
            cn.zswltech.flow.core.util.Page<TaskResp> taskRespPage = flowTaskApiService.querySystemTask(taskSystemPageReq);
            // 找不到分管领导节点的数据
            if (Objects.isNull(taskRespPage) || CollectionUtil.isEmpty(taskRespPage.getContents())) {
                if (reviewEffect) {
                    log.info("checkProjReview1-不释放客户-虽然没找到{}天内通过分管领导节点的数据，但是评审已经生效，考虑历史数据不进行释放", limitDays);
                    return null;
                }
                //找不到可能是还没提交到这一岗
                if (days == limitDays) {
                    projEstablishId = projEstablishBaseInfo.getId();
                }
                continue;
            }
            List<TaskResp> taskRespList = taskRespPage.getContents();
            // 只取审批通过的分管领导任务节点
            taskRespList.removeIf(e -> !Objects.equals(e.getTaskStatus(), TaskBusinessStatusEnum.PASS.getStatus()));
            taskRespList.sort(Comparator.comparing(TaskResp::getTaskEndTime));
            if (CollectionUtil.isNotEmpty(taskRespList)) {
                log.info("checkProjReview1-分管领导已经审批通过-无需提醒");
                return null;
            }
            //兜底-能到这里说明是超过了提醒的时候，但是仅在释放前7天才提醒！！
            if (days == limitDays) {
                projEstablishId = projEstablishBaseInfo.getId();
            }
        }
        //假如里面不符合不会走到这里
        log.info("checkProjReview1-释放客户-所有生效立项对应评审任务在立项生效后{}天内都没有通过分管领导节点,projEstablishId:{}", limitDays, projEstablishId);
        return projEstablishId;
    }


    /**
     * 立项阶段条件 - 获得客户管护权后，超过60天仍未有立项审批通过的项目，系统自动释放该客户权限
     *
     * @param clientAsLesseeInfoDTO
     * @param clientAuthority
     * @param beforeDays            提前天数
     * @param criticalDays          释放要求天数
     * @param acquireAuthDays       获取管护权天数
     */
    private boolean checkProjEstablish(ClientAuthority clientAuthority, ClientAsLesseeInfoDTO clientAsLesseeInfoDTO,
                                       int beforeDays, int criticalDays, long acquireAuthDays) {
        int limitDays = criticalDays - beforeDays;
        boolean dayFlag = acquireAuthDays == limitDays;
        log.info("checkProjEstablish-客户：{} 今天是否符合提醒天数：{}", clientAuthority.getClientId(), dayFlag);
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList();
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            log.info("checkProjEstablish-客户：{}无关联立项", clientAuthority.getClientId());
            return dayFlag;
        }
        //取生效 && 项目主办经理=客户管护经理的
        List<ProjEstablishBaseInfo> filterList = projEstablishBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId()))
                .filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("checkProjEstablish-客户：{}获取管护权至今仅{}天，没有找到任何项目状态=生效、项目主办经理=客户管护经理的立项", clientAuthority.getClientId(), limitDays);
            return dayFlag;
        }
        log.info("checkProjEstablish-客户：{}无需提醒，-获取管护权{}天时存在生效的立项", clientAuthority.getClientId(), acquireAuthDays);
        return false;
    }


    /**
     * 查询全量权利
     */
    private List<ClientAuthority> querryClientAuthorityList(String clientId) {
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        if (StringUtils.isNotBlank(clientId)) {
            query.eq(ClientAuthority::getClientId, clientId);
        }
        return clientAuthorityService.list(query);
    }

}
