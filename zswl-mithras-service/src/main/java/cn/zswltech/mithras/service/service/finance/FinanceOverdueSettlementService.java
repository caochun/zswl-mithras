package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.finance.overdue.*;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordTypeEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueSettlementRelationEnum;
import cn.zswltech.mithras.service.mapper.finance.FinanceOverdueSettlementMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueVersionRelation;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.finance.FinanceOverdueSettlementLibService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.third.service.overduereport.handle.OverdueReportSetBatSaveHandle;
import cn.zswltech.mithras.third.service.overduereport.req.OverdueReportSetBatSaveReq;
import cn.zswltech.mithras.third.service.overduereport.rsp.OverdueReportSetBatSaveRSP;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 应收逾期结算表
* @author vico
* @date 2025-09-15
*/
@Service
@Slf4j
public class FinanceOverdueSettlementService extends ServiceImpl<FinanceOverdueSettlementMapper, FinanceOverdueSettlement> {

    @Resource
    private FinanceOverdueSettlementMapper financeOverdueSettlementMapper;

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinanceOverdueIntegrationService financeOverdueIntegrationService;
    @Resource
    private OverdueReportSetBatSaveHandle overdueReportSetBatSaveHandle;
    @Resource
    private FinanceOverdueVersionRelationService financeOverdueVersionRelationService;
    @Resource
    private FinanceOverdueSettlementLibService financeOverdueSettlementLibService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FinanceOverdueSettlementAddREQ req) {
        FinanceOverdueSettlement info = BeanUtil.copyProperties(req, FinanceOverdueSettlement.class);
        info.setApprovalStatus(ProjProcessState.UN_SUBMIT.name());
        info.setRecordStatus(OverdueRecordStatueEnum.NOT_REPORT.name());
        if(ObjectUtil.isNotEmpty(req.getContractId())) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            if(ObjectUtil.isEmpty(contractBaseInfo)) {
                throw new MithrasException("合同信息不存在");
            }
            info.setContractCode(contractBaseInfo.getContractCode());
            info.setProjName(contractBaseInfo.getProjName());
        }
        info.setClientName(id2NameService.clientId2NameSingle(req.getClientId()));
        financeOverdueSettlementMapper.insert(info);
    }

    /**
     * 计算某个月内发生的逾期还款记录
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void create(Long reportBaseId, LocalDate reportData) {
        if(ObjectUtil.isEmpty(reportBaseId) || ObjectUtil.isEmpty(reportData)) {
            throw new MithrasException("创建应收逾期集成失败, 逾期月份为空");
        }
        LocalDate firstDay = reportData.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = firstDay.plusMonths(1);
        List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoService.list(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .ne(CollectionRecordInfo::getCancelWriteOffFlag, YesOrNoNumberEnum.YES.getCode())
                .ge(CollectionRecordInfo::getCollectionDate, firstDay)
                .lt(CollectionRecordInfo::getCollectionDate, lastDay));
        if (ObjectUtil.isEmpty(collectionRecordInfos)) {
            log.info("FinanceOverdueSettlementService create not collectionRecordInfos, {}, {}", reportBaseId, reportData);
            return;
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.listByIds(collectionRecordInfos.stream().map(CollectionRecordInfo::getCollectionId).collect(Collectors.toList()));
        Map<Long, CollectionBaseInfo>  collectionId2Bean = collectionBaseInfos.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
        List<CollectionRecordInfo> overdueRecords= collectionRecordInfos.stream().filter(e -> {
            CollectionBaseInfo tempBaseInfo = collectionId2Bean.get(e.getCollectionId());
            return ObjectUtil.isNotEmpty(tempBaseInfo) && e.getCollectionDate().isAfter(tempBaseInfo.getPlanCollectionDate());
        }).filter(ObjectUtil::isNotEmpty).filter(e -> LongUtil.null2zero(e.getCollectionAmount()) > 0).collect(Collectors.toList());

        if (ObjectUtil.isEmpty(overdueRecords)) {
            log.info("FinanceOverdueSettlementService create not overdueRecords, {}, {}", reportBaseId, reportData);
            return;
        }
        List<Long> contractIds = collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList());
        Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));

        Map<Long, String> clientId2Name = id2NameService.clientId2Name(collectionBaseInfos.stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet()));

        //构建
        List<FinanceOverdueSettlement> financeOverdueSettlementList = new ArrayList<>();
        overdueRecords.forEach(e -> {
            FinanceOverdueSettlement info = new FinanceOverdueSettlement();
            CollectionBaseInfo collectionBaseInfo = collectionId2Bean.get(e.getCollectionId());
            if (ObjectUtil.isEmpty(collectionBaseInfo) || !CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) || ObjectUtil.isEmpty(collectionBaseInfo.getPhase()) || collectionBaseInfo.getPhase() < 1) {
                return;
            }
            info.setId(IdUtil.getSnowflakeNextId());
            info.setOverdueReportId(reportBaseId);
            info.setCollectionId(e.getCollectionId());
            info.setCollectionRecordId(e.getId());
            info.setRecordStatus(OverdueRecordStatueEnum.NOT_REPORT.name());
            if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                ContractBaseInfo contractBaseInfo = contractId2Bean.get(collectionBaseInfo.getContractId());
                info.setCollectionCode(collectionBaseInfo.getCode());
                info.setContractId(collectionBaseInfo.getContractId());
                info.setClientId(collectionBaseInfo.getClientId());
                info.setClientName(clientId2Name.get(collectionBaseInfo.getClientId()));
                info.setRecordBillDate(collectionBaseInfo.getPlanCollectionDate());
                info.setSettlementDate(e.getCollectionDate());
                info.setVoucherAccountDate(e.getCollectionDate());
                info.setSettlementRelation(OverdueSettlementRelationEnum.AR_LIQ_SETTLE.name());
                info.setSettlementAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(e.getCollectionAmount()).toString()));
                info.setApprovalStatus(ProjProcessState.UN_SUBMIT.name());
                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    info.setContractCode(contractBaseInfo.getContractCode());
                    info.setProjName(contractBaseInfo.getProjName());
                }
            }
            financeOverdueSettlementList.add(info);
        });
        if(ObjectUtil.isNotEmpty(financeOverdueSettlementList)) {
            this.saveBatch(financeOverdueSettlementList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FinanceOverdueSettlementModifyREQ req) {
        FinanceOverdueSettlement originalInfo = financeOverdueSettlementMapper.selectById(Long.parseLong(req.getId()));
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(StrUtil.equalsAny(originalInfo.getApprovalStatus(), ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name(), ProjProcessState.UNDER_APPROVAL.name())) {
            throw new MithrasException("审批中不可修改");
        }
        FinanceOverdueSettlement info = BeanUtil.copyProperties(req, FinanceOverdueSettlement.class);
        info.setApprovalStatus(ProjProcessState.CHANGING_UN_SUBMIT.name());
        info.setRecordStatus(OverdueRecordStatueEnum.NOT_REPORT.name());
        financeOverdueSettlementMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(Long overdueReportId, List<Long> ids, String approvalStatus, String recordStatus) {

        List<FinanceOverdueSettlement> list = this.list(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, overdueReportId)
                .in(ObjectUtil.isNotEmpty(ids), FinanceOverdueSettlement::getId, ids));
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

    public Page<FinanceOverdueSettlement> list(FinanceOverdueSettlementListREQ req) {
        List<Long> ids = null;
        if (ObjectUtil.isNotEmpty(req.getProcessInstanceId())) {
            ids = financeOverdueVersionRelationService.listByProcessInstanceId(req.getProcessInstanceId(), OverdueRecordTypeEnum.SETTLEMENT.name()).stream().map(FinanceOverdueVersionRelation::getRecordId).collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(req.getVersion())) {
            return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                    .eq(FinanceOverdueSettlement::getOverdueReportId, req.getOverdueReportId())
                    .like(ObjectUtil.isNotEmpty(req.getContractCode()), FinanceOverdueSettlement::getContractCode, req.getContractCode())
                    .like(ObjectUtil.isNotEmpty(req.getClientName()), FinanceOverdueSettlement::getClientName, req.getClientName())
                    .like(ObjectUtil.isNotEmpty(req.getCollectionCode()), FinanceOverdueSettlement::getCollectionCode, req.getCollectionCode())
                    .eq(ObjectUtil.isNotEmpty(req.getRecordStatus()), FinanceOverdueSettlement::getRecordStatus, req.getRecordStatus())
                    .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueSettlement::getId, ids)
                    .between(ObjectUtil.isNotEmpty(req.getRecordBillDateFrom()), FinanceOverdueSettlement::getRecordBillDate, req.getRecordBillDateFrom(), req.getRecordBillDateTo())
                    .between(ObjectUtil.isNotEmpty(req.getSettlementDateFrom()), FinanceOverdueSettlement::getSettlementDate, req.getSettlementDateFrom(), req.getSettlementDateTo())
            .orderByDesc(FinanceOverdueSettlement::getContractCode)
            .orderByDesc(FinanceOverdueSettlement::getSettlementDate));
        } else {
            return financeOverdueSettlementLibService.pageByVersion(req, ids);
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FinanceOverdueSettlementRemoveREQ req) {
        FinanceOverdueSettlement originalInfo = financeOverdueSettlementMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        financeOverdueSettlementMapper.deleteById(req.getId());
    }


    @Transactional(rollbackFor = Throwable.class)
    public FinanceOverdueIntegrationPushRSP push(FinanceOverdueIntegrationPushREQ req) {
        List<String> messages = new ArrayList<>();
        int success = 0;
        int failure = 0;
        FinanceOverdueIntegrationPushRSP rsp = new FinanceOverdueIntegrationPushRSP();
        List<Long> ids = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(req.getIds())) {
            ids = req.getIds().stream().map(Long::parseLong).collect(Collectors.toList());
        }
        //先检查
        List<FinanceOverdueSettlement> overdueSettlements = this.list(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, req.getReportBaseId())
                .in(CollectionUtil.isEmpty(ids), FinanceOverdueSettlement::getApprovalStatus, ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name())
                .ne(FinanceOverdueSettlement::getRecordStatus, OverdueRecordStatueEnum.REPORT_SUCCESS.name())
                .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueSettlement::getId, ids));
        overdueSettlements.forEach(e -> {
            if (!StrUtil.equalsAny(e.getApprovalStatus(), ProjProcessState.APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.NEW_APPROVAL_PASS.name())){
                throw new MithrasException(e.getCollectionCode() + "单据未审批通过，请提交审批后推送");
            }
        });
        if (CollectionUtil.isEmpty(overdueSettlements)) {
            rsp.setCount(0);
            return rsp;
        }
        //构建推送参数
        List<OverdueReportSetBatSaveReq> pushReq = new ArrayList<>();
        DateTimeFormatter dayFormatter= DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        Map<Long, FinanceOverdueIntegration> collection2IntegrationMap = financeOverdueIntegrationService.list(Wrappers.<FinanceOverdueIntegration>lambdaQuery()
                .in(FinanceOverdueIntegration::getCollectionId, overdueSettlements.stream().map(FinanceOverdueSettlement::getCollectionId).collect(Collectors.toSet())))
                .stream().collect(Collectors.toMap(FinanceOverdueIntegration::getCollectionId, e -> e, (a, b) -> b));


        for (FinanceOverdueSettlement overdueSettlement : overdueSettlements) {
            OverdueReportSetBatSaveReq bsr = new OverdueReportSetBatSaveReq();
            FinanceOverdueIntegration financeOverdueIntegration = collection2IntegrationMap.get(overdueSettlement.getCollectionId());

            bsr.setCico_origin_no(overdueSettlement.getId().toString());
            bsr.setCico_set_date(overdueSettlement.getSettlementDate() == null ? null : overdueSettlement.getSettlementDate().format(dayFormatter));
            bsr.setCico_doc_date(overdueSettlement.getVoucherAccountDate() == null ? null : overdueSettlement.getVoucherAccountDate().format(dayFormatter));
            bsr.setCico_set_amount(overdueSettlement.getSettlementAmount());
            bsr.setCico_set_amount_f(overdueSettlement.getSettlementAmount());
            bsr.setCico_set_relation(overdueSettlement.getSettlementRelation());
            bsr.setCico_account_org_number(FinancialConstants.RZZL_CODE);
            if (ObjectUtil.isNotEmpty(financeOverdueIntegration)) {
                bsr.setCico_ovedue_no(financeOverdueIntegration.getBillno());
                bsr.setCico_ovedue_origin_no(financeOverdueIntegration.getId().toString());
            }
            pushReq.add(bsr);
        }
        rsp.setCount(pushReq.size());
        //发送请求
        OverdueReportSetBatSaveRSP execute = overdueReportSetBatSaveHandle.execute(pushReq);
        if (ObjectUtil.isNotEmpty(execute) && ObjectUtil.isNotEmpty(execute.getData())) {
            Map<String, FinanceOverdueSettlement> id2bean = overdueSettlements.stream().collect(Collectors.toMap(e -> e.getId().toString(), e -> e, (a, b) -> a));
            for (OverdueReportSetBatSaveRSP.OverdueReportSetBatBody e : execute.getData()) {
                FinanceOverdueSettlement financeOverdueIntegration = id2bean.get(e.getSourcebillno());
                if (ObjectUtil.isEmpty(financeOverdueIntegration)) {
                    continue;
                }
                if (e.getSuccess()) {
                    financeOverdueIntegration.setBillno(e.getBillno());
                    financeOverdueIntegration.setRecordStatus(OverdueRecordStatueEnum.REPORT_SUCCESS.name());
                    success = success + 1;
                } else {
                    financeOverdueIntegration.setRecordStatus(OverdueRecordStatueEnum.REPORT_FAIL.name());
                    failure = failure + 1;
                    messages.add(financeOverdueIntegration.getCollectionCode() + e.getMsg());
                }
            }
            this.updateBatchById(id2bean.values());
        } else {
            overdueSettlements.forEach(e -> e.setRecordStatus(OverdueRecordStatueEnum.REPORT_FAIL.name()));
            this.updateBatchById(overdueSettlements);
        }
        rsp.setMessage(messages);
        rsp.setSuccess(success);
        rsp.setFailure(failure);
        return rsp;
    }

    public void submitCheck(FinanceOverdueVersionSubmitREQ req) {
        if(this.count(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, req.getReportId())
                .in(FinanceOverdueSettlement::getApprovalStatus, ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name(), ProjProcessState.UNDER_APPROVAL.name())
                .in(CollectionUtil.isNotEmpty(req.getSettlementRecordIds()), FinanceOverdueSettlement::getId, req.getSettlementRecordIds())) > 0) {
            throw new MithrasException("存在审批中数据，不可提交");
        }
    }

    public List<FinanceOverdueSettlementContractRelationRsp> contractRelation(FinanceOverdueSettlementContractRelationREQ req) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByClients(Collections.singletonList(req.getClientId()));
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return null;
        }
        Map<Long, List<CollectionBaseInfo>> contractId2Collections = collectionBaseInfoService.listByContractIds(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        List<FinanceOverdueSettlementContractRelationRsp> rsps = new ArrayList<>();
        contractBaseInfos.forEach(contractBaseInfo -> {
            FinanceOverdueSettlementContractRelationRsp rsp = new FinanceOverdueSettlementContractRelationRsp();
            rsp.setContractId(contractBaseInfo.getId());
            rsp.setContractCode(contractBaseInfo.getContractCode());
            List<CollectionBaseInfo> collectionBaseInfos = contractId2Collections.get(contractBaseInfo.getId());
            if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
                List<FinanceOverdueSettlementContractRelationRsp.SettlementContractRelationFlowItem> items = new ArrayList<>();
                collectionBaseInfos.forEach(e -> {
                    FinanceOverdueSettlementContractRelationRsp.SettlementContractRelationFlowItem ib = new FinanceOverdueSettlementContractRelationRsp.SettlementContractRelationFlowItem();
                    ib.setFlowId(e.getId());
                    ib.setFlowCode(e.getCode());
                    items.add(ib);
                });
                rsp.setFlowItem(items);
            }
            rsps.add(rsp);
        });
        return rsps;
    }

    public List<FinanceOverdueSettlement> listByReportId(Long overdueReportId) {
        return this.list(Wrappers.<FinanceOverdueSettlement>lambdaQuery()
                .eq(FinanceOverdueSettlement::getOverdueReportId, overdueReportId));
    }

}