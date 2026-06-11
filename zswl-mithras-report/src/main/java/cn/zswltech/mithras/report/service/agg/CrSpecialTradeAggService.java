package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.ReportChangeREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListRSP;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum;
import cn.zswltech.mithras.report.enums.biz.SpecialTradeTypeEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrSpecialTradeDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrSpecialTrade;
import cn.zswltech.mithras.report.mapper.fullsnap.CrSpecialTradeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrSpecialTradeProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrSpecialTradeDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrSpecialTradeService;
import cn.zswltech.mithras.report.service.procsnap.CrSpecialTradeProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
 * 征信报送-特定交易表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:18 PM
 */
@Service
@Order(-1)
public class CrSpecialTradeAggService implements ICrProcessWorker<CrSpecialTradeDraft, SpecialTradeListREQ> {

    @Resource
    private CrSpecialTradeDraftService crSpecialTradeDraftService;
    @Resource
    private CrSpecialTradeService crSpecialTradeService;
    @Resource
    private CrSpecialTradeProcSnapService crSpecialTradeProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private CrSpecialTradeFullSnapMapper crSpecialTradeFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void reportChange(ReportChangeREQ req) {
        CrSpecialTradeDraft existData = crSpecialTradeDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }

        crSpecialTradeDraftService.lambdaUpdate()
                .eq(CrBaseModel::getId, req.getId())
                .set(CrSpecialTradeDraft::getReportFlag, req.getReportFlag())
                .update();
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void modify(SpecialTradeModifyREQ req) {
        CrSpecialTradeDraft existData = crSpecialTradeDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrSpecialTradeDraft baseModel = BeanUtil.copyProperties(existData, CrSpecialTradeDraft.class, ReportConstants.IGNORE_ID);
        baseModel.setTradeType(req.getTradeType());
        baseModel.setTradeDate(req.getTradeDate());
        baseModel.setTradeAmount(req.getTradeAmount());
        baseModel.setChangeMonthCount(req.getChangeMonthCount());
        baseModel.setProcBusinessKey(existData.getProcBusinessKey());

        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getId())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.SPECIAL.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        String version;
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            modifyDataSnapMapper.updateById(CrModifyDataSnap.builder()
                    .id(modifyDataSnap.getId())
                    .isTakeEffect(YesOrNoNumberEnum.NO.getCode())
                    .build());
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }
        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.MODIFY.name())
                .businessKey(existData.getBusinessKey())
                .dataMap(JSONUtil.toJsonStr(baseModel))
                .oldDataMap(JSONUtil.toJsonStr(existData))
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .tableType(TableTypeEnum.SPECIAL.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

    @Override
    public PageR<Map<String, DiffValue>> list(SpecialTradeListREQ req) {
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
            case "tradeAmount":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            case "tradeType":
                return Optional.ofNullable(SpecialTradeTypeEnum.getByValue(String.valueOf(value.getValue()))).map(SpecialTradeTypeEnum::getDisplay).orElse("");
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(SpecialTradeListREQ req) {
        Page<CrSpecialTradeDraft> dataPage = crSpecialTradeDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrSpecialTradeDraft>lambdaQuery()
                .eq(CrSpecialTradeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrSpecialTradeDraft::getApprovalStatus, req.getApprovalStatus())
                .eq(Objects.nonNull(req.getReportFlag()), CrSpecialTradeDraft::getReportFlag, req.getReportFlag())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrSpecialTradeDraft::getPaymentApplyCode, req.getPaymentApplyCode())
//                .inSql(CrSpecialTradeDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
        );
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrSpecialTradeDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrSpecialTradeDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrSpecialTradeDraft::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrSpecialTradeDraft::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<SpecialTradeListRSP> rspList = records.stream().map(d -> {
            SpecialTradeListRSP specialTrade = BeanUtil.copyProperties(d, SpecialTradeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            specialTrade.setContractCode(info.getContractCode());
            specialTrade.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return specialTrade;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(SpecialTradeListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.SPECIAL, SpecialTradeListRSP.class, batchNo, 1);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(SpecialTradeListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrSpecialTradeProcSnap> dataPage = crSpecialTradeProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrSpecialTradeProcSnap>lambdaQuery()
                    .eq(CrSpecialTradeProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(Objects.nonNull(req.getReportFlag()), CrSpecialTradeProcSnap::getReportFlag, req.getReportFlag())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrSpecialTradeProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
            );
            List<CrSpecialTradeProcSnap> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrSpecialTradeProcSnap::getContractId).collect(Collectors.toSet());
            Set<Long> paymentIds = records.stream().map(CrSpecialTradeProcSnap::getPaymentId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
            Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
            List<SpecialTradeListRSP> rspList = records.stream().map(d -> {
                SpecialTradeListRSP specialTrade = BeanUtil.copyProperties(d, SpecialTradeListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                specialTrade.setContractCode(info.getContractCode());
                specialTrade.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
                return specialTrade;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(SpecialTradeListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.SPECIAL, SpecialTradeListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrSpecialTradeDraft> dataPage = crSpecialTradeDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrSpecialTradeDraft>lambdaQuery()
                    .eq(CrSpecialTradeDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(CrSpecialTradeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(Objects.nonNull(req.getReportFlag()), CrSpecialTradeDraft::getReportFlag, req.getReportFlag())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrSpecialTradeDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrSpecialTradeDraft::getPaymentApplyCode, req.getPaymentApplyCode())
//                    .inSql(CrSpecialTradeDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
            );
            return getMapPageR(dataPage, req.getProcBusinessKey(), null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(SpecialTradeListREQ req) {
        Page<CrSpecialTradeProcSnap> dataPage = crSpecialTradeProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrSpecialTradeProcSnap>lambdaQuery()
                .eq(CrSpecialTradeProcSnap::getBatchId, req.getBatchId())
                .eq(CrSpecialTradeProcSnap::getReportFlag, 1)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrSpecialTradeProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        List<CrSpecialTradeProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrSpecialTradeProcSnap::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrSpecialTradeProcSnap::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<SpecialTradeListRSP> rspList = records.stream().map(d -> {
            SpecialTradeListRSP specialTrade = BeanUtil.copyProperties(d, SpecialTradeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            specialTrade.setContractCode(info.getContractCode());
            specialTrade.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return specialTrade;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(SpecialTradeListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.SPECIAL, SpecialTradeListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(SpecialTradeListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        Page<CrSpecialTrade> dataPage = crSpecialTradeService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrSpecialTrade>lambdaQuery()
                .eq(Objects.nonNull(paymentApplyCode), CrSpecialTrade::getPaymentApplyCode, paymentApplyCode)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrSpecialTrade::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        List<CrSpecialTrade> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrSpecialTrade::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrSpecialTrade::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<SpecialTradeListRSP> rspList = records.stream().map(d -> {
            SpecialTradeListRSP specialTrade = BeanUtil.copyProperties(d, SpecialTradeListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            specialTrade.setContractCode(info.getContractCode());
            specialTrade.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return specialTrade;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(SpecialTradeListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.SPECIAL, SpecialTradeListRSP.class);
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
        List<CrSpecialTradeDraft> draftList = crSpecialTradeDraftService.getBaseMapper().selectList(Wrappers.<CrSpecialTradeDraft>lambdaQuery()
                .eq(CrSpecialTradeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrSpecialTradeDraft::getProcBusinessKey, procBusinessKey)
        );

        // 找到编辑区账户表数据
        Map<String, CrAccountDraft> crAccountDraftMap = CollectionUtils.isEmpty(draftList) ? new HashMap<>()
                : crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, draftList.stream().map(CrSpecialTradeDraft::getPaymentId).collect(Collectors.toSet()))
        ).stream().collect(Collectors.toMap(CrAccountDraft::getPaymentApplyCode, Function.identity(), (a, b) -> a));

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrSpecialTradeProcSnap> procSnapList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(d -> {
                    CrSpecialTradeProcSnap procSnap = BeanUtil.copyProperties(d, CrSpecialTradeProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        crSpecialTradeProcSnapService.saveBatch(procSnapList);

        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.SPECIAL.name())
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
                    CrSpecialTradeDraft specialTradeDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrSpecialTradeDraft.class);
                    BeanUtil.copyProperties(specialTradeDraft, draft, ReportConstants.IGNORE_ID);
                });

                List<CrSpecialTrade> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()) && Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                                Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                        .map(CrAccountDraft::getReportFlag).orElse(0)))
                        .map(d -> BeanUtil.copyProperties(d, CrSpecialTrade.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrSpecialTrade> needInsertList = new ArrayList<>();
                Map<String, CrSpecialTrade> existReportDataMap = crSpecialTradeService.list(Wrappers.<CrSpecialTrade>lambdaQuery()
                                .in(CrSpecialTrade::getBusinessKey, draftList.stream().map(CrSpecialTradeDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrSpecialTrade::getBusinessKey, Function.identity(), (a, b) -> a));

                for (CrSpecialTrade draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrSpecialTrade existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crSpecialTradeService.updateById(draft2Formal);
                        }
                    }
                }
                crSpecialTradeService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crSpecialTradeFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null), Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()) && Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(CrSpecialTradeDraft::getId).collect(Collectors.toList());

        List<Long> notReportDraftIdList = draftList.stream()
                .filter(d -> !Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()) || !Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(CrSpecialTradeDraft::getId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 审批通过才处理成已报送
            crSpecialTradeDraftService.lambdaUpdate()
                    .set(processPass, CrSpecialTradeDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrSpecialTradeDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrSpecialTradeDraft::getProcBusinessKey, null)
                    .in(CrSpecialTradeDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crSpecialTradeDraftService.lambdaUpdate()
                    .set(CrSpecialTradeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrSpecialTradeDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrSpecialTradeDraft::getProcBusinessKey, null)
                    .in(CrSpecialTradeDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void submit(Long procBusinessKey, String batchNo) {
        crSpecialTradeDraftService.lambdaUpdate()
                .eq(CrSpecialTradeDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrSpecialTradeDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrSpecialTradeDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.SPECIAL.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.SPECIAL_TRADE;
    }

    @Override
    public int countInProcessData() {
        return crSpecialTradeDraftService.count(Wrappers.<CrSpecialTradeDraft>lambdaQuery()
                .select(CrSpecialTradeDraft::getId)
                .eq(CrSpecialTradeDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrSpecialTradeDraft> getServiceInstance() {
        return crSpecialTradeDraftService;
    }

}
