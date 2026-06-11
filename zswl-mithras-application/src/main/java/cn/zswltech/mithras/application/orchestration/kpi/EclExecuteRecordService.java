package cn.zswltech.mithras.application.orchestration.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.rating.service.DecisionService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.kpi.excel.importer.EclEcecuteRecordmporter;
import cn.zswltech.mithras.kpi.excel.model.EclExecuteRecordExcelModel;
import cn.zswltech.mithras.kpi.mapper.dto.EclExecuteRecordParam;
import cn.zswltech.mithras.kpi.mapper.EclExecuteRecordLibMapper;
import cn.zswltech.mithras.kpi.mapper.EclExecuteRecordMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionDetail;
import cn.zswltech.mithras.kpi.mapper.model.EclExecuteRecord;
import cn.zswltech.mithras.kpi.mapper.model.EclExecuteRecordLib;
import cn.zswltech.mithras.kpi.application.ecl.EclExecuteRecordLibService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.kpi.bo.EclResultBO;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.foundation.util.CompareUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.util.VersionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 资产减值记录表
 * @date 2025-09-28
 */
@Service
@Slf4j
public class EclExecuteRecordService extends ServiceImpl<EclExecuteRecordMapper, EclExecuteRecord> {

    private final static String ECL_PD_TEMP = "ecl_pd_temp";
    private final static String ECL_OUTER_LEVEL = "ecl_outer_level";
    private final static String ECL_INNER_PD = "ecl_inner_pd";
    private final static String ECL_EAD = "ecl_ead";
    private final static String ECL_STEP = "ecl_step";
    private final static String ECL_FACTOR_T = "ecl_factor_t";
    private final static String BASE_PD_FORWARD = "base_pd_forward";
    private final static String OPT_PD_FORWARD = "opt_pd_forward";
    private final static String GLO_PD_FORWARD = "glo_pd_forward";
    private final static String ECL_BASE_IFRS9 = "ecl_base_ifrs9";
    private final static String ECL_OPT_IFRS9 = "ecl_opt_ifrs9";
    private final static String ECL_GLO_IFRS9 = "ecl_glo_ifrs9";
    private final static String BASE_ECL = "base_ecl";
    private final static String OPT_ECL = "opt_ecl";
    private final static String GLO_ECL = "glo_ecl";
    private final static String ECL = "ecl";
    private final static String ECL_STEP_PROMOTION= "ecl_step_promotion";
    private final static String ONE_HUNDRED_MILLION = "100000000";
    private final static List<String> CCCList = ListUtil.toList("C", "CC", "CCC");
    private final static Set<String> ignoreField;

    static {
        ignoreField = new HashSet<>();
        ignoreField.add("createTime");ignoreField.add("createBy");ignoreField.add("updateTime");
        ignoreField.add("updateBy");ignoreField.add("deleted");ignoreField.add("modelRecordKey");
    }

    @Resource
    private EclExecuteRecordMapper eclExecuteRecordMapper;
    @Resource
    private EclExecuteRecordLibService eclExecuteRecordLibService;
    @Resource
    private EclExecuteRecordLibMapper eclExecuteRecordLibMapper;
    @Resource
    private EclEcecuteRecordmporter eclEcecuteRecordmporter;
    @Resource
    private DecisionService decisionService;
    @Resource
    @Lazy
    private KpiProvisionDetailService kpiProvisionDetailService;
    @Resource
    @Lazy
    private KpiProvisionBaseInfoService kpiProvisionBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private OrgDOMapper orgDOMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(EclExecuteRecordAddREQ req) {
        checkContract(Collections.singletonList(req.getContractCode()));
        EclExecuteRecord info = BeanUtil.copyProperties(req, EclExecuteRecord.class);
        info.setClassify(req.getRiskLevel());
        info.setSourceType(YesOrNoNumberEnum.YES.getCode());
        //同步到拨备
        KpiProvisionBaseInfo lastProvision = null;
        if (ObjectUtil.isNotEmpty(req.getProvisionId())) {
            lastProvision = kpiProvisionBaseInfoService.getById(req.getProvisionId());
        } else {
            lastProvision = kpiProvisionBaseInfoService.getLastProvision();
        }
        if (ObjectUtil.isEmpty(lastProvision)) {
            throw new MithrasException("无最新未生效拨备数据，请先创建再添加");
        }
        List<KpiProvisionDetail> kpiProvisionDetails = SpringContextHolder.getBean(EclExecuteRecordService.class).saveKpiProvisionDetail(Collections.singletonList(req), lastProvision);
        if (ObjectUtil.isNotEmpty(kpiProvisionDetails)) {
            Map<Long, Long> receiptId2ProvisionDetail = kpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getReceiptId, KpiProvisionDetail::getId, (a, b) -> b));
            info.setKpiProvisionDetailId(receiptId2ProvisionDetail.get(req.getReceiptId()));
        }
        eclExecuteRecordMapper.insert(info);
        //转存数据
        List<Long> libIds = save2Lib(Collections.singletonList(info));
        //调用记录
        SpringContextHolder.getBean(KpiProvisionDetailService.class).buildProfitCurrent2(kpiProvisionDetails.stream().map(KpiProvisionDetail::getId).collect(Collectors.toList()), lastProvision.getProvisionDate());
        //删除原始记录
        EclExecuteRecordRemoveREQ eclExecuteRecordRemoveREQ = new EclExecuteRecordRemoveREQ();
        eclExecuteRecordRemoveREQ.setId(info.getId());
        this.remove(eclExecuteRecordRemoveREQ);
        eclExecuteRecordRemoveREQ.setIds(libIds);
        eclExecuteRecordRemoveREQ.setId(null);
        eclExecuteRecordLibService.remove(eclExecuteRecordRemoveREQ);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addBatch(List<EclExecuteRecordAddREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            return;
        }
        checkContract(req.stream().map(EclExecuteRecordAddREQ::getContractCode).collect(Collectors.toList()));

        List<EclExecuteRecord> records = new ArrayList<>();

        //同步到拨备
        KpiProvisionBaseInfo lastProvision = kpiProvisionBaseInfoService.getLastProvision();
        if (ObjectUtil.isEmpty(lastProvision)) {
            throw new MithrasException("无最新未生效拨备数据，请先创建再添加");
        }
        List<KpiProvisionDetail> kpiProvisionDetails = saveKpiProvisionDetail(req, lastProvision);
        Map<Long, Long> receiptId2ProvisionDetail = kpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getReceiptId, KpiProvisionDetail::getId, (a, b) -> b));
        req.forEach(info -> {
            EclExecuteRecord record = BeanUtil.copyProperties(info, EclExecuteRecord.class);
            record.setSourceType(YesOrNoNumberEnum.YES.getCode());
            record.setKpiProvisionDetailId(receiptId2ProvisionDetail.get(info.getReceiptId()));
            records.add(record);
        });
        this.saveBatch(records);
        //转存数据
        save2Lib(records);
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<KpiProvisionDetail> saveKpiProvisionDetail(List<EclExecuteRecordAddREQ> infos, KpiProvisionBaseInfo lastProvision) {
        List<KpiProvisionDetail> addDetails = new ArrayList<>();
        if (ObjectUtil.isEmpty(infos)) {
            return addDetails;
        }
        Set<String> receiptCodes = infos.stream().map(EclExecuteRecordAddREQ::getReceiptCode).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(receiptCodes)) {
            return addDetails;
        }
        //检查借据编号
       /* List<ContractReceipt> contractReceipts = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery()
                .in(ContractReceipt::getReceiptCode, receiptCodes));
        if (ObjectUtil.isEmpty(contractReceipts)) {
            return addDetails;
        }*/
        //合同编号转id
        Map<String, ContractBaseInfo> contractCode2Bean = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractCode, infos.stream().map(EclExecuteRecordAddREQ::getContractCode).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, e -> e, (a, b) -> b));
        //客户名称
        Map<String, Client> clientName2Bean = clientService.list(Wrappers.<Client>lambdaQuery()
                .in(Client::getClientName, infos.stream().map(EclExecuteRecordAddREQ::getClientName).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(Client::getClientName, e -> e, (a, b) -> b));

        //部门
        Map<String, Long> orgCode2Id = orgDOMapper.selectAll().stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId, (a, b) -> b));
        /*Set<String> oldContractReceipts = contractReceipts.stream().map(ContractReceipt::getReceiptCode).collect(Collectors.toSet());
        Set<String> notContractReceipts = receiptCodes.stream().filter(e -> !oldContractReceipts.contains(e)).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(notContractReceipts)) {
            throw new MithrasException("借据编号不存在{}" + notContractReceipts);
        }*/
        //Map<String, Long> receiptCode2Id = contractReceipts.stream().collect(Collectors.toMap(ContractReceipt::getReceiptCode, ContractReceipt::getId, (a, b) -> b));
        //查询已有数据
        List<KpiProvisionDetail> kpiProvisionDetails = kpiProvisionDetailService.list(Wrappers.<KpiProvisionDetail>lambdaQuery()
                .eq(KpiProvisionDetail::getProvisionId, lastProvision.getId())
                .in(KpiProvisionDetail::getReceiptId, receiptCodes)
        );
        Map<Long, KpiProvisionDetail> receiptId2UpdateDetailMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(kpiProvisionDetails)) {
            kpiProvisionDetails.forEach(e -> {
                if (!ObjectUtil.equals(e.getSourceType(), YesOrNoNumberEnum.YES.getCode())) {
                    throw new MithrasException("新增项目与存量项目" + e.getContractCode() +"重复，请核对后重新上传");
                }
            });
            receiptId2UpdateDetailMap = kpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getReceiptId, e -> e, (a, b) -> a));
        }
        KpiProvisionDetail kpiProvisionDetail;
        List<KpiProvisionDetail> updateDetails = new ArrayList<>();

        for(EclExecuteRecordAddREQ addREQ : infos) {
            kpiProvisionDetail = new KpiProvisionDetail();
            ContractBaseInfo contractBaseInfo = contractCode2Bean.get(addREQ.getContractCode());
            Client client = clientName2Bean.get(addREQ.getClientName());
            kpiProvisionDetail.setProvisionId(lastProvision.getId());
            kpiProvisionDetail.setReceiptCode(addREQ.getReceiptCode());
            kpiProvisionDetail.setBizType(ProjectBizType.getNameByDisplay(addREQ.getContractBizType()));
            kpiProvisionDetail.setLeaseType(LeaseType.getNameByDisplay(addREQ.getContractLeaseType()));
            kpiProvisionDetail.setProjClassify(addREQ.getProjClassify());
            kpiProvisionDetail.setProfitBelongDeptId(orgCode2Id.get(addREQ.getProfitBelongDeptName()));
            kpiProvisionDetail.setContractCode(addREQ.getContractCode());
            kpiProvisionDetail.setEndDate(addREQ.getContractExpirationDate());
            kpiProvisionDetail.setRemainingPrincipal(LongUtil.other2Long(addREQ.getRemainPrincipal()));
            kpiProvisionDetail.setEarnestBalance(LongUtil.other2Long(addREQ.getDeposit()));
            kpiProvisionDetail.setExposure(LongUtil.other2Long(addREQ.getRiskExposure()));
            kpiProvisionDetail.setRiskLevel(Optional.ofNullable(AssetClassifyResultEnum.getByDisplay(addREQ.getRiskLevel())).map(AssetClassifyResultEnum::name).orElse(null));
            kpiProvisionDetail.setAccruedInterest(LongUtil.other2Long(addREQ.getAccruedInterest()));
            kpiProvisionDetail.setNextRent(LongUtil.other2Long(addREQ.getNextRent()));
            kpiProvisionDetail.setProfitCurrent(LongUtil.other2Long(addREQ.getProfitCurrent()));
            kpiProvisionDetail.setProfitTotal(LongUtil.other2Long(addREQ.getProfitTotal()));
            kpiProvisionDetail.setBonusCurrent(LongUtil.other2Long(addREQ.getBonusCurrent()));
            kpiProvisionDetail.setProvisionDate(lastProvision.getProvisionDate());
            kpiProvisionDetail.setSourceType(YesOrNoNumberEnum.YES.getCode());
            if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                kpiProvisionDetail.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
                kpiProvisionDetail.setContractId(contractBaseInfo.getId());
            }
            if (ObjectUtil.isNotEmpty(client)) {
                kpiProvisionDetail.setClientId(client.getId());
            }
            KpiProvisionDetail detail = receiptId2UpdateDetailMap.get(kpiProvisionDetail.getReceiptId());
            if (ObjectUtil.isNotEmpty(detail)) {
                kpiProvisionDetail.setId(detail.getId());
                updateDetails.add(kpiProvisionDetail);
            } else {
                addDetails.add(kpiProvisionDetail);
            }
        }
        if (CollectionUtil.isNotEmpty(addDetails)) {
            kpiProvisionDetailService.saveBatch(addDetails);
        }
        if (CollectionUtil.isNotEmpty(updateDetails)) {
            kpiProvisionDetailService.updateBatchById(updateDetails);
        }
        addDetails.addAll(updateDetails);
        return addDetails;
    }

    public String addCheck(EclExecuteRecordAddREQ req) {
        if (eclExecuteRecordMapper.selectCount(Wrappers.<EclExecuteRecord>lambdaQuery()
                .eq(EclExecuteRecord::getContractCode, req.getContractCode())) > 0) {
            return req.getContractCode();
        }
        checkContract(Collections.singletonList(req.getContractCode()));
        return null;
    }

    /**
     * 判断合同是否重复
     **/
    private void checkContract(List<String> contractCodes) {
        if (CollectionUtil.isEmpty(contractCodes)) {
            return;
        }
        List<EclExecuteRecord> list = this.list(Wrappers.<EclExecuteRecord>lambdaQuery()
                .eq(EclExecuteRecord::getSourceType, YesOrNoNumberEnum.NO.getCode())
                .in(EclExecuteRecord::getContractCode, contractCodes));
        if (CollectionUtil.isNotEmpty(list)) {
            throw new MithrasException(String.format("新增项目与存量项目%s重复，请核对后重新上传", list.stream().map(EclExecuteRecord::getContractCode).collect(Collectors.toSet())));
        }
    }

    public void importFile(EclExecuteRecordImportREQ req) {
        List<EclExecuteRecordExcelModel> parse;
        try {
           parse = eclEcecuteRecordmporter.parse(req.getFile().getInputStream());
        } catch (IOException e) {
            throw new MithrasException("文件解析异常");
        }
        if (CollectionUtil.isEmpty(parse)) {
            throw new MithrasException("未解析到数据");
        }
        List<EclExecuteRecordAddREQ> records = BeanUtil.copyToList(parse, EclExecuteRecordAddREQ.class);
        this.addBatch(records);
    }

    public Set<String> importFileCheck(EclExecuteRecordImportREQ req) {
        List<EclExecuteRecordExcelModel> parse;
        try {
            parse = eclEcecuteRecordmporter.parse(req.getFile().getInputStream());
        } catch (IOException e) {
            throw new MithrasException("文件解析异常");
        }
        if (CollectionUtil.isEmpty(parse)) {
            throw new MithrasException("未解析到数据");
        }
        List<EclExecuteRecord> records = BeanUtil.copyToList(parse, EclExecuteRecord.class);
        //records.forEach(w -> w.setSourceType(YesOrNoNumberEnum.YES.getCode()));
        //合同号相同的需覆盖
        if (ObjectUtil.isEmpty(records)) {
            return null;
        }
        List<String> contractCodes = records.stream().filter(e -> ObjectUtil.isNotEmpty(e.getContractCode())).map(EclExecuteRecord::getContractCode).collect(Collectors.toList());

        List<EclExecuteRecord> oldExecuteRecord = this.list(Wrappers.<EclExecuteRecord>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(contractCodes), EclExecuteRecord::getContractCode, contractCodes));
        if (ObjectUtil.isNotEmpty(oldExecuteRecord)) {
            Set<String> contractCodeList = oldExecuteRecord.stream().filter(ObjectUtil::isNotEmpty).map(EclExecuteRecord::getContractCode).collect(Collectors.toSet());
            if(CollectionUtil.isNotEmpty(contractCodeList)) {
                return contractCodeList;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<EclExecuteRecord> modifyRecordByEclResultBO(List<EclResultBO> eclResultBOList) {
        List<EclExecuteRecord> records = buildByEclResultBO(eclResultBOList);
        Map<Long, EclExecuteRecord> kpiProvisionId2Record = this.list(Wrappers.<EclExecuteRecord>lambdaQuery()
                .in(EclExecuteRecord::getKpiProvisionDetailId, records.stream().map(EclExecuteRecord::getKpiProvisionDetailId).collect(Collectors.toList()))).stream().collect(Collectors.toMap(EclExecuteRecord::getKpiProvisionDetailId, e -> e, (a, b) -> b));
        List<EclExecuteRecord> addRecords = new ArrayList<>();
        List<EclExecuteRecord> updateRecords = new ArrayList<>();
        Map<Long, String> receiptId2code = contractReceiptService.listByIds(records.stream().map(EclExecuteRecord::getReceiptId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getReceiptCode, (a, b) -> b));


        records.forEach(e -> {
            EclExecuteRecord eclExecuteRecord = kpiProvisionId2Record.get(e.getKpiProvisionDetailId());
            if (ObjectUtil.isNotEmpty(e.getReceiptId()) && ObjectUtil.isEmpty(e.getReceiptCode())) {
                e.setReceiptCode(receiptId2code.get(e.getReceiptId()));
            }
            if (ObjectUtil.isNotEmpty(eclExecuteRecord)) {
                e.setId(eclExecuteRecord.getId());
                e.setCreateBy(eclExecuteRecord.getCreateBy());
                e.setCreateTime(eclExecuteRecord.getCreateTime());
                e.setUpdateBy(eclExecuteRecord.getUpdateBy());
                e.setUpdateTime(eclExecuteRecord.getUpdateTime());
                updateRecords.add(e);
            } else {
                addRecords.add(e);
            }
        });
        if (!updateRecords.isEmpty()) {
            SpringContextHolder.getBean(EclExecuteRecordService.class).updateBatchById(updateRecords);
        }
        if (!addRecords.isEmpty()) {
            SpringContextHolder.getBean(EclExecuteRecordService.class).saveBatch(addRecords);
        }

        //转存版本 这里所有都要存入版本
        this.save2Lib(updateRecords);

        //转存版本 这里所有都要存入版本
        this.save2Lib(addRecords);

        return records;
    }

    public List<Long> save2Lib(List<EclExecuteRecord> addRecords) {
        EclExecuteRecordLib lastLib = eclExecuteRecordLibService.getOne(Wrappers.<EclExecuteRecordLib>lambdaQuery().gt(EclExecuteRecordLib::getCreateTime, LocalDate.now())
                .orderByDesc(EclExecuteRecordLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        String version = VersionUtil.generateVersion(lastLib == null ? null : lastLib.getVersion());
        //转存版本 这里所有都要存入版本
        if (!addRecords.isEmpty()) {
            List<EclExecuteRecordLib> libs = new ArrayList<>();
            addRecords.forEach(e -> {
                EclExecuteRecordLib lib = BeanUtil.copyProperties(e, EclExecuteRecordLib.class, "id", "createTime", "createBy", "updateTime", "updateBy");
                lib.setOriginId(e.getId());
                lib.setDataCreateBy(e.getCreateBy());
                lib.setDataCreateTime(e.getCreateTime());
                lib.setDataUpdateBy(e.getUpdateBy());
                lib.setDataUpdateTime(e.getUpdateTime());
                lib.setVersion(version);
                libs.add(lib);
            });
            eclExecuteRecordLibService.saveBatch(libs);
            return libs.stream().map(EclExecuteRecordLib::getId).collect(Collectors.toList());
        }
        return null;
    }

    public List<EclExecuteRecord> buildByEclResultBO(List<EclResultBO> eclResultBOList) {
        if (CollectionUtil.isEmpty(eclResultBOList)) {
            return null;
        }
        List<EclExecuteRecord> records = new ArrayList<>();
        eclResultBOList.forEach(eclResultBO -> {
            EclExecuteRecord record = new EclExecuteRecord();
            //存入参
            KpiExpectedLossDecisionQuery query = eclResultBO.getQuery();
            if (ObjectUtil.isNotEmpty(query)) {
                record.setClientName(query.getClient_name());
                record.setClientId(query.getActualClientId());
                record.setClientName(query.getActualClientName());
                record.setEvaluationSubjectId(query.getClientId());
                record.setEvaluationSubjectName(query.getClient_name());
                record.setContractCode(query.getOrder_num());
                record.setInnerMdLevel(query.getInner_level());
                record.setEclPd(query.getInner_pd());
                record.setGroup(query.getGroup());
                record.setClassify(query.getClassify());
                record.setLateDay(query.getLate_day());
                record.setLateDate(query.getLateDate());
                record.setLeaseType(query.getLease_type());
                record.setRemainPrincipal(query.getRemain_principal());
                record.setAccruedInterest(query.getAccrued_interest());
                record.setDeposit(query.getDeposit());
                record.setEclParamZ(JSONUtil.toJsonStr(ListUtil.toList(query.getRzy_ecl_base_z(), query.getRzy_ecl_opt_z(), query.getRzy_ecl_glo_z())));
                record.setEclParamWeight(JSONUtil.toJsonStr(ListUtil.toList(query.getBase_weight(), query.getOpt_weight(), query.getGlo_weight())));
                record.setLgd(query.getLgd());
                record.setRzyEclDownLevel(query.getRzy_ecl_down_level());
                record.setOuterLevel(query.getOuter_level());
                record.setEclOuterPd(query.getEcl_out_pd());
                record.setPromotionResult(query.getPromotionResult());
            }
            //返回
            DecisionExecuteEclResult result = eclResultBO.getResult();
            if (ObjectUtil.isNotEmpty(result)) {
                record.setModelRecordKey(result.getTraceId());
                Map<String, DecisionExecuteEclResult.OutputValue> outputMap = result.getOutputMap();
                if (ObjectUtil.isNotEmpty(outputMap)) {
                    //record.setEclPd(getOutputValueBigDecimal(outputMap, ECL_PD_TEMP));
                    //record.setOuterLevel(getOutputValue(outputMap, ECL_OUTER_LEVEL));
                    //record.setEclOuterPd(getOutputValue(outputMap, ECL_PD_TEMP));
                    //record.setEclPd(getOutputValueBigDecimal(outputMap, ECL_INNER_PD));
                    record.setEad(getOutputValueBigDecimal(outputMap, ECL_EAD));
                    //默认为1
                    record.setEclStep(Math.max(1, getOutputValueBigDecimal(outputMap, ECL_STEP).intValue()));
                    record.setEclFactorT(getOutputValueBigDecimal(outputMap, ECL_FACTOR_T));
                    record.setBasePdForward(getOutputValueBigDecimal(outputMap, BASE_PD_FORWARD));
                    record.setOptPdForward(getOutputValueBigDecimal(outputMap, OPT_PD_FORWARD));
                    record.setGloPdForward(getOutputValueBigDecimal(outputMap, GLO_PD_FORWARD));
                    record.setEclBaseIfrs9(getOutputValueBigDecimal(outputMap, ECL_BASE_IFRS9));
                    record.setEclOptIfrs9(getOutputValueBigDecimal(outputMap, ECL_OPT_IFRS9));
                    record.setEclGloIfrs9(getOutputValueBigDecimal(outputMap, ECL_GLO_IFRS9));
                    record.setBaseEcl(getOutputValueBigDecimal(outputMap, BASE_ECL));
                    record.setOptEcl(getOutputValueBigDecimal(outputMap, OPT_ECL));
                    record.setGloEcl(getOutputValueBigDecimal(outputMap, GLO_ECL));
                    record.setEcl(getOutputValueBigDecimal(outputMap, ECL));
                    record.setPromotionResult(getOutputValueBigDecimal(outputMap, ECL_STEP_PROMOTION).intValue());
                }
            }
            //拨备
            KpiProvisionDetail kpiProvisionDetail = eclResultBO.getKpiProvisionDetail();
            if (ObjectUtil.isNotEmpty(kpiProvisionDetail)) {
                record.setKpiProvisionDetailId(kpiProvisionDetail.getId());
                record.setClientId(kpiProvisionDetail.getClientId());
                record.setContractId(kpiProvisionDetail.getContractId());
                record.setRiskExposure(LongUtil.tenThousand2Dollar(LongUtil.null2zero(kpiProvisionDetail.getExposure()).toString()));
                record.setContractExpirationDate(kpiProvisionDetail.getEndDate());
                record.setReceiptId(kpiProvisionDetail.getReceiptId());
                record.setReceiptCode(kpiProvisionDetail.getReceiptCode());
                record.setSourceType(kpiProvisionDetail.getSourceType());
            }

            //添加备注
            // 如确定债项所处阶段时，只命中了“从初始确认（数据初始化时）以来评级结果下迁大于等于四个等级”，则返回“下迁结果大于等于四个等级”。
            //如确定债项所处阶段时，处于三阶段，且合同对应主承租人风险敞口（客户维度）大于1亿，则返回“处于债项三阶段，且风险敞口大于1亿”。
            //如评级结果为C、CC、CCC且处于一阶段，则返回“评级结果为C、CC、CCC且处于一阶段”。
            if (ObjectUtil.isNotEmpty(record.getEclStep())) {
                if (ObjectUtil.equals(record.getEclStep(), record.getPromotionResult())) {
                    if (ObjectUtil.equals(record.getEclStep(), 3) && ObjectUtil.isNotEmpty(record.getRiskExposure()) && record.getRiskExposure().compareTo(new BigDecimal(ONE_HUNDRED_MILLION)) >= 0) {
                        record.setRemark("处于债项三阶段，且风险敞口大于1亿");
                    } else if (ObjectUtil.equals(record.getEclStep(), 1) && CCCList.contains(record.getInnerMdLevel())) {
                        record.setRemark("评级结果为C、CC、CCC且处于一阶段");
                    } else if (ObjectUtil.isNotEmpty(record.getRzyEclDownLevel()) && record.getRzyEclDownLevel() >= 4) {
                        record.setRemark("下迁结果大于等于四个等级");
                    } else {
                        record.setRemark("-");
                    }
                } else {
                    record.setRemark("已上迁一级");
                }
            }
            records.add(record);
        });
        return records;
    }

    private String getOutputValue(Map<String, DecisionExecuteEclResult.OutputValue> outputValueMap, String code) {
        DecisionExecuteEclResult.OutputValue outputValue = outputValueMap.get(code);
        if (ObjectUtil.isNotEmpty(outputValue)) {
            return outputValue.getValue();
        }
        return null;
    }

    private BigDecimal getOutputValueBigDecimal(Map<String, DecisionExecuteEclResult.OutputValue> outputValueMap, String code) {
        DecisionExecuteEclResult.OutputValue outputValue = outputValueMap.get(code);
        if (ECL_STEP.equals(code)) {
            Map<String, String> paramsValueMap = outputValue.getParamsValueMap();
            if (ObjectUtil.isNotEmpty(paramsValueMap) && ObjectUtil.isNotEmpty(paramsValueMap.get(ECL_STEP))) {
                return new BigDecimal(paramsValueMap.get(ECL_STEP));
            }
        } else if (ObjectUtil.isNotEmpty(outputValue) && ObjectUtil.isNotEmpty(outputValue.getValue())) {
            return new BigDecimal(outputValue.getValue());
        }
        return BigDecimal.ZERO;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(EclExecuteRecordModifyREQ req) {
        EclExecuteRecordLib originalInfo = eclExecuteRecordLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        EclExecuteRecordLib info = BeanUtil.copyProperties(req, EclExecuteRecordLib.class);
        eclExecuteRecordLibMapper.updateById(info);
        //反改拨备
        Long kpiProvisionDetailId = originalInfo.getKpiProvisionDetailId();
        if (ObjectUtil.isNotEmpty(kpiProvisionDetailId)) {
            KpiProvisionDetail byId = kpiProvisionDetailService.getById(kpiProvisionDetailId);
            if (ObjectUtil.isNotEmpty(byId)){
                EclExecuteRecordAddREQ addREQ = BeanUtil.copyProperties(req, EclExecuteRecordAddREQ.class, "id");
                saveKpiProvisionDetail(Collections.singletonList(addREQ), kpiProvisionBaseInfoService.getById(byId.getProvisionId()));
            }
        }
    }

    //查询所有记录
    public Page<EclExecuteRecordLib> listLib(EclExecuteRecordListREQ req) {
        LocalDateTime localDateTime = null;
        if (ObjectUtil.isNotEmpty(req.getCreateTimeTo())){
            localDateTime = req.getCreateTimeTo().atTime(23, 59, 59);
        }
        return eclExecuteRecordLibService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclExecuteRecordLib>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(req.getClientName()), EclExecuteRecordLib::getClientName, req.getClientName())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), EclExecuteRecordLib::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getInnerMdLevel()), EclExecuteRecordLib::getInnerMdLevel, req.getInnerMdLevel())
                .eq(ObjectUtil.isNotEmpty(req.getGroup()), EclExecuteRecordLib::getGroup, req.getGroup())
                .eq(ObjectUtil.isNotEmpty(req.getClassify()), EclExecuteRecordLib::getClassify, req.getClassify())
                .eq(ObjectUtil.isNotEmpty(req.getEclStep()), EclExecuteRecordLib::getEclStep, req.getEclStep())
                .eq(ObjectUtil.isNotEmpty(req.getLeaseType()), EclExecuteRecordLib::getLeaseType, req.getLeaseType())
                .ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), EclExecuteRecordLib::getUpdateTime, req.getCreateTimeFrom())
                .le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), EclExecuteRecordLib::getUpdateTime, localDateTime)
                .orderByDesc(EclExecuteRecordLib::getUpdateTime)
        );
    }

    //查询今天比对数据
    public List<EclExecuteRecord> listCompare(EclExecuteRecordListREQ req) {

        LocalDateTime localDateTime = null;
        if (ObjectUtil.isNotEmpty(req.getCreateTimeTo())){
            localDateTime = req.getCreateTimeTo().atTime(23, 59, 59);
        }
        //查询今天的数据
        List<EclExecuteRecord> list = this.list(Wrappers.<EclExecuteRecord>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(req.getClientName()), EclExecuteRecord::getClientName, req.getClientName())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), EclExecuteRecord::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getInnerMdLevel()), EclExecuteRecord::getInnerMdLevel, req.getInnerMdLevel())
                .eq(ObjectUtil.isNotEmpty(req.getGroup()), EclExecuteRecord::getGroup, req.getGroup())
                .eq(ObjectUtil.isNotEmpty(req.getClassify()), EclExecuteRecord::getClassify, req.getClassify())
                .eq(ObjectUtil.isNotEmpty(req.getEclStep()), EclExecuteRecord::getEclStep, req.getEclStep())
                .ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), EclExecuteRecord::getUpdateTime, req.getCreateTimeFrom())
                .le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), EclExecuteRecord::getUpdateTime, localDateTime)
                .eq(ObjectUtil.isNotEmpty(req.getLeaseType()), EclExecuteRecord::getLeaseType, req.getLeaseType())
                .ge(EclExecuteRecord::getUpdateTime, LocalDate.now()));
        if (ObjectUtil.isEmpty(list)) {
            return null;
        }
        //查询每个拨备下最新的一条记录
        EclExecuteRecordParam param = new EclExecuteRecordParam();
        param.setClientName(req.getClientName());
        param.setContractCode(req.getContractCode());
        param.setCreateTimeFrom(LocalDate.now());
        List<EclExecuteRecordLib> eclExecuteRecordLibPage = eclExecuteRecordLibMapper.listCompare(param);
        if (ObjectUtil.isEmpty(eclExecuteRecordLibPage)) {
            return list;
        }
        Map<Long, EclExecuteRecordLib> kpiId2EclExecuteRecordMap = eclExecuteRecordLibPage.stream().collect(Collectors.toMap(EclExecuteRecordLib::getKpiProvisionDetailId, e -> e, (a, b) -> b));
        List<EclExecuteRecord> changeList = new ArrayList<>();
        list.forEach(e -> {
            EclExecuteRecordLib lib = kpiId2EclExecuteRecordMap.get(e.getKpiProvisionDetailId());
            if (isChange(e, lib)) {
                changeList.add(e);
            }
        });
        return changeList;
    }

    private boolean isChange(EclExecuteRecord eclExecuteRecord, EclExecuteRecordLib eclExecuteRecordLib) {
        if (ObjectUtil.isEmpty(eclExecuteRecordLib)) {
            return true;
        }
        EclExecuteRecord old = BeanUtil.copyProperties(eclExecuteRecordLib, EclExecuteRecord.class);
        old.setId(eclExecuteRecordLib.getOriginId());
        Map<String, DiffValue> compare = CompareUtil.compare(eclExecuteRecord, old, ignoreField);
        for(DiffValue diffValue : compare.values()) {
            if (ObjectUtil.isNotEmpty(diffValue) && diffValue.getIsChange()) {
                return true;
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(EclExecuteRecordRemoveREQ req) {
        LambdaUpdateWrapper<EclExecuteRecord> libLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        libLambdaUpdateWrapper.set(EclExecuteRecord::getDeleted, YesOrNoNumberEnum.YES.getCode());
        libLambdaUpdateWrapper.eq(EclExecuteRecord::getId, req.getId());
        this.update(null, libLambdaUpdateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void calculate(List<EclExecuteRecord> eclExecuteRecords) {
        if (ObjectUtil.isEmpty(eclExecuteRecords)) {
            return;
        }
        LocalDate now = LocalDate.now();
        //转为模型参数
        eclExecuteRecords.forEach(e -> {
            KpiExpectedLossDecisionQuery query = BeanUtil.copyProperties(e, KpiExpectedLossDecisionQuery.class);
            DecisionExecuteEclResult result = decisionService.eclExecute(query);
            if (ObjectUtil.isNotEmpty(e.getLateDate())) {
                query.setLate_day(ChronoUnit.DAYS.between(e.getLateDate() ,now));
            }
            if(result == null){
                log.warn("EclExecuteRecordService calculate EclExecuteRecord: {}", e);
            }

        });
    }


}