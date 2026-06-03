package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailDto;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.payment.PaymentActualDetailConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.domain.enums.CapitalSource;
import cn.zswltech.mithras.payment.domain.enums.PaymentMethod;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.third.enums.CQPaymentMethodENUM;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentCollectionInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailUnconfirmedMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.ExceptionRequestInfoService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2WithdrawReq;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2WithdrawRSP;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@Slf4j
@Service
public class PaymentActualDetailUnconfirmedService extends ServiceImpl<PaymentActualDetailUnconfirmedMapper, PaymentActualDetailUnconfirmed> {
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;
    @Resource
    private ProcessService processService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private PaymentActualDetailConverter converter;
    @Resource
    private HistoryService historyService;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private PaymentCollectionInfoMapper paymentCollectionInfoMapper;
    @Resource
    private PaymentActualDetailUnconfirmedService thisService;
    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;

    public List<PaymentActualDetailUnconfirmed> listConfirmedByPaymentId(Long paymentId) {
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId);
        query.in(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.CONFIRM.name(), WriteOffStatus.COMMIT.name(), WriteOffStatus.TO_BE_WRITE_OFF.name());
        return this.list(query);
    }

    public ActualDetailDto detail(Long actualId) {
        ActualDetailDto rsp = converter.unconfirmedEntityToDto(this.getById(actualId));
        // 填充合同编号和客户名称
        rsp.setContractCode(Optional.ofNullable(SpringUtil.getBean(ContractBaseInfoService.class).getById(rsp.getContractId())).map(ContractBaseInfo::getContractCode).orElse(null));
        rsp.setClientName(SpringUtil.getBean(Id2NameService.class).clientId2NameSingle(rsp.getClientId()));
        rsp.setPaymentMethodEnum(Optional.ofNullable(PaymentMethod.findByDisplay(rsp.getPaymentMethod())).map(Enum::name).orElse(null));
        rsp.setCapitalSourceEnum(Optional.ofNullable(CapitalSource.findByDisplay(rsp.getCapitalSource())).map(Enum::name).orElse(null));
        rsp.setFinancingCode(rsp.getFinancingCode());
        if (ObjectUtil.isNotEmpty(rsp.getMaterialsList())) {
            PaymentMaterialsListRsp paymentMaterialsListRsp = rsp.getMaterialsList().get(0);
            rsp.setEnclosureId(paymentMaterialsListRsp.getId());
            rsp.setEnclosureName(paymentMaterialsListRsp.getFileName());
        }
        return rsp;
    }

    public List<PaymentActualDetailUnconfirmed> listByPaymentId(Long paymentId) {
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId);
        return this.list(query);
    }

    public List<PaymentActualDetailUnconfirmed> listByContractId(Long contractId) {
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetailUnconfirmed::getContractId, contractId);
        query.notIn(PaymentActualDetailUnconfirmed::getWriteOffStatus,Arrays.asList(WriteOffStatus.TO_BE_WRITE_OFF.name(),WriteOffStatus.COMMIT.name()
                ,WriteOffStatus.CLOSED.name(),WriteOffStatus.IGNORE.name()));
        return this.list(query);
    }

    public <T extends PaymentActualDetailAddReq> Long save(T req) {
        PaymentMethod paymentMethod = PaymentMethod.findByName(req.getPaymentMethod());
        if (Objects.isNull(paymentMethod)) {
            throw new MithrasException("未定义的付款方式");
        }
        // 预检查
        PaymentBaseInfo paymentBaseInfo = this.preCheck(req.getPaymentId());
        // 查询我方账户
        BaseDataBankAccount baseDataBankAccount = baseDataBankAccountService.getById(req.getOurAccountId());
        if (Objects.isNull(baseDataBankAccount)) {
            throw new MithrasException("我方账户信息不存在");
        }
        PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed;
        if (req instanceof PaymentActualDetailModifyReq) {
            // 编辑
            PaymentActualDetailModifyReq paymentActualDetailModifyReq = (PaymentActualDetailModifyReq) req;
            paymentActualDetailUnconfirmed = this.getById(paymentActualDetailModifyReq.getId());
            if (Objects.isNull(paymentActualDetailUnconfirmed)) {
                throw new MithrasException("对应付款核销记录信息不存在");
            }
            if (Objects.equals(paymentActualDetailUnconfirmed.getWriteOffStatus(), WriteOffStatus.CLOSED.name())) {
                throw new MithrasException("已关闭的付款核销不允许操作");
            }
            // 校验金额
            long existAmount = this.calculateAmount(paymentBaseInfo.getId(), paymentActualDetailUnconfirmed.getId());
            long canWriteOffAmount = paymentBaseInfoService.getCanWriteOffMaxAmount(paymentBaseInfo);
            if ((existAmount + paymentActualDetailModifyReq.getPaidInAmount()) > canWriteOffAmount) {
                throw new MithrasException("当前付款申请最多可核销" + Util.toYuan(canWriteOffAmount) + "元");
            }
        } else {
            boolean paymentIsInProcess = processService.isInProcess(String.valueOf(req.getPaymentId()), BusinessModuleEnum.PAYMENT.getModelKeyList());
            if (paymentIsInProcess) {
                throw new MithrasException("付款申请处于流程中不允许新增付款核销记录");
            }
            // 校验金额
            long existAmount = this.calculateAmount(paymentBaseInfo.getId(), null);
            long canWriteOffAmount = paymentBaseInfoService.getCanWriteOffMaxAmount(paymentBaseInfo);
            if ((existAmount + req.getPaidInAmount()) > canWriteOffAmount) {
                throw new MithrasException("当前付款申请最多可核销" + Util.toYuan(canWriteOffAmount) + "元");
            }
            // 新增
            int actualDetailCount = this.count(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery().eq(PaymentActualDetailUnconfirmed::getPaymentId, req.getPaymentId()));
            paymentActualDetailUnconfirmed = new PaymentActualDetailUnconfirmed();
            paymentActualDetailUnconfirmed.setPaymentId(req.getPaymentId());
            paymentActualDetailUnconfirmed.setSeqCode(String.format("%02d", actualDetailCount + 1));
            paymentActualDetailUnconfirmed.setContractId(paymentBaseInfo.getContractId());
            paymentActualDetailUnconfirmed.setInfoSource("人工确认");
            paymentActualDetailUnconfirmed.setWriteOffStatus(WriteOffStatus.TO_BE_WRITE_OFF.name());
            paymentActualDetailUnconfirmed.setFlowId(LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
            paymentActualDetailUnconfirmed.setClientId(paymentBaseInfo.getClientId());
        }
        paymentActualDetailUnconfirmed.setPaymentMethod(paymentMethod.getDisplay());
        paymentActualDetailUnconfirmed.setPaidInDate(LocalDateTimeUtil.parseDate(req.getPaidInDate(), DatePattern.NORM_DATE_PATTERN));
        paymentActualDetailUnconfirmed.setPaidInAmount(req.getPaidInAmount());
        paymentActualDetailUnconfirmed.setOurAccountId(baseDataBankAccount.getId());
        paymentActualDetailUnconfirmed.setOurAccountName(baseDataBankAccount.getAccountName());
        paymentActualDetailUnconfirmed.setOurAccountNumber(baseDataBankAccount.getAccountNumber());
        paymentActualDetailUnconfirmed.setOurAccountBank(baseDataBankAccount.getAccountBank());
        paymentActualDetailUnconfirmed.setOppositeAccountName(req.getOppositeAccountName());
        paymentActualDetailUnconfirmed.setOppositeAccountNumber(req.getOppositeAccountNo());
        paymentActualDetailUnconfirmed.setOppositeAccountBank(req.getOppositeAccountBank());
        paymentActualDetailUnconfirmed.setCapitalSource(req.getCapitalSource());
        paymentActualDetailUnconfirmed.setFinancingCode(req.getFinancingCode());
        thisService.saveOrUpdate(paymentActualDetailUnconfirmed);
        return paymentActualDetailUnconfirmed.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long actualId) {
        log.info("用户删除未确认的付款核销记录[id:{}, currentUserId:{}]", actualId, AccountUtil.getLoginInfo().getId());
        PaymentActualDetailUnconfirmed originalInfo = this.getById(actualId);
        this.preCheck(originalInfo.getPaymentId());
        if (Objects.equals(originalInfo.getWriteOffStatus(), WriteOffStatus.COMMIT.name())) {
            throw new MithrasException("数据已在流程中，不允许删除");
        }
        if (Objects.equals(originalInfo.getWriteOffStatus(), WriteOffStatus.CONFIRM.name())) {
            throw new MithrasException("数据已确认，不允许删除");
        }
        this.removeById(actualId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submitReviewInAdvanced(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请数据不存在");
        }
        // 如果有审批中的流程则不允许发起
        ProcessResp existProcess = processService.findRelatedProcess(paymentId.toString(), Collections.singletonList(ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.name()));
        if (Objects.nonNull(existProcess)) {
            throw new MithrasException(String.format("已存在正在审批中的流程[流程ID为%s]", existProcess.getProcessInstanceId()));
        }
        // 发起新流程
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.name());
        startProcessReq.setBusinessKey(paymentId.toString());
        startProcessReq.setProcessInstanceName(paymentBaseInfo.getPaymentCode() + ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.getDisplay());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        // 寻找当前登陆用户作为资金经理所在的部门
        List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            // 再尝试找一下资深资金经理
            orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.deepmoneymanager.name());
        }
        if (CollectionUtil.isNotEmpty(orgList)) {
            startProcessReq.setStartUserDeptId(String.valueOf(orgList.get(0).getId()));
        }
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        // 辅助表落数据
        bizProcessDataService.recordBizData(processInstanceId, paymentBaseInfo.getClientId());
        return processInstanceId;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submit(PaymentActualDetailSubmitReq req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请信息不存在");
        }
        if (!Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())) {
            throw new MithrasException("仅生效的付款申请允许提交审批");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if(Objects.isNull(contractBaseInfo)){
            throw new MithrasException("合同信息不存在");
        }
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetailUnconfirmed::getPaymentId, req.getPaymentId());
        query.eq(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.TO_BE_WRITE_OFF.name());
        List<PaymentActualDetailUnconfirmed> todoList = this.list(query);
        if (CollectionUtil.isEmpty(todoList)) {
            throw new MithrasException("没有未提交的付款核销记录，无需提交审批");
        }
        // 变更状态
        for (PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed : todoList) {
            paymentActualDetailUnconfirmed.setWriteOffStatus(WriteOffStatus.COMMIT.name());
        }
        thisService.updateBatchById(todoList);
        // 保留用户选择
        paymentBaseInfoService.update(Wrappers.<PaymentBaseInfo>lambdaUpdate()
                .eq(PaymentBaseInfo::getId,paymentBaseInfo.getId())
                .set(PaymentBaseInfo::getIsFinishPut,req.getIsFinishPut()));
        // 生成审批流
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.PaymentActualDetailFlow.name());
        startProcessReq.setBusinessKey(String.valueOf(req.getPaymentId()));
        startProcessReq.setProcessInstanceName(paymentBaseInfo.getPaymentCode() + ProcessModelTypeEnum.PaymentActualDetailFlow.getDisplay());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        // 寻找当前登陆用户作为资金经理所在的部门
        List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            // 再尝试找一下资深资金经理
            orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.deepmoneymanager.name());
        }
        if (CollectionUtil.isNotEmpty(orgList)) {
            startProcessReq.setStartUserDeptId(String.valueOf(orgList.get(0).getId()));
        }
        // 计算FTP考核价格
        if (Objects.nonNull(paymentBaseInfo.getReceiptId())) {
            try {
                SpringUtil.getBean(FtpAssessmentInfoService.class).tryRecalculateFtpAssessmentInfo(paymentBaseInfo.getReceiptId());
            } catch (Exception e) {
                log.error("尝试计算FTP价格发生异常[{}]", paymentBaseInfo.getPaymentCode(), e);
                throw new MithrasException("尝试计算FTP价格发生异常");
            }
        }
        // 计算付款申请已生效的天数
        long paymentEffectDays = this.calculateEffectDays(paymentBaseInfo.getId(), LocalDate.now());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("paymentEffectDays", paymentEffectDays);
        varMap.put("financialManager", new ArrayList<>(sysUserService.getFinancialManagerIdsByDeptCode(contractBaseInfo.getBizDeptId())));
        varMap.put("isFinishPut", req.getIsFinishPut() ? 1 : 0);
        List<String> cosponsorUserIds = JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), String.class);
        Set<String> userIds = new HashSet<>();
        userIds.addAll(cosponsorUserIds);
        userIds.add(String.valueOf(contractBaseInfo.getProjSponsorUserId()));
        varMap.put("projectSponsors", new ArrayList<>(userIds));
//        varMap.put("isLoanReviewAdvanced", req.getIsLoanReviewAdvanced());
        startProcessReq.setVariables(varMap);
        // 启动流程
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        // 辅助表落数据
        bizProcessDataService.recordBizData(processInstanceId, paymentBaseInfo.getClientId());
        // 如果有审批中的付款实际核销运营提前审核流程的话需要系统关闭
        ThreadPoolUtil.getCommonPool().execute(() -> {
            String needCloseProcessInstanceId = null;
            try {
                ProcessResp needCloseProcess = processService.findRelatedProcess(paymentBaseInfo.getId().toString(), Collections.singletonList(ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.name()));
                if (Objects.nonNull(needCloseProcess)) {
                    needCloseProcessInstanceId = needCloseProcess.getProcessInstanceId();
                    if (StrUtil.isNotBlank(needCloseProcessInstanceId)) {
                        ExecutionProcessBaseREQ rejectReq = new ExecutionProcessBaseREQ();
                        rejectReq.setProcessInstanceId(needCloseProcessInstanceId);
                        rejectReq.setMessage(String.format("已提交<%s>流程，系统自动关闭该流程", ProcessModelTypeEnum.PaymentActualDetailFlow.getDisplay()));
                        SpringUtil.getBean(ExecutionService.class).rejectAll(rejectReq);
                    }
                }
            } catch (Exception e) {
                log.error("系统关闭<{}-付款审核核销运营提前审核流程>发生异常", needCloseProcessInstanceId, e);
            }
        });
        return processInstanceId;
    }

    public long calculateEffectDays(Long paymentId, LocalDate toDate) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            log.error("没有找到对应的付款申请信息[{}]", paymentId);
            throw new MithrasException("没有找到对应的付款申请信息");
        }
        LocalDate fromDate = paymentBaseInfo.getYunyingReviewDate();
        if (Objects.isNull(fromDate)) {
            // 查询流程实例
            List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKey(ProcessModelTypeEnum.PaymentCreateFlow.name())
                    .processInstanceBusinessKey(paymentId.toString())
                    .list();
            if (CollectionUtil.isEmpty(list)) {
                return 0;
            }
            list.sort(Comparator.comparing(HistoricProcessInstance::getEndTime));
            HistoricProcessInstance lastOne = list.get(list.size() - 1);
            if (Objects.nonNull(lastOne.getEndTime())) {
                fromDate = LocalDateTimeUtil.of(lastOne.getEndTime()).toLocalDate();
            }
        }
        if (Objects.isNull(fromDate)) {
            log.error("无法付款申请计算超期天数[{}]", paymentId);
            throw new MithrasException("无法付款申请计算超期天数");
        }
        return baseDataSpecialDateService.calculateWorkDays(fromDate, toDate);
    }

    private PaymentBaseInfo preCheck(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请信息不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
//        if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//            throw new MithrasException("直租合同不允许手动操作，会通过财务系统同步");
//        }
        if (!Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())) {
            throw new MithrasException("仅生效状态的付款申请允许操作实际核销记录");
        }
        return paymentBaseInfo;
    }

    private long calculateAmount(Long paymentId, Long ignoreActualDetailId) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetail::getPaymentId, paymentId);
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(query);
        long confirmedAmount = paymentActualDetailList.stream()
                .filter(e -> !Objects.equals(e.getPaymentMethod(), GlobalConstants.AUTO_DEDUCTION_PAY_METHOD))
                .filter(e -> Objects.nonNull(e.getPaidInAmount()))
                .mapToLong(PaymentActualDetail::getPaidInAmount)
                .sum();
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> unconfirmedQuery = Wrappers.lambdaQuery();
        unconfirmedQuery.eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId);
        unconfirmedQuery.in(PaymentActualDetailUnconfirmed::getWriteOffStatus, Arrays.asList(WriteOffStatus.TO_BE_WRITE_OFF.name(), WriteOffStatus.COMMIT.name()));
        if (Objects.nonNull(ignoreActualDetailId)) {
            unconfirmedQuery.ne(PaymentActualDetailUnconfirmed::getId, ignoreActualDetailId);
        }
        List<PaymentActualDetailUnconfirmed> paymentActualDetailUnconfirmedList = this.list(unconfirmedQuery);
        long unconfirmedAmount = paymentActualDetailUnconfirmedList.stream()
                .filter(e -> !Objects.equals(e.getPaymentMethod(), GlobalConstants.AUTO_DEDUCTION_PAY_METHOD))
                .filter(e -> Objects.nonNull(e.getPaidInAmount()))
                .mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount)
                .sum();
        return confirmedAmount + unconfirmedAmount;
    }

    public Long collectionAdd(PaymentCollectionAddReq req) {
        PaymentCollectionInfo paymentCollectionInfo = new PaymentCollectionInfo();
        BeanUtils.copyProperties(req,paymentCollectionInfo);
        PaymentCollectionInfo byPaymentId = paymentCollectionInfoMapper.selectOne(Wrappers.<PaymentCollectionInfo>lambdaQuery()
                .eq(PaymentCollectionInfo::getPaymentId, req.getPaymentId()));
        if(byPaymentId != null) {
            paymentCollectionInfo.setId(byPaymentId.getId());
            paymentCollectionInfoMapper.updateById(paymentCollectionInfo);
        }else{
            paymentCollectionInfoMapper.insert(paymentCollectionInfo);
        }
        return paymentCollectionInfo.getId();
    }

    public PaymentCollectionRsp collectionDetail(Long paymentId) {
        PaymentCollectionInfo paymentCollectionInfo = paymentCollectionInfoMapper.selectOne(Wrappers.<PaymentCollectionInfo>lambdaQuery()
                .eq(PaymentCollectionInfo::getPaymentId, paymentId));
        PaymentCollectionRsp paymentCollectionRsp = new PaymentCollectionRsp();
        paymentCollectionRsp.setPaymentId(paymentId);
        if(paymentCollectionInfo != null){
            BeanUtils.copyProperties(paymentCollectionInfo,paymentCollectionRsp);
        }
        // 质保金收款方式从申请中获取
        if(ObjectUtil.isEmpty(paymentCollectionRsp.getWarrantyPayWay()) || "-".equals(paymentCollectionRsp.getWarrantyPayWay())){
            PaymentDetailRsp detail = paymentBaseInfoService.detail(paymentId);
            paymentCollectionRsp.setWarrantyPayWay(detail.getRetentionMoneyType());
        }

        return paymentCollectionRsp;
    }

    /**
     * 推送银企直连付款申请单功能
     **/
    public String sendPaymentApply(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<PaymentActualDetailUnconfirmed> paymentActualDetailUnconfirmedList = this.listByPaymentId(paymentId);
        paymentActualDetailUnconfirmedList.removeIf(e -> !Objects.equals(e.getWriteOffStatus(), WriteOffStatus.COMMIT.name()) || !ObjectUtil.equals(e.getPaymentMethod(), CQPaymentMethodENUM.JSFS15.getDisplay()));
        if (CollectionUtil.isEmpty(paymentActualDetailUnconfirmedList)) {
            return "本次付款未含有银企直联";
        }
        List<PaymentActualDetailUnconfirmed> needSendActualDetail = new ArrayList<>();
        List<ExceptionRequestInfo> needWithdrawException = new ArrayList<>();
        //需要判断是否推送过苍穹
        List<ExceptionRequestInfo> oldException = getBean(ExceptionRequestInfoService.class)
                .list(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                        .eq(ExceptionRequestInfo::getPlatform, PlatformApiEnum.CQ2_PAYMENT.name())
                        .eq(ExceptionRequestInfo::getSource, ExceptionSourceENUM.BUSINESS_FLOW.name())
                        .eq(ExceptionRequestInfo::getBusinessKey, paymentId)
                        .eq(ExceptionRequestInfo::getRetryFlag, YesOrNoNumberEnum.YES.getCode()));
        //比对上次推送情况
        if (ObjectUtil.isNotEmpty(oldException)) {
            Map<String, ExceptionRequestInfo> oldPaymentVo = oldException.stream().collect(Collectors.toMap(ExceptionRequestInfo::getBusinessTitle, e -> e, (a, b) -> b));
            for (PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed : paymentActualDetailUnconfirmedList) {
                ExceptionRequestInfo cq2PaymentReq = oldPaymentVo.get(String.join("-", paymentBaseInfo.getPaymentCode(), paymentActualDetailUnconfirmed.getSeqCode()));
                if (ObjectUtil.isNotEmpty(cq2PaymentReq)) {
                    List<CQ2PaymentVO> paymentVOS = JSONUtil.toList(cq2PaymentReq.getReqData(), CQ2PaymentVO.class);
                    CQ2PaymentVO cq2PaymentVO = null;
                    if (ObjectUtil.isNotEmpty(paymentVOS)) {
                        cq2PaymentVO = paymentVOS.get(0);
                    }
                    if (ObjectUtil.isNotEmpty(cq2PaymentVO) && ObjectUtil.isNotEmpty(cq2PaymentVO.getEntry()) && ObjectUtil.isNotEmpty(cq2PaymentVO.getEntry().get(0).getE_applyamount())) {
                        //区分是否变化
                        if (!cq2PaymentVO.getCico_payzh_number().equals(paymentActualDetailUnconfirmed.getOurAccountNumber()) ||
                                !cq2PaymentVO.getApplydate().equals(paymentActualDetailUnconfirmed.getPaidInDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))) ||
                                !cq2PaymentVO.getEntry().get(0).getCico_pay_bank_number_number().equals(paymentActualDetailUnconfirmed.getOurAccountNumber()) ||
                                !cq2PaymentVO.getEntry().get(0).getCico_pay_bank_name_name().equals(paymentActualDetailUnconfirmed.getOurAccountBank()) ||
                                !(cq2PaymentVO.getEntry().get(0).getE_applyamount().compareTo(new BigDecimal(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(paymentActualDetailUnconfirmed.getPaidInAmount()))).toString())) == 0)
                        ) {
                            needSendActualDetail.add(paymentActualDetailUnconfirmed);
                            needWithdrawException.add(cq2PaymentReq);
                        }
                    }
                } else {
                    needSendActualDetail.add(paymentActualDetailUnconfirmed);
                }
            }
        } else {
            needSendActualDetail = paymentActualDetailUnconfirmedList;
        }
        String resultMessage = null;
        //银企直联的需要在这里发送给苍穹
        if (ObjectUtil.isEmpty(needSendActualDetail)) {
            return "本次付款银企直联无需重新推送";
        }
        //先撤回
        if (ObjectUtil.isNotEmpty(needWithdrawException)) {
            resultMessage = removePaymentApply(needWithdrawException);
        }
        //推送新单据
        List<CQ2PaymentVO> cq2PaymentVOS = new ArrayList<>();
        needSendActualDetail.forEach(detail -> {
            CQ2PaymentVO cq2PaymentVO = getBean(PaymentWriteOffService.class).buildPaymentOther(paymentBaseInfo, detail);
            if (ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                cq2PaymentVOS.add(cq2PaymentVO);
            }
        });
        //付款单
        if (!cq2PaymentVOS.isEmpty()) {
            SpringContextHolder.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(paymentBaseInfo.getContractId()), cq2PaymentVOS);
        }
        return resultMessage == null ? "推送成功" : resultMessage;
    }

    public String removePaymentApply( List<ExceptionRequestInfo> needWithdrawException) {
        String msg = "苍穹付款单已删除";
        if (ObjectUtil.isNotEmpty(needWithdrawException)) {
            for (ExceptionRequestInfo exceptionRequestInfo : needWithdrawException) {
                PlatformApiHandler<CQ2WithdrawReq, CQ2WithdrawRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_WITHDRAW);
                CQ2WithdrawReq collectionWithdrawReq = new CQ2WithdrawReq();
                collectionWithdrawReq.setBillIdentification(PlatformApiEnum.getWithdrawType(PlatformApiEnum.CQ2_PAYMENT));
                collectionWithdrawReq.setBillNo(platformApiHandleFactory.getPlatformApiRequestInspector(PlatformApiEnum.CQ2_PAYMENT).getBillNo(exceptionRequestInfo.getReqData()));
                CQ2WithdrawRSP execute = platformApiHandler.execute(collectionWithdrawReq);
                if (ObjectUtil.isEmpty(execute) || !ObjectUtil.equals(execute.getSuccess(), Boolean.TRUE)) {
                    msg = "苍穹付款单删除失败，请手工处理";
                }
            }
        }
        return msg;
    }
}
