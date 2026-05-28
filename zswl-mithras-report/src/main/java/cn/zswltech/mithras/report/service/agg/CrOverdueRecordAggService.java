package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListRSP;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordModifyREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordRemoveREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import cn.zswltech.mithras.report.mapper.fullsnap.CrOverdueRecordFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrOverdueRecordProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrOverdueRecordDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrOverdueRecordService;
import cn.zswltech.mithras.report.service.procsnap.CrOverdueRecordProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
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
 * 征信报送-逾期表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:19 PM
 */
@Service
@Order(-1)
public class CrOverdueRecordAggService implements ICrProcessWorker<CrOverdueRecordDraft, OverdueRecordListREQ> {

    @Resource
    private CrOverdueRecordDraftService crOverdueRecordDraftService;
    @Resource
    private CrOverdueRecordService crOverdueRecordService;
    @Resource
    private CrOverdueRecordProcSnapService crOverdueRecordProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrOverdueRecordFullSnapMapper crOverdueRecordFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void modify(OverdueRecordModifyREQ req) {
        CrOverdueRecordDraft existData = crOverdueRecordDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrOverdueRecordDraft baseModel = BeanUtil.copyProperties(existData, CrOverdueRecordDraft.class, ReportConstants.IGNORE_ID);
        baseModel.setOverduePrincipal(req.getOverduePrincipal());
        baseModel.setOverdueDay(req.getOverdueDay());
        baseModel.setOverdueTotal(req.getOverdueTotal());
        baseModel.setOverdueChangeDate(req.getOverdueChangeDate());
        baseModel.setProcBusinessKey(existData.getProcBusinessKey());

        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
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
                .oldDataMap(JSONUtil.toJsonStr(existData))
                .dataMap(JSONUtil.toJsonStr(baseModel))
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .tableType(TableTypeEnum.OVERDUE.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void remove(OverdueRecordRemoveREQ req) {
        CrOverdueRecordDraft existData = crOverdueRecordDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
                    .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey()));
            if (ObjectUtil.isNull(modifyDataSnap)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            existData = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrOverdueRecordDraft.class);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许删除");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再删除！");
        }
        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
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
                .label(DataShowTypeEnum.REMOVE.name())
                .businessKey(existData.getBusinessKey())
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .tableType(TableTypeEnum.OVERDUE.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .reason(req.getReason())
                .versionType(VersionTypeConstants.NORMAL)
                .build());
    }

    @Override
    public PageR<Map<String, DiffValue>> list(OverdueRecordListREQ req) {
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
    public String converter(@NotNull String fieldName, DiffValue value) {
        switch (fieldName) {
            case "overduePrincipal":
            case "overdueTotal":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(OverdueRecordListREQ req) {
        Page<CrOverdueRecordDraft> dataPage = crOverdueRecordDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrOverdueRecordDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrOverdueRecordDraft::getPaymentApplyCode, req.getPaymentApplyCode()));
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrOverdueRecordDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrOverdueRecordDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrOverdueRecordDraft::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrOverdueRecordDraft::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<OverdueRecordListRSP> rspList = records.stream().map(d -> {
            OverdueRecordListRSP overdueRecord = BeanUtil.copyProperties(d, OverdueRecordListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            overdueRecord.setContractCode(info.getContractCode());
            overdueRecord.setBusinessKey(d.getBusinessKey());
            overdueRecord.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return overdueRecord;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(OverdueRecordListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.OVERDUE, OverdueRecordListRSP.class, batchNo, isShow);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(OverdueRecordListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrOverdueRecordProcSnap> dataPage = crOverdueRecordProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrOverdueRecordProcSnap>lambdaQuery()
                    .eq(CrOverdueRecordProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrOverdueRecordProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
            );
            List<CrOverdueRecordProcSnap> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrOverdueRecordProcSnap::getContractId).collect(Collectors.toSet());
            Set<Long> paymentIds = records.stream().map(CrOverdueRecordProcSnap::getPaymentId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
            Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
            List<OverdueRecordListRSP> rspList = records.stream().map(d -> {
                OverdueRecordListRSP overdueRecord = BeanUtil.copyProperties(d, OverdueRecordListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                overdueRecord.setContractCode(info.getContractCode());
                overdueRecord.setBusinessKey(d.getBusinessKey());
                overdueRecord.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
                return overdueRecord;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(OverdueRecordListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.OVERDUE, OverdueRecordListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrOverdueRecordDraft> dataPage = crOverdueRecordDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                    .eq(CrOverdueRecordDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrOverdueRecordDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrOverdueRecordDraft::getPaymentApplyCode, req.getPaymentApplyCode())
//                    .inSql(CrOverdueRecordDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
            );
            return getMapPageR(dataPage, req.getProcBusinessKey(), null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(OverdueRecordListREQ req) {
        Page<CrOverdueRecordProcSnap> dataPage = crOverdueRecordProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrOverdueRecordProcSnap>lambdaQuery()
                .eq(CrOverdueRecordProcSnap::getBatchId, req.getBatchId())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrOverdueRecordProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())

        );
        List<CrOverdueRecordProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrOverdueRecordProcSnap::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrOverdueRecordProcSnap::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<OverdueRecordListRSP> rspList = records.stream().map(d -> {
            OverdueRecordListRSP overdueRecord = BeanUtil.copyProperties(d, OverdueRecordListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            overdueRecord.setContractCode(info.getContractCode());
            overdueRecord.setBusinessKey(d.getBusinessKey());
            overdueRecord.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return overdueRecord;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(OverdueRecordListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.OVERDUE, OverdueRecordListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(OverdueRecordListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        Page<CrOverdueRecord> dataPage = crOverdueRecordService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrOverdueRecord>lambdaQuery()
                .eq(Objects.nonNull(paymentApplyCode), CrOverdueRecord::getPaymentApplyCode, paymentApplyCode)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrOverdueRecord::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        List<CrOverdueRecord> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrOverdueRecord::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrOverdueRecord::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<OverdueRecordListRSP> rspList = records.stream().map(d -> {
            OverdueRecordListRSP overdueRecord = BeanUtil.copyProperties(d, OverdueRecordListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            overdueRecord.setContractCode(info.getContractCode());
            overdueRecord.setBusinessKey(d.getBusinessKey());
            overdueRecord.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return overdueRecord;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(OverdueRecordListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.OVERDUE, OverdueRecordListRSP.class);
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
        List<CrOverdueRecordDraft> draftList = crOverdueRecordDraftService.getBaseMapper().selectList(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrOverdueRecordDraft::getProcBusinessKey, procBusinessKey)
        );

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrOverdueRecordProcSnap> procSnapList = CollectionUtils.isEmpty(draftList) ? new ArrayList<>() : draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(d -> {
                    CrOverdueRecordProcSnap procSnap = BeanUtil.copyProperties(d, CrOverdueRecordProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(procSnapList)) {
            crOverdueRecordProcSnapService.saveBatch(procSnapList);
        }

        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
                        .eq(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.YES.getCode())
                        .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

                Map<String, CrModifyDataSnap> modifyDataSnapMap = new LinkedHashMap<>();
                if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                    modifyDataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
                }

                Map<String, CrModifyDataSnap> finalModifyDataSnapMap = modifyDataSnapMap;
                draftList.forEach(draft -> {
                    CrModifyDataSnap modifyDataSnap = finalModifyDataSnapMap.get(draft.getBusinessKey());
                    if (ObjectUtil.isNull(modifyDataSnap) || CharSequenceUtil.isBlank(modifyDataSnap.getDataMap())) {
                        return;
                    }
                    CrOverdueRecordDraft overdueRecordDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrOverdueRecordDraft.class);
                    BeanUtil.copyProperties(overdueRecordDraft, draft, ReportConstants.IGNORE_ID);
                });

                List<CrOverdueRecord> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                        .map(d -> BeanUtil.copyProperties(d, CrOverdueRecord.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrOverdueRecord> needInsertList = new ArrayList<>();
                Map<String, CrOverdueRecord> existReportDataMap = crOverdueRecordService.list(Wrappers.<CrOverdueRecord>lambdaQuery()
                                .in(CrOverdueRecord::getBusinessKey, draftList.stream().map(CrOverdueRecordDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrOverdueRecord::getBusinessKey, Function.identity(), (a, b) -> a));

                for (CrOverdueRecord draft2Formal : draft2FormalList) {
                    CrModifyDataSnap snap = finalModifyDataSnapMap.get(draft2Formal.getBusinessKey());
                    if (!finalModifyDataSnapMap.isEmpty() && Objects.nonNull(snap) && DataShowTypeEnum.REMOVE.name().equals(snap.getLabel())) {
                        //啥也不干
                        continue;
                    }
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrOverdueRecord existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crOverdueRecordService.updateById(draft2Formal);
                        }
                    }
                }
                crOverdueRecordService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crOverdueRecordFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null), Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag())).map(CrOverdueRecordDraft::getId).collect(Collectors.toList());
        List<Long> notReportDraftIdList = draftList.stream().filter(d -> !Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag())).map(CrOverdueRecordDraft::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 已报送数据处理
            // 审批通过才处理成已报送
            crOverdueRecordDraftService.lambdaUpdate()
                    .set(processPass, CrOverdueRecordDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrOverdueRecordDraft::getProcBusinessKey, null)
                    .in(CrOverdueRecordDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crOverdueRecordDraftService.lambdaUpdate()
                    .set(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrOverdueRecordDraft::getProcBusinessKey, null)
                    .in(CrOverdueRecordDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crOverdueRecordDraftService.lambdaUpdate()
                .eq(CrOverdueRecordDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrOverdueRecordDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.OVERDUE_RECORD;
    }

    @Override
    public int countInProcessData() {
        return crOverdueRecordDraftService.count(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .select(CrOverdueRecordDraft::getId)
                .eq(CrOverdueRecordDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrOverdueRecordDraft> getServiceInstance() {
        return crOverdueRecordDraftService;
    }

    public void cancelRemove(OverdueRecordRemoveREQ req) {
        CrOverdueRecordDraft existData = crOverdueRecordDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.OVERDUE.name())
                .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));

        if (ObjectUtil.isNull(modifyDataSnap)) {
            throw new MithrasException("非法操作， 该数据未进行过删除操作");
        }

        String version;
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }

        SpringContextHolder.getBean(CrModifyDataSnapService.class).lambdaUpdate()
                .eq(CrModifyDataSnap::getId, modifyDataSnap.getId())
                .set(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.NO.getCode())
                .update();

        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.CANCEL_REMOVE.name())
                .businessKey(existData.getBusinessKey())
                .isTakeEffect(YesOrNoNumberEnum.NO.getCode())
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .tableType(TableTypeEnum.OVERDUE.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .build());
    }
}
