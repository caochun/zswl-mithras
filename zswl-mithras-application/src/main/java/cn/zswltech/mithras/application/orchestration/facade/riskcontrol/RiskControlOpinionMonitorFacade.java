package cn.zswltech.mithras.application.orchestration.facade.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.riskcontrol.application.RiskControlOpinionMonitorApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.riskcontrol.flow.dynamicform.risk.opinion.RiskOpinionHandleCheckHandler;
import cn.zswltech.mithras.system.mapper.SystemConfigMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.system.mapper.model.SystemConfig;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionVersionService;
import cn.zswltech.mithras.third.riskopinion.client.req.RiskControlOpinionPullListREQ;
import cn.zswltech.mithras.third.riskopinion.client.resp.RiskControlOpinionPullListRsp;
import cn.zswltech.mithras.riskcontrol.util.IdGeneratorUtils;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import static cn.hutool.core.util.ObjectUtil.isNotEmpty;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.CLOSED;
import static cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus.*;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static cn.zswltech.mithras.foundation.state.ProjProcessState.APPROVAL_REJECT;
import static cn.zswltech.mithras.foundation.state.ProjProcessState.NEW_REJECT;
import static java.util.Objects.isNull;

/**
 * @author vico
 * @description risk_control_opinion_monitor
 * @date 2023-03-09
 */
@Service
@Slf4j
public class RiskControlOpinionMonitorFacade implements RiskControlOpinionMonitorApplicationService {

    private final static String YQJK = "舆情监控";
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;
    @Value("${xinsight.ips.primary}")
    private String XinsightIp;
    private final static String config_key = "FHC_OPINION_SWITCH";
    private final static String config_value = "CLOSE";
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private IdGeneratorUtils idGenerator;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Void> handleOpinion(RiskControlOpinionHandleREQ req) {
        RiskControlOpinionMonitor record = riskControlOpinionMonitorService.getById(req.getId());
        err(isNull(record), RECORD_NOT_EXIST);
        RiskControlOpinionHandleStatus status = RiskControlOpinionHandleStatus.of(record.getHandleStatus());
        err(!equalsAny(record.getHandleStatus(), PEND_HANDLE.name(), REJECTED.name()),
                String.format("该舆情【%s】，无需处置", null == status ? "未知状态" : status.display));
        riskControlOpinionMonitorService.handle(req.getId(), req.getAdvisement());
        return R.ok();
    }

    @Override
    public R<Void> fixAdvisement(RiskControlOpinionHandleREQ req) {
        RiskControlOpinionMonitor record = riskControlOpinionMonitorService.getById(req.getId());
        err(isNull(record), RECORD_NOT_EXIST);
        if (ObjectUtil.isNotEmpty(req.getHandleResult()) && ObjectUtil.isNotEmpty(req.getProcessInstanceId())) {
            getBean(RuntimeService.class).setVariable(req.getProcessInstanceId(), RiskOpinionHandleCheckHandler.HANDLE_TYPE, req.getHandleResult());
        }
       /* RiskControlOpinionHandleStatus status = RiskControlOpinionHandleStatus.of(record.getHandleStatus());
        err(!equalsAny(record.getHandleStatus(), HANDLE_ING.name()), "该舆情不在处置中");*/
        riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setId(req.getId()).setAdvisement(req.getAdvisement()).setHandleResult(req.getHandleResult()));
        return R.ok();
    }

    @Override
    public R<PageR<RiskControlOpinionMonitorListRSP>> list(RiskControlOpinionMonitorListREQ req) {
        // 获取创建人ID
        Long accountId = AccountUtil.getLoginInfo().getId();
        Page<RiskControlOpinionMonitor> data = riskControlOpinionMonitorService.list(req,accountId);
        List<RiskControlOpinionMonitor> records = data.getRecords();
        // 获取创建人名称
        Set<Long> creatorIdList = records.stream().map(RiskControlOpinionMonitor::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> nameMap = id2NameService.sysUserId2Name(creatorIdList);
        // 转换实体类
        List<RiskControlOpinionMonitorListRSP> list = copyToList(records, RiskControlOpinionMonitorListRSP.class);
        //此处做变更，基础舆情和工商舆情的titleTarget进行区分处理
        list.forEach(e -> {
            if (e.getDataSource().equals(RiskDataSourceEnum.FHC.name())){
                // 原 金控数据逻辑
                if (e.getRiskType()==1){
                    e.setTitleTarget(String.format("https://www.baidu.com/s?wd=%s", Optional.ofNullable(e.getTitle()).map(URLUtil::encode).orElse("")));
                }else if (e.getRiskType()==2){
                    e.setTitleTarget(String.format("https://www.baidu.com/s?wd=%s", Optional.ofNullable(e.getChiName()).map(URLUtil::encode).orElse("")));
                }
            }else if (e.getDataSource().equals(RiskDataSourceEnum.XINSIGHT.name())){
                // 慧眼数据处理逻辑
                // 目前慧眼只有正式环境, 拼接上url, 可以访问舆情数据详情页面
                e.setNewsUrl(XinsightIp + e.getNewsUrl());
            }else {
                // 人工录入舆情
                e.setTitleTarget(e.getLinkAddress());
            }
            // 人工新增舆情需求：增加返回字段
            e.setOpinionType(source2Type(e.getDataSource()));
            e.setCreateName(nameMap.get(e.getCreateBy()));
            // 编辑跟删除操作权限判定
            if (ObjectUtil.equals(accountId, e.getCreateBy()) && ObjectUtil.equals(UNSUBMITTED.name(), e.getHandleStatus())) {
                e.setOperableFlag(YesOrNoNumberEnum.YES.getCode());
            } else {
                e.setOperableFlag(YesOrNoNumberEnum.NO.getCode());
            }
        });
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    private String source2Type(String dataSource){
        if (dataSource.equals(RiskDataSourceEnum.MANUAL.name())){
            return RiskDataSourceEnum.MANUAL_ENTRY.display();
        }else {
            return RiskDataSourceEnum.AUTOMATIC_IMPORT.display();
        }
    }

    /**
     * 需要关注舆情的客户列表
     *
     * @return 统一社会信用代码集合
     */
    public Set<String> focusClientUscCodes() {
        //进行中的合同
        List<ContractBaseInfo> tradingContracts = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(),
                        ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
        //存续合同所有承租人、联合承租人
        Set<Long> clientIdSet = getBean(ContractTenantryService.class).listByContractIds(tradingContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
        //存续担保人
        getBean(ContractGuarantorService.class).listByContractIds(tradingContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())).forEach(e -> {
            String guarantorIds = e.getGuarantorIds();
            if (isNotBlank(guarantorIds)) {
                clientIdSet.addAll(JSON.parseArray(guarantorIds, Long.class));
            }
        });
        //评审阶段的承租人和担保人也需要关注
        List<ProjReviewBaseInfo> allList = getBean(ProjReviewBaseInfoService.class).list();
        List<ProjReviewBaseInfo> reviewList = allList.stream().filter(e ->
                        !(CLOSED.name().equals(e.getProjReviewStatus()) || RecordStatus.EXPIRE.name().equals(e.getProjReviewStatus())) &&
                                !equalsAny(e.getProjReviewProcessStatus(), NEW_REJECT.name(), APPROVAL_REJECT.name()))
                .collect(Collectors.toList());

        for (ProjReviewBaseInfo baseInfo : reviewList) {
            if (StrUtil.isNotBlank(baseInfo.getLesseeInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getLesseeInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if (StrUtil.isNotBlank(baseInfo.getCreditorInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getCreditorInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if (StrUtil.isNotBlank(baseInfo.getGuaranteeInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
        }
        return getBean(ClientService.class).listByIds(clientIdSet).stream().map(Client::getUscCode).collect(Collectors.toSet());
    }

    /**
     * 需要关注舆情的客户列表 - 已放款的合同,且风险敞口不为零
     *
     * @return 统一社会信用代码集合
     */
    public Set<String> focusClientUscCodesHasExposure() {
        //进行中的合同
        List<ContractBaseInfo> tradingContracts = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(),
                        ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
        if (ObjectUtil.isEmpty(tradingContracts)) {
            return new HashSet<>();
        }
        //且风险敞口不为零
        Map<Long, Long> stockRiskExposureByContracts = getBean(ContractBaseInfoService.class).getStockRiskExposureByContracts(tradingContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));
        Set<Long> contractIdSet = new HashSet<>();
        stockRiskExposureByContracts.forEach((k, v) -> {
            if (!ObjectUtil.equals(v, 0L)) {
                contractIdSet.add(k);
            }
        });
        tradingContracts.removeIf(e -> !contractIdSet.contains(e.getId()));
        //存续合同所有承租人、联合承租人
        Set<Long> clientIdSet = getBean(ContractTenantryService.class).listByContractIds(tradingContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
        //存续担保人
        getBean(ContractGuarantorService.class).listByContractIds(tradingContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())).forEach(e -> {
            String guarantorIds = e.getGuarantorIds();
            if (isNotBlank(guarantorIds)) {
                clientIdSet.addAll(JSON.parseArray(guarantorIds, Long.class));
            }
        });
        //评审阶段的承租人和担保人也需要关注
        List<ProjReviewBaseInfo> allList = getBean(ProjReviewBaseInfoService.class).list();
        List<ProjReviewBaseInfo> reviewList = allList.stream().filter(e ->
                !(CLOSED.name().equals(e.getProjReviewStatus()) || RecordStatus.EXPIRE.name().equals(e.getProjReviewStatus())) &&
                        !equalsAny(e.getProjReviewProcessStatus(), NEW_REJECT.name(), APPROVAL_REJECT.name()))
                .collect(Collectors.toList());

        for (ProjReviewBaseInfo baseInfo : reviewList) {
            if (StrUtil.isNotBlank(baseInfo.getLesseeInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getLesseeInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if (StrUtil.isNotBlank(baseInfo.getCreditorInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getCreditorInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if (StrUtil.isNotBlank(baseInfo.getGuaranteeInfo())) {
                List<ProjEstablishPersonInfo> personInfos = JSON.parseArray(baseInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class);
                clientIdSet.addAll(personInfos.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
        }
        return getBean(ClientService.class).listByIds(clientIdSet).stream().map(Client::getUscCode).collect(Collectors.toSet());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> notice(@Valid RiskControlOpinionNoticeReq req) {
        // 通过开关控制是否接受金控舆情数据
        if (getSwitchClose()){
            log.info("金控舆情开关已关闭,不在接收金控数据");
            return R.ok();
        }
        Set<String> focusClientUscCodes = focusClientUscCodes();

        //增量查询数据
        RiskControlOpinionPullListREQ riskControlOpinionPullListREQ = new RiskControlOpinionPullListREQ();
        riskControlOpinionPullListREQ.setId(req.getStartId());
        PlatformApiHandler<RiskControlOpinionPullListREQ, RiskControlOpinionPullListRsp> platformApiHandler =
                platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.PO_LIST);
        RiskControlOpinionPullListRsp pullListRsp = platformApiHandler.execute(riskControlOpinionPullListREQ);

        if (ObjectUtil.equal(pullListRsp.getSuccess(), Boolean.TRUE)) {
            List<RiskControlOpinionMonitor> list = copyToList(pullListRsp.getData(), RiskControlOpinionMonitor.class);
            if (isNotEmpty(list)) {
                list.forEach(
                        e -> {
                            //没有存量合作合同的客户的舆情，也不用处理
                            if (e.getWarnStar() < 2 || !focusClientUscCodes.contains(e.getCreditCode())) {
                                e.setHandleStatus(IGNORED.name());
                            } else {
                                e.setHandleStatus(PEND_HANDLE.name());
                            }
                        }
                );

                //过滤掉系统已经存在的；不存在的才插入
                Set<Long> existIdSet = riskControlOpinionMonitorService.listByIds(list.stream().map(RiskControlOpinionMonitor::getId).collect(Collectors.toList()))
                        .stream().map(RiskControlOpinionMonitor::getId).collect(Collectors.toSet());
                list = list.stream().filter(e -> !existIdSet.contains(e.getId())).collect(Collectors.toList());
                if (!list.isEmpty()) {
                    riskControlOpinionMonitorService.saveBatch(list);
                    notice2BizPersonIfNeeded(list);
                    //这里舆情发起流程，通知对应风控经理
                    riskControlOpinionVersionService.opinionInitiateApproval(list);
                }
            }
        } else {
            err("获取舆情信息失败");
        }
        return R.ok();
    }

    @Override
    public R<Void> send(RiskControlOpinionSendReq req) {
        List<RiskControlOpinionMonitor> riskControlOpinionMonitors = riskControlOpinionMonitorService.listByIds(req.getIds());
        notice2BizPersonIfNeeded(riskControlOpinionMonitors);
        return R.ok();
    }

    @Override
    public R<PageR<UnresolvedClientOpinionRSP>> unresolvedList(UnresolvedClientOpinionREQ req) {
//        Long currentUserId = AccountUtil.getLoginInfo().getId();
//        Page<RiskControlOpinionMonitor> data = riskControlOpinionMonitorService.getBaseMapper()
//                .listBySponsor(new Page<>(req.getPage(), req.getPageSize()), currentUserId);
        Page<RiskControlOpinionMonitor> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<RiskControlOpinionMonitor> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.and(true, innerQuery ->
                innerQuery.eq(RiskControlOpinionMonitor::getHandleStatus, PEND_HANDLE.name())
                .or()
                .eq(RiskControlOpinionMonitor::getHandleStatus, REJECTED.name())
        );
        riskControlOpinionMonitorService.fillUscCodesByJob(conditionQuery, true);
        Page<RiskControlOpinionMonitor> data = riskControlOpinionMonitorService.page(pageQuery, conditionQuery);
        List<UnresolvedClientOpinionRSP> list = copyToList(data.getRecords(), UnresolvedClientOpinionRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<RiskControlOpinionMonitorListRSP> detail(SinglePkREQ req) {
        RiskControlOpinionMonitor byId = riskControlOpinionMonitorService.getById(req.getId());
        RiskControlOpinionMonitorListRSP rsp = BeanUtil.copyProperties(byId, RiskControlOpinionMonitorListRSP.class);
        if (ObjectUtil.isNotEmpty(rsp) && ObjectUtil.isNotEmpty(rsp.getDataSource())){
            if (rsp.getDataSource().equals(RiskDataSourceEnum.XINSIGHT.name())){
                // 慧眼数据处理逻辑: IP拼接上url, 可以访问慧眼舆情数据详情页面
                rsp.setNewsUrl(XinsightIp + rsp.getNewsUrl());
            }
        }
        return R.ok(rsp);
    }

    @Override
    public R<String> view(SinglePkREQ req) {
        return R.ok(riskControlOpinionMonitorService.getProcessInstanceId(req.getId()));
    }

    @Override
    public R<Integer> countByClientId(@Valid ClientIdREQ req) {
        return R.ok(riskControlOpinionMonitorService.countByClientId(req.getClientId()));
    }

    public void notice2BizPersonIfNeeded(List<RiskControlOpinionMonitor> list) {
        //获取数据库的数据，有状态字段
        list = riskControlOpinionMonitorService.listByIds(list.stream().map(RiskControlOpinionMonitor::getId).collect(Collectors.toList()));
        //只发送星级高的
        List<RiskControlOpinionMonitor> level3List = list.stream()
                .filter(e -> isNotNull(e.getWarnStar()) && e.getWarnStar() >= 2).collect(Collectors.toList());
        if (level3List.isEmpty()) {
            return;
        }


        Set<String> createCodes = level3List.stream().map(RiskControlOpinionMonitor::getCreditCode).collect(Collectors.toSet());
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getUscCode, createCodes));

        Map<Long, Set<Long>> clientRelatedSponsorMap = new HashMap<>();
        //客户的主办
        clients.forEach(client -> {
            clientRelatedSponsorMap.putIfAbsent(client.getId(), new HashSet<>());
            clientRelatedSponsorMap.get(client.getId()).add(client.getBelongSponsorId());
        });

        /*level3List.forEach(item -> {
            Set<Long> target = clientRelatedSponsorMap.get(clientMap.get(item.getCreditCode()));
            if (isNotEmpty(target)) {
                if (Boolean.FALSE.equals(item.getNoticed())) {
                    MessageAddREQ messageAddREQ = buildMessage(ListUtil.toList(target), item);
                    messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
                }
            }
        });
        riskControlOpinionMonitorService.update(new RiskControlOpinionMonitor().setNoticed(true),
                Wrappers.<RiskControlOpinionMonitor>lambdaUpdate().in(
                        RiskControlOpinionMonitor::getId, level3List.stream().map(RiskControlOpinionMonitor::getId).collect(Collectors.toList()))
        );*/
    }

    private MessageAddREQ buildMessage(List<Long> to, RiskControlOpinionMonitor riskControlOpinionMonitor) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        //不通知oq
        messageAddREQ.setNeedOa(Boolean.FALSE);
        messageAddREQ.setFrom(YQJK);
        messageAddREQ.setTo(to);
        messageAddREQ.setFlowid(String.valueOf(riskControlOpinionMonitor.getId()));
        messageAddREQ.setRelation(String.format("【%s】客户出现高风险舆情事件，请检查", riskControlOpinionMonitor.getChiName()));
        messageAddREQ.setContent(String.valueOf(riskControlOpinionMonitor.getId()));
        messageAddREQ.setNoticeSource(NoticeSourceENUM.OPINION_MONITOR.name());
        messageAddREQ.setMessageType(MessageTypeEnum.OPINION_MONITOR.name());
        messageAddREQ.setPcurl(riskControlOpinionMonitor.getLinkAddress());
        messageAddREQ.setBusinessId(String.valueOf(riskControlOpinionMonitor.getId()));
        return messageAddREQ;
    }

    @Override
    public R<Void> close(OpinionMonitorCloseREQ req) {
        riskControlOpinionMonitorService.close(req);
        return R.ok();
    }

    @Override
    public R<RiskControlOpinionManualRSP> confirm(ClientIdREQ req) {
        // 对登录人权限判断
        boolean flag = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name(), JobEnum.assetmanagement.name());
        if (!flag) {
            throw new MithrasException("仅风控经理、资产经理可以发起新增舆情");
        }
        RiskControlOpinionManualRSP rsp = new RiskControlOpinionManualRSP();
        // 查询客户信息
        Client client = clientMapper.selectById(req.getClientId());
        rsp.setClientId(client.getId());
        rsp.setClientName(client.getClientName());
        rsp.setCreditCode(client.getUscCode());
        // 生成舆情ID
        Long nextId = idGenerator.generateNextId();
        rsp.setId(String.valueOf(nextId));
        return R.ok(rsp);
    }

    @Override
    public R<Void> save(RiskControlOpinionManualDetailREQ req) {
        riskControlOpinionMonitorService.saveOpinion(req);
        return R.ok();
    }

    @Override
    public R<Void> submit(RiskControlOpinionManualDetailREQ req) {
        riskControlOpinionMonitorService.submitOpinion(req);
        return R.ok();
    }

    @Override
    public R<Void> delete(SinglePkREQ req) {
        riskControlOpinionMonitorService.deleteOpinion(req);
        return R.ok();
    }

    private boolean getSwitchClose(){
        SystemConfig config = SpringUtil.getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, config_key)
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        String configValue = config.getConfigValue();
        return StrUtil.isNotEmpty(configValue) && configValue.equals(config_value);
    }
}