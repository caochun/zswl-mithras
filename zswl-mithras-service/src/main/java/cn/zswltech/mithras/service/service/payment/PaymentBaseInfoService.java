package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.minio.MinioOssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.version.PaymentEffectREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryRSP;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRuleConfigService;
import cn.zswltech.mithras.blackgray.service.external.JKBlackGrayCollisionLibraryHandle;
import cn.zswltech.mithras.blackgray.service.external.remote.JKBlackGrayCollisionLibraryRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.convert.payment.PaymentConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.afterlease.ClientRole;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.gendoc.render.PaymentApprovalBLRender;
import cn.zswltech.mithras.service.gendoc.render.PaymentApprovalZLRender;
import cn.zswltech.mithras.third.mapper.ExceptionRequestInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.PaymentListDto;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.margin.MarginBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.policy.PolicyInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.service.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.service.service.lib.contract.*;
import cn.zswltech.mithras.service.service.margin.MarginBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.newftp.service.FtpService;
import cn.zswltech.mithras.service.service.payment.model.FillReceiptInfoReq;
import cn.zswltech.mithras.service.service.payment.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.third.service.financial.impl.handle.WithdrawHandle;
import cn.zswltech.mithras.third.service.financial.req.CQWithdrawREQ;
import cn.zswltech.mithras.third.service.financial.resp.FinancialCommonRSP;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.others.MithrasException.err;


/**
 * @author zhaozhengkang
 * @description payment_base_info
 * @date 2022-08-12
 */
@Slf4j
@Service
public class PaymentBaseInfoService extends ServiceImpl<PaymentBaseInfoMapper, PaymentBaseInfo> implements PaymentUpdateAdvice {
    public static final String CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY = "targetPaymentId";

    private static Pattern seqPattern = Pattern.compile("\\(([^}]*)\\)");
    private static Pattern yearPattern = Pattern.compile("(.*)【(.*)】(.*)");

    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private PaymentActualDetailUnconfirmedService actualDetailUnconfirmedService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private PaymentQuestionnaireAnswerService answerService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private PaymentApprovalZLRender paymentApprovalZLRender;
    @Resource
    private PaymentApprovalBLRender paymentApprovalBLRender;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FtpService ftpService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private ExceptionRequestInfoMapper exceptionRequestInfoMapper;
    @Resource
    private WithdrawHandle withdrawHandle;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private PaymentPolicyInfoMapper paymentPolicyInfoMapper;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ProcessService processService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;
    @Resource
    private ContractPledgeLibService contractPledgeLibService;
    @Resource
    private ClientService clientService;
    @Autowired
    private TypeConversionWorker typeConversionWorker;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;
    @Resource
    private BlackGrayWarehouseRuleConfigService blackGrayWarehouseRuleConfigService;
    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;
    @Resource
    private JKBlackGrayCollisionLibraryHandle jkBlackGrayCollisionLibraryHandle;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private PublicInfoQueryService publicInfoQueryService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private MyTaskService myTaskService;

    public List<PaymentBaseInfo> listAddNewReceiptCandidate(Long contractId) {
        ProcessResp processResp = processService.findRelatedProcess(String.valueOf(contractId), Collections.singletonList(ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name()));
        if (Objects.nonNull(processResp)) {
            // 如果是自动发起的新增借据流程则只能选择对应结束投放操作的付款申请
            Map<String, Object> varMap = flowVariableApiService.getVariables(processResp.getProcessInstanceId(), Collections.singletonList(CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY));
            Object paymentId = varMap.get(CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY);
            if (Objects.isNull(paymentId)) {
                throw new MithrasException("没有找到符合条件的付款申请");
            }
            PaymentBaseInfo paymentBaseInfo = this.getById(Long.valueOf(paymentId.toString()));
            if (Objects.isNull(paymentBaseInfo)) {
                throw new MithrasException("付款申请信息不存在");
            }
            if (Objects.nonNull(paymentBaseInfo.getReceiptIdFinal())) {
                throw new MithrasException("数据异常，付款申请已经被借据关联");
            }
            return Collections.singletonList(paymentBaseInfo);
        }
        // 非自动发起流程则列出所有可选择的付款申请
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getContractId, contractId);
        query.eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name());
        List<PaymentBaseInfo> paymentBaseInfoList = this.list(query);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        paymentBaseInfoList.removeIf(e -> Objects.nonNull(e.getReceiptIdFinal()));
        return paymentBaseInfoList;
    }

    public PaymentBaseInfo getOneByPaymentCode(String paymentCode) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getPaymentCode, paymentCode);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public long getCanWriteOffMaxAmount(PaymentBaseInfo paymentBaseInfo) {
        long maxMount = 0L;
        maxMount += LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount());
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getDownPaymentType())) {
            maxMount -= LongUtil.null2zero(paymentBaseInfo.getDownPayment());
        }
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getRetentionMoneyType())) {
            maxMount -= LongUtil.null2zero(paymentBaseInfo.getRetentionMoney());
        }
        return maxMount;
    }

    public LocalDate getEarliestPayDate(Long contractId) {
        List<PaymentBaseInfo> paymentBaseInfoList = this.listByContractIds(Collections.singletonList(contractId));
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        List<PaymentActualDetail> paymentActualDetailList = actualDetailService.listByPaymentIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet()));
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return null;
        }
        List<PaymentActualDetail> filterList = paymentActualDetailList.stream().filter(e -> Objects.equals(e.getWriteOffStatus(), WriteOffStatus.WRITTEN_OFF.name())).sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterList)) {
            return null;
        }
        return filterList.get(0).getPaidInDate();
    }

    public List<PaymentBaseInfo> listWriteOffTargetYear(Long contractId, int year) {
        LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
        paymentQuery.eq(PaymentBaseInfo::getContractId, contractId);
        paymentQuery.eq(PaymentBaseInfo::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        List<PaymentBaseInfo> paymentBaseInfoList = this.list(paymentQuery);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        Map<Long, PaymentBaseInfo> paymentMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        List<PaymentActualDetail> paymentActualDetailList = actualDetailService.listByPaymentIds(paymentMap.keySet());
        Map<Long, List<PaymentActualDetail>> paymentActualMap = paymentActualDetailList.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
        List<PaymentBaseInfo> result = new LinkedList<>();
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : paymentActualMap.entrySet()) {
            List<PaymentActualDetail> list = entry.getValue();
            list.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            LocalDate paidInDate = list.get(0).getPaidInDate();
            if (Objects.nonNull(paidInDate) && paidInDate.getYear() == year) {
                result.add(paymentMap.get(entry.getKey()));
            }
        }
        return result;
    }

//    @Transactional(rollbackFor = Throwable.class)
//    public void tryFixedFtpCost(Long contractId) {
//        List<PaymentBaseInfo> paymentBaseInfoList = this.listEffectPaymentByContractId(contractId);
//        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
//            return;
//        }
//        // 遍历付款申请进行处理
//        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
//            if (Objects.nonNull(paymentBaseInfo.getCashFtp())) {
//                paymentBaseInfo.setCashFtpFinal(paymentBaseInfo.getCashFtp());
//            }
//            if (Objects.nonNull(paymentBaseInfo.getBillFtp())) {
//                paymentBaseInfo.setBillFtpFinal(paymentBaseInfo.getBillFtp());
//            }
//        }
//        // 批量更新
//        this.updateBatchById(paymentBaseInfoList);
//    }

//    public void tryInitFtpCost(Long paymentId) {
//        PaymentBaseInfo paymentBaseInfo = this.getById(paymentId);
//        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款信息不存在"));
//        LocalDate now = LocalDate.now();
//        CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(paymentBaseInfo.getContractId(), now);
//        if (Objects.isNull(paymentBaseInfo.getCashFtp())) {
//            Set<Long> contractIds = new HashSet<>();
//            contractIds.add(paymentBaseInfo.getContractId());
//            Integer ftp = ftpService.getCashFtp(cashFtpInfluenceBO, contractIds);
//            log.info("付款申请初始化现金FTP成本[paymentCode:{}, cashFtp:{}]", paymentBaseInfo.getPaymentCode(), ftp);
//            paymentBaseInfo.setCashFtp(ftp);
//        }
//        if (Objects.isNull(paymentBaseInfo.getBillFtp())) {
//            BillFtpBO billFtp = ftpService.getBillFtp(now);
//            log.info("付款申请初始化票据FTP成本[paymentCode:{}, billFtp:{}]", paymentBaseInfo.getPaymentCode(), billFtp.getBillFtpSell());
//            paymentBaseInfo.setBillFtp(billFtp.getBillFtpSell());
//        }
//        this.updateById(paymentBaseInfo);
//    }

    public List<PaymentBaseInfo> listEffectPaymentByContractId(Long contractId) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getContractId, contractId);
        query.in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        return this.list(query);
    }

    public List<PaymentBaseInfo> listEffectPaymentByClientId(Long clientId) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getClientId, clientId);
        query.in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        return this.list(query);
    }

    /**
     * 查询审批通过的付款申请列表
     * @param contractId 合同id  必填
     * @return 付款申请列表
     */
    public List<PaymentBaseInfo> queryListWithContractId(Long contractId) {
        Assert.notNull(contractId, () -> MithrasException.newException("合同id不能为空"));
        return paymentBaseInfoMapper.queryListWithContractId(contractId);
    }

    public List<PaymentWrittenOffAmountRsp> listWrittenOffAmountByContractId(Long contractId) {
        List<PaymentBaseInfo> paymentBaseInfoList = this.listEffectPaymentByContractId(contractId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        List<Long> paymentIdList = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        // 付款查询付款核销
        Map<Long, List<PaymentActualDetail>> paymentActualDetailMap = actualDetailService.getMapByPaymentIds(paymentIdList);
        // 首期租金、保证金、质保金、服务费/咨询费/手续费、名义价款都查询收款核销
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listBy(contractId, null);
        // 取出名义价款单独处理（因为名义价款属于合同维度，不属于付款维度）
        List<CollectionBaseInfo> nominalPriceList = new LinkedList<>();
        Iterator<CollectionBaseInfo> iterator = collectionBaseInfoList.iterator();
        List<CollectionBaseInfo> firstInterestCollectionList = new LinkedList<>();
        // 去掉名义价款、租金、提前终止补偿金类型再处理
        while (iterator.hasNext()) {
            CollectionBaseInfo item = iterator.next();
            // 未核销的不要
            if (Objects.equals(item.getWriteOffStatus(), CollectionWriteOffStatusEnum.UNCOLLECTION.name()) || Objects.equals(item.getWriteOffStatus(), CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name())) {
                iterator.remove();
                continue;
            }
            if (Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.EARLY_STOP_COMPENSATION.name())) {
                iterator.remove();
            }
            if (Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
                // 因为首期利息也被放在了租金类型，如果遇到第0期则保存下来
                if (Objects.equals(item.getPhase(), 0)) {
                    firstInterestCollectionList.add(item);
                } else {
                    iterator.remove();
                }
            }
            if (Objects.equals(item.getCashFlowItem(), CashFlowItemEnum.NOMINAL_PRICE.name())) {
                nominalPriceList.add(item);
                iterator.remove();
            }
        }
        // 按照付款id进行分组
        Map<Long, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().filter(item -> Objects.nonNull(item.getPaymentId())).collect(Collectors.groupingBy(CollectionBaseInfo::getPaymentId));
//        // 补填特殊处理的首期利息
//        if (CollectionUtil.isNotEmpty(firstInterestCollectionList)) {
//            for (CollectionBaseInfo cbi : firstInterestCollectionList) {
//                collectionMap.get(cbi.getPaymentId()).add(cbi);
//            }
//        }
        Integer cashFtpNow = null;
        Integer billFtpNow = null;
        // 处理返回参数
        List<PaymentWrittenOffAmountRsp> result = new ArrayList<>(paymentBaseInfoList.size());
        Map<Long, FtpAssessmentInfo> assessmentInfoMap = getBean(FtpAssessmentInfoService.class).list(Wrappers.<FtpAssessmentInfo>lambdaQuery()
                        .in(FtpAssessmentInfo::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(FtpAssessmentInfo::getPaymentId, Function.identity(), (k1, k2) -> k1));
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.lambdaQuery().eq(ContractBaseInfo::getId, paymentBaseInfo.getContractId()).one();
            PaymentWrittenOffAmountRsp rsp = new PaymentWrittenOffAmountRsp();
            // 设置FTP审核信息
            PaymentWrittenOffAmountRsp.FtpAssessDTO dto = Optional.ofNullable(assessmentInfoMap.get(paymentBaseInfo.getId()))
                    .map(obj -> BeanUtil.copyProperties(obj, PaymentWrittenOffAmountRsp.FtpAssessDTO.class)).orElse(null);
            rsp.setFtpAssessDto(dto);
            rsp.setPaymentId(paymentBaseInfo.getId());
            rsp.setPaymentCode(paymentBaseInfo.getPaymentCode());
            rsp.setBizType(Objects.isNull(contractBaseInfo) ? null : contractBaseInfo.getBizType());
//            rsp.setCommission(paymentBaseInfo.getCommission());
//            rsp.setFirstInstallmentInterest(paymentBaseInfo.getFirstInstallmentInterest());
            // 计算付款核销合计金额和首次付款日期
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMap.get(paymentBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                //过滤反核销
                paymentActualDetailList = paymentActualDetailList.stream().filter(e -> ObjectUtil.equals(YesOrNoNumberEnum.NO.getCode(), e.getCancelWriteOffFlag())).collect(Collectors.toList());
                List<PaymentActualDetail> list = paymentActualDetailList.stream().filter(item -> WriteOffStatus.WRITTEN_OFF.name().equals(item.getWriteOffStatus())).sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(list)) {
                    rsp.setActualPayDate(Optional.ofNullable(list.get(0).getPaidInDate()).map(item -> LocalDateTimeUtil.format(item, DatePattern.NORM_DATE_PATTERN)).orElse(null));
                    rsp.setPaymentAmount(list.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
                }
            }
            // 计算收款核销合计金额
            List<CollectionBaseInfo> collectionList = collectionMap.get(paymentBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(collectionList)) {
                Map<String, List<CollectionBaseInfo>> map = collectionList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getCashFlowItem));
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.FIRST_RENT.name()))) {
                    rsp.setDownPaymentAmount(map.get(CashFlowItemEnum.FIRST_RENT.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.EARNEST_MONEY.name()))) {
                    rsp.setEarnestMoneyAmount(map.get(CashFlowItemEnum.EARNEST_MONEY.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.OTHERAMOUNT.name()))) {
                    rsp.setConsultingFeeAmount(map.get(CashFlowItemEnum.OTHERAMOUNT.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.RETENTION_MONEY.name()))) {
                    rsp.setRetentionMoneyAmount(map.get(CashFlowItemEnum.RETENTION_MONEY.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
                // 手续费
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.COMMISSION.name()))) {
                    rsp.setCommission(map.get(CashFlowItemEnum.COMMISSION.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
                // 首期利息(特殊处理，这里的RENT类型应该只有首期利息，上面的代码处理过了)
                if (CollectionUtil.isNotEmpty(map.get(CashFlowItemEnum.RENT.name()))) {
                    rsp.setFirstInstallmentInterest(map.get(CashFlowItemEnum.RENT.name()).stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
                }
            }
            // 计算名义价款
            if (CollectionUtil.isNotEmpty(nominalPriceList)) {
                rsp.setNominalPriceAmount(nominalPriceList.stream().mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum());
            }

            // 这里现在前端不需要这两个字段了，所以不处理
/*            if (Objects.isNull(paymentBaseInfo.getCashFtp())) {
                if (Objects.isNull(cashFtpNow)) {
                    Set<Long> contractIds = new HashSet<>();
                    contractIds.add(contractId);
                    CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(contractId, LocalDate.now());
                    cashFtpNow = ftpService.getCashFtp(cashFtpInfluenceBO, contractIds);
                }
                rsp.setCashFtp(cashFtpNow);
            } else {
                rsp.setCashFtp(paymentBaseInfo.getCashFtp());
            }
            if (Objects.isNull(paymentBaseInfo.getBillFtp())) {
                if (Objects.isNull(billFtpNow)) {
                    billFtpNow = ftpService.getBillFtp(LocalDate.now()).getBillFtpSell();
                }
                rsp.setBillFtp(billFtpNow);
            } else {
                rsp.setBillFtp(paymentBaseInfo.getBillFtp());
            }*/
//            rsp.setBillFtp(paymentBaseInfo.getBillFtp());
//            rsp.setCashFtp(paymentBaseInfo.getCashFtp());
            result.add(rsp);
        }
        return result;
    }

    public static String generatePaymentCode(String contractCode, int paymentSeq) {
        if (StringUtils.isEmpty(contractCode)) {
            throw new MithrasException("合同编号为空");
        }
        Matcher yearMatcher = yearPattern.matcher(contractCode);
        String year = null;
        String seq = null;
        while (yearMatcher.find()) {
            year = yearMatcher.group(2);
        }
        Matcher seqMatcher = seqPattern.matcher(contractCode);
        while (seqMatcher.find()) {
            seq = seqMatcher.group();
        }
        if (null == year || null == seq) {
            throw new MithrasException("合同编号不符合规则");
        }
        seq = seq.substring(1, seq.length() - 1);
        String[] split = seq.split("-");
        if (split.length != 2) {
            throw new MithrasException("合同编号不符合规则");
        }
        seq = split[0] + split[1];
        return join("-", year + seq, String.format("%02d", paymentSeq));
    }

    public void cancelJoinReceiptByReceiptId(Long receiptId) {
        List<PaymentBaseInfo> paymentBaseInfoList = this.listByReceiptId(receiptId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        this.cancelJoinReceipt(paymentBaseInfoList);
    }

    public void cancelJoinReceiptByContractId(Long contractId) {
        List<PaymentBaseInfo> paymentBaseInfoList = this.detailByContractIds(Collections.singletonList(contractId));
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        this.cancelJoinReceipt(paymentBaseInfoList);
    }

    /**
     * 填入借据信息到付款申请记录中
     * 异常情况
     * - 付款记录不存在
     * - 借据信息已经有了
     * - 未更新到借据信息
     *
     * @return
     */
    public void fillReceiptInfo(FillReceiptInfoReq req) {
        req.valid();
        PaymentBaseInfo pbi = this.getById(req.getPaymentId());
        if (isNull(pbi)) {
            err(RECORD_NOT_EXIST);
        }
        // 可能会发生更新，比如第一次选择付款申请导入后，用户取消了操作，重新导入，此处不校验
//        if (isNotBlank(pbi.getReceiptCode()) || isNotNull(pbi.getReceiptId())) {
//            err("借据信息已存在");
//        }
        PaymentBaseInfo toBe = new PaymentBaseInfo().setReceiptId(req.getReceiptId()).setReceiptCode(req.getReceiptCode());
        boolean updateSucceed = this.update(toBe,
                Wrappers.<PaymentBaseInfo>lambdaUpdate()
                        .eq(PaymentBaseInfo::getId, pbi.getId())
//                        .isNull(PaymentBaseInfo::getReceiptId)
//                        .isNull(PaymentBaseInfo::getReceiptCode)
        );
//        if (!updateSucceed) {
//            err("未更新到借据信息");
//        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void fixedContractReceiptInfo(Long contractId) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getContractId, contractId);
        query.isNotNull(PaymentBaseInfo::getReceiptId);
        query.isNull(PaymentBaseInfo::getReceiptIdFinal);
        List<PaymentBaseInfo> paymentBaseInfoList = this.list(query);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            paymentBaseInfo.setReceiptIdFinal(paymentBaseInfo.getReceiptId());
        }
        this.updateBatchById(paymentBaseInfoList);
    }

    public String chooseApprovalDocTemplate(OutputStream outputStream, Long paymentId) throws Exception {
        PaymentBaseInfo paymentBaseInfo = this.getById(paymentId);
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("没有找到付款申请记录"));
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());
        Assert.notNull(contractBaseInfoLib, () -> MithrasException.newException("没有找到生效的合同版本数据"));
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfoLib.getBizType());
        Assert.notNull(projectBizType, () -> MithrasException.newException("未定义的项目类型"));
        switch (projectBizType) {
            case ZL: {
                return paymentApprovalZLRender.render(outputStream, paymentBaseInfo);
            }
            case BL: {
                return paymentApprovalBLRender.render(outputStream, paymentBaseInfo);
            }
            default: {
                throw new MithrasException("暂不支持下载模板的业务类型");
            }
        }
    }

    /**
     * 批量计算合同的已申请和已付金额
     *
     * @param contractIds
     * @return
     */
    public Map<Long, Pair<Long, Long>> calculateCapitalDistributionBatch(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.EMPTY_MAP;
        }
        // 获取所有的payment信息
        List<PaymentBaseInfo> paymentBaseInfos = baseMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, contractIds)
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name())));
        if (ObjectUtil.isEmpty(paymentBaseInfos)) {
            return Collections.EMPTY_MAP;
        }
        Map<Long, Long> appliedAmounts = new HashMap<>();
        List<Long> paymentIds = new ArrayList<>();
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
            paymentIds.add(paymentBaseInfo.getId());
            Long key = paymentBaseInfo.getContractId();
            if (paymentBaseInfo.getApplyPaymentAmount() != null) {
                appliedAmounts.put(key, appliedAmounts.getOrDefault(key, 0L) + paymentBaseInfo.getApplyPaymentAmount());
            }
        }
        Map<Long, Long> amountsPaid = actualDetailService.calculatePaidAmountBatch(paymentIds);
        Map<Long, Pair<Long, Long>> res = new HashMap<>();
        appliedAmounts.forEach((contractId, amountApplied) ->
                res.put(contractId, new Pair<>(amountApplied, amountsPaid.getOrDefault(contractId, 0L))));
        return res;
    }


    /**
     * 批量计算合同预算
     * 1 付款申请审批中且项目经理节点审批通过的数据、付款申请审批通过且核销状态为未付款 取“申请付款金额（元）”
     * 2 付款申请审批通过且核销状态为核销完毕/部分核销 取“已付金额（元）”
     * @param contractIds 合同id集合
     * @return 支付金额
     */
    public BigDecimal calculateBatchForBudget(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return null;
        }
        // 获取所有的payment信息
        List<PaymentBaseInfo> paymentBaseInfos = baseMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getPaymentProcessStatus, Arrays.asList(ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.APPROVAL_PASS.name()))
                .in(PaymentBaseInfo::getContractId, contractIds)
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name() , PaymentStatusEnum.NEW.name())));
        if (ObjectUtil.isEmpty(paymentBaseInfos)) {
            return null;
        }
        // 支付记录的合同
        List<Long> hasPayContractIdList = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList());
        contractIds.clear();
        contractIds.addAll(hasPayContractIdList);
        // 申请付款金额+已付金额
        BigDecimal amountSum = new BigDecimal(0);

        List<String> businessKeyList = paymentBaseInfos.stream()
                .filter(item -> ProcessStatus.UNDER_APPROVAL.name().equals(item.getPaymentProcessStatus()))
                .map(item -> String.valueOf(item.getId()))
                .collect(Collectors.toList());

        if(CollectionUtil.isNotEmpty(businessKeyList)){
            ProcessListREQ processListREQ = new ProcessListREQ();
            processListREQ.setModelKey(ProcessModelTypeEnum.PaymentCreateFlow.name());
            processListREQ.setBusinessKeyList(businessKeyList);
            processListREQ.setPage(1);
            processListREQ.setPageSize(5000);
            PageR<ProcessListRSP> processListRSPPageR = myTaskService.searchList(processListREQ);
            List<String> passProjectMangerIds = processListRSPPageR.getList().stream().filter(item -> !Arrays.asList("userTask_startUser", "userTask_projectmanager").contains(item.getCurTaskActivityIds()))
                    .map(ProcessListRSP::getBusinessKey).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(passProjectMangerIds)){
                Optional<Long> reduce = paymentBaseInfos.stream().filter(item -> passProjectMangerIds.contains(String.valueOf(item.getId())) && Objects.nonNull(item.getApplyPaymentAmount()))
                        .map(PaymentBaseInfo::getApplyPaymentAmount).reduce(Long::sum);
                if (reduce.isPresent()){
                    amountSum = amountSum.add(new BigDecimal(reduce.get()));
                }
            }
        }

        List<Long> passList = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).filter(id -> !businessKeyList.contains(String.valueOf(id))).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(passList)){
            Map<Long, Long> actualMap = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .in(PaymentActualDetail::getPaymentId, passList)
                            .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())).stream()
                    .collect(Collectors.groupingBy(
                            PaymentActualDetail::getPaymentId,
                            Collectors.summingLong(PaymentActualDetail::getPaidInAmount)
                    ));

            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
                if(passList.contains(paymentBaseInfo.getId())){
                    if(actualMap.containsKey(paymentBaseInfo.getId())){
                        amountSum = amountSum.add(new BigDecimal(actualMap.get(paymentBaseInfo.getId())));
                    }else {
                        amountSum = amountSum.add(new BigDecimal(paymentBaseInfo.getApplyPaymentAmount()));
                    }
                }
            }
        }
        return amountSum;
    }

    /**
     * @param contractId
     * @param excludePaymentId 计算剩余可申请时排除当前的payment
     * @return
     */
    public Pair<Long, Long> calculateCapitalDistribution(Long contractId, Long excludePaymentId) {
        // 获取所有生效的payment信息
        List<PaymentBaseInfo> paymentBaseInfos = baseMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractId)
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name())));
        if (ObjectUtil.isEmpty(paymentBaseInfos)) {
            return new Pair<>(0L, 0L);
        }
        List<Long> paymentIds = new ArrayList<>();
        Long appliedAmount = 0L;
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
            paymentIds.add(paymentBaseInfo.getId());
//            if (paymentBaseInfo.getId().equals(excludePaymentId)) {
//                continue;
//            }
            if (paymentBaseInfo.getApplyPaymentAmount() != null) {
                appliedAmount += paymentBaseInfo.getApplyPaymentAmount();
            }
        }
        Long paidAmount = actualDetailService.calculatePaidAmount(paymentIds);
        return new Pair<>(appliedAmount, paidAmount);
    }

    /**
     * 模糊匹配合同
     *
     * @param req
     * @return
     */
    public List<PaymentContractListRsp> fuzzyMatchContracts(PaymentContractListReq req) {
        List<ContractBaseInfoLib> originalContracts = contractBaseInfoService.list(req.getClientId());
        if (ObjectUtil.isEmpty(originalContracts)) {
            return null;
        }
        // 过滤出合同状态为生效和起租，并且主办为当前登陆人的合同
        // 付款创建变更为运营经办人创建，不再过滤合同主办
//        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<ContractBaseInfoLib> contracts = originalContracts.stream()
                .filter(item -> Objects.equals(item.getContractStatus(), ContractStatus.TAKE_EFFECT.name()) || Objects.equals(item.getContractStatus(), ContractStatus.START_RENT.name()))
//                .filter(item -> Objects.equals(currentUserId, item.getProjSponsorUserId()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(contracts)) {
            return Collections.emptyList();
        }
        List<Long> contractIds = new ArrayList<>();
        List<PaymentContractListRsp> rspData = new ArrayList<>();
        for (ContractBaseInfoLib record : contracts) {
            contractIds.add(record.getOriginId());
            PaymentContractListRsp paymentContractListRsp = paymentConvert.listRspToPaymentListRsp(record);
            rspData.add(paymentContractListRsp);
        }
//        Map<Long, ContractLeasePrice> priceMap = contractPriceService.listByContractIds(contractIds);
        // FIXME 如果合同数量较多需要改为批量查询接口，目前数量较少，循环获取可以接受
        Map<Long, ContractPriceDetailRSP> priceMap = new HashMap<>();
        for (Long contractId : contractIds) {
            ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
            contractPriceDetailREQ.setContractId(contractId);
            ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.editionDetail(contractPriceDetailREQ);
            if (Objects.nonNull(contractPriceDetailRSP)) {
                priceMap.put(contractId, contractPriceDetailRSP);
            }
        }
        Map<Long, Pair<Long, Long>> capitalDistribution = calculateCapitalDistributionBatch(contractIds);
        for (PaymentContractListRsp rspDatum : rspData) {
//            ContractLeasePrice leasePrice = priceMap.get(rspDatum.getContractId());
            ContractPriceDetailRSP leasePrice = priceMap.get(rspDatum.getContractId());
            rspDatum.setPlanedPaidAmount(leasePrice == null ? null : leasePrice.getApplyCreditAmount());
            Pair<Long, Long> amountPair = capitalDistribution.getOrDefault(rspDatum.getContractId(), new Pair<>(0L, 0L));
            rspDatum.setPayables("授信款");
            rspDatum.setAmountApplied(amountPair.getKey());
            rspDatum.setAmountPaid(amountPair.getValue());
            rspDatum.setRemainingApplyAmount(this.getRemainingApplyAmount(rspDatum.getPlanedPaidAmount(), rspDatum.getContractId(), null));
        }
        return rspData;
    }

    /**
     * 创建申请
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public Long add(PaymentAddReq req) {
        if (req.getRemainingApplyAmount() <= 0) {
            throw new MithrasException("该合同剩余可支付金额为0");
        }
        PaymentBaseInfo baseInfo = paymentConvert.addReqToEntity(req);
        baseInfo.setPaymentStatus(PaymentStatusEnum.NEW.name());
        baseInfo.setPaymentProcessStatus(ProcessStatus.UN_SUBMIT.name());
        baseInfo.setWriteOffStatus(PaymentWriteOffStatus.NO_PAID.name());
        // FIXME 如果是直租合同，在添加付款的时候就关联到借据上（和产品沟通，最后还是放在了付款创建的时候，感觉代码上有点不太顺，可以和产品PK更换关联逻辑）
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()) || Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            //  第一次编辑区带入 增加手续费、首期利息
            ContractLeasePrice contractLeasePrice = contractLeasePriceLibService.getLatestLib(baseInfo.getContractId());
            if (ObjectUtil.isNotEmpty(contractLeasePrice)) {
                baseInfo.setCommission(contractLeasePrice.getCommission());
                baseInfo.setFirstInstallmentInterest(contractLeasePrice.getFirstInstallmentInterest());
            }
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
                Assert.notEmpty(contractReceiptList, () -> MithrasException.newException("数据异常，借据信息不存在"));
                Assert.isTrue(contractReceiptList.size() == 1, () -> MithrasException.newException("数据异常，存在多个借据信息"));
                ContractReceipt contractReceipt = contractReceiptList.get(0);
                baseInfo.setReceiptId(contractReceipt.getId());
                baseInfo.setReceiptCode(contractReceipt.getReceiptCode());
            }
        }
        Integer paymentCount = baseMapper.selectCount(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, req.getContractId()));
        if (paymentCount == null) {
            paymentCount = 0;
        }
        baseInfo.setPaymentCode(generatePaymentCode(req.getContractCode(), paymentCount + 1));
//        Long cacheGuidePrice;
//        Long cachePledgePrice;
//        CashFtpInfluenceBO cashFtpInfluence = ftpService.getCashFtpInfluence(contractBaseInfo.getId(), LocalDate.now());
//        BillFtpBO billFtp = ftpService.getBillFtp(cashFtpInfluence.getTargetDate());
//        Set<Long> contractIds = new HashSet<>(Collections.singletonList(contractBaseInfo.getId()));
//        // 只走一次
//        cachePledgePrice = Long.valueOf(ftpService.handlePledge(cashFtpInfluence, contractIds));
//        // 只走一次
//        cacheGuidePrice = Long.valueOf(ftpService.getCashFtp(cashFtpInfluence, contractIds));
//        baseInfo.setCashFtp(Math.toIntExact(cacheGuidePrice - cachePledgePrice + cachePledgePrice));
//        Integer ticketPrice = Optional.ofNullable(billFtp).map(BillFtpBO::getBillFtpSell).orElse(0);
//        baseInfo.setBillFtp(Math.toIntExact(Long.parseLong(String.valueOf(ticketPrice))));
        baseMapper.insert(baseInfo);
        answerService.create(baseInfo.getId());


//        FtpAssessmentInfo ftpAssessmentInfo = FtpAssessmentInfo.builder()
//                .mountainAdjustment(0L).gradeAdjustment(0L).handAdjustment(0L)
//                .paymentId(baseInfo.getId()).build();
//        ftpAssessmentInfo.setTicketPrice(Long.parseLong(String.valueOf(ticketPrice)));
//
//        // 一开始他们两个应该是一样的
//        ftpAssessmentInfo.setGuidePrice(cacheGuidePrice - cachePledgePrice);
//        ftpAssessmentInfo.setBasePrice(cacheGuidePrice - cachePledgePrice);
//        ftpAssessmentInfo.setPledgePrice(cachePledgePrice);
//        // 这里存在执行顺序，后面两个set方法必须后执行
//        ftpAssessmentInfo.setGuidePrice();
//        ftpAssessmentInfo.setAssessmentPrice();
//        ftpAssessmentInfoService.save(ftpAssessmentInfo);

        //同步台账维护的保单信息到付款
        //policyInfoService.syncPolicy2Payment(baseInfo.getId(), baseInfo.getContractId());
        //占用合同下保单
        paymentPolicyInfoMapper.occupyPolicy(baseInfo.getContractId(), baseInfo.getId());
        //更新合同为已签约
        contractBaseInfo.setIsSigned(1);
        contractBaseInfoService.updateById(contractBaseInfo);
        return baseInfo.getId();
    }

    /**
     * 取消占用的付款保单信息
     **/
    @Transactional(rollbackFor = Throwable.class)
    public int cancelOccupy(Long contractId, Long paymentId) {
        return paymentPolicyInfoMapper.cancelOccupyPolicy(contractId, paymentId);
    }

    /**
     * 主页的模糊查询接口
     *
     * @param req
     * @return
     */
    public PageR<PaymentListRsp> list(PaymentListReq req) {
        PaymentListDto dto = paymentConvert.listReqToListDto(req);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<PaymentBaseInfo> page = baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        List<PaymentListRsp> resPageData = new ArrayList<>();
        List<Long> clientIds = new ArrayList<>();
        List<Long> applicantIds = new ArrayList<>();
        for (PaymentBaseInfo record : page.getRecords()) {
            PaymentListRsp paymentListRsp = paymentConvert.entityToListRsp(record);
            if (Objects.isNull(record.getBeyondDays()) || record.getBeyondDays() <= 0) {
                // 置空
                paymentListRsp.setBeyondDays(null);
            }
            resPageData.add(paymentListRsp);
            applicantIds.add(record.getCreateBy());
            clientIds.add(paymentListRsp.getClientId());
        }
        Map<Long, String> clientNames = id2NameService.clientId2Name(clientIds);
        Map<Long, String> applicantNames = id2NameService.sysUserId2Name(applicantIds);
        for (PaymentListRsp resPageDatum : resPageData) {
            resPageDatum.setClientName(clientNames.get(resPageDatum.getClientId()));
            resPageDatum.setApplicant(applicantNames.get(resPageDatum.getCreateBy()));
        }
        return PageR.of(page, resPageData);
    }

    @Transactional(rollbackFor = Throwable.class)
    public PaymentDetailRsp detail(Long paymentId) {
        PaymentBaseInfo baseInfo = getById(paymentId);
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ContractBaseInfoDetailREQ contractDetailReq = new ContractBaseInfoDetailREQ();
        contractDetailReq.setId(baseInfo.getContractId());
        ContractBaseInfoDetailRSP contractDetail = contractBaseInfoService.editionDetail(contractDetailReq);

        ContractPriceDetailREQ priceDetailReq = new ContractPriceDetailREQ();
        priceDetailReq.setContractId(baseInfo.getContractId());
        ContractPriceDetailRSP priceDetail = contractPriceService.editionDetail(priceDetailReq);

        PaymentDetailRsp paymentDetailRsp = joinContractInfo(contractDetail, priceDetail, baseInfo);
        // 填充合同概算IRR
        paymentDetailRsp.setContractEstimateIrr(priceDetail.getIrr());
        // 填充项目剩余未付金额
        ProjReviewPriceDetailRSP projReviewPriceDetailRSP = projReviewPriceService.detail(contractDetail.getProjReviewId());
        if (Objects.nonNull(projReviewPriceDetailRSP) && Objects.nonNull(projReviewPriceDetailRSP.getApplyCreditAmount())) {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(contractDetail.getProjReviewId());
            List<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(contractIds)) {
                List<PaymentActualDetail> paymentActualDetailList = actualDetailService.listByContractIds(contractIds);
                long paidAmount = 0;
                if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                    paidAmount = paymentActualDetailList.stream().filter(e -> Objects.nonNull(e.getPaidInAmount())).mapToLong(PaymentActualDetail::getPaidInAmount).sum();
                }
                paymentDetailRsp.setProjectRemainingUnpaidAmount(projReviewPriceDetailRSP.getApplyCreditAmount() - paidAmount);
            }
        }
//        // 缓存contract一些信息
//        ContractInfoCache.set(paymentDetailRsp.getContractId(),paymentConvert.detailRsp2CacheInfo(paymentDetailRsp));
        //生成快照后不再实时更新
        fillRedundant(baseInfo, contractDetail, priceDetail);
        baseMapper.updateById(baseInfo);
        return paymentDetailRsp;
    }

    /**
     * 聚合，detail接口和compare handler 调用
     *
     * @param baseInfo
     * @return
     */
    public PaymentDetailRsp joinContractInfo(ContractBaseInfoDetailRSP contractDetail,
                                             ContractPriceDetailRSP priceDetail,
                                             PaymentBaseInfo baseInfo) {
        if (contractDetail == null) {
            ContractBaseInfoDetailREQ contractDetailReq = new ContractBaseInfoDetailREQ();
            contractDetailReq.setId(baseInfo.getContractId());
            contractDetail = contractBaseInfoService.editionDetail(contractDetailReq);
        }
        if (priceDetail == null) {
            ContractPriceDetailREQ priceDetailReq = new ContractPriceDetailREQ();
            priceDetailReq.setContractId(baseInfo.getContractId());
            priceDetail = contractPriceService.editionDetail(priceDetailReq);
        }
        PaymentDetailRsp rsp = paymentConvert.joinPaymentDetail(contractDetail, priceDetail, baseInfo);
        // 手动填充
        rsp.setYunyingReviewState(baseInfo.getYunyingReviewState());
        if (Objects.nonNull(baseInfo.getYunyingReviewState())) {
            rsp.setYunyingReviewStateDisplay(Objects.equals(baseInfo.getYunyingReviewState(), YesOrNoNumberEnum.YES.getCode()) ? "已审核" : "未审核");
        }
        rsp.setYunyingReviewDate(baseInfo.getYunyingReviewDate());
        rsp.setApplyCreditAmount(priceDetail.getApplyCreditAmount());
        rsp.setPlanedPaidAmount(priceDetail.getApplyCreditAmount());
        rsp.setContractEarnestMoney(priceDetail.getEarnestMoney());
        rsp.setContractConsultingFee(priceDetail.getConsultingFee());
        rsp.setContractCommission(priceDetail.getCommission());
        rsp.setContractDownPayment(priceDetail.getDownPayment());
        rsp.setContractNominalPrice(priceDetail.getNominalPrice());
        rsp.setPolicyFlag(baseInfo.getPolicyFlag());
        rsp.setPaymentStatus(baseInfo.getPaymentStatus());
        rsp.setLowestIrr(baseInfo.getLowestIrr());
        rsp.setIsFinishPut(baseInfo.getIsFinishPut());
        rsp.setApprovedAmount(contractDetail.getApprovedAmount());

        if (ProjectBizType.ZL.name().equals(contractDetail.getBizType())) {
            rsp.setLeaseType(join("-", ProjectBizType.ZL.display, LeaseType.valueOf(contractDetail.getLeaseType()).display));
        } else {
            rsp.setLeaseType(ProjectBizType.valueOf(contractDetail.getBizType()).display);
        }
        rsp.setLeaseTypeCode(contractDetail.getLeaseType());
        rsp.setBizTypeCode(contractDetail.getBizType());
        Long currentLonginUserId = AccountUtil.getLoginInfo().getId();
        List<Long> userIds = new ArrayList<>();
        userIds.add(currentLonginUserId);
        if (null != rsp.getWriteOffUserIds()) {
            userIds.addAll(rsp.getWriteOffUserIds());
        }
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIds);
        rsp.setApplicant(userId2NameMap.get(currentLonginUserId));
        rsp.setCreateBy(baseInfo.getCreateBy());
        if (ObjectUtil.isNotEmpty(rsp.getWriteOffUserIds())) {
            rsp.setWriteOffUserNames(rsp.getWriteOffUserIds().stream().map(userId2NameMap::get).collect(Collectors.toList()));
        }
        rsp.setPayables("授信款");
        Pair<Long, Long> capitalDistribute = calculateCapitalDistribution(baseInfo.getContractId(), baseInfo.getId());
        rsp.setAmountApplied(capitalDistribute.getKey());
        rsp.setAmountPaid(capitalDistribute.getValue());
//        rsp.setRemainingApplyAmount(rsp.getPlanedPaidAmount() - rsp.getAmountApplied());
        // since 2022-11-17 改为计划付款金额 - 所有非关闭的付款申请金额
        rsp.setRemainingApplyAmount(this.getRemainingApplyAmount(contractDetail.getApprovedAmount(), rsp.getContractId(), baseInfo));
        return rsp;
    }

    /**
     * 更新接口，主要校验金额
     *
     * @param req
     */
    @Transactional(rollbackFor = Throwable.class)
    public void modify(PaymentModifyReq req) {

        PaymentBaseInfo baseInfo = baseMapper.selectById(req.getId());
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        //  校验手续费和首期利息
        Long commission = req.getCommission();
        Long firstInstallmentInterest = req.getFirstInstallmentInterest();
        //手续费，首期利息是租赁类型得到
        if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()) || Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            if (ObjectUtils.isEmpty(commission) || ObjectUtils.isEmpty(firstInstallmentInterest)) {
                throw new MithrasException("手续费和首期利息不能为空");
            }
            if (commission < 0) {
                throw new MithrasException("手续费必须填写大于等于零的数");
            }
            if (firstInstallmentInterest < 0) {
                throw new MithrasException("首期利息必须填写大于等于零的数");
            }

        }
        saveCheck(baseInfo);
//        Pair<Long, Long> capitalDistribute = calculateCapitalDistribution(baseInfo.getContractId(), null);
//        if (baseInfo.getConApplyCreditAmount() - capitalDistribute.getKey() < 0) {
//            throw new MithrasException("申请付款金额大于剩余可申请金额");
//        }
        // since 2022-11-17 改为计划付款金额 - 所有非关闭的付款申请金额
        ContractPriceDetailREQ priceDetailReq = new ContractPriceDetailREQ();
        priceDetailReq.setContractId(baseInfo.getContractId());
        ContractPriceDetailRSP priceDetail = contractPriceService.detail(priceDetailReq);
        long remainingApplyAmount = this.getRemainingApplyAmount(priceDetail.getApplyCreditAmount(), baseInfo.getContractId(), baseInfo);
        Assert.isTrue(remainingApplyAmount >= req.getApplyPaymentAmount(), () -> MithrasException.newException("申请付款金额大于剩余可申请金额"));
        PaymentBaseInfo entity = paymentConvert.modifyReqToEntity(req);
        // 补全一些数据，防止更新丢失
        entity.setReceiptId(baseInfo.getReceiptId());
        entity.setReceiptCode(baseInfo.getReceiptCode());
        entity.setReceiptIdFinal(baseInfo.getReceiptIdFinal());
        baseMapper.updateAnnotationIncludeNullById(entity);
    }

    //获取收款默认天，没有取第一次付款天数
    public Integer getDefaultCollectionDay(Long paymentId) {
        PaymentBaseInfo baseInfo = baseMapper.selectById(paymentId);
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        if (ObjectUtil.isNotEmpty(baseInfo.getDefaultCollectionDay())) {
            return baseInfo.getDefaultCollectionDay();
        }
        //取付款核销第一期
        PaymentActualDetail actualDetail = actualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, paymentId)
                .orderByAsc(PaymentActualDetail::getPaidInDate)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(actualDetail) && ObjectUtil.isNotEmpty(actualDetail.getPaidInAmount())) {
            return convertDefaultCollectionDay(actualDetail.getPaidInDate());
        }
        return 0;
    }

    public void modifyDefaultCollectionDay(ModifyCollectionDayREQ req) {
        LambdaUpdateWrapper<PaymentBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PaymentBaseInfo::getSameStartDate, req.getSameStartDate());
        if (req.getSameStartDate()) {
            // 如果是同起租日，先去获取实际起租日，如果实际起租日不存在，则去获取计划起租日
            // 1、通过借据表查询实际起租日（通过借据表）
            final PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
            if (Objects.isNull(paymentBaseInfo)) {
                throw new MithrasException("付款申请表不存在");
            }
            final ContractReceipt contractReceipt = contractReceiptService.getById(paymentBaseInfo.getReceiptIdFinal());
            if (Objects.nonNull(contractReceipt) && Objects.nonNull(contractReceipt.getReceiptStartDate())) {
                log.info("通过借据表查询实际起租日: {}", contractReceipt.getReceiptStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                updateWrapper.set(PaymentBaseInfo::getDefaultCollectionDay, contractReceipt.getReceiptStartDate().getDayOfMonth());
            } else {
                // 2、获取计划起租日（通过合同表）
                final ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                if (Objects.nonNull(contractBaseInfo)) {
                    if (Objects.nonNull(contractBaseInfo.getActualLeaseDate())) {
                        log.info("通过通过合同表查询实际起租日: {}", contractBaseInfo.getActualLeaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        updateWrapper.set(PaymentBaseInfo::getDefaultCollectionDay, contractBaseInfo.getActualLeaseDate().getDayOfMonth());
                    } else {
                        final LocalDate firstPaymentDate = getFirstPaymentDate(req.getPaymentId());
                        if (Objects.nonNull(firstPaymentDate)) {
                            updateWrapper.set(PaymentBaseInfo::getDefaultCollectionDay, firstPaymentDate.getDayOfMonth());
                        }
//                        if (Objects.nonNull(contractBaseInfo.getEstimatedLeaseDate())) {
//                            log.info("通过通过合同表查询计划起租日: {}", contractBaseInfo.getEstimatedLeaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//                            updateWrapper.set(PaymentBaseInfo::getDefaultCollectionDay, contractBaseInfo.getEstimatedLeaseDate().getDayOfMonth());
//                        }
                    }
                }
            }
        } else {
            updateWrapper.set(PaymentBaseInfo::getDefaultCollectionDay, req.getDefaultCollectionDay());
        }
        updateWrapper.eq(PaymentBaseInfo::getId, req.getPaymentId());
        baseMapper.update(null, updateWrapper);
    }

    /**
     * disable 接口
     *
     * @param req
     */
    @Transactional(rollbackFor = Throwable.class)
    @Deprecated
    public void disable(PaymentCloseReq req) {
        if (CollUtil.isEmpty(req.getIds())) {
            return;
        }
        List<PaymentBaseInfo> paymentBaseInfos = baseMapper.selectBatchIds(req.getIds());
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
            if (ObjectUtil.equal(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.CLOSED.name())) {
                throw new MithrasException(String.format("请勿重复关闭申请[%s]", paymentBaseInfo.getPaymentCode()));
            }
            if (!ObjectUtil.equal(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.NEW.name())
                    && !ObjectUtil.equal(paymentBaseInfo.getPaymentProcessStatus(), ProcessStatus.APPROVAL_REJECT)) {
                throw new MithrasException(String.format("申请[%s]不允许关闭，只能关闭未发起和审批拒绝的申请", paymentBaseInfo.getPaymentCode()));
            }
        }
        for (Long id : req.getIds()) {
            ProcessResp processResp = paymentService.findRelatedProcess(id);
            if (Objects.nonNull(processResp)) {
                ExecutionProcessBaseREQ cancelProcessReq = new ExecutionProcessBaseREQ();
                cancelProcessReq.setProcessInstanceId(processResp.getProcessInstanceId());
                cancelProcessReq.setMessage("因关闭付款申请，审批自动取消");
                executionService.cancelProcess(cancelProcessReq);
            }
            PaymentBaseInfo entity = new PaymentBaseInfo();
            entity.setId(id);
            entity.setPaymentStatus(PaymentStatusEnum.CLOSED.name());
            baseMapper.updateById(entity);
        }
    }

    /**
     * disable 接口,可关闭未被引用，且未付款收款核销
     * 这里涉及第三方，无法保证事物
     */
    //@Transactional(rollbackFor = Throwable.class)
    public String disable2(PaymentCloseReq req) {
        List<PaymentBaseInfo> paymentBaseInfos = baseMapper.selectBatchIds(req.getIds());
        if (CollUtil.isEmpty(paymentBaseInfos)) {
            return "无此付款";
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
            if (ObjectUtil.equal(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.CLOSED.name())) {
                throw new MithrasException(String.format("请勿重复关闭申请[%s]", paymentBaseInfo.getPaymentCode()));
            }
            if (ObjectUtil.equal(paymentBaseInfo.getPaymentProcessStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
                throw new MithrasException(String.format("申请[%s]处于流程中，请先关闭流程", paymentBaseInfo.getPaymentCode()));
            }
            if (!ObjectUtil.equal(AccountUtil.getLoginInfo().getId(), paymentBaseInfo.getCreateBy())) {
                throw new MithrasException(String.format("申请[%s]无权限关闭", paymentBaseInfo.getPaymentCode()));
            }
        }
        //已经被借据占用
        StringBuilder sb = new StringBuilder();
        List<PaymentBaseInfo> occupyPayment =
                paymentBaseInfos.stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId()) && !PaymentStatusEnum.NEW.name().equals(base.getPaymentStatus())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(occupyPayment)) {
            occupyPayment.forEach(pay -> sb.append(pay.getPaymentCode()));
            throw new MithrasException(sb + "已被借据占用");
        }
        //查询是否有付款
        List<PaymentBaseInfo> recordPayment = paymentBaseInfos.stream().filter(base -> !PaymentWriteOffStatus.NO_PAID.name().equals(base.getWriteOffStatus())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(recordPayment)) {
            recordPayment.forEach(pay -> sb.append(pay.getPaymentCode()));
            throw new MithrasException(String.format("申请[%s]已存在付款核销，不允许关闭！", sb.toString()));
        }
        //查询收款
        List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getPaymentId, req.getIds())
                .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name())));
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(pay -> sb.append(pay.getPaymentCode()));
            throw new MithrasException(String.format("申请[%s]已存在收款核销，不允许关闭！", sb.toString()));
        }
        //退付款核销
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoMapper.listLastRequest(PlatformApiEnum.CQ_BILL_PAYMENT.name(),
                paymentBaseInfos.stream().map(PaymentBaseInfo::getPaymentCode).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(exceptionRequestInfos)) {
            List<CQWithdrawREQ> cqWithdrawREQS = new ArrayList<>();
            List<String> payCodes = new ArrayList<>();
            exceptionRequestInfos.forEach(exceptionRequestInfo -> {
                FinancialCommonRSP rsp = JSONObject.parseObject(exceptionRequestInfo.getResponse(), FinancialCommonRSP.class);
                if (ObjectUtil.isNotEmpty(rsp) && ObjectUtil.isNotEmpty(rsp.getData())) {
                    rsp.getData().forEach(data -> {
                        CQWithdrawREQ withdrawBody = new CQWithdrawREQ();
                        withdrawBody.setSourcebillno(data.getSourcebillno());
                        withdrawBody.setBilltype("C");
                        cqWithdrawREQS.add(withdrawBody);
                    });
                }
            });
            if (ObjectUtil.isNotEmpty(cqWithdrawREQS)) {
                FinancialCommonRSP rsp = withdrawHandle.execute(cqWithdrawREQS);
                if (ObjectUtil.isEmpty(rsp) || CollectionUtil.isEmpty(rsp.getData())) {
                    throw new MithrasException("付款撤回请求失败，请稍后重试");
                }
                rsp.getData().forEach(data -> {
                    if (data.getSuccess()) {
                        payCodes.add(data.getSourcebillno());
                    } else {
                        sb.append("编号:");
                        sb.append(data.getSourcebillno());
                        sb.append("失败原因:");
                        sb.append(Optional.ofNullable(data.getMsg()).orElse("未知原因"));
                        sb.append("\n");
                    }
                });
            }
            //保存撤回结果
            if (CollectionUtil.isNotEmpty(payCodes)) {
                paymentBaseInfoMapper.updateFinanceStatusByCode(payCodes);
            }
        }
        //撤回除租金外收款
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getPaymentId, req.getIds())
                .ne(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            List<String> collectionIds = collectionBaseInfoList.stream().map(CollectionBaseInfo::getCode).collect(Collectors.toList());
            List<ExceptionRequestInfo> collectionRequestInfos = exceptionRequestInfoMapper.listLastRequest(PlatformApiEnum.CQ_RECEIVE.name(), collectionIds);
            if (CollectionUtil.isNotEmpty(collectionRequestInfos)) {
                List<CQWithdrawREQ> cqWithdrawREQS = new ArrayList<>();
                collectionRequestInfos.forEach(exceptionRequestInfo -> {
                    FinancialCommonRSP rsp = JSONObject.parseObject(exceptionRequestInfo.getResponse(), FinancialCommonRSP.class);
                    if (ObjectUtil.isNotEmpty(rsp) && ObjectUtil.isNotEmpty(rsp.getData())) {
                        rsp.getData().forEach(data -> {
                            CQWithdrawREQ withdrawBody = new CQWithdrawREQ();
                            withdrawBody.setSourcebillno(data.getSourcebillno());
                            withdrawBody.setBilltype("A");
                            cqWithdrawREQS.add(withdrawBody);
                        });
                    }
                });
                if (ObjectUtil.isNotEmpty(cqWithdrawREQS)) {
                    FinancialCommonRSP rsp = withdrawHandle.execute(cqWithdrawREQS);
                    if (ObjectUtil.isEmpty(rsp) || CollectionUtil.isEmpty(rsp.getData())) {
                        /*throw new MithrasException("付款撤回请求失败，请稍后重试");*/
                        //收款失败只做提示，不阻断
                        cqWithdrawREQS.forEach(data -> {
                            if (ObjectUtil.isNotEmpty(data.getSourcebillno())) {
                                sb.append("编号:");
                                sb.append(data.getSourcebillno());
                                sb.append("失败原因:");
                                sb.append("未知原因");
                                sb.append("\\n");
                            }
                        });
                    } else {
                        rsp.getData().forEach(data -> {
                            if (!data.getSuccess()) {
                                sb.append("编号:");
                                sb.append(data.getSourcebillno());
                                sb.append("失败原因:");
                                sb.append(Optional.ofNullable(data.getMsg()).orElse("未知原因"));
                                sb.append("\\n");
                            }
                        });
                    }
                }
            }
        }
        //这里关闭付款，删除收款数据，清理保证金
        paymentBaseInfos.forEach(base -> base.setPaymentStatus(PaymentStatusEnum.CLOSED.name()));
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            List<Long> collectionIds = collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
            //更新付款
            SpringContextHolder.getBean(PaymentBaseInfoService.class).updateBatchById(paymentBaseInfos);
            //删除收款
            if (CollectionUtil.isNotEmpty(collectionIds)) {
                collectionBaseInfoService.remove(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getId, collectionIds));
            }
            //保证金减去付款保证金
            List<MarginBaseInfo> marginBaseInfos = marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                    .in(MarginBaseInfo::getContractId, paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet())));
            if (CollectionUtil.isNotEmpty(marginBaseInfos)) {
                Map<Long, MarginBaseInfo> marginBaseInfoMap = marginBaseInfos.stream().collect(Collectors.toMap(MarginBaseInfo::getContractId, e -> e, (a, b) -> a));
                paymentBaseInfos.forEach(base -> {
                    MarginBaseInfo marginBaseInfo = marginBaseInfoMap.get(base.getContractId());
                    if (ObjectUtil.isNotEmpty(marginBaseInfo)) {
                        marginBaseInfo.setPlanMarginAmount(LongUtil.null2zero(marginBaseInfo.getPlanMarginAmount()) - LongUtil.null2zero(base.getEarnestMoney()));
                    }
                    //解除保单占用
                    SpringContextHolder.getBean(PaymentBaseInfoService.class).cancelOccupy(base.getContractId(), base.getId());
                });
                marginBaseInfoService.updateBatchById(marginBaseInfoMap.values());
            }
            //删除保单
            policyInfoService.remove(Wrappers.<PolicyInfo>lambdaQuery()
                    .in(PolicyInfo::getPaymentId, req.getIds()));

        });

        //发通知消息
        if (sb.length() > 0) {
            notice(sb, req.getIds());
        } else {
            sb.append("操作成功");
        }
        return sb.toString();
    }

    @Transactional(rollbackFor = Throwable.class)
    public String finish(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = this.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款信息不存在");
        }
        if (!Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())) {
            throw new MithrasException("当前付款申请状态不允许结束投放");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            throw new MithrasException("直租合同不允许手动进行结束投放，请通过合同管理的<合同起租>或<新增投放（借据）>进行操作");
        }
        if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
            throw new MithrasException("合同已起租，不允许重复起租");
        }
        // 判断合同是否处于可操作状态
        if (!ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus())) {
            throw new MithrasException("合同存在未提交的数据，请确认后再执行操作");
        }
        // 查询是否存在审批中的付款核销记录
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> actualDetailUnconfirmedQuery = Wrappers.lambdaQuery();
        actualDetailUnconfirmedQuery.eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId);
        actualDetailUnconfirmedQuery.eq(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.COMMIT.name());
        int detailCommitCount = actualDetailUnconfirmedService.count(actualDetailUnconfirmedQuery);
        if (detailCommitCount > 0) {
            throw new MithrasException("存在审批中的付款核销记录，不允许结束投放");
        }
        // 查询是否存在已确认的付款核销记录
        LambdaQueryWrapper<PaymentActualDetail> actualDetailQuery = Wrappers.lambdaQuery();
        actualDetailQuery.eq(PaymentActualDetail::getPaymentId, paymentId);
        int detailWrittenOffCount = actualDetailService.count(actualDetailQuery);
        if (detailWrittenOffCount == 0) {
            throw new MithrasException("不存在已确认（已核销）的付款核销记录，不允许结束投放");
        }
        // 查询同合同下是否已有已投放的付款申请
        LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
        paymentQuery.eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId());
        paymentQuery.eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.FINISHED.name());
        int paymentFinishCount = this.count(paymentQuery);
        if (paymentFinishCount > 0) {
            // 新增借据场景不支持自动，不处理
            return null;
        }
        // 自动发起合同相关流程
        StartProcessReq req = new StartProcessReq();
        req.setBusinessKey(String.valueOf(contractBaseInfo.getId()));
        req.setStartUserId(String.valueOf(contractBaseInfo.getProjSponsorUserId()));
        req.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        Map<String, Object> varMap = new HashMap<>();
        // 添加参数是为了流程结束时能准确知道应该变更哪一个付款申请的状态
        varMap.put(CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY, paymentId);
        varMap.put("projectSponsor", Collections.singletonList(String.valueOf(contractBaseInfo.getProjSponsorUserId())));
        req.setVariables(varMap);
        boolean isStarted = false;
        //系统调整租金表
        contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_UNCOMMIT.name());
        //补充irr和实际起租日
        LocalDate fistPaymentDate = contractBaseInfoService.getFistPaymentDate(contractBaseInfo.getId());
        contractBaseInfo.setActualLeaseDate(fistPaymentDate);
        contractBaseInfoService.updateById(contractBaseInfo);
        // 这里重新调整默认起租日
        log.info("PaymentBaseInfoService#finish: paymentId: {} sameStartDate: {}", paymentId, paymentBaseInfo.getSameStartDate());
        if (Objects.nonNull(paymentBaseInfo) && paymentBaseInfo.getSameStartDate()) {
            // 如果是默认起租日，则同步更新  直接实际第一笔放款时间的天作为默认还款日
            if (Objects.nonNull(fistPaymentDate)) {
                paymentBaseInfo.setDefaultCollectionDay(fistPaymentDate.getDayOfMonth());
            } else {
                final LocalDate firstPaymentDate = getFirstPaymentDate(paymentId);
                if (Objects.nonNull(firstPaymentDate)) {
                    paymentBaseInfo.setDefaultCollectionDay(firstPaymentDate.getDayOfMonth());
                }
            }
            paymentBaseInfoService.updateById(paymentBaseInfo);
        }
        //调整实际租金相关
        try {
            isStarted = contractBaseInfoService.autoAdjustmentRentActual(contractBaseInfo, paymentId);
        } catch (Exception e) {
            log.error("系统自动调整实际租金表异常[contractId:{}]", contractBaseInfo.getId(), e);
        }
        if (isStarted) {
            contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_COMMIT.name());
            contractBaseInfoService.updateById(contractBaseInfo);
        }
        // 自动发起合同起租流程
        //req.setModelKey(ProcessModelTypeEnum.ContractStartRentAutoFlow.name());
        req.setModelKey(ProcessModelTypeEnum.ContractStartRentFlow.name());
        req.setSubModule(ContractStatus.START_RENT.name());
        req.setProcessInstanceName(contractBaseInfo.getContractCode() + ProcessModelTypeEnum.ContractStartRentAutoFlow.getDisplay());
        //重新计算印花税
        ContractReceiptQueryActualTaxREQ receiptQueryActualTaxREQ = new ContractReceiptQueryActualTaxREQ();
        receiptQueryActualTaxREQ.setContractId(contractBaseInfo.getId());
        try {
            contractReceiptService.computeFinancialCosts(receiptQueryActualTaxREQ, true);
        } catch (Exception e) {
            log.error("计算印花税失败{}", e.getMessage());
        }
        String processInstanceId = null;
        // 计算借据的FTP价格考核信息
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractReceiptList)) {
            contractReceiptList.forEach(contractReceipt -> SpringUtil.getBean(FtpAssessmentInfoService.class).tryRecalculateFtpAssessmentInfo(contractReceipt.getId()));
        }
        if (isStarted) {
            processInstanceId = flowProcessApiService.start(req);
            bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
            try {
                //自动生成实际租金表文件
                ContractReceiptQueryActualTaxREQ contractReceiptQueryActualTaxREQ = new ContractReceiptQueryActualTaxREQ();
                contractReceiptQueryActualTaxREQ.setContractId(contractBaseInfo.getId());
                contractReceiptService.generate(contractReceiptQueryActualTaxREQ);
            } catch (Exception e){
                log.error("自动生成实际租金表文件失败{}", e.getMessage());
            }
        } else {
            CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                    .processType(ProcessModelTypeEnum.ContractStartRentAutoFlow.name())
                    .businessId(String.valueOf(paymentId))
                    .formName(String.join("-", contractBaseInfo.getContractCode(), ProcessModelTypeEnum.ContractStartRentFlow.getDisplay()))
                    .projName(contractBaseInfo.getProjName())
                    .clientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()))
                    .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(contractBaseInfo.getProjSponsorUserId())))
                    .currentNode("项目经理确认")
                    .applyTime(LocalDateTime.now())
                    .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                    .build();
            commonProcessPrepareMapper.insert(prepare);
        }
        return processInstanceId;
    }

    private void notice(StringBuilder sb, List<Long> paymentId) {
        try {
            Long userId = AccountUtil.getLoginInfo().getId();
            CompletableFuture.runAsync(() -> {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(Collections.singletonList(userId));
                messageAddREQ.setPcurl("/cpm/paymentApplication");
                messageAddREQ.setContent("付款申请");
                messageAddREQ.setFlowid(JSONUtil.toJsonStr(paymentId));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setRelation(sb.append("付款撤回失败，请尽快处理").toString());
                messageAddREQ.setMessageType(MessageTypeEnum.REMINDER_NOTICE.name());
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            });
        } catch (Exception e) {
            log.error("付款关闭通知失败", e);
        }
    }


    /**
     * 合同模块调用
     *
     * @param paymentCode
     * @return
     */
    public PaymentBaseInfo detailByPaymentCode(String paymentCode) {
        return getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getPaymentCode, paymentCode));
    }

    public List<PaymentBaseInfo> listByReceiptId(Long receiptId) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getReceiptId, receiptId);
        return this.list(query);
    }

    public LocalDate getFistPaymentDateByClientId(Long clientId) {
        PaymentActualDetail one = actualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getClientId, clientId)
                .isNotNull(PaymentActualDetail::getPaidInDate)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .orderByAsc(PaymentActualDetail::getPaidInDate)
                .last(StringUtil.mysqlLimitOne()));
        return one == null ? null : one.getPaidInDate();
    }


    /**
     * 合同模块调用
     * 批量获取合同下付款申请
     *
     * @return PaymentBaseInfo
     */
    public List<PaymentBaseInfo> detailByContractIds(List<Long> contractIds) {
        return list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, contractIds));
    }

    private void fillRedundant(PaymentBaseInfo baseInfo,
                               ContractBaseInfoDetailRSP contractDetail,
                               ContractPriceDetailRSP priceDetail) {
        if (contractDetail.getProjectType() != null) {
            baseInfo.setConProjectType(contractDetail.getProjectType());
        }
        if (contractDetail.getBizDeptId() != null) {
            baseInfo.setConBizDeptId(contractDetail.getBizDeptId());
        }
        if (contractDetail.getBizDivisionLeaderId() != null) {
            baseInfo.setConBizDivisionLeaderId(contractDetail.getBizDivisionLeaderId());
        }
        if (contractDetail.getRiskControlManagerId() != null) {
            baseInfo.setConRiskControlManagerId(contractDetail.getRiskControlManagerId());
        }
        if (contractDetail.getBizDeptLeaderId() != null) {
            baseInfo.setConBizDeptLeaderId(contractDetail.getBizDeptLeaderId());
        }
        if (priceDetail.getApplyCreditAmount() != null) {
            baseInfo.setConApplyCreditAmount(priceDetail.getApplyCreditAmount());
        }
    }

    public Long getRemainingApplyAmount(Long planedPaidAmount, Long contractId, PaymentBaseInfo currentPayment) {
        if (planedPaidAmount == null) {
            planedPaidAmount = 0L;
        }
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getContractId, contractId);
        if (Objects.nonNull(currentPayment)) {
            query.ne(PaymentBaseInfo::getId, currentPayment.getId());
        }
        query.ne(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name());
        List<PaymentBaseInfo> nonClosePaymentList = this.list(query);
        if (CollectionUtil.isEmpty(nonClosePaymentList)) {
            return planedPaidAmount;
        } else {
            // 2025-10-10 修改，如果已经结束投放的付款申请取实际核销金额，否则取付款金额
//            long amount = 0;
//            Map<String, List<PaymentBaseInfo>> map = nonClosePaymentList.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getPaymentStatus));
//            for (Map.Entry<String, List<PaymentBaseInfo>> entry : map.entrySet()) {
//                if (PaymentStatusEnum.FINISHED.name().equals(entry.getKey())) {
//                    List<PaymentActualDetail> actualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
//                            .in(PaymentActualDetail::getPaymentId, entry.getValue().stream().map(PaymentBaseInfo::getId).collect(Collectors.toList())));
//                    amount += actualDetails.stream().filter(item -> Objects.nonNull(item.getPaidInAmount())).mapToLong(PaymentActualDetail::getPaidInAmount).sum();
//                } else {
//                    amount += entry.getValue().stream().filter(base -> ObjectUtil.isNotEmpty(base.getApplyPaymentAmount())).mapToLong(PaymentBaseInfo::getApplyPaymentAmount).sum();
//                }
//            }
            long amount = nonClosePaymentList.stream().filter(base -> ObjectUtil.isNotEmpty(base.getApplyPaymentAmount())).mapToLong(PaymentBaseInfo::getApplyPaymentAmount).sum();
            return planedPaidAmount - amount;
        }
    }

    private void cancelJoinReceipt(List<PaymentBaseInfo> paymentBaseInfoList) {
        // 取消所有没有生效的关联借据
        List<PaymentBaseInfo> filterList = paymentBaseInfoList.stream().filter(item -> Objects.isNull(item.getReceiptIdFinal())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterList)) {
            return;
        }
//        List<PaymentBaseInfo> targetList = filterList.stream().peek(item -> {
//            item.setReceiptId(null);
//            item.setReceiptCode(null);
//            item.setReceiptIdFinal(null);
//        }).collect(Collectors.toList());
        for (PaymentBaseInfo paymentBaseInfo : filterList) {
            paymentBaseInfo.setReceiptId(null);
            paymentBaseInfo.setReceiptCode(null);
            paymentBaseInfo.setReceiptIdFinal(null);
            getBean(PaymentBaseInfoService.class).getBaseMapper().updateAnnotationIncludeNullById(paymentBaseInfo);
        }
    }

    private boolean isInSpecificFlowNode(Long contractId) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(contractId.toString());
        req.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractAddNewReceiptFlow.name(), ProcessModelTypeEnum.ContractStartRentFlow.name()));
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(req);
        if (CollectionUtil.isEmpty(processRespPage.getContents())) {
            return false;
        }
        for (ProcessResp processResp : processRespPage.getContents()) {
            if (Objects.equals(processResp.getModelKey(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name()) && Objects.equals(processResp.getCurTaskActivityIds(), "userTask_financeManager")) {
                return true;
            }
            if (Objects.equals(processResp.getModelKey(), ProcessModelTypeEnum.ContractStartRentFlow.name()) && Objects.equals(processResp.getCurTaskActivityIds(), "userTask_financeManager")) {
                return true;
            }
        }
        return false;
    }

    public List<PaymentBaseInfo> listByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, contractIds)
                .ne(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name())
        );
    }

    public List<PaymentBaseInfo> listByReceiptIds(List<Long> receiptIds) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.in(PaymentBaseInfo::getReceiptId, receiptIds);
        return this.list(query);
    }

    public PaymentBaseInfo getFistPaymentByClient(Long clientId) {
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getClientId, clientId);
        query.isNotNull(PaymentBaseInfo::getPaidInDate);
        query.orderByAsc(PaymentBaseInfo::getPaidInDate);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<PaymentTransactionStructureInfoRsp> transactionStructureInfo(PaymentTransactionStructureInfoReq req) {
        List<PaymentTransactionStructureInfoRsp> result = getPaymentTransactionStructureInfoRspList(req);
        // 填充存量的风险敞口
        List<Long> clientIds = result.stream().map(PaymentTransactionStructureInfoRsp::getClientId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(clientIds)) {
            Map<Long, Long> clientStockRiskExposureMap = clientService.clientStockRiskExposureMap(clientIds);
            for (PaymentTransactionStructureInfoRsp rsp : result) {
                rsp.setStockRiskExposure(clientStockRiskExposureMap.getOrDefault(rsp.getClientId(), 0L));
            }
        }
        // 填充客户的舆情信息
        Map<String, Integer> stringIntegerMap = riskControlOpinionMonitorService.countByClientIdList(result.stream().map(PaymentTransactionStructureInfoRsp::getClientId).collect(Collectors.toList()));
        for (PaymentTransactionStructureInfoRsp rsp : result) {
            rsp.setCount(stringIntegerMap.getOrDefault(rsp.getClientName(), 0));
        }
        return result;
    }

    @NotNull
    public List<PaymentTransactionStructureInfoRsp> getPaymentTransactionStructureInfoRspList(PaymentTransactionStructureInfoReq req) {
        List<PaymentTransactionStructureInfoRsp> result = new ArrayList<>();
        PaymentBaseInfo paymentBaseInfo = this.getById(req.getPaymentId());
        if (ObjectUtils.isEmpty(paymentBaseInfo)) {
            throw new MithrasException("付款信息为空！");
        }

        //  获取付款申请前最新版本合同信息
        Long contractId = paymentBaseInfo.getContractId();
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.lambdaQuery()
                .eq(ContractBaseInfoLib::getOriginId, contractId)
                .le(ContractBaseInfoLib::getCreateTime, paymentBaseInfo.getCreateTime())
                .orderByDesc(ContractBaseInfoLib::getVersion)
                .last("limit 1")
                .one();
        //  顺序：主承租人/债权人-> 联合承租人/债务人 -> 担保人 -> 抵押人 -> 质押人.
        if (!ObjectUtils.isEmpty(contractBaseInfoLib)) {
            String version = contractBaseInfoLib.getVersion();
            //  主承租人/债权人-> 联合承租人/债务人
            List<ContractTenantryLib> contractTenantryLibs = contractTenantryLibService.lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, contractId)
                    .eq(ContractTenantryLib::getVersion, version).list();
            if (!ObjectUtils.isEmpty(contractTenantryLibs)) {
                //  主承租人
                contractTenantryLibs.stream().filter(item -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(item.getLesseeType())).forEach(item -> {
                    String transactionStructureType = Optional.ofNullable(clientService.getById(item.getLesseeId())).map(Client::getClientType).orElse(null);
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientName(item.getLesseeName());
                    rsp.setClientId(item.getLesseeId());
                    rsp.setClientType(transactionStructureType);
                    rsp.setTransactionStructureType(item.getLesseeType());
                    result.add(rsp);
                });
                //  债权人
                contractTenantryLibs.stream().filter(item -> ClientRole.CREDITOR.name().equals(item.getLesseeType())).forEach(item -> {
                    String transactionStructureType = Optional.ofNullable(clientService.getById(item.getLesseeId())).map(Client::getClientType).orElse(null);
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientName(item.getLesseeName());
                    rsp.setClientId(item.getLesseeId());
                    rsp.setClientType(transactionStructureType);
                    rsp.setTransactionStructureType(item.getLesseeType());
                    result.add(rsp);
                });
                //  联合承租人
                contractTenantryLibs.stream().filter(item -> LesseeTypeEnum.JOINT_LESSEE.name().equals(item.getLesseeType())).forEach(item -> {
                    String transactionStructureType = Optional.ofNullable(clientService.getById(item.getLesseeId())).map(Client::getClientType).orElse(null);
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientName(item.getLesseeName());
                    rsp.setClientId(item.getLesseeId());
                    rsp.setClientType(transactionStructureType);
                    rsp.setTransactionStructureType(item.getLesseeType());
                    result.add(rsp);
                });
                //  债务人
                contractTenantryLibs.stream().filter(item -> ClientRole.DEBTOR.name().equals(item.getLesseeType())).forEach(item -> {
                    String transactionStructureType = Optional.ofNullable(clientService.getById(item.getLesseeId())).map(Client::getClientType).orElse(null);
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientName(item.getLesseeName());
                    rsp.setClientId(item.getLesseeId());
                    rsp.setClientType(transactionStructureType);
                    rsp.setTransactionStructureType(item.getLesseeType());
                    result.add(rsp);
                });
            }

            //  担保人
            List<ContractGuarantorLib> guarantorLibs = contractGuarantorLibService.lambdaQuery()
                    .eq(ContractGuarantorLib::getContractId, contractId)
                    .eq(ContractGuarantorLib::getVersion, version).list();
            for (ContractGuarantorLib contractGuarantorLib : guarantorLibs) {
                List<Client> clientList = clientService.listByClientIds(typeConversionWorker.jsonStringToLongList(contractGuarantorLib.getGuarantorIds()));
                for (Client client : clientList) {
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientType(client.getClientType());
                    rsp.setClientId(client.getId());
                    rsp.setClientName(client.getClientName());
                    rsp.setTransactionStructureType(LesseeTypeEnum.GUARANTOR.name());
                    result.add(rsp);
                }
            }

            //  抵押人
            List<ContractMortgageLib> contractMortgageLibs = contractMortgageLibService.lambdaQuery()
                    .eq(ContractMortgageLib::getContractId, contractId)
                    .eq(ContractMortgageLib::getVersion, version).list();
            for (ContractMortgageLib contractMortgageLib : contractMortgageLibs) {
                List<Client> clientList = clientService.listByClientIds(typeConversionWorker.jsonStringToLongList(contractMortgageLib.getMortgageIds()));
                for (Client client : clientList) {
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientType(client.getClientType());
                    rsp.setClientId(client.getId());
                    rsp.setClientName(client.getClientName());
                    rsp.setTransactionStructureType(LesseeTypeEnum.MORTGAGOR.name());
                    result.add(rsp);
                }
            }

            //  质押人
            List<ContractPledgeLib> contractPledgeLibs = contractPledgeLibService.lambdaQuery()
                    .eq(ContractPledgeLib::getContractId, contractId)
                    .eq(ContractPledgeLib::getVersion, version).list();
            for (ContractPledgeLib contractPledgeLib : contractPledgeLibs) {
                List<Client> clientList = clientService.listByClientIds(typeConversionWorker.jsonStringToLongList(contractPledgeLib.getPledgeIds()));
                for (Client client : clientList) {
                    PaymentTransactionStructureInfoRsp rsp = new PaymentTransactionStructureInfoRsp();
                    rsp.setClientType(client.getClientType());
                    rsp.setClientId(client.getId());
                    rsp.setClientName(client.getClientName());
                    rsp.setTransactionStructureType(LesseeTypeEnum.PLEDGOR.name());
                    result.add(rsp);
                }
            }
        }
        return result;
    }

    private BlackGrayLibraryRSP getLocalLibraryRecord(BlackGrayLibraryREQ req) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        example.orderBy(BlackGrayLibrary.BLACK_GRAY_SORT).asc();
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(blackGrayLibraries)) {
            return null;
        }
        BlackGrayLibrary blackGrayLibrary = blackGrayLibraries.get(0);
        BlackGrayLibraryRSP blackGrayLibraryRSP = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibraryRSP.class, "applyReasonType");
        blackGrayLibraryRSP.setApplyReasonType(JSONUtil.toList(blackGrayLibrary.getApplyReasonType(), String.class));
        blackGrayLibraryRSP.setApplyReasonNames(new ArrayList<>());
        Map<String, BlackGrayWarehouseRuleConfig> stringBlackGrayWarehouseRuleConfigMap = blackGrayWarehouseRuleConfigService.num2BeanBatch(blackGrayLibraryRSP.getApplyReasonType());
        for (String num : blackGrayLibraryRSP.getApplyReasonType()) {
            BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = stringBlackGrayWarehouseRuleConfigMap.get(num);
            if (ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig) && ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig.getRuleName())) {
                blackGrayLibraryRSP.getApplyReasonNames().add(blackGrayWarehouseRuleConfig.getRuleName());
            }
        }
        return blackGrayLibraryRSP;
    }

    private BlackGrayLibraryRSP jkRsp2BlackGrayLibraryRSP(JKBlackGrayCollisionLibraryRSP rsp) {
        if (ObjectUtil.isEmpty(rsp) || ObjectUtil.isEmpty(rsp.getData())) {
            return null;
        }
        BlackGrayLibraryRSP blackGrayLibraryRSP = new BlackGrayLibraryRSP();
        JKBlackGrayCollisionLibraryRSP.JKBlackGrayCollisionLibraryBody data = rsp.getData();
        blackGrayLibraryRSP.setEnterpriseName(data.getEnterpriseName());


        BlackGrayTypeEnum blackGrayTypeEnum = BlackGrayTypeEnum.jkOf(data.getEnterpriseStatusCode());
        if (Objects.nonNull(blackGrayTypeEnum)) {
            blackGrayLibraryRSP.setBlackGrayType(blackGrayTypeEnum.name());
        }
        blackGrayLibraryRSP.setApplyReasonType(data.getApplyReasonType());
        blackGrayLibraryRSP.setApplyReasonNames(data.getApplyReasonTypeName());
        blackGrayLibraryRSP.setApplyReason(data.getApplyReason());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        blackGrayLibraryRSP.setWarehouseTime(data.getWarehouseTime() == null ? null : LocalDate.parse(data.getWarehouseTime(), formatter));
        return blackGrayLibraryRSP;
    }

    /**
     * 25号（含）-4号（含）：默认收款⽇=25号
     * 5号（含）- 14号（含）：默认收款⽇=5号
     * 15号（含）-24号（含）：默认收款⽇=15号
     **/
    private Integer convertDefaultCollectionDay(LocalDate date) {
        if (ObjectUtil.isEmpty(date)) {
            return 0;
        }
        int dayOfMonth = date.getDayOfMonth();
        if (dayOfMonth >= 25 || dayOfMonth <= 4) {
            return 25;
        } else if (dayOfMonth <= 14) {
            return 5;
        } else {
            return 15;
        }
    }

    public void downloadTemplate(ServletOutputStream outputStream) {
        LambdaQueryWrapper<FileTemplate> query = Wrappers.lambdaQuery();
        query.eq(FileTemplate::getTemplateType, "付款-放款底稿");
        query.eq(FileTemplate::getOutdated, false);
        List<FileTemplate> fileTemplateList = fileTemplateService.list(query);
        if (CollectionUtil.isEmpty(fileTemplateList)) {
            throw new MithrasException("没有符合条件的放款审核模板");
        }
        Map<Long, FileTemplate> fileTemplateMap = fileTemplateList.stream().collect(Collectors.toMap(FileTemplate::getId, e -> e));
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.FILE_TEMPLATE.name(), null, new LinkedList<>(fileTemplateMap.keySet()));
        if (CollectionUtil.isEmpty(materialsListList)) {
            throw new MithrasException("没有符合条件的放款审核模板文件");
        }
        String[] paths = new String[materialsListList.size()];
        InputStream[] inputStreams = new InputStream[materialsListList.size()];
        for (int i = 0; i < inputStreams.length; i++) {
            MaterialsList materials = materialsListList.get(i);
            inputStreams[i] = getBean(MinioOssClient.class).downLoad(materials.getOssFilename());
            paths[i] = fileTemplateMap.get(materials.getBelongId()).getFilename();
        }
        ZipUtil.zip(outputStream, paths, inputStreams);

    }

    public Map<Long, Long> getProjReviewPaymentProjReviewId(Set<Long> projReviewIds, LocalDate paidInDateFrom, LocalDate paidInDateTo) {
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return MapUtil.empty();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, projReviewIds));
        Map<Long, List<ContractBaseInfo>> projId2Contract = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        List<PaymentActualDetail> paymentActualDetails = actualDetailService.writtenOffDetailsByContractIds(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()), paidInDateFrom, paidInDateTo);
        if (ObjectUtil.isEmpty(paymentActualDetails)) {
            return MapUtil.empty();
        }
        Map<Long, List<PaymentActualDetail>> contractId2Paymnet = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        Map<Long, Long> projMap = new HashMap<>();
        projId2Contract.forEach((projId, contracts) -> {
            Long sum = 0L;
            for (ContractBaseInfo e : contracts) {
                List<PaymentActualDetail> actualDetails = contractId2Paymnet.get(e.getId());
                if (ObjectUtil.isNotEmpty(actualDetails)) {
                    sum += actualDetails.stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L);
                }
            }
            projMap.put(projId, sum);
        });
        return projMap;
    }

    public Map<Long, LocalDate> getContractFirstPaymentDate(Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        return actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getContractId, contractIds)
                .isNotNull(PaymentActualDetail::getPaidInDate)
                .orderByAsc(PaymentActualDetail::getPaidInDate)).stream().collect(Collectors.toMap(PaymentActualDetail::getContractId, PaymentActualDetail::getPaidInDate, (a, b) -> a.isAfter(b) ? b : a));
    }

    public Map<Long, LocalDate> getReceiptFirstPaymentDate(Set<Long> receiptIds) {
        if (ObjectUtil.isEmpty(receiptIds)) {
            return MapUtil.empty();
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds));
        if (ObjectUtil.isEmpty(paymentBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> paymentId2Receipt = paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal, (a, b) -> a));
        return actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getPaymentId, paymentId2Receipt.keySet())
                .isNotNull(PaymentActualDetail::getPaidInDate)
                .orderByAsc(PaymentActualDetail::getPaidInDate)).stream().collect(Collectors.toMap(e -> paymentId2Receipt.get(e.getPaymentId()), PaymentActualDetail::getPaidInDate, (a, b) -> a.isAfter(b) ? b : a));
    }

    public Boolean checkClientOpinion(PaymentEffectREQ req) {
        PaymentTransactionStructureInfoReq structureInfoReq = new PaymentTransactionStructureInfoReq();
        structureInfoReq.setPaymentId(req.getId());
        List<PaymentTransactionStructureInfoRsp> infoRspList = getPaymentTransactionStructureInfoRspList(structureInfoReq);
        Map<String, Integer> stringIntegerMap = riskControlOpinionMonitorService.countByClientIdList(infoRspList.stream().map(PaymentTransactionStructureInfoRsp::getClientId).collect(Collectors.toList()));
        if (ObjectUtil.isNotEmpty(stringIntegerMap)) {
            return stringIntegerMap.values().stream().mapToInt(LongUtil::null2zero).sum() > 0;
        }
        return false;
    }

    public LocalDate getFirstPaymentDate(Long paymentId) {
        // 先从实际付款记录表获取
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.listByPaymentId(paymentId);
        if (CollectionUtil.isNotEmpty(paymentActualDetails)) {
            paymentActualDetails = paymentActualDetails.stream()
                    .filter(o -> !WriteOffStatus.CLOSED.name().equals(o.getWriteOffStatus()))
                    .filter(o -> !WriteOffStatus.IGNORE.name().equals(o.getWriteOffStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(paymentActualDetails)) {
                Optional<PaymentActualDetail> minPaidInDateRecord = paymentActualDetails.stream()
                        .min(Comparator.comparing(PaymentActualDetail::getPaidInDate));
                if (minPaidInDateRecord.isPresent()) {
                    return minPaidInDateRecord.get().getPaidInDate();
                }
            }
        } else {
            // 如果为空，再从实际付款记录表（未确认）获取
            List<PaymentActualDetailUnconfirmed> paymentActualDetailUnconfirmeds = paymentActualDetailUnconfirmedService.listByPaymentId(paymentId);
            if (CollectionUtil.isNotEmpty(paymentActualDetailUnconfirmeds)) {
                paymentActualDetailUnconfirmeds = paymentActualDetailUnconfirmeds.stream()
                        .filter(o -> !WriteOffStatus.CLOSED.name().equals(o.getWriteOffStatus()))
                        .filter(o -> !WriteOffStatus.IGNORE.name().equals(o.getWriteOffStatus()))
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(paymentActualDetailUnconfirmeds)) {
                    Optional<PaymentActualDetailUnconfirmed> optionalPaymentActualDetailUnconfirmed = paymentActualDetailUnconfirmeds.stream()
                            .min(Comparator.comparing(PaymentActualDetailUnconfirmed::getPaidInDate));
                    if (optionalPaymentActualDetailUnconfirmed.isPresent()) {
                        return optionalPaymentActualDetailUnconfirmed.get().getPaidInDate();
                    }
                }
            }
        }
        return null;
    }
}
