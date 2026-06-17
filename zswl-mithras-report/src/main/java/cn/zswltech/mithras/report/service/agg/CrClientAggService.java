package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.ReportListBaseREQ;
import cn.zswltech.mithras.dto.report.client.ClientListREQ;
import cn.zswltech.mithras.dto.report.client.ClientListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.CrClientTypeEnum;
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
import cn.zswltech.mithras.report.mapper.draft.model.CrClientDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrClient;
import cn.zswltech.mithras.report.mapper.fullsnap.CrClientFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrClientProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrClientDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrClientService;
import cn.zswltech.mithras.report.service.procsnap.CrClientProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.customer.enums.OrgScaleType;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.LongUtil;
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
 * 征信报送-客户表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:18 PM
 */
@Service
@Order(-1)
public class CrClientAggService implements ICrProcessWorker<CrClientDraft, ClientListREQ> {

    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    private CrClientDraftService crClientDraftService;
    @Resource
    private CrClientProcSnapService crClientProcSnapService;
    @Resource
    private CrClientService crClientService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CrClientFullSnapMapper crClientFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;

    @Override
    public PageR<Map<String, DiffValue>> list(ClientListREQ req) {
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
            case "continuousStatus":
                return businessDataRepository.getContinuousTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "economyType":
                return businessDataRepository.getEconomyTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "corpCertType":
                return businessDataRepository.getCertTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "orgScale":
                return Optional.ofNullable(OrgScaleType.of(String.valueOf(value.getValue()))).map(OrgScaleType::display).orElse("");
            case "orgType":
                return businessDataRepository.getOrgTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "registerCapital":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            case "registerCurrencyType":
                return businessDataRepository.getCurrencyTypeNameFromLocalCache(String.valueOf(value.getValue()));
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(ClientListREQ req) {
        Page<CrClientDraft> dataPage = crClientDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrClientDraft>lambdaQuery()
                .eq(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrClientDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getClientName()), CrClientDraft::getClientName, req.getClientName())
        );
        if (CollUtil.isEmpty(dataPage.getRecords())) {
            return PageR.of(Collections.emptyList(), 0);
        }
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrClientDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        if (CollUtil.isEmpty(dataPage.getRecords())) {
            return PageR.of(Collections.emptyList(), 0);
        }
        List<ClientListRSP> rspList = dataPage.getRecords().stream().map(d -> BeanUtil.copyProperties(d, ClientListRSP.class)).collect(Collectors.toList());
        if (CollUtil.isEmpty(rspList)) {
            return PageR.of(Collections.emptyList(), 0);
        }
        fillIndustryName(rspList);
        List<String> businessKeys = rspList.stream().map(ClientListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.CLIENT, ClientListRSP.class, batchNo, isShow);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(ClientListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrClientProcSnap> dataPage = crClientProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrClientProcSnap>lambdaQuery()
                    .eq(CrClientProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .like(StringUtils.isNotBlank(req.getClientName()), CrClientProcSnap::getClientName, req.getClientName())
            );
            List<ClientListRSP> rspList = dataPage.getRecords().stream().map(d -> BeanUtil.copyProperties(d, ClientListRSP.class)).collect(Collectors.toList());
            if (CollUtil.isEmpty(rspList)) {
                return PageR.of(Collections.emptyList(), 0);
            }
            fillIndustryName(rspList);
            List<String> businessKeys = rspList.stream().map(ClientListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.CLIENT, ClientListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrClientDraft> dataPage = crClientDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrClientDraft>lambdaQuery()
                    .eq(CrClientDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrClientDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrClientDraft::getClientName, req.getClientName())
            );
            return getMapPageR(dataPage, null, null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(ClientListREQ req) {
        Page<CrClientProcSnap> dataPage = crClientProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrClientProcSnap>lambdaQuery()
                .eq(CrClientProcSnap::getBatchId, req.getBatchId())
                .like(StringUtils.isNotBlank(req.getClientName()), CrClientProcSnap::getClientName, req.getClientName())
        );
        List<ClientListRSP> rspList = dataPage.getRecords().stream().map(d -> BeanUtil.copyProperties(d, ClientListRSP.class)).collect(Collectors.toList());
        fillIndustryName(rspList);
        if (CollUtil.isEmpty(rspList)) {
            return PageR.of(Collections.emptyList(), 0);
        }
        List<String> businessKeys = rspList.stream().map(ClientListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.CLIENT, ClientListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(ClientListREQ req) {
        Long clientId = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            clientId = crAccount.getClientId();
        }
        Page<CrClient> dataPage = crClientService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrClient>lambdaQuery()
                .eq(Objects.nonNull(clientId), CrClient::getClientId, clientId)
                .like(StringUtils.isNotBlank(req.getClientName()), CrClient::getClientName, req.getClientName())
        );
        List<ClientListRSP> rspList = dataPage.getRecords().stream().map(d -> BeanUtil.copyProperties(d, ClientListRSP.class)).collect(Collectors.toList());
        if (CollUtil.isEmpty(rspList)) {
            return PageR.of(Collections.emptyList(), 0);
        }
        fillIndustryName(rspList);
        List<String> businessKeys = rspList.stream().map(ClientListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.CLIENT, ClientListRSP.class);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrClientDraft> draftList = crClientDraftService.getBaseMapper().selectList(Wrappers.<CrClientDraft>lambdaQuery()
                .eq(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrClientDraft::getProcBusinessKey, procBusinessKey)
        );

        // 1.编辑区数据插入ProcSnap
        List<CrClientProcSnap> procSnapList = draftList.stream().map(d -> {
            CrClientProcSnap procSnap = BeanUtil.copyProperties(d, CrClientProcSnap.class, ReportConstants.IGNORE_ID);
            procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
            procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
            return procSnap;
        }).collect(Collectors.toList());
        crClientProcSnapService.saveBatch(procSnapList);
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .in(CrModifyDataSnap::getBusinessKey, draftList.stream()
                                .map(CrBaseModel::getBusinessKey).collect(Collectors.toList()))
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.CLIENT.name())
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
                    CrClientDraft crClientDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrClientDraft.class);
                    BeanUtil.copyProperties(crClientDraft, draft, ReportConstants.IGNORE_ID);
                });
                List<CrClient> draft2FormalList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                        .map(d -> BeanUtil.copyProperties(d, CrClient.class, ReportConstants.IGNORE_ID)).collect(Collectors.toList());
                List<CrClient> needInsertList = new ArrayList<>();
                Map<String, CrClient> existReportDataMap = crClientService.list(Wrappers.<CrClient>lambdaQuery()
                                .in(CrClient::getBusinessKey, draftList.stream().map(CrClientDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrClient::getBusinessKey, Function.identity(), (a, b) -> a));
                for (CrClient draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrClient existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crClientService.updateById(draft2Formal);
                        }
                    }
                }
                crClientService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crClientFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null), Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        if (CollectionUtils.isNotEmpty(draftList)) {
            // 审批通过才处理成已报送
            crClientDraftService.lambdaUpdate()
                    .set(processPass, CrClientDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrClientDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrClientDraft::getProcBusinessKey, null)
                    .in(CrClientDraft::getId, draftList.stream().map(CrClientDraft::getId).collect(Collectors.toList()))
                    .eq(CrClientDraft::getReportFlag, YesOrNoNumberEnum.YES.getCode())
                    .update();
        }
        // 将进入流程后未报送的数据还原成待报送
        crClientDraftService.lambdaUpdate()
                .set(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrClientDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                .set(CrClientDraft::getProcBusinessKey, null)
                .eq(CrClientDraft::getReportFlag, YesOrNoNumberEnum.NO.getCode())
                .update();
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crClientDraftService.lambdaUpdate()
                .eq(CrClientDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrClientDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrClientDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.CLIENT.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.CLIENT;
    }

    @Override
    public int countInProcessData() {
        return crClientDraftService.count(Wrappers.<CrClientDraft>lambdaQuery()
                .select(CrClientDraft::getId)
                .eq(CrClientDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrClientDraft> getServiceInstance() {
        return this.crClientDraftService;
    }

    private void fillIndustryName(List<ClientListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        Map<String, String> industryNameMap = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                .in(IndustryType::getCode, rspList.stream().map(ClientListRSP::getIndustryType).collect(Collectors.toSet()))
        ).stream().collect(Collectors.toMap(IndustryType::getCode, IndustryType::getDisplay));
        rspList.forEach(rsp -> rsp.setIndustryTypeName(industryNameMap.get(rsp.getIndustryType())));
    }

}
