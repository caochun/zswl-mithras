package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.finance.overdue.*;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.finance.enums.financeoverdue.OverduePaymentNumberEnum;
import cn.zswltech.mithras.finance.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.finance.enums.financeoverdue.OverdueRecordTypeEnum;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountNumberENUM;
import cn.zswltech.mithras.finance.mapper.finance.FinanceOverdueIntegrationMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueVersionRelation;
import cn.zswltech.mithras.finance.service.FinanceOverdueVersionRelationService;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.finance.service.lib.finance.FinanceOverdueIntegrationLibService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.handle.OverdueReportBatSaveHandle;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.req.OverdueReportBatSaveReq;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.rsp.OverdueReportBatSaveRSP;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description 应收逾期集成表
* @author vico
* @date 2025-09-15
*/
@Service
public class FinanceOverdueIntegrationService extends ServiceImpl<FinanceOverdueIntegrationMapper, FinanceOverdueIntegration> {

    @Resource
    private FinanceOverdueIntegrationMapper financeOverdueIntegrationMapper;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;
    @Resource
    private UserService userService;
    @Resource
    private OverdueReportBatSaveHandle overdueReportBatSaveHandle;
    @Resource
    private FinanceOverdueVersionRelationService financeOverdueVersionRelationService;
    @Resource
    private FinanceOverdueIntegrationLibService financeOverdueIntegrationLibService;

    private final static String LONG_TERM_RECEIVABLE = "长期应收款";
    private final static String PAY_RENT_SCHEDULE = "按期支付租金";


    @Transactional(rollbackFor = Throwable.class)
    public void create(Long reportBaseId, LocalDate reportData) {
        if(ObjectUtil.isEmpty(reportBaseId) || ObjectUtil.isEmpty(reportData)) {
            throw new MithrasException("创建应收逾期集成失败, 逾期月份为空");
        }
        LocalDate firstDay = reportData.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = firstDay.plusMonths(1);
        //查询截止日前的逾期情况 应收日期在本月且有逾期的
        List<CollectionBaseInfo> overdueList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .ge(CollectionBaseInfo::getPlanCollectionDate, firstDay)
                .lt(CollectionBaseInfo::getPlanCollectionDate, lastDay)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPenaltyInterest, 0));
        if (CollectionUtil.isEmpty(overdueList)) {
            return;
        }

        //计算某期下未逾期还款
        Map<Long, List<CollectionRecordInfo>> collectionId2Record = collectionRecordInfoService.listByCollectionIds(overdueList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

        Set<Long> contractIds = overdueList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractId2BeanMap = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(overdueList.stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet()));
        List<FinanceOverdueIntegration> overdueIntegrations = new ArrayList<>();
        overdueList.forEach(collectionBaseInfo -> {
            ContractBaseInfo contractBaseInfo = contractId2BeanMap.get(collectionBaseInfo.getContractId());
            List<CollectionRecordInfo> recordInfos = collectionId2Record.get(collectionBaseInfo.getId());
            FinanceOverdueIntegration info = new FinanceOverdueIntegration();
            info.setId(IdUtil.getSnowflakeNextId());
            info.setOverdueReportId(reportBaseId);
            info.setCollectionId(collectionBaseInfo.getId());
            info.setCollectionCode(collectionBaseInfo.getCode());
            info.setRecordStatus(OverdueRecordStatueEnum.NOT_REPORT.name());
            info.setContractId(collectionBaseInfo.getContractId());
            info.setContractCode(collectionBaseInfo.getContractCode());
            info.setClientId(collectionBaseInfo.getClientId());
            info.setClientName(clientId2Name.get(collectionBaseInfo.getClientId()));
            info.setPaymentNumber(OverduePaymentNumberEnum.KX08.name());
            info.setRecordStartDate(collectionBaseInfo.getPlanCollectionDate());
            info.setRecordBillDate(reportData);
            info.setAccounttypeNumber(LONG_TERM_RECEIVABLE);
            info.setRecordDueDate(collectionBaseInfo.getPlanCollectionDate());
            info.setRecordPaymentTerms(PAY_RENT_SCHEDULE);
            info.setReceAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()).toString()));
            if (CollectionUtil.isNotEmpty(recordInfos)) {
                //减去正常还款金额
                Long reduce = recordInfos.stream().filter(e -> !e.getCollectionDate().isAfter(collectionBaseInfo.getPlanCollectionDate())).map(CollectionRecordInfo::getCollectionAmount).reduce(0L, Long::sum);
                info.setReceAmount(info.getReceAmount().subtract(LongUtil.tenThousand2Dollar(LongUtil.null2zero(reduce).toString())));
            }
            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                info.setProjName(contractBaseInfo.getProjName());
            }
            info.setCashFlowItem(collectionBaseInfo.getCashFlowItem());
            info.setApprovalStatus(ProjProcessState.UN_SUBMIT.name());
            overdueIntegrations.add(info);
        });
        this.saveBatch(overdueIntegrations);
    }

    @Transactional(rollbackFor = Throwable.class)
    public FinanceOverdueIntegrationPushRSP push(FinanceOverdueIntegrationPushREQ req) {
        List<String> messages = new ArrayList<>();
        int success = 0;
        int failure = 0;
        FinanceOverdueIntegrationPushRSP rsp = new FinanceOverdueIntegrationPushRSP();
        //先检查
        List<Long> ids = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(req.getIds())) {
            ids = req.getIds().stream().map(Long::parseLong).collect(Collectors.toList());
        }
        List<FinanceOverdueIntegration> overdueIntegrations = this.list(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, req.getReportBaseId())
                .in(CollectionUtil.isEmpty(ids), FinanceOverdueIntegration::getApprovalStatus, ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.NEW_APPROVAL_PASS.name())
                .ne(FinanceOverdueIntegration::getRecordStatus, OverdueRecordStatueEnum.REPORT_SUCCESS.name())
                .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueIntegration::getId, ids));
        overdueIntegrations.forEach(e -> {
            if (!StrUtil.equalsAny(e.getApprovalStatus(), ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.NEW_APPROVAL_PASS.name())){
                throw new MithrasException(e.getCollectionCode() + "单据未审批通过，请提交审批后推送");
            }
        });
        if (CollectionUtil.isEmpty(overdueIntegrations)) {
            rsp.setCount(0);
            return rsp;
        }
        //构建推送参数
        List<OverdueReportBatSaveReq> pushReq = new ArrayList<>();
        Map<Long, Client> clientId2Bean = clientService.listByClientIds(overdueIntegrations.stream().map(FinanceOverdueIntegration::getClientId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(overdueIntegrations.stream().map(FinanceOverdueIntegration::getContractId).collect(Collectors.toSet()));
        Map<Long, ContractBaseInfo> contractId2BeanMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, String> userId2Code = userService.getUserInfoByIds(contractBaseInfos.stream().map(ContractBaseInfo::getProjSponsorUserId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(UserVO::getId, UserVO::getMainCode, (a, b) -> b));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        DateTimeFormatter dayFormatter= DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);

        LocalDateTime now = LocalDateTime.now();

        overdueIntegrations.forEach(e -> {
            OverdueReportBatSaveReq bsr = new OverdueReportBatSaveReq();
            Client client = clientId2Bean.get(e.getClientId());
            ContractBaseInfo contractBaseInfo = contractId2BeanMap.get(e.getContractId());
            bsr.setCico_contract_number(FinancialConstants.HT99);
            bsr.setCico_account_org_number(FinancialConstants.RZZL_CODE);
            bsr.setCico_payment_number(OverduePaymentNumberEnum.switchCq(e.getCashFlowItem()).name());
            bsr.setCico_accounttype_number(FinancialAccountNumberENUM.LONG_TERM_ACCOUNT_RECEIVABLE.getCode());
            bsr.setCico_origin_currency_number(FinancialConstants.RMB_NAME);
            bsr.setCico_functional_currency_number(FinancialConstants.RMB_NAME);
            bsr.setModifytime(now.format(formatter));
            bsr.setCico_origin_no(e.getId().toString());
            bsr.setCico_customer_type(FinancialConstants.BD_CUSTOMER);
            bsr.setCico_start_date(e.getRecordStartDate() == null ? null : e.getRecordStartDate().format(dayFormatter));
            bsr.setCico_bill_date(e.getRecordBillDate() == null ? null : e.getRecordBillDate().format(dayFormatter));
            bsr.setCico_due_date(e.getRecordDueDate() == null ? null : e.getRecordDueDate().format(dayFormatter));
            bsr.setCico_payment_terms(e.getRecordPaymentTerms());
            bsr.setCico_rece_amount(e.getReceAmount());
            bsr.setCico_rece_amount_f(e.getReceAmount());
            bsr.setCico_collection_cycle(0);
            if(ObjectUtil.isNotEmpty(client)) {
                bsr.setCico_customer_number(client.getClientCode());
            }
            if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                bsr.setCico_handler_number(userId2Code.get(contractBaseInfo.getProjSponsorUserId()));
            }
            pushReq.add(bsr);
        });
        rsp.setCount(pushReq.size());
        //发送请求
        OverdueReportBatSaveRSP execute = overdueReportBatSaveHandle.execute(pushReq);
        if (ObjectUtil.isNotEmpty(execute) && ObjectUtil.isNotEmpty(execute.getData())) {
            Map<String, FinanceOverdueIntegration> id2bean = overdueIntegrations.stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), e -> e, (a, b) -> a));
            for (OverdueReportBatSaveRSP.OverdueReportBatBody e : execute.getData()) {
                FinanceOverdueIntegration financeOverdueIntegration = id2bean.get(e.getSourcebillno());
                if (ObjectUtil.isEmpty(financeOverdueIntegration)) {
                    continue;
                }
                if (e.getSuccess()) {
                    financeOverdueIntegration.setBillno(e.getBillno());
                    financeOverdueIntegration.setRecordStatus(OverdueRecordStatueEnum.REPORT_SUCCESS.name());
                    success = success + 1;
                } else {
                    financeOverdueIntegration.setRecordStatus(OverdueRecordStatueEnum.REPORT_FAIL.name());
                    messages.add(financeOverdueIntegration.getCollectionCode() + ":" + e.getMsg());
                    failure = failure + 1;
                }
            }
            this.updateBatchById(id2bean.values());
        } else {
                overdueIntegrations.forEach(e -> e.setRecordStatus(OverdueRecordStatueEnum.REPORT_FAIL.name()));
                this.updateBatchById(overdueIntegrations);
        }
        rsp.setSuccess(success);
        rsp.setFailure(failure);
        rsp.setMessage(messages);
        return rsp;
    }

    public void submitCheck(FinanceOverdueVersionSubmitREQ req) {
        if(this.count(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, req.getReportId())
                .in(FinanceOverdueIntegration::getApprovalStatus, ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name(), ProjProcessState.UNDER_APPROVAL.name())
                .in(CollectionUtil.isNotEmpty(req.getIntegrationRecordIds()), FinanceOverdueIntegration::getId, req.getIntegrationRecordIds())) > 0) {
            throw new MithrasException("存在审批中数据，不可提交");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FinanceOverdueIntegrationModifyREQ req) {
        FinanceOverdueIntegration originalInfo = financeOverdueIntegrationMapper.selectById(Long.parseLong(req.getId()));
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(StrUtil.equalsAny(originalInfo.getApprovalStatus(), ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name(), ProjProcessState.UNDER_APPROVAL.name())) {
            throw new MithrasException("审批中不可修改");
        }
        FinanceOverdueIntegration info = BeanUtil.copyProperties(req, FinanceOverdueIntegration.class);
        info.setRecordStatus(OverdueRecordStatueEnum.NOT_REPORT.name());
        info.setApprovalStatus(ProjProcessState.CHANGING_UN_SUBMIT.name());
        financeOverdueIntegrationMapper.updateById(info);
    }

    public Page<FinanceOverdueIntegration> list(FinanceOverdueIntegrationListREQ req) {
        List<Long> ids = null;
        if (ObjectUtil.isNotEmpty(req.getProcessInstanceId())) {
            ids = financeOverdueVersionRelationService.listByProcessInstanceId(req.getProcessInstanceId(), OverdueRecordTypeEnum.INTEGRATION.name()).stream().map(FinanceOverdueVersionRelation::getRecordId).collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(req.getVersion())) {
            return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                    .eq(FinanceOverdueIntegration::getOverdueReportId, req.getOverdueReportId())
                    .eq(ObjectUtil.isNotEmpty(req.getCollectionCode()), FinanceOverdueIntegration::getCollectionCode, req.getCollectionCode())
                    .eq(ObjectUtil.isNotEmpty(req.getRecordStatus()), FinanceOverdueIntegration::getRecordStatus, req.getRecordStatus())
                    .like(ObjectUtil.isNotEmpty(req.getContractCode()), FinanceOverdueIntegration::getContractCode, req.getContractCode())
                    .like(ObjectUtil.isNotEmpty(req.getClientName()), FinanceOverdueIntegration::getClientName, req.getClientName())
                    .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueIntegration::getId, ids)
                    .between(ObjectUtil.isNotEmpty(req.getRecordStartDateFrom()), FinanceOverdueIntegration::getRecordStartDate, req.getRecordStartDateFrom(), req.getRecordStartDateTo())
                    .orderByDesc(FinanceOverdueIntegration::getContractId)
                    .orderByDesc(FinanceOverdueIntegration::getRecordStartDate));
        } else {
            return financeOverdueIntegrationLibService.pageByVersion(req, ids);
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FinanceOverdueIntegrationRemoveREQ req) {
        FinanceOverdueIntegration originalInfo = financeOverdueIntegrationMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        financeOverdueIntegrationMapper.deleteById(req.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(Long overdueReportId, List<Long> ids, String approvalStatus, String recordStatus) {
        List<FinanceOverdueIntegration> list = this.list(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, overdueReportId)
                .in(ObjectUtil.isNotEmpty(ids), FinanceOverdueIntegration::getId, ids));
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        list.forEach(e -> {
            if (ProjProcessState.APPROVAL_PASS.name().equals(approvalStatus)) {
                e.setApprovalStatus(ProjProcessState.NEW_UNDER_APPROVAL.name().equals(e.getApprovalStatus()) ? ProjProcessState.NEW_APPROVAL_PASS.name() : ProjProcessState.CHANGING_APPROVAL_PASS.name());
            } else {
                e.setApprovalStatus(ProjProcessState.NEW_UNDER_APPROVAL.name().equals(e.getApprovalStatus()) ? ProjProcessState.NEW_REJECT.name() : ProjProcessState.CHANGE_REJECT.name());
            }
        });
        this.updateBatchById(list);
    }

    public List<FinanceOverdueIntegration> listByReportId(Long overdueReportId) {
        return this.list(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .eq(FinanceOverdueIntegration::getOverdueReportId, overdueReportId));
    }

}