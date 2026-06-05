package cn.zswltech.mithras.service.service.afterlese.impl;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.afterlease.application.convert.AfterLeaseCheckPlanConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.afterlease.domain.enums.*;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckPlanBaseLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckPlanClientLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckReportMetaLibMapper;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckChangeRecordService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportBaseService;
import cn.zswltech.mithras.service.service.afterlese.NewAfterLeaseCheckReportDetailService;
import cn.zswltech.mithras.service.service.afterlese.factory.AfterLeaseClientPlanFactory;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseListExpandBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanBaseLibService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanClientLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanVersionService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.util.BigDecimalUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckPlanBaseServiceImpl extends ServiceImpl<NewAfterLeaseCheckPlanBaseMapper, NewAfterLeaseCheckPlanBase> implements AfterLeaseCheckPlanBaseService {
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckPlanBaseLibService checkPlanBaseLibService;
    @Resource
    private AfterLeaseCheckPlanClientLibService checkPlanProjectLibService;
    @Resource
    private ProcessService processService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private MyTaskService myTaskService;
    @Resource
    private NewAfterLeaseCheckReportDetailService afterLeaseCheckReportDetailService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private NewAfterLeaseCheckPlanBaseMapper newAfterLeaseCheckPlanBaseMapper;
    @Resource
    private AfterLeaseCheckChangeRecordService afterLeaseCheckChangeRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private MessageService messageService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private AfterLeaseCheckReportBaseService afterLeaseCheckReportBaseService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private NewAfterLeaseCheckPlanBaseLibMapper planBaseLibMapper;
    @Resource
    private NewAfterLeaseCheckPlanClientLibMapper planClientLibMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private OrgDOMapper orgDOMapper;

    private static final String PROJECT_MANAGER = "project_manager";
    private static final String USER_TASK_START_USER = "userTask_startUser";

    @Override
    public void close(Long id) {
        NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = this.getById(id);
        Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
        if (!newAfterLeaseCheckPlanBase.getPlanType().equals(AfterLeaseCheckPlanTypeEnum.COMMONLY.name())) {
            Assert.isTrue(Objects.equals(newAfterLeaseCheckPlanBase.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.NEW.name()), () -> MithrasException.newException("只有新建状态的计划可以被关闭"));
        }
        NewAfterLeaseCheckPlanBase toUpdate = new NewAfterLeaseCheckPlanBase();
        toUpdate.setId(id);
        toUpdate.setPlanStatus(AfterLeaseCheckPlanStatusEnum.CLOSE.name());
        this.updateById(toUpdate);
        //关闭租后计划通知项目经理
        if (newAfterLeaseCheckPlanBase.getPlanType().equals(AfterLeaseCheckPlanTypeEnum.COMMONLY.name())
                && newAfterLeaseCheckPlanBase.getPlanStatus().equals(AfterLeaseCheckPlanStatusEnum.CHECKING.name())) {
            NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanClient::getPlanId, newAfterLeaseCheckPlanBase.getId())
                    .orderByDesc(NewAfterLeaseCheckPlanClient::getId)
                    .last(StringUtil.mysqlLimitOne())
            );
            List<Long> userId = new ArrayList<>();
            userId.add(checkPlanClient.getBelongSponsorId());
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统提醒");
            messageAddREQ.setFlowid(String.valueOf(newAfterLeaseCheckPlanBase.getId()));
            messageAddREQ.setRelation(newAfterLeaseCheckPlanBase.getPlanName());
            messageAddREQ.setNeedOa(false);
            messageAddREQ.setContent(newAfterLeaseCheckPlanBase.getPlanName() + "-任务已关闭");
            messageAddREQ.setNoticeSource("关闭租后检查计划通知");
            messageAddREQ.setMessageType(MessageTypeEnum.AFTER_LEASE_CHECK_CLOSE.name());
            messageAddREQ.setTo(userId);
            messageAddREQ.setPcurl(StringUtils.format("/afterLease/checkPlan/commonTemplate/%s", newAfterLeaseCheckPlanBase.getId()));
            MessageModel messageModel = messageConver.reqToMessage(messageAddREQ);
            messageService.sendMessage(messageModel);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long add(AfterLeaseCheckPlanBaseAddREQ req) {
        NewAfterLeaseCheckPlanBase newPlan = new NewAfterLeaseCheckPlanBase();
        newPlan.setPlanType(req.getPlanType());
        newPlan.setPlanName(req.getPlanName());
        newPlan.setDeadLine(req.getDeadLine());
        newPlan.setId(req.getPlanId());
        newPlan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.NEW.name());
        if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(req.getPlanType())) {
            newPlan.setCheckWay(req.getCheckWay());
            newPlan.setTerm(req.getTerm());
            //获取客户的上一检查计划
            NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanClient::getClientId, req.getClientId())
                    .ne(Objects.nonNull(req.getPlanId()), NewAfterLeaseCheckPlanClient::getPlanId, req.getPlanId())
                    .orderByDesc(NewAfterLeaseCheckPlanClient::getPlanId)
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(checkPlanClient)) {
                NewAfterLeaseCheckPlanBase planBase = this.getById(checkPlanClient.getPlanId());
                if (ObjectUtil.isNotEmpty(planBase)) {
                    if (Objects.nonNull(planBase.getDeadLine()) && !planBase.getDeadLine().isEqual(req.getDeadLine())) {
                        newPlan.setDeadlineLabel(JSONUtil.toJsonStr(ListUtil.of(AfterLeaseDeadlineLabelEnum.ASSERT_MANAGER_CONFIRM.name())));
                    }
                    newPlan.setLastCheckWay(planBase.getCheckWay());
                    newPlan.setLastEndDate(planBase.getEndDate());
                }
            }
        }
        CommonProcessPrepare commonProcessPrepare = null;
        if (Objects.nonNull(req.getCommonId())) {
            commonProcessPrepare = commonProcessPrepareService.getById(req.getCommonId());
            if (Objects.nonNull(commonProcessPrepare) && Objects.nonNull(commonProcessPrepare.getFirstCheckFlag())
                    && Objects.equals(YesOrNoNumberEnum.YES.getCode(), commonProcessPrepare.getFirstCheckFlag())) {
                List<String> labels = new ArrayList<>();
                if (ObjectUtil.isNotEmpty(newPlan.getDeadlineLabel())) {
                    labels = JSONUtil.toList(newPlan.getDeadlineLabel(), String.class);
                }
                labels.add(AfterLeaseDeadlineLabelEnum.FIRST_CHECK.name());
                newPlan.setDeadlineLabel(JSONUtil.toJsonStr(labels));
                //  避免重复新增
                if(AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(req.getPlanType()) && StrUtil.isNotEmpty(commonProcessPrepare.getBusinessData())){
                    newPlan.setId(Long.parseLong(commonProcessPrepare.getBusinessData()));
                }
            }
        }
        this.saveOrUpdate(newPlan);
        //如果一般检查，直接插入客户
        if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(req.getPlanType())) {
            //回调待发起
            if (ObjectUtil.isNotEmpty(req.getCommonId())) {
                if (ObjectUtil.isNotEmpty(commonProcessPrepare)) {
                    commonProcessPrepareService.updateById(CommonProcessPrepare.builder().id(commonProcessPrepare.getId()).businessData(String.valueOf(newPlan.getId())).build());
                }
                NewAfterLeaseCheckPlanBase existPlanBase = baseMapper.selectById(req.getPlanId());
                if (Objects.nonNull(existPlanBase) && !req.getDeadLine().isEqual(existPlanBase.getDeadLine())) {
                    newPlan.setDeadlineLabel(JSONUtil.toJsonStr(ListUtil.of(AfterLeaseDeadlineLabelEnum.ASSERT_MANAGER_CONFIRM.name())));
                }
            }
            NewAfterLeaseCheckPlanClient planClient = BeanUtil.copyProperties(req, NewAfterLeaseCheckPlanClient.class, "belongCosponsorUserIds");
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
                planClient.setGuaranteeIds(JSONUtil.toJsonStr(clientedId2Name.keySet()));
                planClient.setGuaranteeNames(JSONUtil.toJsonStr(clientedId2Name.values()));
            }
            planClient.setPlanId(newPlan.getId());
            planClient.setStockRiskExposure(req.getStockRiskExposure());
            planClient.setId(req.getPlanClientId());
            Map<Long, Long> stockRiskExposureMap = clientService.clientStockRiskExposureMap(Collections.singletonList(req.getClientId()));
            if (CollUtil.isNotEmpty(stockRiskExposureMap)) {
                planClient.setRemainingPrincipal(stockRiskExposureMap.get(req.getClientId()));
            }
            planClient.setClientName(id2NameService.clientId2NameSingle(planClient.getClientId()));
            planClient.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            planClient.setIsCheck(YesOrNoNumberEnum.YES.getCode());
            PaymentBaseInfo fistPaymentByClient = paymentBaseInfoService.getFistPaymentByClient(req.getClientId());
            if (ObjectUtil.isNotEmpty(fistPaymentByClient)) {
                planClient.setPaymentDate(fistPaymentByClient.getPaidInDate());
            }
            planClient.setRiskManagerName(id2NameService.sysUserId2NameSingle(planClient.getRiskManagerId()));
            AfterLeaseCheckReportTypeEnum reportType = null;
            if (CharSequenceUtil.equals(req.getPlanType(), AfterLeaseCheckPlanTypeEnum.COMMONLY.name())) {
                if (CharSequenceUtil.isBlank(req.getReportType())) {
                    throw MithrasException.newException("请选择检查报告模板类型");
                }
                reportType = Optional.of(req.getReportType()).map(AfterLeaseCheckReportTypeEnum::ofName)
                        .orElseThrow(() -> new MithrasException("检查报告类型非法"));
            }
            afterLeaseCheckPlanClientService.saveOrUpdate(planClient);
            // 非自动发起直接通过
            if (ObjectUtil.isEmpty(req.getCommonId())) {
                // 直接发布
                SpringContextHolder.getBean(AfterLeaseCheckPlanBaseService.class).startChecking(newPlan.getId(), reportType);
                // 直接审批通过
                // 一般检查直接发起
                // 逻辑并入startChecking方法中
//                newPlan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
//                newPlan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
//                newPlan.setUpdateTime(LocalDateTimeUtil.now());
//                this.updateById(newPlan);
//                afterLeaseCheckPlanVersionService.recordVersion(newPlan.getId(), VersionTypeEnum.EFFECT, null,
//                        null, VersionTypeConstants.NORMAL);
                //这里添加一个
                AfterLeaseCheckReportBaseRSP checkReportBaseRsp = afterLeaseCheckReportBaseService.getCheckReportBaseRSP(planClient.getId(), null);
                if (ObjectUtil.isNotEmpty(checkReportBaseRsp)) {
                    AfterLeaseCheckReportBaseREQ afterLeaseCheckReportBaseReq = BeanUtil.copyProperties(checkReportBaseRsp, AfterLeaseCheckReportBaseREQ.class);
                    afterLeaseCheckReportBaseReq.setCheckPlanClientId(planClient.getId());
                    afterLeaseCheckReportBaseReq.setCheckTime(planClient.getCheckTime());
                    afterLeaseCheckReportBaseReq.setIsNotCheck(Boolean.TRUE);
                    if (ObjectUtil.isNotEmpty(req.getStockRiskExposure())) {
                        afterLeaseCheckReportBaseReq.setRiskExposure(req.getStockRiskExposure());
                    }
                    afterLeaseCheckReportBaseService.saveReportBase(afterLeaseCheckReportBaseReq);
                }
                //给项目经理推一般检查流程
                afterLeaseCheckPlanClientService.submitApproval(planClient.getId(), false);
                //通知
                return planClient.getId();
            }else {
                // 只生成报告
                SpringContextHolder.getBean(AfterLeaseCheckPlanBaseService.class).generateCheckReportMetaList(newPlan.getId(), reportType);
            }
        }
        return newPlan.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long modify(AfterLeaseCheckPlanBaseModifyREQ req) {
        NewAfterLeaseCheckPlanBase plan = this.getById(req.getId());
        Assert.notNull(plan, () -> MithrasException.newException("租后检查计划不存在"));
        plan.setPlanName(req.getPlanName());
        if (AfterLeaseCheckPlanTypeEnum.QUARTER.name().equals(plan.getPlanType())) {
            Assert.notNull(req.getQuarter(), () -> MithrasException.newException("季度不能为空"));
        } else {
            Assert.notNull(req.getMonth(), () -> MithrasException.newException("月份不能为空"));
        }
        plan.setYear(req.getYear());
        plan.setQuarter(req.getQuarter());
        plan.setMonth(req.getMonth());
        plan.setStartDate(LocalDateTimeUtil.parseDate(req.getStartDate(), DatePattern.NORM_DATE_PATTERN));
        plan.setEndDate(LocalDateTimeUtil.parseDate(req.getEndDate(), DatePattern.NORM_DATE_PATTERN));
        plan.setUpdateBy(AccountUtil.getLoginInfo().getId());
        plan.setUpdateTime(LocalDateTimeUtil.now());
        plan.setCheckWay(req.getCheckWay());
        if (ObjectUtil.isNotEmpty(req.getSponsorId()) || ObjectUtil.isNotEmpty(req.getRiskManagerId())) {
            //修改客户信息
            LambdaUpdateWrapper<NewAfterLeaseCheckPlanClient> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(NewAfterLeaseCheckPlanClient::getBelongSponsorId, req.getSponsorId());
            updateWrapper.set(NewAfterLeaseCheckPlanClient::getRiskManagerId, req.getRiskManagerId());
            updateWrapper.set(NewAfterLeaseCheckPlanClient::getRiskManagerName, id2NameService.sysUserId2NameSingle(req.getRiskManagerId()));
            updateWrapper.eq(NewAfterLeaseCheckPlanClient::getPlanId, req.getId());
            afterLeaseCheckPlanClientService.update(null, updateWrapper);
        }
        this.updateById(plan);
        this.changeModifyStatus(plan.getId());
        return plan.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void publish(AfterLeaseCheckPlanPublishREQ req) {
        NewAfterLeaseCheckPlanBase plan = this.getById(req.getPlanId());
        publishCheck(plan);
        //非创建的不能发起
        if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(plan.getPlanType())) {
            CommonProcessPrepare commonProcessPrepare = commonProcessPrepareService.getOne(Wrappers.<CommonProcessPrepare>lambdaQuery()
                    .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                    .eq(CommonProcessPrepare::getBusinessData, String.valueOf(plan.getId()))
                    .in(CommonProcessPrepare::getStatus, Arrays.asList(CommonProcessPrepareStatus.PEND_COMMIT,CommonProcessPrepareStatus.COMMITTED,CommonProcessPrepareStatus.AUTO_COMMITTED))
                    .orderByDesc(CommonProcessPrepare::getId)
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(commonProcessPrepare)) {
                throw new MithrasException("请在统一代办中确认计划！");
            }
        }
        // 根据当前状态判断发布后应该到达的审批状态
        if (Objects.equals(AfterLeaseCheckPlanStatusEnum.NEW.name(), plan.getPlanStatus())) {
            plan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.NEW_COMMIT.name());
        } else if (Objects.equals(AfterLeaseCheckPlanStatusEnum.MODIFY.name(), plan.getPlanStatus()) || Objects.equals(AfterLeaseCheckPlanStatusEnum.PUBLISH.name(), plan.getPlanStatus())) {
            plan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.MODIFY.name());
            plan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.MODIFY_COMMIT.name());
        } else {
            throw new MithrasException("当前计划状态不允许该操作");
        }
        this.updateById(plan);
        if (AfterLeaseCheckPlanTypeEnum.COMMONLY.name().equals(plan.getPlanType())) {
            //一般检查直接发起
            plan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
            plan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
            plan.setUpdateTime(LocalDateTimeUtil.now());
            this.updateById(plan);
            afterLeaseCheckPlanVersionService.recordVersion(plan.getId(), VersionTypeEnum.EFFECT, null,
                    null, VersionTypeConstants.NORMAL);
        }
        // 发起流程
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(plan.getId().toString());
        if (Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.NEW.name())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name());
        }
        AccountVO accountVO = AccountUtil.getLoginInfo();
        startProcessReq.setStartUserDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).map(Objects::toString).orElse(""));
        startProcessReq.setStartUserId(Optional.ofNullable(accountVO).map(AccountVO::getId).map(String::valueOf).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setProcessInstanceName(plan.getPlanName());
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, null);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void finish(Long planId) {
        NewAfterLeaseCheckPlanBase plan = this.getById(planId);
        Assert.notNull(plan, () -> MithrasException.newException("租后检查计划不存在"));
        Assert.isTrue(Objects.equals(AfterLeaseCheckPlanStatusEnum.CHECKING.name(), plan.getPlanStatus()), () -> MithrasException.newException("当前状态不允许提交完结审批"));
        // 变更流程状态
        plan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.FINISH_COMMIT.name());
        this.updateById(plan);
        // 发起流程
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(plan.getId().toString());
        startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name());
        AccountVO accountVO = AccountUtil.getLoginInfo();
        startProcessReq.setStartUserId(Optional.ofNullable(accountVO).map(AccountVO::getId).map(String::valueOf).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setProcessInstanceName(plan.getPlanName());
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, null);
    }

    @Override
    public AfterLeaseCheckPlanDetailRSP detail(Long planId, String version) {
        NewAfterLeaseCheckPlanBase plan;
        List<NewAfterLeaseCheckPlanClient> checkPlanProjectList;
        if (StrUtil.isBlank(version)) {
            // 查询检查计划信息
            plan = this.getById(planId);
            Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
            // 查询项目信息
            checkPlanProjectList = afterLeaseCheckPlanClientService.listBy(planId);
        } else {
            plan = checkPlanBaseLibService.getByOriginIdAndVersion(planId, version);
            Assert.notNull(plan, () -> MithrasException.newException("指定数据版本的检查计划不存在"));
            checkPlanProjectList = checkPlanProjectLibService.listByPlanIdAndVersion(planId, version);
        }
        AfterLeaseCheckPlanDetailRSP rsp = AfterLeaseCheckPlanConvert.toAfterLeaseCheckPlanDetailRSP(plan);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        Tuple tuple = this.calculateCount(checkPlanProjectList, canViewDeptIds);
        rsp.setTotalCount(tuple.get(0));
        rsp.setFinishCount(tuple.get(1));
        return rsp;
    }

    @Override
    public AfterLeaseCheckPlanCommonlyDetailRSP commonlyDetail(AfterLeaseCheckPlanCommonlyDetailREQ req) {
        if (Objects.isNull(req.getCheckPlanId())) {
            return null;
        }
        //查询待发起
        CommonProcessPrepare commonProcessPrepare = commonProcessPrepareService.getById(req.getTodoId());
        if (ObjectUtil.isEmpty(commonProcessPrepare) || ObjectUtil.isEmpty(commonProcessPrepare.getBusinessData())) {
            return null;
        }
        commonProcessPrepareService.lambdaUpdate()
                .eq(CommonProcessPrepare::getId, req.getTodoId())
                .set(CommonProcessPrepare::getBusinessData, req.getCheckPlanId())
                .update();
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getPlanId, req.getCheckPlanId())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getId)
                .last(StringUtil.mysqlLimitOne())
        );
        if (ObjectUtil.isEmpty(checkPlanClient)) {
            return null;
        }
        NewAfterLeaseCheckPlanBase planBase = this.getById(req.getCheckPlanId());
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        if (ObjectUtil.isNotEmpty(planBase)) {
            AfterLeaseCheckPlanCommonlyDetailRSP rsp = new AfterLeaseCheckPlanCommonlyDetailRSP();
            rsp.setPlanId(planBase.getId());
            rsp.setPlanClientId(checkPlanClient.getId());
            rsp.setPlanName(planBase.getPlanName());
            rsp.setPlanType(planBase.getPlanType());
            rsp.setClientId(checkPlanClient.getClientId());
            rsp.setClientName(id2NameService.clientId2NameSingle(checkPlanClient.getClientId()));
            rsp.setBelongSponsorId(checkPlanClient.getBelongSponsorId());
            rsp.setBelongSponsorName(id2NameService.sysUserId2NameSingle(checkPlanClient.getBelongSponsorId()));
            rsp.setCheckWay(checkPlanClient.getCheckWay());
            rsp.setRiskManagerId(checkPlanClient.getRiskManagerId());
            rsp.setRiskManagerName(id2NameService.sysUserId2NameSingle(checkPlanClient.getRiskManagerId()));
            rsp.setDeadLine(planBase.getDeadLine());
            rsp.setDeadlineLabel(JSONUtil.toList(planBase.getDeadlineLabel(), String.class));
            if (Objects.nonNull(checkReportMeta)) {
                rsp.setReportType(checkReportMeta.getReportType());
            }
            rsp.setRiskExposure(checkPlanClient.getStockRiskExposure());
            return rsp;
        }
        return null;
    }

    @Override
    public PageR<AfterLeaseCheckPlanListRSP> pageList(AfterLeaseCheckPlanListREQ req) {
        // 是否业务部门用户 如果有任何非业务部门，都不是业务部门 取高
        boolean bizDeptFlag = sysUserService.getUserDeptList().stream().allMatch(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType()));
        Page<NewAfterLeaseCheckPlanBase> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<NewAfterLeaseCheckPlanBase> query = Wrappers.lambdaQuery();
        if (bizDeptFlag) {
            // 业务部门只能看到检查中和计划完结的计划
            query.in(NewAfterLeaseCheckPlanBase::getPlanStatus, Arrays.asList(AfterLeaseCheckPlanStatusEnum.CHECKING.name(), AfterLeaseCheckPlanStatusEnum.FINISH.name()));
            AccountVO accountVO = Optional.ofNullable(AccountUtil.getLoginInfo()).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
            List<NewAfterLeaseCheckPlanClient> allCheckPlanClient = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanClient::getBelongSponsorId, accountVO.getId()));
            if (ObjectUtil.isEmpty(allCheckPlanClient)) {
                return null;
            }
            Set<Long> allPlanIds = allCheckPlanClient.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).collect(Collectors.toSet());
            query.in(CollectionUtil.isNotEmpty(allPlanIds), NewAfterLeaseCheckPlanBase::getId, allPlanIds);
        }
        query.like(StrUtil.isNotBlank(req.getPlanName()), NewAfterLeaseCheckPlanBase::getPlanName, req.getPlanName());
        query.eq(Objects.nonNull(req.getYear()), NewAfterLeaseCheckPlanBase::getYear, req.getYear());
        query.eq(StrUtil.isNotBlank(req.getPlanType()), NewAfterLeaseCheckPlanBase::getPlanType, req.getPlanType());
        query.eq(StrUtil.isNotBlank(req.getCheckWay()), NewAfterLeaseCheckPlanBase::getCheckWay, req.getCheckWay());
        query.eq(StrUtil.isNotBlank(req.getLastCheckWay()), NewAfterLeaseCheckPlanBase::getLastCheckWay, req.getLastCheckWay());
        query.ge(ObjectUtil.isNotEmpty(req.getDeadLineForm()), NewAfterLeaseCheckPlanBase::getDeadLine, req.getDeadLineForm());
        query.le(ObjectUtil.isNotEmpty(req.getDeadLineTo()), NewAfterLeaseCheckPlanBase::getDeadLine, req.getDeadLineTo());
        query.ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), NewAfterLeaseCheckPlanBase::getCreateTime, req.getCreateTimeFrom());
        query.le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), NewAfterLeaseCheckPlanBase::getCreateTime, req.getCreateTimeTo());
        query.orderByDesc(NewAfterLeaseCheckPlanBase::getId);
        Page<NewAfterLeaseCheckPlanBase> pageResult = this.page(pageQuery, query);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<NewAfterLeaseCheckPlanBase> dbList = pageResult.getRecords();
        List<Long> planIdList = dbList.stream().map(NewAfterLeaseCheckPlanBase::getId).collect(Collectors.toList());
        Map<Long, List<NewAfterLeaseCheckPlanClient>> map = afterLeaseCheckPlanClientService.getClientMapByPlan(planIdList);
        List<AfterLeaseCheckPlanListRSP> result = new ArrayList<>(dbList.size());
        //查询一般检查客户
        List<Long> planIds = dbList.stream().filter(e -> ObjectUtil.equals(e.getPlanType(), AfterLeaseCheckPlanTypeEnum.COMMONLY.name())).map(NewAfterLeaseCheckPlanBase::getId).collect(Collectors.toList());
        Map<Long, Long> planId2Client = new HashMap<>();
        if (CollectionUtil.isNotEmpty(planIds)) {
            List<NewAfterLeaseCheckPlanClient> list = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery().in(NewAfterLeaseCheckPlanClient::getPlanId, planIds));
            planId2Client.putAll(list.stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanClient::getPlanId, NewAfterLeaseCheckPlanClient::getId, (a, b) -> a)));
        }
        // 找到当前数据列表的租后检查报告基本信息
        Map<Long, AfterLeaseListExpandBO> boMap = new HashMap<>();
        Map<Long, Long> stockRiskExposureMap = new HashMap<>();
        Map<Long, Long> planIdClientIdMap = new HashMap<>();
        Map<Long, Long> clientRemainingPrincipalMap = new HashMap<>();
        Map<Long, String> userIdNameMap = new HashMap<>();
        Map<Long, String> deptIdNameMap = new HashMap<>();
        List<AfterLeaseListExpandBO> afterLeaseListExpandBOList = afterLeaseCheckReportBaseService.getAfterLeaseListExpandBO(dbList.stream().map(NewAfterLeaseCheckPlanBase::getId).collect(Collectors.toList()));
        if (CollUtil.isNotEmpty(afterLeaseListExpandBOList)) {
            boMap = afterLeaseListExpandBOList.stream().collect(Collectors.toMap(AfterLeaseListExpandBO::getPlanId, Function.identity(), (a, b) -> a));
            List<Long> ids = afterLeaseListExpandBOList.stream().map(AfterLeaseListExpandBO::getClientId).collect(Collectors.toList());
            stockRiskExposureMap = clientService.clientStockRiskExposureMap(ids);
            planIdClientIdMap = afterLeaseListExpandBOList.stream().filter(obj -> Objects.nonNull(obj.getClientId()))
                    .collect(Collectors.toMap(AfterLeaseListExpandBO::getPlanId, AfterLeaseListExpandBO::getClientId, (a, b) -> a));
            clientRemainingPrincipalMap = clientService.getClientRemainingPrincipalMap(ids);
            // 在这里还要找到业务部门信息和客户名称
            userIdNameMap = userDOMapper.selectByIds(afterLeaseListExpandBOList.stream().map(AfterLeaseListExpandBO::getProjSponsorUserId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUserName, (a, b) -> a));
            deptIdNameMap = orgDOMapper.selectByIds(afterLeaseListExpandBOList.stream().map(AfterLeaseListExpandBO::getBizDeptId).collect(Collectors.toList()), 1)
                    .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (a, b) -> a));
        }

        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        for (NewAfterLeaseCheckPlanBase planBase : dbList) {
            AfterLeaseCheckPlanListRSP rsp = AfterLeaseCheckPlanConvert.toAfterLeaseCheckPlanListRSP(planBase);
            // 填充计划包含项目数和检查完毕项目（需根据当前查看对象做不同处理）
            Tuple tuple = this.calculateCount(map.get(planBase.getId()), canViewDeptIds);
            rsp.setTotalCount(tuple.get(0));
            rsp.setFinishCount(tuple.get(1));
            rsp.setCheckPlanClientId(planId2Client.get(rsp.getId()));
            rsp.setDeadLine(planBase.getDeadLine());
            // 设置业务部门信息、剩余本金、风险敞口、风险风控经理、项目主办
            AfterLeaseListExpandBO bo = boMap.get(planBase.getId());
            Long clientId = planIdClientIdMap.get(planBase.getId());
            if (Objects.nonNull(bo) && ObjectUtil.equals(rsp.getPlanType(), AfterLeaseCheckPlanTypeEnum.COMMONLY.name())) {
                rsp.setBizDeptId(bo.getBizDeptId());
                rsp.setBizDeptName(deptIdNameMap.get(bo.getBizDeptId()));
                rsp.setProjSponsorUserId(bo.getProjSponsorUserId());
                rsp.setProjSponsorUserName(userIdNameMap.get(bo.getProjSponsorUserId()));
                rsp.setRiskControlManagerId(bo.getRiskControlManagerId());
                rsp.setRiskControlManagerName(bo.getRiskControlManagerName());
            }
            if (Objects.nonNull(clientId)) {
                Long aLong = stockRiskExposureMap.get(clientId);
                rsp.setRiskExposure(Optional.ofNullable(aLong).map(Util::toWanYuanWithoutSplit).orElse(null));
                rsp.setRemainingPrincipal(Optional.ofNullable(clientRemainingPrincipalMap.get(clientId)).map(Util::toWanYuanWithoutSplit).orElse(null));
            }
            result.add(rsp);
        }
        //补充当前审批人 一般检查计划
        if (ObjectUtil.isNotEmpty(result)) {
            List<String> checkPlanIds = result.stream().filter(e -> ObjectUtil.equals(e.getPlanType(), AfterLeaseCheckPlanTypeEnum.COMMONLY.name())).map(AfterLeaseCheckPlanListRSP::getCheckPlanClientId).map(String::valueOf).collect(Collectors.toList());
            ProcessListREQ processListREQ = new ProcessListREQ();
            processListREQ.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name());
            processListREQ.setBusinessKeyList(checkPlanIds);
            processListREQ.setPage(1);
            processListREQ.setPageSize(5000);
            PageR<ProcessListRSP> processListRSPPageR = myTaskService.searchList(processListREQ);
            if (ObjectUtil.isNotEmpty(processListRSPPageR) && ObjectUtil.isNotEmpty(processListRSPPageR.getList())) {
                //回填数据
                Map<Long, ProcessListRSP> businessKey2Flow = processListRSPPageR.getList().stream().collect(Collectors.toMap(e -> Long.parseLong(e.getBusinessKey()), e -> e, (a, b) -> a));
                result.forEach(e -> {
                    ProcessListRSP processListRSP = businessKey2Flow.get(e.getCheckPlanClientId());
                    if (ObjectUtil.isNotEmpty(processListRSP)) {
                        e.setCurAssigneeNames(processListRSP.getCurAssigneeNames());
                    }
                });
            }
        }
        return PageR.of(result, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void startChecking(Long id, AfterLeaseCheckReportTypeEnum reportType) {
        NewAfterLeaseCheckPlanBase planBase = this.getById(id);
        planBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
        planBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.CHECKING.name());
        planBase.setUpdateTime(LocalDateTimeUtil.now());
        this.updateById(planBase);
        afterLeaseCheckPlanVersionService.recordVersion(planBase.getId(), VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
        generateCheckReportMetaList(id, reportType);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generateCheckReportMetaList(Long id, AfterLeaseCheckReportTypeEnum reportType) {
        // 报告元数据生成，后续针对检查报告的报告类型查询及变更，都操作元数据表（解决客户表和报告表不是同步增减版本的问题）
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = afterLeaseCheckPlanClientService.listBy(id);
        List<NewAfterLeaseCheckReportDetail> checkReportDetailList = new ArrayList<>(checkPlanClientList.size());
        List<NewAfterLeaseCheckReportMeta> checkReportMetaList = new ArrayList<>(checkPlanClientList.size());
        List<NewAfterLeaseCheckReportMeta> checkReportMetas = afterLeaseCheckReportMetaService.listByCheckPlanClientIds(checkPlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getId).collect(Collectors.toList()));
        Map<Long, Long> checkReportMetaMap = new HashMap<>();
        if (CollUtil.isNotEmpty(checkReportMetas)) {
            checkReportMetaMap = checkReportMetas.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, NewAfterLeaseCheckReportMeta::getId));
        }
        for (NewAfterLeaseCheckPlanClient item : checkPlanClientList) {
            // 处理元数据
            NewAfterLeaseCheckReportMeta checkReportMeta = new NewAfterLeaseCheckReportMeta();
            checkReportMeta.setId(checkReportMetaMap.get(item.getId()));
            checkReportMeta.setCheckPlanClientId(item.getId());
            checkReportMeta.setReportTemplateVersion(AfterLeaseCheckReportTemplateVersionEnum.getLatestVersion(reportType.name()));
            checkReportMeta.setReportType(reportType.name());
            checkReportMetaList.add(checkReportMeta);
            // 处理报告详情数据
            NewAfterLeaseCheckReportDetail reportDetail = new NewAfterLeaseCheckReportDetail();
            reportDetail.setCheckPlanClientId(item.getId());
            checkReportDetailList.add(reportDetail);
        }
        if (CollectionUtil.isNotEmpty(checkReportMetaList)) {
            afterLeaseCheckReportMetaService.saveOrUpdateBatch(checkReportMetaList);
        }
        if (CollectionUtil.isNotEmpty(checkReportDetailList)) {
            afterLeaseCheckReportDetailService.saveBatch(checkReportDetailList);
        }
    }

    @Override
    public void changeModifyStatus(Long planId) {
        NewAfterLeaseCheckPlanBase plan = this.getById(planId);
        Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
        plan.setUpdateBy(AccountUtil.getLoginInfo().getId());
        plan.setUpdateTime(LocalDateTimeUtil.now());
        if (Objects.equals(plan.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.PUBLISH.name())) {
            if (!Objects.equals(plan.getApprovalStatus(), AfterLeaseCheckPlanProcessStatusEnum.MODIFY_COMMIT.name())) {
                plan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.MODIFY.name());
                plan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.MODIFY_UN_COMMIT.name());
            }
        }
        this.updateById(plan);
    }

    @Override
    public String formatPlanTime(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase) {
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.nonNull(newAfterLeaseCheckPlanBase.getYear())) {
            stringBuilder.append(newAfterLeaseCheckPlanBase.getYear()).append("年");
        }
        if (Objects.equals(newAfterLeaseCheckPlanBase.getPlanType(), AfterLeaseCheckPlanTypeEnum.QUARTER.name())) {
            if (Objects.nonNull(newAfterLeaseCheckPlanBase.getQuarter())) {
                stringBuilder.append("第").append(newAfterLeaseCheckPlanBase.getQuarter()).append("季度");
            }
        } else {
            if (Objects.nonNull(newAfterLeaseCheckPlanBase.getMonth())) {
                stringBuilder.append(newAfterLeaseCheckPlanBase.getMonth()).append("月");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void publishCheck(Long id) {
        NewAfterLeaseCheckPlanBase plan = this.getById(id);
        publishCheck(plan);
    }

    @Override
    public void publishCheck(NewAfterLeaseCheckPlanBase plan) {
        Assert.notNull(plan, () -> MithrasException.newException("租后检查计划不存在"));
        Assert.notBlank(plan.getPlanName(), () -> MithrasException.newException("计划名称不能为空"));
        Assert.notNull(plan.getStartDate(), () -> MithrasException.newException("计划开始时间不能为空"));
        Assert.notNull(plan.getEndDate(), () -> MithrasException.newException("计划结束时间不能为空"));
        if (AfterLeaseCheckPlanTypeEnum.QUARTER.name().equals(plan.getPlanType())) {
            Assert.notNull(plan.getQuarter(), () -> MithrasException.newException("季度不能为空"));
        } else {
            Assert.notNull(plan.getMonth(), () -> MithrasException.newException("月份不能为空"));
        }
        // 校验是否有项目数据
        List<NewAfterLeaseCheckPlanClient> checkPlanProjectList = afterLeaseCheckPlanClientService.listBy(plan.getId());
        Assert.notEmpty(checkPlanProjectList, () -> MithrasException.newException("请先添加计划客户信息"));
        boolean hasCheckProject = false;
        // 项目数据是否完整
        for (NewAfterLeaseCheckPlanClient checkPlanProject : checkPlanProjectList) {
            if (Objects.equals(checkPlanProject.getIsCheck(), YesOrNoNumberEnum.YES.getCode())) {
                hasCheckProject = true;
                if (Objects.equals(checkPlanProject.getCheckWay(), AfterLeaseCheckWayEnum.SITE.name())) {
                    Assert.notNull(checkPlanProject.getRiskManagerId(), () -> MithrasException.newException("现场检查客户协查风控经理不能为空"));
                    Assert.notNull(checkPlanProject.getRiskManagerName(), () -> MithrasException.newException("现场检查客户协查风控经理不能为空"));
                }
            }
        }
        Assert.isTrue(hasCheckProject, () -> MithrasException.newException("至少要有一个需检查的项目"));
    }

    @Override
    public AfterLeaseAuditFlowPreRSP afterLeaseAuditPreSelect(AfterLeaseAuditFlowPreREQ request) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(request.getPlanClientId()));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList());
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (processResp == null) {
            return AfterLeaseAuditFlowPreRSP.builder().processInstanceId("").auditButtonOpenFlag(0).build();
        } else {
            return AfterLeaseAuditFlowPreRSP.builder().processInstanceId(processResp.getProcessInstanceId()).auditButtonOpenFlag(1).build();
        }
    }

    /**
     * 1.五级分类 非正常类（非正常类）每90天现场全面检查
     * 2.风控行业分类
     * 2.1 公用事业类、民生消费类 每180天非现场检查 + 每360天现场全面检查
     * 3.风险敞口
     * 3.1 >= 5000万 每90天现场全面检查
     * 3.2 1500万元（含）～5000万元（不含） 每90天非现场检查 + 每180天现场全面检查
     * 3.3 <=1500万元（不含）—非新投每180天非现场检查
     * 3.4 <=1500万元（不含）—新投 首次90天现场全面检查，后续每180天非现场检查
     **/
    @Override
    public List<AfterLeaseClientPlanRSP> getClientPlan(Long clientId, LocalDate riskExposureDate) {
        if (ObjectUtil.isEmpty(clientId) || ObjectUtil.isEmpty(riskExposureDate)) {
            return null;
        }
        List<AfterLeaseClientPlanRSP> rsps = new ArrayList<>();
        //PaymentBaseInfo fistPaymentByClient = paymentBaseInfoService.getFistPaymentByClient(clientId);
        //替换为上次结束时间
        LocalDate now = riskExposureDate;
       /* if (ObjectUtil.isNotEmpty(fistPaymentByClient)) {
            now = fistPaymentByClient.getPaidInDate();
        }*/
        //查客户信息
//        CorpCommerceInfo corpCommerceInfo = Optional.ofNullable(corpCommerceInfoService.detail(clientId, null)).orElseThrow(() -> new MithrasException(ResultMsg.RECORD_NOT_EXIST));
        List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(clientId);
        if (CollectionUtil.isEmpty(corpCommerceInfoList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
        //查询合同信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, clientId)
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                .orderByDesc(ContractBaseInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        Long belongSponsorId;
        Long bizDeptId;
        if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
            belongSponsorId = contractBaseInfo.getProjSponsorUserId();
            bizDeptId = contractBaseInfo.getBizDeptId();
        } else {
            bizDeptId = null;
            belongSponsorId = null;
        }
        String belongSponsorName = id2NameService.sysUserId2NameSingle(belongSponsorId);
        int phase = 0;
        //查询检查信息
        List<NewAfterLeaseCheckPlanClient> newAfterLeaseCheckPlanClients = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .ge(NewAfterLeaseCheckPlanClient::getCreateTime, now.with(TemporalAdjusters.firstDayOfYear()))
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId));
        if (CollectionUtil.isNotEmpty(newAfterLeaseCheckPlanClients)) {
            Set<Long> planIds = newAfterLeaseCheckPlanClients.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).collect(Collectors.toSet());
            if (ObjectUtil.isNotEmpty(planIds)) {
                phase = newAfterLeaseCheckPlanBaseMapper.selectCount(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                        .in(NewAfterLeaseCheckPlanBase::getId, planIds)
                        .eq(NewAfterLeaseCheckPlanBase::getPlanType, AfterLeaseCheckPlanTypeEnum.COMMONLY.name()));
            }
        }
        //
        List<NewAfterLeaseCheckPlanClient> allNewAfterLeaseCheckPlanClients = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId));
        if (CollectionUtil.isNotEmpty(allNewAfterLeaseCheckPlanClients)) {
            NewAfterLeaseCheckPlanBase planBase = newAfterLeaseCheckPlanBaseMapper.selectOne(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                    .in(NewAfterLeaseCheckPlanBase::getId, allNewAfterLeaseCheckPlanClients.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).collect(Collectors.toSet()))
                    .eq(NewAfterLeaseCheckPlanBase::getPlanType, AfterLeaseCheckPlanTypeEnum.COMMONLY.name())
                    .ne(NewAfterLeaseCheckPlanBase::getPlanStatus, AfterLeaseCheckPlanStatusEnum.CLOSE)
                    .orderByDesc(NewAfterLeaseCheckPlanBase::getId)
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(planBase) && ObjectUtil.isNotEmpty(planBase.getEndDate())) {
                now = LocalDate.of(planBase.getEndDate().getYear(), now.getMonthValue(), now.getDayOfMonth()).plusMonths((long) planBase.getEndDate().getMonthValue() - (long) now.getMonthValue());
            }
            if (ObjectUtil.isNotEmpty(planBase) && ObjectUtil.isNotEmpty(planBase.getDeadLine())) {
                now = LocalDate.of(planBase.getDeadLine().getYear(), now.getMonthValue(), now.getDayOfMonth()).plusMonths((long) planBase.getDeadLine().getMonthValue() - (long) now.getMonthValue());
            }
        }
        //风险敞口
        long stockRiskExposure = LongUtil.null2zero(contractBaseInfoService.getStockRiskExposure(clientId, null, null, riskExposureDate.plusDays(5)));
        //最新五级分类
        AssetClassifyClient lastOneByClientId = assetClassifyClientService.getLastOneByClientId(clientId, null);
        if (ObjectUtil.isNotEmpty(lastOneByClientId) && !AssetClassifyResultEnum.NORMAL.name().equals(lastOneByClientId.getClassifyResult())) {
            //非正常
            phase++;
            rsps.add(AfterLeaseClientPlanRSP.builder()
                    .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                    .term(AfterLeaseCheckTermEnum.QUARTER.term)
                    .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.QUARTER.month))
                    .belongSponsorId(belongSponsorId)
                    .belongSponsorName(belongSponsorName)
                    .belongDeptId(bizDeptId)
                    .stockRiskExposure(stockRiskExposure)
                    .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                    .build());
            String clientName = id2NameService.clientId2NameSingle(clientId);
            rsps.forEach(e -> {
                e.setClientId(clientId);
                e.setClientName(clientName);
            });
            return rsps;
        }
        LocalDate finalNow = now;
        AtomicInteger finalPhase = new AtomicInteger(phase);
        List<AfterLeaseClientPlanRSP> afterLeaseClientPlanRSPList = AfterLeaseClientPlanFactory.getInstance().retrieveAfterLeaseClientPlans(
                corpCommerceInfo.getRiskControlIndustryClassify(),
                (afterLeaseClientPlanRSP) -> {
                    afterLeaseClientPlanRSP.setDeadLine(finalNow.plusDays(afterLeaseClientPlanRSP.getTerm()));
                    afterLeaseClientPlanRSP.setBelongSponsorId(belongSponsorId);
                    afterLeaseClientPlanRSP.setBelongSponsorName(belongSponsorName);
                    afterLeaseClientPlanRSP.setBelongDeptId(bizDeptId);
                    afterLeaseClientPlanRSP.setPlanName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), finalNow.getYear(), finalPhase.incrementAndGet()));
                },
                stockRiskExposure);
        rsps.addAll(afterLeaseClientPlanRSPList);
            if(CollUtil.isEmpty(afterLeaseClientPlanRSPList)) {
            //>= 5000万 每90天现场全面检查
            if (stockRiskExposure >= 500000000000L) {
                phase++;
                rsps.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.QUARTER.term)
                        .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.QUARTER.month))
                        .belongSponsorId(belongSponsorId)
                        .belongSponsorName(belongSponsorName)
                        .belongDeptId(bizDeptId)
                        .stockRiskExposure(stockRiskExposure)
                        .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                        .build());
            } else if (stockRiskExposure >= 150000000000L) {
                //1500万元（含）～5000万元（不含） 每90天非现场检查 + 每180天现场全面检查
                phase++;
                rsps.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                        .term(AfterLeaseCheckTermEnum.QUARTER.term)
                        .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.QUARTER.month))
                        .belongSponsorId(belongSponsorId)
                        .belongSponsorName(belongSponsorName)
                        .belongDeptId(bizDeptId)
                        .stockRiskExposure(stockRiskExposure)
                        .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                        .build());
                phase++;
                rsps.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.QUARTER.term)
                        .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.QUARTER.month))
                        .belongSponsorId(belongSponsorId)
                        .belongSponsorName(belongSponsorName)
                        .belongDeptId(bizDeptId)
                        .stockRiskExposure(stockRiskExposure)
                        .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                        .build());
            } else {
                //<=1500万元（不含）—非新投每180天非现场检查
                //<=1500万元（不含）—新投 首次90天现场全面检查，后续每180天非现场检查
                phase++;
                if (ObjectUtil.isEmpty(afterLeaseCheckPlanClientService.listByClientIds(Collections.singleton(clientId)))) {
                    rsps.add(AfterLeaseClientPlanRSP.builder()
                            .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                            .term(AfterLeaseCheckTermEnum.QUARTER.term)
                            .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.QUARTER.month))
                            .belongSponsorId(belongSponsorId)
                            .belongSponsorName(belongSponsorName)
                            .belongDeptId(bizDeptId)
                            .stockRiskExposure(stockRiskExposure)
                            .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                            .build());
                } else {
                    rsps.add(AfterLeaseClientPlanRSP.builder()
                            .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                            .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                            .deadLine(now.plusMonths(AfterLeaseCheckTermEnum.HALF_A_YEAR.month))
                            .belongSponsorId(belongSponsorId)
                            .belongSponsorName(belongSponsorName)
                            .belongDeptId(bizDeptId)
                            .stockRiskExposure(stockRiskExposure)
                            .planName(String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), now.getYear(), phase))
                            .build());
                }
            }
        }
        String clientName = id2NameService.clientId2NameSingle(clientId);
        String reportType = null;
        String checkWay = null;
        LocalDate nextDeadlineDate = null;
        String deadlineLabel = AfterLeaseDeadlineLabelEnum.SYSTEM_CALCULATE.name();
        NewAfterLeaseCheckPlanClient planClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId)
                .eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotNull(planClient)) {
            NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(planClient.getId());
            NewAfterLeaseCheckPlanBase base = this.getById(planClient.getPlanId());
            nextDeadlineDate = planClient.getNextDeadline();
            checkWay = planClient.getNextCheckWay();
            if (ObjectUtil.isNotNull(base)) {
                if (Objects.nonNull(base.getDeadlineLabel())) {
                    deadlineLabel = base.getDeadlineLabel();
                }
                if (ObjectUtil.isNotNull(checkReportMeta)) {
                    reportType = checkReportMeta.getReportType();
                } else {
                    if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(corpCommerceInfo.getRiskControlIndustryClassify()) || RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(corpCommerceInfo.getRiskControlIndustryClassify())) {
                        reportType = AfterLeaseCheckReportTypeEnum.PUBLIC.name();
                    } else {
                        reportType = AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name();
                    }
                }
            }
        } else {
            if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(corpCommerceInfo.getRiskControlIndustryClassify()) || RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(corpCommerceInfo.getRiskControlIndustryClassify())) {
                reportType = AfterLeaseCheckReportTypeEnum.PUBLIC.name();
            } else {
                reportType = AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name();
            }
        }
        for (AfterLeaseClientPlanRSP e : rsps) {
            e.setDeadlineLabel(new ArrayList<>(Collections.singleton(deadlineLabel)));
            e.setReportType(reportType);
            e.setClientId(clientId);
            if (CharSequenceUtil.isNotBlank(checkWay)) {
                e.setCheckWay(checkWay);
            }
            if (Objects.nonNull(nextDeadlineDate)) {
                e.setDeadLine(nextDeadlineDate);
            }
            e.setClientName(clientName);
        }
        return rsps;
    }

    @Override
    public PageR<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategy(AfterLeaseAssetStrategyREQ req) {
        Page<AfterLeaseAssetStrategyRSP> pageResult = newAfterLeaseCheckPlanBaseMapper.afterLeaseAssetStrategy(new Page<>(req.getPage(), req.getPageSize()), req);
        if (ObjectUtil.isNotEmpty(pageResult.getRecords())) {
            pageResult.getRecords().forEach(AfterLeaseCheckPlanBaseServiceImpl::accept);
            Map<Long, Long> clientStockRiskExposureMap = clientService.clientStockRiskExposureMap(pageResult.getRecords().stream().map(AfterLeaseAssetStrategyRSP::getClientId).collect(Collectors.toList()));
            pageResult.setRecords(pageResult.getRecords().stream().map(e -> {
                e.setRiskExposure(clientStockRiskExposureMap.get(e.getClientId()));
                return e;
            }).collect(Collectors.toList()));
        }
        return PageR.of(pageResult.getRecords(), pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    private static void accept(AfterLeaseAssetStrategyRSP e) {
        e.setTermName(Optional.ofNullable(AfterLeaseCheckTermEnum.ofTerm(e.getTerm())).map(AfterLeaseCheckTermEnum::name).orElse(null));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void afterLeaseAssetStrategyModify(AfterLeaseAssetStrategyModifyREQ req) {
        AfterLeaseCheckTermEnum afterLeaseCheckTermEnum = Optional.ofNullable(AfterLeaseCheckTermEnum.of(req.getTerm())).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        NewAfterLeaseCheckPlanBase planBase = Optional.ofNullable(newAfterLeaseCheckPlanBaseMapper.selectById(req.getPlanId())).orElseThrow(() -> new MithrasException(ResultMsg.RECORD_NOT_EXIST));
        List<NewAfterLeaseCheckPlanClient> planClients = Optional.ofNullable(afterLeaseCheckPlanClientService.listBy(planBase.getId())).orElseThrow(() -> new MithrasException(ResultMsg.RECORD_NOT_EXIST));
        planClients.forEach(planClient -> {
            if (!planBase.getPlanType().equals(AfterLeaseCheckPlanTypeEnum.COMMONLY.name())) {
                if (StrUtil.equalsAny(planClient.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.APPROVAL_PASS.name())) {
                    throw new MithrasException("任务已完成,不可修改");
                }
            }
        });
        //校验，项目经理已经提交不可修改
        ProcessPageReq processReq = new ProcessPageReq();
        processReq.setBusinessKey(String.valueOf(planClients.get(0).getId()));
        processReq.setPageIndex(1);
        processReq.setPageSize(1);
        processReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name());
        processReq.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(processReq).getContents().stream().findFirst().orElse(null);
        if (Objects.nonNull(processResp)) {
            ProcessHistoryREQ historyReq = new ProcessHistoryREQ();
            historyReq.setPage(1);
            historyReq.setPageSize(Integer.MAX_VALUE);
            historyReq.setProcessInstanceId(processResp.getProcessInstanceId());
            PageR<ProcessHistoryRSP> historyList = processService.history(historyReq);
            ProcessListREQ processListREQ = new ProcessListREQ();
            processListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
            PageR<ProcessListRSP> processListRSPPageR = myTaskService.searchList(processListREQ);
            ProcessListRSP processListRSP = null;
            if (ObjectUtil.isNotEmpty(processListRSPPageR) && ObjectUtil.isNotEmpty(processListRSPPageR.getList())) {
                processListRSP = processListRSPPageR.getList().get(0);
            }

            if (ObjectUtil.isNotEmpty(historyList) && ObjectUtil.isNotEmpty(historyList.getList())) {
                //当前处理项目经理节点之后
                if (!(processListRSP != null && processListRSP.getCurTaskActivityIds() != null && processListRSP.getCurTaskActivityIds().contains(USER_TASK_START_USER))) {
                    for (ProcessHistoryRSP historyRsp : historyList.getList()) {
                        if (ObjectUtil.equals(PROJECT_MANAGER, historyRsp.getTaskActivityId()) && StrUtil.equalsAny(historyRsp.getType(), ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_CONDITION_AGREE.name())) {
                            throw new MithrasException("项目经理已提交，不可修改");
                        }
                    }
                }
            }
        }
        Map<Long, String> orgMap = userDOMapper.selectAll().stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUserName, (v1, v2) -> v1));
        //变更数据
        LambdaUpdateWrapper<NewAfterLeaseCheckPlanBase> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(NewAfterLeaseCheckPlanBase::getCheckWay, req.getCheckWay());
        updateWrapper.set(NewAfterLeaseCheckPlanBase::getTerm, afterLeaseCheckTermEnum.term);
        updateWrapper.set(NewAfterLeaseCheckPlanBase::getDeadLine, req.getDeadLine());
        updateWrapper.eq(NewAfterLeaseCheckPlanBase::getId, req.getPlanId());
        newAfterLeaseCheckPlanBaseMapper.update(null, updateWrapper);
        if (ObjectUtil.isNotEmpty(req.getRiskManagerId())) {
            String riskManagerName = id2NameService.sysUserId2NameSingle(req.getRiskManagerId());
            LambdaUpdateWrapper<NewAfterLeaseCheckPlanClient> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(NewAfterLeaseCheckPlanClient::getRiskManagerId, req.getRiskManagerId());
            wrapper.set(NewAfterLeaseCheckPlanClient::getRiskManagerName, riskManagerName);
            wrapper.eq(NewAfterLeaseCheckPlanClient::getPlanId, req.getPlanId());
            newAfterLeaseCheckPlanClientMapper.update(null, wrapper);
        }
        //修改记录
        List<AfterLeaseCheckChangeRecord> recordList = new ArrayList<>();
        boolean notice = false;
        if (!ObjectUtil.equals(req.getCheckWay(), planBase.getCheckWay())) {
            AfterLeaseCheckChangeRecord record = new AfterLeaseCheckChangeRecord();
            record.setPlanId(req.getPlanId());
            record.setCheckPlanClientId(req.getCheckPlanClientId());
            record.setOldContent("跟进形式" + Optional.ofNullable(AfterLeaseCheckWayEnum.find(planBase.getCheckWay())).map(AfterLeaseCheckWayEnum::getDisplay).orElse("-"));
            record.setNowContent("跟进形式" + Optional.ofNullable(AfterLeaseCheckWayEnum.find(req.getCheckWay())).map(AfterLeaseCheckWayEnum::getDisplay).orElse("-"));
            recordList.add(record);
            notice = true;
        }
        if (!ObjectUtil.equals(afterLeaseCheckTermEnum.term, planBase.getTerm())) {
            AfterLeaseCheckChangeRecord record = new AfterLeaseCheckChangeRecord();
            record.setPlanId(req.getPlanId());
            record.setCheckPlanClientId(req.getCheckPlanClientId());
            record.setOldContent("跟进频率" + Optional.ofNullable(AfterLeaseCheckTermEnum.ofTerm(planBase.getTerm())).map(AfterLeaseCheckTermEnum::display).orElse("-"));
            record.setNowContent("跟进频率" + Optional.ofNullable(AfterLeaseCheckTermEnum.of(req.getTerm())).map(AfterLeaseCheckTermEnum::display).orElse("-"));
            recordList.add(record);
        }
        if (ObjectUtil.isNotEmpty(req.getDeadLine()) && !req.getDeadLine().equals(planBase.getDeadLine())) {
            AfterLeaseCheckChangeRecord record = new AfterLeaseCheckChangeRecord();
            record.setPlanId(req.getPlanId());
            record.setCheckPlanClientId(req.getCheckPlanClientId());
            String time1 = ObjectUtil.isNotEmpty(planBase.getDeadLine()) ? planBase.getDeadLine().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)) : "-";
            record.setOldContent("下次跟进时间" + time1);
            String time2 = ObjectUtil.isNotEmpty(req.getDeadLine()) ? req.getDeadLine().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)) : "-";
            record.setNowContent("下次跟进时间" + time2);
            recordList.add(record);
            notice = true;
        }
        Long riskManagerId = req.getRiskManagerId();
        if (ObjectUtil.isNotEmpty(req.getRiskManagerId()) && !riskManagerId.equals(planClients.get(0).getRiskManagerId())) {
            AfterLeaseCheckChangeRecord record = new AfterLeaseCheckChangeRecord();
            record.setPlanId(req.getPlanId());
            record.setCheckPlanClientId(req.getCheckPlanClientId());
            record.setOldContent("上次协查风控经理：" + orgMap.get(planClients.get(0).getRiskManagerId()));
            record.setNowContent("下次协查风控经理：" + orgMap.get(riskManagerId));
            recordList.add(record);
            notice = true;
        }
        if (notice) {
            notice(planBase.getPlanName(), planClients.get(0).getId(), planClients.get(0).getBelongSponsorId());
            noticeChange(req.getCheckPlanClientId(), JSONUtil.toJsonStr(recordList));
        }
        if (!recordList.isEmpty()) {
            afterLeaseCheckChangeRecordService.saveBatch(recordList);
        }
    }

    private void noticeChange(Long checkPlanClientId, String message) {
        CommonProcessPrepare prepare = commonProcessPrepareService.getOne(Wrappers.<CommonProcessPrepare>lambdaQuery()
                .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name())
                .eq(CommonProcessPrepare::getBusinessId, String.valueOf(checkPlanClientId))
                .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(prepare)) {
            return;
        }
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.AFTER_LEASE_CHECK_UPDATE.name());
        messageAddREQ.setContent(null);
        List<Long> to = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(prepare.getCurrentAssignee())) {
            to.addAll(JSONUtil.toList(prepare.getCurrentAssignee(), Long.class));
        }
        messageAddREQ.setTo(to);
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNeedQa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.AFTER_LEASE_CHECK.name());
        MessageModel messageModel = messageConvert.reqToMessage(messageAddREQ);
        NoticeMessageBody bodie = (NoticeMessageBody) messageModel.getBodie();
        // 先校验流程状态，流程状态为结清审批通过才支持修改合同状态，不然的话把合同状态的变更放到审批通过后去做
        bodie.setTitle(message);
        //发送通知
        messageService.sendMessage(messageModel);
    }

    //第一次投放，添加检查
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clientFirstCheck(Long clientId) {
        List<CommonProcessPrepare> existPrepare = SpringContextHolder.getBean(CommonProcessPrepareService.class).list(
                Wrappers.<CommonProcessPrepare>lambdaQuery()
                        .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                        .eq(CommonProcessPrepare::getBusinessId, String.valueOf(clientId))
                        .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.WAITING_PEND.name())
        );
        if(CollectionUtil.isEmpty(existPrepare)){
            String clientName = id2NameService.clientId2NameSingle(clientId);
            List<Long> jobIds = sysUserService.queryJobUserIds(JobEnum.assetmanagement.name());
            // XMX-34 租后检查计划代办-截止日前60天可见，此处设置状态不可见WAITING_PEND，由定时任务每日触发判断(updateCheckPlanStatus)
            CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                    .status(CommonProcessPrepareStatus.WAITING_PEND.name())
                    .businessId(String.valueOf(clientId))
                    .clientName(clientName)
                    .firstCheckFlag(1)
                    .processType(ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                    .formName(clientName + "-租后检查（一般检查计划）")
                    .currentNode("资产管理岗确认")
                    .currentAssignee(JSONUtil.toJsonStr(jobIds))
                    .build();
            SpringContextHolder.getBean(CommonProcessPrepareService.class).save(prepare);
        }
    }

    @Override
    public PageR<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategys(AfterLeaseAssetStrategyREQ req) {
        Page<AfterLeaseAssetStrategyRSP> pageResult = newAfterLeaseCheckPlanBaseMapper.afterLeaseAssetStrategys(new Page<>(req.getPage(), req.getPageSize()), req);
        if (ObjectUtil.isNotEmpty(pageResult.getRecords())) {
            List<AfterLeaseAssetStrategyRSP> records = pageResult.getRecords();
            //获取项目经理名称
            List<AfterLeaseAssetStrategyRSP> sponsorName = this.baseMapper.getSponsorName();
            Map<Long, String> sponsorMap = sponsorName.stream().collect(Collectors.toMap(AfterLeaseAssetStrategyRSP::getSponsorId,
                    AfterLeaseAssetStrategyRSP::getSponsorName));
            //获取所有客户名称
            List<Client> userList = clientService.list();
            // 没用的代码注释掉
            // Map<Long, String> userMap = userList.stream().collect(Collectors.toMap(Client::getId, Client::getClientName));
            //获取所有部门名称
            List<AfterLeaseAssetStrategyRSP> deptList = this.baseMapper.queryAllDeptName();
            Map<Long, String> deptMap = deptList.stream().collect(Collectors.toMap(AfterLeaseAssetStrategyRSP::getBizDeptId,
                    AfterLeaseAssetStrategyRSP::getBizDeptName));
            //获取五级分类结果
            List<AfterLeaseAssetStrategyRSP> classifyResultList = this.baseMapper.getClassifyResult();
            Map<Long, String> ClassifyMap = classifyResultList.stream().collect(Collectors.toMap(AfterLeaseAssetStrategyRSP::getClientId,
                    AfterLeaseAssetStrategyRSP::getClassifyResult));
            Map<Long, NewAfterLeaseCheckPlanBaseLib> planBaseLibMap = planBaseLibMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanBaseLib>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanBaseLib::getVersionType, 1)
                    .groupBy(NewAfterLeaseCheckPlanBaseLib::getOriginId)
                    .orderByDesc(NewAfterLeaseCheckPlanBaseLib::getVersion)
            ).stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanBaseLib::getOriginId, c -> c, (v1, v2) -> v1));
            Map<Long, NewAfterLeaseCheckPlanClientLib> planClientLibMap = planClientLibMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanClientLib>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanClientLib::getVersionType, 1)
                    .groupBy(NewAfterLeaseCheckPlanClientLib::getPlanId)
                    .orderByDesc(NewAfterLeaseCheckPlanClientLib::getVersion)
            ).stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanClientLib::getPlanId, c -> c, (k1, k2) -> k1));

            records.stream().forEach(r -> {
                NewAfterLeaseCheckPlanBaseLib planBaseLib = planBaseLibMap.get(r.getPlanId());
                NewAfterLeaseCheckPlanClientLib planClientLib = planClientLibMap.get(r.getPlanId());
                r.setBizDeptName(StringUtils.isBlank(r.getBizDeptName()) ? deptMap.get(planClientLib.getBelongDeptId()) : r.getBizDeptName());
                r.setPaymentDate(r.getPaymentDate() != null ? r.getPaymentDate() : planClientLib.getPaymentDate());
                r.setClassifyResult(StringUtils.isBlank(r.getClassifyResult()) ? ClassifyMap.get(planClientLib.getClientId()) : r.getClassifyResult());
                r.setSponsorName(StringUtils.isBlank(r.getSponsorName()) ? sponsorMap.get(planClientLib.getBelongSponsorId()) : r.getSponsorName());
                r.setRiskManagerName(StringUtils.isBlank(r.getRiskManagerName()) ? planClientLib.getRiskManagerName() : r.getRiskManagerName());
                r.setTermName(Optional.ofNullable(AfterLeaseCheckTermEnum.ofTerm(r.getTerm())).map(AfterLeaseCheckTermEnum::name)
                        .orElse(null));
                if (StringUtils.isBlank(r.getTermName())) {
                    r.setTermName(Optional.ofNullable(AfterLeaseCheckTermEnum.ofTerm(planBaseLib.getTerm())).map(AfterLeaseCheckTermEnum::name)
                            .orElse(null));
                }
                r.setCheckWay(StringUtils.isBlank(r.getCheckWay()) ? planBaseLib.getCheckWay() : r.getCheckWay());
                r.setDeadLine(r.getDeadLine() != null ? r.getDeadLine() : planBaseLib.getDeadLine());
            });
        }
        return PageR.of(pageResult.getRecords(), pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    private void notice(String relation, Long planClientId, Long userId) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.AFTER_LEASE_CHECK_CHANGE.name());
        messageAddREQ.setRelation(relation);
        messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.AFTER_LEASE_CHECK_CHANGE.pcUrl, planClientId));
        messageAddREQ.setTo(Collections.singletonList(userId));
        messageAddREQ.setNeedQa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.CREDIT_REPORT.name());
        messageService.sendMessage(SpringContextHolder.getBean(MessageConver.class).reqToMessage(messageAddREQ));
    }


    private Tuple calculateCount(List<NewAfterLeaseCheckPlanClient> checkPlanProjectList, List<Long> canViewDeptIds) {
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Long loginUserId = AccountUtil.getLoginInfo().getId();

        int finishCount = 0;
        int totalCount = 0;
        if (CollectionUtil.isNotEmpty(checkPlanProjectList)) {
            for (NewAfterLeaseCheckPlanClient checkPlanProject : checkPlanProjectList) {
                if (isBizUser) {
                    if (!canViewDeptIds.contains(checkPlanProject.getBelongDeptId())
                            && !Objects.equals(loginUserId, checkPlanProject.getBelongSponsorId())) {
                        continue;
                    }
                }
                if (Objects.equals(checkPlanProject.getIsCheck(), YesOrNoNumberEnum.NO.getCode())) {
                    // 不检查的不计入总数
                    continue;
                }
                totalCount++;
                if (Objects.equals(checkPlanProject.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name()) || Objects.equals(checkPlanProject.getCheckWay(), AfterLeaseCheckWayEnum.WITHOUT_CHECK.name())) {
                    finishCount++;
                }
            }
        }
        return new Tuple(totalCount, finishCount);
    }

    @Override
    public List<AfterLeaseClientPlanRSP> getNextCheckPlan(Long clientId, Long todoId) {
        List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(clientId);
        if (CollectionUtil.isEmpty(corpCommerceInfoList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        NewAfterLeaseCheckPlanBase planBase = new NewAfterLeaseCheckPlanBase();
        //查询上一次结束的一般检查任务
        NewAfterLeaseCheckPlanClient lastCheckPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId)
                .eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.nonNull(lastCheckPlanClient)) {
            planBase = this.getById(lastCheckPlanClient.getPlanId());
        }
        //上一次租后截止日期+5天为风险敞口日
        LocalDate riskExposureDate;
        if (Objects.nonNull(lastCheckPlanClient) && lastCheckPlanClient.getNextDeadline() != null) {
            riskExposureDate = lastCheckPlanClient.getNextDeadline();
        } else if (Objects.nonNull(planBase) && planBase.getDeadLine() != null) {
            // 兼容历史数据
            riskExposureDate = planBase.getDeadLine();
        } else {
            riskExposureDate = paymentBaseInfoService.getFistPaymentDateByClientId(clientId);
        }
        List<AfterLeaseClientPlanRSP> clientPlan;
        clientPlan = this.getClientPlan(clientId, riskExposureDate);
        //根据上一次的结束形式判断下一次的形式(租后计划)
        List<AfterLeaseClientPlanRSP> result = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(planBase) && ObjectUtil.isNotEmpty(clientPlan)) {
            for (AfterLeaseClientPlanRSP item : clientPlan) {
                if (Objects.nonNull(lastCheckPlanClient) && StrUtil.equals(item.getCheckWay(), lastCheckPlanClient.getNextCheckWay())) {
                    result.add(item);
                } else if (!StrUtil.equals(item.getCheckWay(), planBase.getCheckWay())) {
                    result.add(item);
                }
            }
        }
        List<AfterLeaseClientPlanRSP> rspList = CollectionUtil.isNotEmpty(result) ? result : clientPlan;
        if (Objects.nonNull(lastCheckPlanClient)) {
            List<NewAfterLeaseCheckReportMetaLib> libs = SpringUtil.getBean(NewAfterLeaseCheckReportMetaLibMapper.class)
                    .selectLatestListByPlanClientId(Collections.singleton(lastCheckPlanClient.getId()));
            if (CollUtil.isNotEmpty(libs) && CollUtil.isNotEmpty(rspList)) {
                rspList.forEach(item -> item.setReportType(libs.get(0).getReportType()));
            }
        }
        if (Objects.nonNull(lastCheckPlanClient) && lastCheckPlanClient.getNextDeadline() != null) {
            rspList.forEach(item -> {
                item.setDeadLine(lastCheckPlanClient.getNextDeadline());
                item.setCheckWay(lastCheckPlanClient.getNextCheckWay());
                ArrayList<String> deadlineLabel = new ArrayList<>();
                deadlineLabel.add(AfterLeaseDeadlineLabelEnum.ASSERT_MANAGER_CONFIRM.name());
                item.setDeadlineLabel(deadlineLabel);
            });
        }
        if (Objects.nonNull(todoId)) {
            CommonProcessPrepare commonProcessPrepare = commonProcessPrepareService.getById(todoId);
            if (Objects.nonNull(commonProcessPrepare) && Objects.nonNull(commonProcessPrepare.getFirstCheckFlag())
                    && commonProcessPrepare.getFirstCheckFlag().equals(YesOrNoNumberEnum.YES.getCode())) {
                rspList.forEach(item -> {
                    List<String> arrayList = CollUtil.isNotEmpty(item.getDeadlineLabel()) ? item.getDeadlineLabel() :new ArrayList<>();
                    arrayList.add(AfterLeaseDeadlineLabelEnum.FIRST_CHECK.name());
                    item.setDeadlineLabel(arrayList);
                });
            }
        }
        return rspList;
    }
}
