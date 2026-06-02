package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.pledge.PledgeListREQ;
import cn.zswltech.mithras.dto.report.pledge.PledgeListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.*;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrPledgeDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrPledge;
import cn.zswltech.mithras.report.mapper.fullsnap.CrPledgeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrPledgeProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrPledgeDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrPledgeService;
import cn.zswltech.mithras.report.service.fullsnap.CrPledgeFullSnapService;
import cn.zswltech.mithras.report.service.procsnap.CrPledgeProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-质押表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:18 PM
 */
@Service
@Order(-1)
public class CrPledgeAggService implements ICrProcessWorker<CrPledgeDraft, PledgeListREQ> {

    @Resource
    private CrPledgeService crPledgeService;
    @Autowired
    private CrPledgeDraftService crPledgeDraftService;
    @Resource
    private CrPledgeProcSnapService crPledgeProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrPledgeFullSnapMapper crPledgeFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private BusinessDataRepository businessDataRepository;

    @Override
    public PageR<Map<String, DiffValue>> list(PledgeListREQ req) {
        ReportBizUtil.checkListREQ(req);
        QueryChannel channel = QueryChannel.of(req.getChannel());
        switch (channel) {
            case EDIT:
                return listByEdit(req);
            case PROC:
                return listByProc(req);
            case PROC_BATCH:
                return listByBatchIncre(req);
            case EFFECT:
                return listByEffect(req);
            default:
                break;
        }
        return PageR.of(new ArrayList<>(), 0);
    }

    @Override
    public String converter(String fieldName, DiffValue value) {
        switch (fieldName) {
            case "applyPaymentAmount":
            case "assessedValue":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            case "maxFlag":
                return Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(String.valueOf(value.getValue()))).map(YesOrNoNumberEnum::getChinese).orElse(YesOrNoNumberEnum.NO.getChinese());
//            case "pledgeIdType":
//                return businessDataRepository.getCertTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "pledgeType":
                return Optional.ofNullable(CrClientTypeEnum.getByValue(String.valueOf(value.getValue()))).map(CrClientTypeEnum::getDisplay).orElse("");
            case "type":
                return PledgeDataTypeEnum.getByValue(String.valueOf(value.getValue())).getDisplay();
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(PledgeListREQ req) {
        Page<CrPledgeDraft> dataPage = crPledgeDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrPledgeDraft>lambdaQuery()
                .eq(CrPledgeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrPledgeDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrPledgeDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrPledgeDraft::getPledgeName, req.getClientName()));
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrPledgeDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrPledgeDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrPledgeDraft::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<PledgeListRSP> rspList = records.stream().map(d -> {
            PledgeListRSP pledge = BeanUtil.copyProperties(d, PledgeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            pledge.setContractCode(info.getContractCode());
            return pledge;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(PledgeListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.PLEDGE, PledgeListRSP.class, batchNo, isShow);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(PledgeListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrPledgeProcSnap> dataPage = crPledgeProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrPledgeProcSnap>lambdaQuery()
                    .eq(CrPledgeProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrPledgeProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrPledgeProcSnap::getPledgeName, req.getClientName())
            );
            List<CrPledgeProcSnap> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrPledgeProcSnap::getContractId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            List<PledgeListRSP> rspList = records.stream().map(d -> {
                PledgeListRSP pledge = BeanUtil.copyProperties(d, PledgeListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                pledge.setContractCode(info.getContractCode());
                return pledge;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(PledgeListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.PLEDGE, PledgeListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrPledgeDraft> dataPage = crPledgeDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrPledgeDraft>lambdaQuery()
                            .eq(CrPledgeDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                            .eq(CrPledgeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                            .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrPledgeDraft::getApprovalStatus, req.getApprovalStatus())
                            .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrPledgeDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                            .like(StringUtils.isNotBlank(req.getClientName()), CrPledgeDraft::getPledgeName, req.getClientName())
//                    .inSql(CrPledgeDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
            );
            return getMapPageR(dataPage, req.getProcBusinessKey(), null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(PledgeListREQ req) {
        Page<CrPledgeProcSnap> dataPage = crPledgeProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrPledgeProcSnap>lambdaQuery()
                .eq(CrPledgeProcSnap::getBatchId, req.getBatchId())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrPledgeProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrPledgeProcSnap::getPledgeName, req.getClientName())

        );
        List<CrPledgeProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrPledgeProcSnap::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<PledgeListRSP> rspList = records.stream().map(d -> {
            PledgeListRSP pledge = BeanUtil.copyProperties(d, PledgeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            pledge.setContractCode(info.getContractCode());
            return pledge;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(PledgeListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.PLEDGE, PledgeListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(PledgeListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        Page<CrPledge> dataPage = crPledgeService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrPledge>lambdaQuery()
                .eq(Objects.nonNull(paymentApplyCode), CrPledge::getPaymentApplyCode, paymentApplyCode)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrPledge::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrPledge::getPledgeName, req.getClientName())
        );
        List<CrPledge> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrPledge::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<PledgeListRSP> rspList = records.stream().map(d -> {
            PledgeListRSP pledge = BeanUtil.copyProperties(d, PledgeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            pledge.setContractCode(info.getContractCode());
            return pledge;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(PledgeListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.PLEDGE, PledgeListRSP.class);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    /**
     * 请注意该类需要在accountAggService之前执行，依赖AccountDraft数据的状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrPledgeDraft> draftList = crPledgeDraftService.getBaseMapper().selectList(Wrappers.<CrPledgeDraft>lambdaQuery()
                .eq(CrPledgeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrPledgeDraft::getProcBusinessKey, procBusinessKey)
        );

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrPledgeProcSnap> procSnapList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(d -> {
                    CrPledgeProcSnap procSnap = BeanUtil.copyProperties(d, CrPledgeProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(procSnapList)) {
            crPledgeProcSnapService.saveBatch(procSnapList);
        }
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            if (CollectionUtils.isNotEmpty(draftList)) {

                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.PLEDGE.name())
                        .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

                Map<String, CrModifyDataSnap> modifyDataSnapMap = new LinkedHashMap<>();
                if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                    modifyDataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
                }

                Map<String, CrModifyDataSnap> finalModifyDataSnapMap = modifyDataSnapMap;
                draftList.forEach(draft -> {
                    CrModifyDataSnap modifyDataSnap = finalModifyDataSnapMap.get(draft.getBusinessKey());
                    if (ObjectUtil.isNull(modifyDataSnap)) {
                        return;
                    }
                    CrPledgeDraft pledgeDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrPledgeDraft.class);
                    BeanUtil.copyProperties(pledgeDraft, draft, ReportConstants.IGNORE_ID);
                });

                List<CrPledge> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                        .map(d -> BeanUtil.copyProperties(d, CrPledge.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrPledge> needInsertList = new ArrayList<>();
                Map<String, CrPledge> existReportDataMap = crPledgeService.list(Wrappers.<CrPledge>lambdaQuery()
                                .in(CrPledge::getBusinessKey, draftList.stream().map(CrPledgeDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrPledge::getBusinessKey, Function.identity(), (a, b) -> a));
                for (CrPledge draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrPledge existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crPledgeService.updateById(draft2Formal);
                        }
                    }
                }
                crPledgeService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crPledgeFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag())).map(CrPledgeDraft::getId).collect(Collectors.toList());
        List<Long> notReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.NO.getCode(), d.getReportFlag())).map(CrPledgeDraft::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 审批通过才处理成已报送
            crPledgeDraftService.lambdaUpdate()
                    .set(processPass, CrPledgeDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrPledgeDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrPledgeDraft::getProcBusinessKey, null)
                    .in(CrPledgeDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crPledgeDraftService.lambdaUpdate()
                    .set(CrPledgeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrPledgeDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrPledgeDraft::getProcBusinessKey, null)
                    .in(CrPledgeDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crPledgeDraftService.lambdaUpdate()
                .eq(CrPledgeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrPledgeDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrPledgeDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.PLEDGE.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.PLEDGE;
    }

    @Override
    public int countInProcessData() {
        return crPledgeDraftService.count(Wrappers.<CrPledgeDraft>lambdaQuery()
                .select(CrPledgeDraft::getId)
                .eq(CrPledgeDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrPledgeDraft> getServiceInstance() {
        return crPledgeDraftService;
    }

}
