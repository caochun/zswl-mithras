package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.report.enums.biz.FiveClassEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrFiveClassDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrFiveClass;
import cn.zswltech.mithras.report.service.draft.CrFiveClassDraftService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyNodeRecordLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.L;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-五级分类表
 * 需要在accountHandler之后执行
 *
 * @author wangchuanhao
 * @date 2022/10/8 2:52 PM
 */
@Component
@Slf4j
@Order(100)
public class CrFiveClassHandler extends CrAbstractHandler<CrFiveClassDraft, CrFiveClass> {

    @Resource
    private CrAccountDraftMapper crAccountDraftMapper;
    @Resource
    private AssetClassifyLibService assetClassifyLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private CrFiveClassDraftService crFiveClassDraftService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private AssetClassifyNodeRecordLibService assetClassifyNodeRecordLibService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.FIVE_CLASS;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {

        // 如果account对应的paymentCode没有五级分类记录或者相对于上一次报送的记录状态发生变化，就要新增一条
        List<CrAccountDraft> all = crAccountDraftMapper.selectList(null);
        //过滤掉不是在租的合同
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(all.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()));
        if (CollUtil.isEmpty(contractBaseInfos)) {
            return;
        }
        Set<Long> contractIds = contractBaseInfos.stream()
                .filter(a -> ContractStatus.START_RENT.name().equals(a.getContractStatus()) || ContractStatus.TAKE_EFFECT.name().equals(a.getContractStatus()))
                .map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<CrAccountDraft> accountDraftList = all.stream().filter(a -> contractIds.contains(a.getContractId())).collect(Collectors.toList());

        //获取当前年、季度
        Map<Integer, Integer> currentQuarter = getCurrentQuarter(dealTime.toLocalDate());

        //第一次默认正常
        List<CrFiveClassDraft> classDrafts = crFiveClassDraftService.list(Wrappers.<CrFiveClassDraft>lambdaQuery()
                .in(CrFiveClassDraft::getContractId, contractIds));
        if (CollUtil.isNotEmpty(classDrafts)) {
            Set<String> longSet = classDrafts.stream().map(CrFiveClassDraft::getPaymentApplyCode).collect(Collectors.toSet());
            List<CrAccountDraft> collect = accountDraftList.stream().filter(a -> !longSet.contains(a.getPaymentApplyCode())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                saveNewHandler(collect);
            }
        }

        if (CollUtil.isEmpty(classDrafts)) {
            saveNewHandler(accountDraftList);
        }
        //查询当前年份当前季度的所有客户五级分类(最新版本)
        int year = dealTime.toLocalDate().getYear();
        List<AssetClassifyLib> classifyList = assetClassifyLibService.list(Wrappers.<AssetClassifyLib>lambdaQuery()
                .eq(AssetClassifyLib::getYear, year)
                .eq(AssetClassifyLib::getVersionType, VersionTypeConstants.NORMAL)
                .eq(AssetClassifyLib::getQuarter, currentQuarter.get(year))
                .eq(AssetClassifyLib::getFinish, YesOrNoNumberEnum.YES.getCode()));

        if (CollUtil.isEmpty(classifyList)) {
            return;
        }
        //根据ID分组并取出最新版本
        classifyList.sort(Comparator.comparing(AssetClassifyLib::getVersion).reversed());
        AssetClassifyLib assetClassifyLib = classifyList.get(0);

        List<AssetClassifyClientAuxiliaryLib> classifyClientList = assetClassifyClientAuxiliaryLibService.list(
                Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                        .eq(AssetClassifyClientAuxiliaryLib::getAssetClassifyId, assetClassifyLib.getOriginId())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersion, assetClassifyLib.getVersion())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(AssetClassifyClientAuxiliaryLib::getReviewStatus, PaymentStatusEnum.FINISHED.name())
        );

        List<AssetClassifyNodeRecordLib> list = assetClassifyNodeRecordLibService.list(Wrappers.<AssetClassifyNodeRecordLib>lambdaQuery()
                .eq(AssetClassifyNodeRecordLib::getAssetClassifyId, assetClassifyLib.getOriginId())
                .eq(AssetClassifyNodeRecordLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(AssetClassifyNodeRecordLib::getVersion)
        );
        list.sort(Comparator.comparing(AssetClassifyNodeRecordLib::getId).reversed());
        AssetClassifyNodeRecordLib classifyNodeRecordLib = list.get(0);

        //根据客户ID分组并取出最新版本
        Map<Long, AssetClassifyClientAuxiliaryLib> clientAuxiliaryLibMap = classifyClientList.stream()
                .collect(Collectors.toMap(AssetClassifyClientAuxiliaryLib::getClientId, Function.identity(), (k1, k2) -> k1));

        Set<String> existFiveClassPaymentIdSet = new HashSet<>();
        if (CollUtil.isNotEmpty(classDrafts)) {
            existFiveClassPaymentIdSet = classDrafts.stream().map(CrFiveClassDraft::getPaymentApplyCode).collect(Collectors.toSet());
        }
        List<CrFiveClassDraft> needInsertList = new ArrayList<>();
        List<CrAccountDraft> tempAccountDraftList = new ArrayList<>();
        for (int i = 0; i < accountDraftList.size(); i++) {
            CrAccountDraft accountDraft = accountDraftList.get(i);
            if (!existFiveClassPaymentIdSet.contains(accountDraft.getPaymentApplyCode())) {
                //新增的不需要考虑比较的问题
                CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
                fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
                fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                fiveClassDraft.setPaymentId(accountDraft.getPaymentId());
                fiveClassDraft.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
                fiveClassDraft.setIdentificationDate(classifyNodeRecordLib.getEndTime());
                fiveClassDraft.setFiveClass(FiveClassEnum.NORMAL.getValue());
                fiveClassDraft.setContractId(accountDraft.getContractId());
                fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
                needInsertList.add(fiveClassDraft);
            }
            tempAccountDraftList.add(accountDraft);
        }

        List<CrFiveClassDraft> needUpdateList = new LinkedList<>();
        //处理需要更新的或者新插入一条的
        Set<Long> paymentIds = tempAccountDraftList.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(paymentIds)) {
            Map<String, CrFiveClassDraft> fiveClassDraftMap = new HashMap<>(8);
            Map<String, List<CrFiveClassDraft>> longListMap = crFiveClassDraftService.list(Wrappers.<CrFiveClassDraft>lambdaQuery().in(CrFiveClassDraft::getPaymentId, paymentIds))
                    .stream().collect(Collectors.groupingBy(CrFiveClassDraft::getPaymentApplyCode));
            longListMap.forEach((k, v) -> {
                if (CollUtil.isNotEmpty(v)) {
                    v.sort(Comparator.comparing(CrFiveClassDraft::getCreateTime).reversed());
                    fiveClassDraftMap.put(k, v.get(0));
                }
            });
            for (CrAccountDraft draft : tempAccountDraftList) {
                AssetClassifyClient clientLib = clientAuxiliaryLibMap.get(draft.getClientId());
                if (Objects.nonNull(clientLib)) {
                    CrFiveClassDraft classDraft = fiveClassDraftMap.get(draft.getPaymentApplyCode());
                    if (Objects.nonNull(classDraft) && !Objects.equals(FiveClassEnum.of(clientLib.getClassifyResult()).getValue(), classDraft.getFiveClass())) {
                        //查询征信五级分类表判断修改还是新增
                        if (ReportState.TO_BE_REPORT.name().equals(classDraft.getReportState())) {
                            //更新
                            CrFiveClassDraft build = new CrFiveClassDraft();
                            build.setFiveClass(FiveClassEnum.of(clientLib.getClassifyResult()).getValue());
                            build.setId(classDraft.getId());
                            build.setIdentificationDate(classifyNodeRecordLib.getEndTime());
                            needUpdateList.add(build);
                        } else {
                            //新增
                            CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
                            fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
                            fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                            fiveClassDraft.setPaymentId(draft.getPaymentId());
                            fiveClassDraft.setPaymentApplyCode(draft.getPaymentApplyCode());
                            fiveClassDraft.setIdentificationDate(classifyNodeRecordLib.getEndTime());
                            fiveClassDraft.setFiveClass(FiveClassEnum.of(clientLib.getClassifyResult()).getValue());
                            fiveClassDraft.setContractId(draft.getContractId());
                            fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
                            needInsertList.add(fiveClassDraft);
                        }
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(needUpdateList)) {
            draftService.saveOrUpdateBatch(needUpdateList);
        }
        if (CollUtil.isNotEmpty(needInsertList)) {
            draftService.saveBatch(needInsertList);
        }
    }

    private void saveNewHandler(List<CrAccountDraft> collect) {
        List<CrFiveClassDraft> temp = new LinkedList<>();
        for (CrAccountDraft accountDraft : collect) {
            CrFiveClassDraft fiveClassDraft = new CrFiveClassDraft();
            fiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
            fiveClassDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
            fiveClassDraft.setPaymentId(accountDraft.getPaymentId());
            fiveClassDraft.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
            fiveClassDraft.setIdentificationDate(accountDraft.getLendingDate().atStartOfDay());
            fiveClassDraft.setFiveClass(FiveClassEnum.NORMAL.getValue());
            fiveClassDraft.setContractId(accountDraft.getContractId());
            fiveClassDraft.setBusinessKey(fiveClassDraft.genBusinessKey());
            temp.add(fiveClassDraft);
        }
        crFiveClassDraftService.saveBatch(temp);
    }

    /**
     * @return Map<Integer年, Integer季度>
     */
    private Map<Integer, Integer> getCurrentQuarter(LocalDate date) {
        Map<Integer, Integer> map = new HashMap<>(8);
        map.put(date.getYear(), (date.getMonth().getValue() / 3) + 1);
        return map;
    }

    @Override
    public Integer sort() {
        return 14;
    }
}
