package cn.zswltech.mithras.service.service.trackEvent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.trackEvent.*;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewBaseInfoConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.trackEvent.TrackFrequencyEnum;
import cn.zswltech.mithras.service.enums.trackEvent.TrackTaskBizSourceEnum;
import cn.zswltech.mithras.service.enums.trackEvent.TrackTaskTypeEnum;
import cn.zswltech.mithras.service.excel.exporter.TrackEventExcelManagerExporter;
import cn.zswltech.mithras.service.excel.model.TrackEventExcelExporter;
import cn.zswltech.mithras.service.mapper.model.afterlease.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.trackEvent.TrackEventInfo;
import cn.zswltech.mithras.service.mapper.trackEvent.TrackEventMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TrackEventService extends ServiceImpl<TrackEventMapper, TrackEventInfo> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private TrackEventService trackEventService;
    @Resource
    private TrackEventExcelManagerExporter trackEventExcelManagerExporter;
    @Resource
    private ProjReviewBaseInfoConverter baseInfoConverter;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private OrgService orgService;

    public PageR<TrackEventListRSP> queryList(TrackEventListREQ req) {
        Page<TrackEventInfo> page = new Page<>();
        page.setCurrent(req.getPage()).setSize(req.getPageSize());
        LambdaQueryWrapper<TrackEventInfo> wrapper = setCondition(req);
        List<TrackEventInfo> records = this.page(page, wrapper).getRecords();
        Map<Long, String> userMap = getUserMap(records,null);
        Map<Long, String> clientMap = id2NameService.clientId2Name(records.stream().map(TrackEventInfo::getClientId).collect(Collectors.toList()));

        List<TrackEventListRSP> collect = records.stream().map(m -> {
            TrackEventListRSP trackEventListRSP = new TrackEventListRSP();
            BeanUtils.copyProperties(m, trackEventListRSP);
            trackEventListRSP.setClientName(getNameById(m.getClientId(),clientMap))
                    .setProcessor(getNameById(m.getProcessorId(),userMap))
                    .setCreateByName(getNameById(m.getCreateBy(),userMap));
            return trackEventListRSP;
        }).collect(Collectors.toList());
        return PageR.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

    public List<TrackEventListRSP> queryListByProjReviewMeetMinuteId(Long projReviewMeetMinuteId) {
       if(ObjectUtil.isEmpty(projReviewMeetMinuteId)) {
           return null;
       }
        List<TrackEventInfo> records = this.list(Wrappers.<TrackEventInfo>lambdaQuery()
                .eq(TrackEventInfo::getProjReviewMeetMinuteId, projReviewMeetMinuteId)
                .ne(TrackEventInfo::getTaskStatus, YesOrNoNumberEnum.NO.getCode()));
        Map<Long, String> userMap = getUserMap(records,null);
        Map<Long, String> clientMap = id2NameService.clientId2Name(records.stream().map(TrackEventInfo::getClientId).collect(Collectors.toList()));

        List<TrackEventListRSP> collect = records.stream().map(m -> {
            TrackEventListRSP trackEventListRSP = new TrackEventListRSP();
            BeanUtils.copyProperties(m, trackEventListRSP);
            trackEventListRSP.setClientName(getNameById(m.getClientId(),clientMap))
                    .setProcessor(getNameById(m.getProcessorId(),userMap))
                    .setCreateByName(getNameById(m.getCreateBy(),userMap));
            return trackEventListRSP;
        }).collect(Collectors.toList());
        return collect;
    }

    public TrackEventDetailRSP detail(Long id) {
        TrackEventInfo trackEventInfo = selectById(id);
        TrackEventContractInfoRSP contractRSP = generateBizInfo(trackEventInfo);
        // 映射id
        Map<Long, String> userMap = getUserMap(Collections.singletonList(trackEventInfo),contractRSP);
        id2NameData(contractRSP);
        TrackEventDetailRSP trackEventDetailRSP = new TrackEventDetailRSP();
        BeanUtils.copyProperties(trackEventInfo,trackEventDetailRSP);
        trackEventDetailRSP.setTrackEventContractInfo(contractRSP)
                .setProcessor(getNameById(trackEventInfo.getProcessorId(),userMap))
                .setCreateByName(getNameById(trackEventInfo.getCreateBy(),userMap));
        return trackEventDetailRSP;
    }

    public TrackEventContractInfoRSP contractInfo(TrackEventContractInfoREQ req) {
        TrackEventContractInfoRSP contractRSP = new TrackEventContractInfoRSP();
        TrackTaskBizSourceEnum bizSourceEnum = TrackTaskBizSourceEnum.find(req.getBizSource());
        if (bizSourceEnum != null) {
            switch (bizSourceEnum){
                case CONTRACT:
                    List<ContractBaseInfo> contractBaseInfoList = null;
                    LambdaQueryWrapper<ContractBaseInfo> wrapper = Wrappers.lambdaQuery();
                    wrapper.eq(req.getBizId() != null,ContractBaseInfo::getId,req.getBizId());
                    wrapper.eq(req.getContractCode() != null,ContractBaseInfo::getContractCode,req.getContractCode());
                    contractBaseInfoList = contractBaseInfoService.list(wrapper);
                    if(CollectionUtils.isNotEmpty(contractBaseInfoList)){
                        ContractBaseInfo contractBaseInfo = contractBaseInfoList.get(0);
                        contractData(contractBaseInfo, contractRSP);
                        contractRSP.setBizSource(bizSourceEnum.name());
                    }
                    break;
                case PROJ_REVIEW:
                    List<ProjReviewBaseInfo> projReviewBaseInfoList = null;
                    LambdaQueryWrapper<ProjReviewBaseInfo> wrapperReview = Wrappers.lambdaQuery();
                    wrapperReview.eq(req.getBizId() != null,ProjReviewBaseInfo::getId,req.getBizId());
                    wrapperReview.eq(req.getProjName() != null,ProjReviewBaseInfo::getProjName,req.getProjName());
                    wrapperReview.eq(req.getClientId() != null,ProjReviewBaseInfo::getClientId,req.getClientId());
                    projReviewBaseInfoList = projReviewBaseInfoService.list(wrapperReview);
                    if(CollectionUtils.isNotEmpty(projReviewBaseInfoList)){
                        projReviewData(projReviewBaseInfoList.get(0), contractRSP);
                        contractRSP.setBizSource(bizSourceEnum.name());
                    }
                    break;
                case AFTER_LEASE:
                    List<AfterLeaseAdjustInfo> afterLeaseAdjustInfoList = null;
                    LambdaQueryWrapper<AfterLeaseAdjustInfo> wrapperLease = Wrappers.lambdaQuery();
                    wrapperLease.eq(req.getBizId() != null,AfterLeaseAdjustInfo::getId,req.getBizId());
                    wrapperLease.eq(req.getProjName() != null,AfterLeaseAdjustInfo::getProjName,req.getProjName());
                    wrapperLease.eq(req.getClientId() != null,AfterLeaseAdjustInfo::getClientId,req.getClientId());
                    try {
                        afterLeaseAdjustInfoList = afterLeaseAdjustInfoService.list(wrapperLease);
                        if(CollectionUtils.isNotEmpty(afterLeaseAdjustInfoList)){
                            afterLeaseData(afterLeaseAdjustInfoList.get(0), contractRSP);
                            contractRSP.setBizSource(bizSourceEnum.name());
                        }
                    }catch (MyBatisSystemException e){
                        return null;
                    }
                    break;
                case PAYMENT:
                    List<PaymentBaseInfo> paymentBaseInfoList = null;
                    LambdaQueryWrapper<PaymentBaseInfo> wrapperPayment = Wrappers.lambdaQuery();
                    wrapperPayment.eq(req.getBizId() != null,PaymentBaseInfo::getId,req.getBizId());
                    wrapperPayment.eq(req.getContractCode() != null,PaymentBaseInfo::getContractCode,req.getContractCode());
                    wrapperPayment.eq(req.getClientId() != null,PaymentBaseInfo::getClientId,req.getClientId());
                    paymentBaseInfoList = paymentBaseInfoService.list(wrapperPayment);
                    if(CollectionUtils.isNotEmpty(paymentBaseInfoList)){
                        paymentData(paymentBaseInfoList.get(0), contractRSP);
                        contractRSP.setBizSource(bizSourceEnum.name());
                    }
                    break;
                case LEDGER:
                    if(req.getContractCode() != null){
                        req.setBizSource(TrackTaskBizSourceEnum.CONTRACT.name());
                        return contractInfo(req);
                    }else if(req.getProjName() != null){
                        req.setBizSource(TrackTaskBizSourceEnum.PROJ_REVIEW.name());
                        return contractInfo(req);
                    }else if(req.getClientId() != null){
                        req.setBizSource(TrackTaskBizSourceEnum.AFTER_LEASE.name());
                        TrackEventContractInfoRSP trackEventContractInfoRSP = contractInfo(req);
                        if(trackEventContractInfoRSP == null){
                            req.setBizSource(TrackTaskBizSourceEnum.PAYMENT.name());
                            return contractInfo(req);
                        }
                        return trackEventContractInfoRSP;
                    }
                    break;
            }
        }
        if(contractRSP.getBizSource() == null){
            contractRSP.setContractCode(req.getContractCode());
            contractRSP.setProjName(req.getProjName());
            contractRSP.setClientId(req.getClientId());
        }
        id2NameData(contractRSP);

        return contractRSP;
    }


    public Boolean add(TrackEventAddREQ req) {
        addValid(req);
        TrackEventInfo trackEventInfo = new TrackEventInfo();
        BeanUtils.copyProperties(req,trackEventInfo);
        return this.save(trackEventInfo);
    }


//    public Boolean edit(TrackEventUpdateREQ req) {
//        editValid(req);
//        TrackEventInfo trackEventInfo = selectById(req.getId());
//        BeanUtils.copyProperties(req,trackEventInfo);
//        return this.updateById(trackEventInfo);
//    }

    public Boolean close(Long id) {
        TrackEventInfo trackEventInfo = selectById(id);
        if(trackEventInfo.getCreateBy().equals(AccountUtil.getLoginInfo().getId())){
            trackEventInfo.setTaskStatus(false);
            return updateById(trackEventInfo);
        }else{
            throw new MithrasException("只允许关闭自己新增的任务");
        }

    }

    public List<String> getNameById(List<Long> userId,Map<Long, String> map){
        if(CollectionUtils.isEmpty(userId)){
            return null;
        }
        return userId.stream().map(map::get).collect(Collectors.toList());
    }

    public String getNameById(Long userId,Map<Long, String> map){
        return map.get(userId);
    }

    private Map<Long, String> getUserMap(List<TrackEventInfo> list,TrackEventContractInfoRSP contractInfo) {
        List<Long> createIds = list.stream().map(TrackEventInfo::getCreateBy).collect(Collectors.toList());
        List<Long> updateIds = list.stream().map(TrackEventInfo::getUpdateBy).collect(Collectors.toList());
        List<Long> processorIds = list.stream().map(TrackEventInfo::getProcessorId).collect(Collectors.toList());
        Set<Long> ids = Stream.of(createIds, updateIds, processorIds).flatMap(List::stream).collect(Collectors.toSet());
        if(contractInfo != null){
            List<Long> projCosponsorUserIds = Optional.ofNullable(contractInfo.getProjCosponsorUserIds()).orElse(new ArrayList<>());
            projCosponsorUserIds.add(contractInfo.getProjSponsorUserId());
            ids.addAll(projCosponsorUserIds);
        }
        return id2NameService.sysUserId2Name(ids);
    }

    private TrackEventContractInfoRSP generateBizInfo(TrackEventInfo trackEventInfo){
        TrackTaskBizSourceEnum bizSourceEnum = TrackTaskBizSourceEnum.find(trackEventInfo.getBizSource());
        TrackEventContractInfoRSP contractInfoRSP = new TrackEventContractInfoRSP();
        if(bizSourceEnum != null){
            switch (bizSourceEnum){
                case CONTRACT:
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(trackEventInfo.getBizId());
                    if(contractBaseInfo != null) {
                        contractData(contractBaseInfo,contractInfoRSP);
                    }
                    break;
                case PROJ_REVIEW:
                    ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(trackEventInfo.getBizId());
                    if(projReviewBaseInfo != null){
                        projReviewData(projReviewBaseInfo,contractInfoRSP);
                    }
                    break;
                case AFTER_LEASE:
                    AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.getById(trackEventInfo.getBizId());
                    if(afterLeaseAdjustInfo != null) {
                        afterLeaseData(afterLeaseAdjustInfo,contractInfoRSP);
                    }
                    break;
                case PAYMENT:
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(trackEventInfo.getBizId());
                    if(paymentBaseInfo != null) {
                        paymentData(paymentBaseInfo,contractInfoRSP);
                    }
                    break;
            }
        }
        return contractInfoRSP;
    }


    public TrackEventInfo selectById(Long id){
        TrackEventInfo trackEventInfo = this.getById(id);
        if(trackEventInfo == null){
            throw new MithrasException("数据不存在");
        }
        return trackEventInfo;
    }

    public List<UserRSP> queryProcess() {
        // 运营管理部 orgCode
        String YYGLBName = null;
        Example example = new Example(OrgDO.class);
        example.createCriteria().andEqualTo("code","YYGLB");
        List<OrgDO> orgDOList = orgService.selectByExample(example);
        if(CollectionUtils.isNotEmpty(orgDOList)){
            YYGLBName = orgDOList.get(0).getName();
        }
        List<UserRSP> userList = sysUserService.getUserList(Collections.singletonList("LDC"));
        List<UserRSP> YYGLList = new ArrayList<>();
        if(YYGLBName != null) {
            String finalYYGLBName = YYGLBName;
            YYGLList = userList.stream().filter(f -> f.getOrgs() != null).filter(f -> f.getOrgs().contains(finalYYGLBName)).collect(Collectors.toList());
        }
        List<UserRSP> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(YYGLList)){
            result.addAll(YYGLList);
            userList.removeAll(YYGLList);
        }
        result.addAll(userList);
        return result;
    }


    @XxlJob("trackEventFlowHandler")
    public void startEffect(){
        List<TrackEventInfo> list = this.list(Wrappers.<TrackEventInfo>lambdaQuery().eq(TrackEventInfo::getTaskStatus, true));
        List<String> taskName = new ArrayList<>();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<TrackEventInfo> trackEventInfoList = autoCloseTrackEvent(list);
        Map<Long, LocalDate> leaseDateMap = getStartRentDate(trackEventInfoList);
        for (TrackEventInfo trackEventInfo : trackEventInfoList) {
            // 1.校验频率
            TrackFrequencyEnum trackFrequencyEnum = TrackFrequencyEnum.find(trackEventInfo.getRemindFrequency());
            if(trackFrequencyEnum == null){
                continue;
            }
            if(trackEventInfo.getOnFlowCount() > 0) {
                switch (trackFrequencyEnum) {
                    case ONCE:
                        if (trackEventInfo.getOnFlowCount() > 0) {// 判断是否执行过
                            continue;
                        }
                        break;
                    case WEEK:
                        if (LocalDate.now().getDayOfWeek() != DayOfWeek.MONDAY) {// 判断是否为一周的第一天
                            continue;
                        }
                        break;
                    case MONTH:
                        if (LocalDate.now().getDayOfMonth() != 1) {// 判断是否为一个月的第一天
                            continue;
                        }
                        break;
                    case SEASON:
                        if (!LocalDate.now().equals(getFirstDayOfQuarter(LocalDate.now()))) {// 判断是否为一个季度的第一天
                            continue;
                        }
                        break;
                }
            }
            // 2.校验时间
            if(trackEventInfo.getStartRentAfterDay() != null){
                LocalDate startRentTime = leaseDateMap.get(trackEventInfo.getId());
                if(startRentTime == null){
                    continue;
                }
                LocalDate localDate = startRentTime.plusDays(trackEventInfo.getStartRentAfterDay());
                if(!LocalDate.now().isBefore(localDate)){
                    trackEventService.effect(trackEventInfo);
                    taskName.add(trackEventInfo.getTaskName());
                }
            }else{
                if(!LocalDate.now().isBefore(trackEventInfo.getPlanTime())){
                    trackEventService.effect(trackEventInfo);
                    taskName.add(trackEventInfo.getTaskName());
                }
            }
        }
        log.info("跟踪事项自动发起审批流程定时任务执行完毕,自动发起流程任务名称：{}",taskName);
    }

    /**
     * 根据依赖的大流程状态校验是否关闭自动跟踪事项
     */
    private List<TrackEventInfo> autoCloseTrackEvent(List<TrackEventInfo> list) {
        List<TrackEventInfo> trackEventInfoList = new ArrayList<>();
        List<TrackEventInfo> trackEventInfoCloseList = new ArrayList<>();
        for (TrackEventInfo trackEventInfo : list) {
            TrackTaskBizSourceEnum bizSourceEnum = TrackTaskBizSourceEnum.find(trackEventInfo.getBizSource());
            if(bizSourceEnum == null){
                continue;
            }
            switch (bizSourceEnum) {
                case CONTRACT:
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(trackEventInfo.getBizId());
                    if (contractBaseInfo != null && !trackEventInfo.getIsLedger() && Arrays.asList(ContractStatus.CLOSED.name(),
                            ContractStatus.INVALID.name(),ContractStatus.SETTLE.name()).contains(contractBaseInfo.getContractStatus())) {
                        // 合同状态为已关闭、作废、结清，则合同相关的跟踪事项状态均关闭；
                        trackEventInfo.setTaskStatus(false);
                        trackEventInfoCloseList.add(trackEventInfo);
                    }else{
                        trackEventInfoList.add(trackEventInfo);
                    }
                    break;
                case PROJ_REVIEW:
                    ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(trackEventInfo.getBizId());
                    if (projReviewBaseInfo != null && !trackEventInfo.getIsLedger() && (Objects.equals(RecordStatus.CLOSED.name(), projReviewBaseInfo.getProjReviewStatus()) || ObjectUtil.equals(RecordStatus.EXPIRE.name(), projReviewBaseInfo.getProjReviewStatus()))) {
                        // 项目评审流程若项目若项目状态为【关闭】则项目相关的跟踪事项状态均关闭
                        trackEventInfo.setTaskStatus(false);
                        trackEventInfoCloseList.add(trackEventInfo);
                    }else{
                        trackEventInfoList.add(trackEventInfo);
                    }
                    break;
                case AFTER_LEASE:
                    trackEventInfoList.add(trackEventInfo);
                    break;
                case PAYMENT:
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(trackEventInfo.getBizId());
                    if (paymentBaseInfo != null && !trackEventInfo.getIsLedger() &&
                            PaymentStatusEnum.CLOSED.name().equals(paymentBaseInfo.getPaymentStatus())) {
                        // 付款申请，付款状态为关闭，则付款相关的跟踪事项状态均关闭；
                        trackEventInfo.setTaskStatus(false);
                        trackEventInfoCloseList.add(trackEventInfo);
                    }else{
                        trackEventInfoList.add(trackEventInfo);
                    }
                    break;
            }
        }
        log.info("跟踪事项定时任务开始执行，自动关闭跟踪任务:{}",trackEventInfoCloseList.stream().map(TrackEventInfo::getTaskName).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(trackEventInfoCloseList)) {
            trackEventService.updateBatchById(trackEventInfoCloseList);
        }
        return trackEventInfoList;

    }

    /**
     * 提交审批流
     * @param trackEventInfo
     */
    @Transactional(rollbackFor = Throwable.class)
    public void effect(TrackEventInfo trackEventInfo){
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.TrackEventCreateFlow.name());
        // 表单名称命名规则：跟踪事项-${任务名称}-第${推送次数}次流程推送
        String processName = String.format("跟踪事项-%s-第%s次流程推送", trackEventInfo.getTaskName(), trackEventInfo.getOnFlowCount() + 1);
        startProcessReq.setProcessInstanceName(processName);
        Map<String, Object> varMap = new HashMap<>();
        List<String> processor = Objects.nonNull(trackEventInfo.getProcessorId()) ? ListUtil.toList(String.valueOf(trackEventInfo.getProcessorId())) : new ArrayList<>();
        varMap.put("processor", processor);
        // 202050526逻辑变更，需要在流程运行时获取当前处理人，为了不影响流程启动，这里先随便塞一个值
        //20251216逻辑变更，当处理人和部门负责人相同时会跳过处理人节点，导致结束监听报错，判断相同时先塞一个admin进负责人节点
        List<String> principal = getPrincipal(trackEventInfo.getCreateBy());
        if(principal.equals(processor)){
            principal = new ArrayList<>();
            principal.add("3");
        }
        varMap.put("principal", principal);
        // 处理人岗位=法律合规部(资产保全部)是：走项目经理；否：走原来逻辑
        log.info("跟踪事项创建：{},处理人: {} ,处理人岗位 {}",  trackEventInfo.getTaskName(), trackEventInfo.getProcessorId(),trackEventInfo.getProcessorDept());
        // 根据合同获取项目经理和部门负责人
        final TrackEventContractInfoREQ req = new TrackEventContractInfoREQ();
        req.setContractCode(trackEventInfo.getContractCode());
        req.setBizId(trackEventInfo.getBizId());
        req.setProjName(trackEventInfo.getProjName());
        req.setClientId(trackEventInfo.getClientId());
        req.setBizSource(trackEventInfo.getBizSource());
        final TrackEventContractInfoRSP trackEventContractInfoRSP = this.contractInfo(req);
        if (Objects.nonNull(trackEventContractInfoRSP)) {
            // 设置项目经理
            varMap.put("projectManager", Objects.nonNull(trackEventContractInfoRSP.getProjSponsorUserId()) ? ListUtil.toList(String.valueOf(trackEventContractInfoRSP.getProjSponsorUserId())) : new ArrayList<>());
            // 设置部门负责人
            varMap.put("bizDeptLeader", Objects.nonNull(trackEventContractInfoRSP.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(trackEventContractInfoRSP.getBizDeptLeaderId())) : new ArrayList<>());
            // 设置风控经理
            varMap.put("riskControlManager", Objects.nonNull(trackEventContractInfoRSP.getRiskControlManagerId()) ? ListUtil.toList(String.valueOf(trackEventContractInfoRSP.getRiskControlManagerId())) : new ArrayList<>());
        } else {
            varMap.put("projectManager", new ArrayList<>());
            varMap.put("bizDeptLeader", new ArrayList<>());
            varMap.put("riskControlManager", new ArrayList<>());
        }
        if (StringUtils.isNotEmpty(trackEventInfo.getProcessorDept()) && trackEventInfo.getProcessorDept().contains("法律合规部（资产保全部）")) {
            varMap.put("isFlhgbDeptFlag", true);
        } else {
            varMap.put("isFlhgbDeptFlag", false);
        }
        //是否授信评审创建/变更流程”、“项目评审创建/变更流程”发起
        if (StringUtils.isEmpty(trackEventInfo.getBizSource())||(StringUtils.isNotEmpty(trackEventInfo.getBizSource()) && BusinessModuleEnum.PROJ_REVIEW.name().equals(trackEventInfo.getBizSource()))) {
            varMap.put("isReview", true);
        } else {
            varMap.put("isReview", false);
        }
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(trackEventInfo.getId()));
        startProcessReq.setStartUserId(String.valueOf(trackEventInfo.getCreateBy()));
//        List<OrgDO> specificUserDeptList = Optional.ofNullable(sysUserService.getSpecificUserDeptList(trackEventInfo.getCreateBy())).orElse(new ArrayList<>());
//        startProcessReq.setStartUserDeptId(Optional.of(specificUserDeptList.stream().map(OrgDO::getId).collect(Collectors.toList())).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, trackEventInfo.getClientId());
        trackEventInfo.setOnFlowCount(trackEventInfo.getOnFlowCount()+1);
        this.updateById(trackEventInfo);
    }

    public List<String> contractCodeList() {
        LambdaQueryWrapper<ContractBaseInfo> queryWrapper = Wrappers.<ContractBaseInfo>lambdaQuery();
        queryWrapper.orderByDesc(ContractBaseInfo::getId);
        List<ContractBaseInfo> list = contractBaseInfoService.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            return list.stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toList());
        }
        return null;
    }

    public void download(ServletOutputStream outputStream, TrackEventMainREQ req) {
//        Page<TrackEventInfo> page = new Page<>();
//        page.setCurrent(1).setSize(5000);
//        LambdaQueryWrapper<TrackEventInfo> wrapper = Wrappers.lambdaQuery();
//        Set<Long> userIdAll = new HashSet<>();
//        if(sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())){
//            // 分管领导查看处理人为所管理部门的任务
//            List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.leaderincharge.name());
//            Set<Long> userIdSet = orgDOList.stream().map(OrgDO::getId).map(sysUserService::getUserByDeptId).flatMap(Collection::stream).map(UserDO::getId).collect(Collectors.toSet());
//            if(CollectionUtils.isNotEmpty(userIdSet)){
//                userIdAll.addAll(userIdSet);
//            }
//        }
//        if(sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name())){
//            // 业务负责人只能查看到处理人为自己部门的任务
//            List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.businesshead.name());
//            Set<Long> userIdSet = orgDOList.stream().map(OrgDO::getId).map(sysUserService::getUserByDeptId).flatMap(Collection::stream).map(UserDO::getId).collect(Collectors.toSet());
//            if(CollectionUtils.isNotEmpty(userIdSet)){
//                userIdAll.addAll(userIdSet);
//            }
//        }
//        if(sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())){
//            // 项目经理只能查看到处理人是自己的任务
//            userIdAll.add(AccountUtil.getLoginInfo().getId());
//        }
//        wrapper.in(CollectionUtils.isNotEmpty(userIdAll),TrackEventInfo::getProcessorId,userIdAll);
//        wrapper.in(CollectionUtils.isNotEmpty(req.getIds()),TrackEventInfo::getId,req.getIds());
//        wrapper.like(Objects.nonNull(req.getTaskName()),TrackEventInfo::getTaskName,req.getTaskName());
//        wrapper.eq(Objects.nonNull(req.getTaskType()),TrackEventInfo::getTaskType,req.getTaskType());
//        wrapper.eq(Objects.nonNull(req.getContractCode()),TrackEventInfo::getContractCode,req.getContractCode());
//        wrapper.eq(Objects.nonNull(req.getClientId()),TrackEventInfo::getClientId,req.getClientId());
//        wrapper.eq(Objects.nonNull(req.getProjName()),TrackEventInfo::getProjName,req.getProjName());
//        wrapper.eq(Objects.nonNull(req.getTaskStatus()),TrackEventInfo::getTaskStatus,req.getTaskStatus());
//        wrapper.eq(Objects.nonNull(req.getProcessorId()),TrackEventInfo::getProcessorId,req.getProcessorId());
//        wrapper.eq(Objects.nonNull(req.getCreateBy()),TrackEventInfo::getCreateBy,req.getCreateBy());
//        List<TrackEventInfo> records = this.page(page, wrapper).getRecords();
        List<TrackEventInfo> records;
        if (CollectionUtils.isNotEmpty(req.getIds())) {
            records = this.listByIds(req.getIds());
        } else {
            // 和列表接口保持相同逻辑
            Page<TrackEventInfo> page = new Page<>();
            page.setCurrent(1).setSize(5000);
            LambdaQueryWrapper<TrackEventInfo> wrapper = setCondition(req);
            records = this.page(page, wrapper).getRecords();
        }
        if (CollUtil.isEmpty(records)) {
            throw new MithrasException("不存在数据，导出失败");
        }
        Map<Long, String> userMap = getUserMap(records,null);
        Map<Long, String> clientMap = id2NameService.clientId2Name(records.stream().map(TrackEventInfo::getClientId).collect(Collectors.toList()));

        List<TrackEventExcelExporter> excelModels = records.stream().map(m -> {
            TrackEventExcelExporter trackEventExcelExporter = new TrackEventExcelExporter();
            BeanUtils.copyProperties(m, trackEventExcelExporter);
            TrackFrequencyEnum trackFrequencyEnum = TrackFrequencyEnum.find(m.getRemindFrequency());
            trackEventExcelExporter.setClientName(getNameById(m.getClientId(),clientMap))
                    .setProcessor(getNameById(m.getProcessorId(),userMap))
                    .setCreateByName(getNameById(m.getCreateBy(),userMap))
                    .setRemindFrequency(trackFrequencyEnum != null ? trackFrequencyEnum.display : m.getRemindFrequency())
                    .setTaskType(Objects.requireNonNull(TrackTaskTypeEnum.find(m.getTaskType())).getDisplay())
                    .setTaskStatus(m.getTaskStatus() ? "生效" : "关闭");
            return trackEventExcelExporter;
        }).collect(Collectors.toList());

        trackEventExcelManagerExporter.exportExcel(excelModels, outputStream);
    }

    /**
     * id映射
     */
    private void id2NameData(TrackEventContractInfoRSP contractRSP) {
        List<Long> userIdList = CollectionUtils.isNotEmpty(contractRSP.getProjCosponsorUserIds()) ? contractRSP.getProjCosponsorUserIds() : new ArrayList<>();
        userIdList.add(contractRSP.getProjSponsorUserId());
        userIdList.add(contractRSP.getBizDeptLeaderId());
        Map<Long, String> userMap = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptMap = id2NameService.deptId2Name(Arrays.asList(contractRSP.getBizDeptId(), contractRSP.getBizDeptLeaderId()));
        Map<Long, String> clientMap = id2NameService.clientId2Name(Collections.singletonList(contractRSP.getClientId()));
        contractRSP.setClientName(getNameById(contractRSP.getClientId(),clientMap))
                .setProjCosponsorUserNames(getNameById(contractRSP.getProjCosponsorUserIds(),userMap))
                .setProjSponsorUserName(getNameById(contractRSP.getProjSponsorUserId(),userMap))
                .setBizDeptName(getNameById(contractRSP.getBizDeptId(),deptMap))
                .setBizDeptLeaderName(getNameById(contractRSP.getBizDeptLeaderId(),userMap));
    }

    /**
     * 字段校验
     * @param req
     */
    private void addValid(TrackEventAddREQ req) {
        if(req.getBizSource() == null){
            throw new MithrasException("请选择项目名称");
        }
        editValid(req);
        String taskName = req.getTaskName();
        List<TrackEventInfo> list = this.list(Wrappers.<TrackEventInfo>lambdaQuery().eq(TrackEventInfo::getTaskStatus,true));
        if(CollectionUtils.isNotEmpty(list)) {
            if(list.stream().map(TrackEventInfo::getTaskName).collect(Collectors.toList()).contains(taskName)){
                throw new MithrasException("任务名称为"+taskName+"的任务已存在");
            }
        }
    }

    /**
     * 字段校验
     * @param req
     */
    private void editValid(TrackEventAddREQ req) {
        if(req.getPlanTime() == null && req.getStartRentAfterDay() == null){
            throw new MithrasException("请填写【计划日期】或【起租后X自然日】");
        }
        TrackTaskTypeEnum trackTaskTypeEnum = TrackTaskTypeEnum.find(req.getTaskType());
        if(trackTaskTypeEnum != null && req.getIsLedger()) {
            switch (trackTaskTypeEnum) {
                case CONTRACT_FILE:
                case LEASE_FILE:
                case ASSET_ATTENTION_EVENT:
                    if (req.getContractCode() == null) {
                        throw new MithrasException("任务类型为" + trackTaskTypeEnum.getDisplay() + "时：合同编号不得为空");
                    }
                    break;
                case AFTER_LEASE_EVENT:
                case OTHER:
                    if (req.getClientId() == null) {
                        throw new MithrasException("任务类型为" + trackTaskTypeEnum.getDisplay() + "时：客户名称不得为空");
                    }
                    break;
            }
        }
    }

    private LambdaQueryWrapper<TrackEventInfo> setCondition(TrackEventListREQ req) {
        LambdaQueryWrapper<TrackEventInfo> wrapper = Wrappers.lambdaQuery();
        Set<Long> userIdAll = new HashSet<>();
        if(sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())){
            // 分管领导查看处理人为所管理部门的任务
            List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.leaderincharge.name());
            Set<Long> userIdSet = orgDOList.stream().map(OrgDO::getId).map(sysUserService::getUserByDeptId).flatMap(Collection::stream).map(UserDO::getId).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(userIdSet)){
                userIdAll.addAll(userIdSet);
            }
        }
        if(sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name())){
            List<Long> list = sysUserService.canViewDeptIds();
            // 中后台部门负责人不做权限控制
            if(list != null) {
                // 业务负责人只能查看到处理人为自己部门的任务
                List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.businesshead.name());
                Set<Long> userIdSet = orgDOList.stream().map(OrgDO::getId).map(sysUserService::getUserByDeptId).flatMap(Collection::stream).map(UserDO::getId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(userIdSet)) {
                    userIdAll.addAll(userIdSet);
                }
            }
        }
        if(sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())){
            // 项目经理只能查看到处理人是自己的任务
            userIdAll.add(AccountUtil.getLoginInfo().getId());
        }
        wrapper.in(CollectionUtils.isNotEmpty(userIdAll),TrackEventInfo::getProcessorId,userIdAll);

        if(!TrackTaskBizSourceEnum.LEDGER.name().equals(req.getBizSource())){
            // 不从台账进入，只展示对应流程中创建的
            wrapper.eq(Objects.nonNull(req.getBizSource()),TrackEventInfo::getBizSource,req.getBizSource());
            wrapper.eq(Objects.nonNull(req.getBizId()),TrackEventInfo::getBizId,req.getBizId());
        }

        wrapper.like(Objects.nonNull(req.getTaskName()),TrackEventInfo::getTaskName,req.getTaskName());
        wrapper.eq(Objects.nonNull(req.getTaskType()),TrackEventInfo::getTaskType,req.getTaskType());
        wrapper.eq(Objects.nonNull(req.getContractCode()),TrackEventInfo::getContractCode,req.getContractCode());
        wrapper.eq(Objects.nonNull(req.getClientId()),TrackEventInfo::getClientId,req.getClientId());
        wrapper.eq(Objects.nonNull(req.getProjName()),TrackEventInfo::getProjName,req.getProjName());
        wrapper.eq(Objects.nonNull(req.getTaskStatus()),TrackEventInfo::getTaskStatus,req.getTaskStatus());
        wrapper.eq(Objects.nonNull(req.getProcessorId()),TrackEventInfo::getProcessorId,req.getProcessorId());
        wrapper.eq(Objects.nonNull(req.getCreateBy()),TrackEventInfo::getCreateBy,req.getCreateBy());

        wrapper.orderByDesc(TrackEventInfo::getCreateTime);
        return wrapper;
    }

    /**
     * 判断是否起租
     * @param trackEventInfoList
     * @return
     */
    private Map<Long,LocalDate> getStartRentDate(List<TrackEventInfo> trackEventInfoList){
        Map<Long, LocalDate> dateMap = new HashMap<>();
        for (TrackEventInfo trackEventInfo : trackEventInfoList) {
            TrackTaskBizSourceEnum bizSourceEnum = TrackTaskBizSourceEnum.find(trackEventInfo.getBizSource());
            if (bizSourceEnum != null) {
                switch (bizSourceEnum) {
                    case CONTRACT:
                        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(trackEventInfo.getBizId());
                        if (contractBaseInfo != null && contractBaseInfo.getActualLeaseDate() != null) {
                            dateMap.put(trackEventInfo.getId(), contractBaseInfo.getActualLeaseDate());
                        }
                        break;
                    case PROJ_REVIEW:
                        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(trackEventInfo.getBizId());
                        if (projReviewBaseInfo != null) {
                            List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjCode, projReviewBaseInfo.getProjCode()));
                            if (CollectionUtils.isNotEmpty(list)) {
                                List<ContractBaseInfo> collect = list.stream().filter(c -> c.getActualLeaseDate() != null).collect(Collectors.toList());
                                // 若存在多个已起租合同 取第一个
                                if(CollectionUtils.isNotEmpty(collect) && collect.get(0).getActualLeaseDate() != null) {
                                    dateMap.put(trackEventInfo.getId(), collect.get(0).getActualLeaseDate());
                                }
                            }
                        }
                        break;
                    case AFTER_LEASE:
                        AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.getById(trackEventInfo.getBizId());
                        if (afterLeaseAdjustInfo != null) {
                            List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjCode, afterLeaseAdjustInfo.getProjCode()));
                            if (CollectionUtils.isNotEmpty(list)) {
                                List<ContractBaseInfo> collect = list.stream().filter(c -> c.getActualLeaseDate() != null).collect(Collectors.toList());
                                // 若存在多个已起租合同 取第一个
                                if(CollectionUtils.isNotEmpty(collect) && collect.get(0).getActualLeaseDate() != null) {
                                    dateMap.put(trackEventInfo.getId(), collect.get(0).getActualLeaseDate());
                                }
                            }
                        }
                        break;
                    case PAYMENT:
                        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(trackEventInfo.getBizId());
                        if (paymentBaseInfo != null) {
                            ContractBaseInfo byId = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                            if (byId != null && byId.getActualLeaseDate() != null) {
                                dateMap.put(trackEventInfo.getId(), byId.getActualLeaseDate());
                            }
                        }
                        break;
                }
            }
        }
        return dateMap;
    }
    /**
     * 获取部门负责人
     */
    private List<String> getPrincipal(Long createBy) {
        List<String> JobCodeList = sysUserService.queryUserJobList(createBy);
        String job = "";
        if(CollectionUtils.isNotEmpty(JobCodeList)){
            if(JobCodeList.contains(JobEnum.operationmanagementagent.name())){
                job =  JobEnum.headofyyglb.name();
            }else if(JobCodeList.contains(JobEnum.secretaryjury.name())){
                job =  JobEnum.riskdeptmanager.name();
            }else if(JobCodeList.contains(JobEnum.assetmanagement.name())){
                job =  JobEnum.headoflegalcompliance.name();
            }else{
                return new ArrayList<>();
            }
            List<Long> list = sysUserService.jobUsers(Sets.newHashSet(job));
            return list.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取每个季度的第一天
     */
    private static LocalDate getFirstDayOfQuarter(LocalDate date) {
        Month currentMonth = date.getMonth();
        int quarter = (currentMonth.getValue() - 1) / 3 + 1;

        Month firstMonthOfQuarter;
        switch (quarter) {
            case 1:
                firstMonthOfQuarter = Month.JANUARY;
                break;
            case 2:
                firstMonthOfQuarter = Month.APRIL;
                break;
            case 3:
                firstMonthOfQuarter = Month.JULY;
                break;
            default:
                firstMonthOfQuarter = Month.OCTOBER;
                break;
        }
        return date.with(TemporalAdjusters.firstDayOfMonth())
                .withMonth(firstMonthOfQuarter.getValue());
    }

    /**
     * 付款数据填充
     */
    private void paymentData(PaymentBaseInfo paymentBaseInfo, TrackEventContractInfoRSP contractRSP) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if(contractBaseInfo != null) {
            BeanUtils.copyProperties(contractBaseInfo, contractRSP);
            if(contractBaseInfo.getLeaseType() != null){
                contractRSP.setRiskControlIndustryClassify(contractBaseInfo.getRiskControlIndustryClassify());
                contractRSP.setLeaseType(Collections.singletonList(contractBaseInfo.getLeaseType()));
            }
        }
        BeanUtils.copyProperties(paymentBaseInfo,contractRSP);
        contractRSP.setBizId(paymentBaseInfo.getId());
    }

    /**
     * 租后数据填充
     */
    private void afterLeaseData(AfterLeaseAdjustInfo afterLeaseAdjustInfo, TrackEventContractInfoRSP contractRSP) {
        BeanUtils.copyProperties(afterLeaseAdjustInfo,contractRSP);
        contractRSP.setBizId(afterLeaseAdjustInfo.getId());
        contractRSP.setLeaseType(JSON.parseArray(Optional.ofNullable(afterLeaseAdjustInfo.getLeaseTypes()).orElse(""),String.class));
    }

    /**
     * 评审数据填充
     */
    private void projReviewData(ProjReviewBaseInfo projReviewBaseInfo, TrackEventContractInfoRSP contractRSP) {
        ProjReviewBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(projReviewBaseInfo);
        // 填充客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(rsp.getClientId());
        rsp.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
        BeanUtils.copyProperties(rsp,contractRSP);
        contractRSP.setLeaseType(rsp.getLeaseTypes());
        contractRSP.setBizId(rsp.getId());
    }

    /**
     * 合同数据填充
     */
    private void contractData(ContractBaseInfo contractBaseInfo, TrackEventContractInfoRSP contractRSP) {
        BeanUtils.copyProperties(contractBaseInfo,contractRSP);
        if(contractBaseInfo.getLeaseType() != null){
            contractRSP.setLeaseType(Collections.singletonList(contractBaseInfo.getLeaseType()));
        }
        contractRSP.setBizId(contractBaseInfo.getId());
    }
}
