package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.margin.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.GuaranteeMethodEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.enums.PaymentMethod;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractGuarantorMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentEstimateMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAccountLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentEstimateLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.payment.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.payment.mapper.lib.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.model.contract.ContractAccountLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimateLib;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.payment.mapper.model.PaymentPlanedDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentPlanedDetailLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentPlanedDetailMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.contract.ContractService;
import cn.zswltech.mithras.contract.versioning.application.ContractVersionService;
import cn.zswltech.mithras.payment.application.lib.service.impl.PaymentVersionServiceImpl;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.zswltech.mithras.others.hand.extract.ImportCommonHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static cn.zswltech.mithras.others.hand.extract.ImportCommonHelper.*;

/**
 * 合同数据、收付款导入
 *
 * @author wangchuanhao
 * @date 2022/9/25 10:05 AM
 */
@Component
@Slf4j
public class ContractPaymentImporter {

    @Resource
    private ImportCommonHelper importCommonHelper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewLeasePriceMapper projReviewLeasePriceMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private ContractRentEstimateMapper contractRentEstimateMapper;
    @Resource
    private ContractRentEstimateLibMapper contractRentEstimateLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoLibMapper paymentBaseInfoLibMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private ContractTenantryMapper contractTenantryMapper;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private ContractAccountMapper contractAccountMapper;
    @Resource
    private ContractAccountLibMapper contractAccountLibMapper;
    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;
    @Resource
    private ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    private PaymentPlanedDetailMapper paymentPlanedDetailMapper;
    @Resource
    private PaymentPlanedDetailLibMapper paymentPlanedDetailLibMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private PaymentVersionServiceImpl paymentVersionService;
    @Resource
    private ContractService contractService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @SneakyThrows
    public void initBaseData() {
        Map<String, Long> userIdMap = importCommonHelper.queryUserIdMap();
        Map<Long, Long> userOrgMap = importCommonHelper.queryUserOrgMap(userIdMap.values());
        Map<String, Long> orgIdMap = importCommonHelper.queryOrgMap();
        Map<String, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
//                        .in(Client::getHandImportFlag, Arrays.asList(1, 2))
                )
                .stream().collect(Collectors.toMap(Client::getClientName, c -> c, (k1,k2) -> k1));
        Map<String, ProjReviewBaseInfo> projReviewMap = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()).stream()
                .collect(Collectors.toMap(ProjReviewBaseInfo::getProjName, c -> c, (k1,k2) -> k1));
        Map<Long, ProjReviewLeasePrice> projReviewPriceMap = projReviewLeasePriceMapper.selectList(Wrappers.<ProjReviewLeasePrice>lambdaQuery()).stream()
                .collect(Collectors.toMap(ProjReviewLeasePrice::getId, c -> c, (k1,k2) -> k1));

        String handContractDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得合同模块导出_20220928.json").getInputStream(), Charset.defaultCharset());
        JSONArray handContractDataArray = JSONArray.parseArray(handContractDataString);
        JSONObject handContractDataAllObj = new JSONObject();
        for (int i = 0; i < handContractDataArray.size(); i++) {
            JSONObject handContractDataObj = handContractDataArray.getJSONObject(i);
            handContractDataObj.put("contract_number", handContractDataObj.getString("contract_number").trim().replaceAll("（", "(").replaceAll("）", ")"));
            handContractDataAllObj.put(handContractDataObj.getString("contract_number"), handContractDataObj);
        }
        String handPaymentDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/数据导入/汉得付款模块导出_20220923.json").getInputStream(), Charset.defaultCharset());
        JSONArray handPaymentDataArray = JSONArray.parseArray(handPaymentDataString);
        JSONObject paymentArrayObj = new JSONObject();
        for (int i = 0; i < handPaymentDataArray.size(); i++) {
            JSONObject paymentObj = handPaymentDataArray.getJSONObject(i);
            paymentArrayObj.put(paymentObj.getString("contract_number").trim().replaceAll("（", "(").replaceAll("）", ")"), paymentObj);
        }
        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理.xlsx"));
        Set<String> sheetNameSheet = new HashSet<>(tzExcelReader.getSheetNames());
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
        Set<String> allHandContractCode = new HashSet<>();
        for (List<Object> rowData : contractSheetList) {
            if (rowData.size() > 5
                    && rowData.get(5) instanceof String
                    && ((String) rowData.get(5)).contains("号")
            ) {
                String handContractCode = ((String) rowData.get(5)).trim().replaceAll("（", "(").replaceAll("）", ")");
                if (allHandContractCode.contains(handContractCode)) {
                    // 已经有一条了， 后面的不处理，取第一条
                    continue;
                }
                allHandContractCode.add(handContractCode);
                JSONObject rowDataObj = new JSONObject();
                excelDataObj.put(handContractCode, rowDataObj);
                rowDataObj.put("actualContractCode", ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")"));
                rowDataObj.put("detailSheetName", ((String) rowData.get(9)).replaceAll("（", "(").replaceAll("）", ")"));
                rowDataObj.put("projectClassify", rowData.get(0));
                rowDataObj.put("contractStatus", rowData.get(44));
                rowDataObj.put("actualFinishDate", rowData.get(45));
            }
        }
//        System.out.println(excelDataObj);

        Set<String> errContractCodeSet = new HashSet<>(Arrays.asList("中拓租【2018】租字第(C-0006)号"/*, "浙商租【2022】租字第(C-0031)号", "浙商租【2022】租字第(C-0030)号", "浙商租【2022】租字第(C-0026)号", "浙商租【2022】租字第(C-0024)号", "浙商租【2021】租字第(A-0053)号", "浙商租【2021】租字第(A-0019)号"*/));
        Set<String> needHandleContractCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(A-0067)号","中拓租【2019】租字第(A-0002)号"));

        transactionTemplate.execute(status -> {
            try {
                for (int i = 0; i < handContractDataArray.size(); i++) {
                    JSONObject handContractDataObj = handContractDataArray.getJSONObject(i);
                    handContractDataObj.put("contract_number", handContractDataObj.getString("contract_number").trim().replaceAll("（", "(").replaceAll("）", ")"));
                    log.info("处理合同编号{}:{}", i, handContractDataObj.getString("contract_number"));
                    if (!allHandContractCode.contains(handContractDataObj.getString("contract_number"))) {
                        log.info("台账不包含数据:{}", handContractDataObj.getString("contract_number"));
                        continue;
                    }
                    JSONObject excelSimpleObj = excelDataObj.getJSONObject(handContractDataObj.getString("contract_number"));
                    String detailSheetName = excelSimpleObj.getString("detailSheetName");
                    if (!sheetNameSheet.contains(detailSheetName)) {
                        log.error("sheet不存在:{}", detailSheetName);
                        continue;
                    }
                    if (errContractCodeSet.contains(handContractDataObj.getString("contract_number"))) {
                        log.info("错误数据不导入:{}", handContractDataObj.getString("contract_number"));
                        continue;
                    }
                    if (!needHandleContractCodeSet.contains(excelSimpleObj.getString("actualContractCode"))) {
                        continue;
                    }
                    log.info("需要处理的数据:{}, 实际编号:{}", handContractDataObj.getString("contract_number"), excelSimpleObj.getString("actualContractCode"));
                    tzExcelReader.setSheet(detailSheetName);
                    List<List<Object>> detailSheetDataList = tzExcelReader.read();
                    JSONObject priceObj = handContractDataObj.getJSONArray("priceArray").getJSONObject(0);
                    ProjReviewBaseInfo projReviewBaseInfo = projReviewMap.getOrDefault(handContractDataObj.getString("project_name"), new ProjReviewBaseInfo());
                    ProjReviewLeasePrice projReviewLeasePrice = projReviewPriceMap.getOrDefault(projReviewBaseInfo.getId(), new ProjReviewLeasePrice());

                    // 创建人 根据名字拿，拿不到说明已离职 就先用管理员
                    Long createByUserId = userIdMap.getOrDefault(handContractDataObj.getString("employee_id_n"), 3L);
                    // 创建部门 管理员创建的需找到汉得实际创建部门 非管理员创建的直接拿系统中该用户的部门
                    Long createByDeptId = Objects.equals(3L, createByUserId) ? orgIdMap.get(handContractDataObj.getString("lease_organization_n")) : userOrgMap.get(createByUserId);
                    Long bizDeptLeaderId = sysUserService.getUserIdByOrgJob(createByDeptId, JobEnum.businesshead.name());
                    Long bizDivisionLeaderId = sysUserService.getUserIdByOrgJob(createByDeptId, JobEnum.leaderincharge.name());

                    // 基本信息
                    ContractBaseInfo contractBaseInfo = new ContractBaseInfo();
                    String baseClientName = handContractDataObj.getString("bp_id_tenant_n").trim();
                    if ("阳光王子（寿光）特种纸有限公司,上海裕亿机械设备有限公司".equals(baseClientName)) {
                        baseClientName = "阳光王子（寿光）特种纸有限公司";
                    }
                    contractBaseInfo.setClientId(Optional.ofNullable(clientMap.get(baseClientName)).map(Client::getId).orElse(null));
                    if (contractBaseInfo.getClientId() == null) {
                        log.error("合同对应的客户数据缺失:{}, 合同编号:{}", handContractDataObj.getString("bp_id_tenant_n"), handContractDataObj.getString("contract_number"));
                    }
                    contractBaseInfo.setContractCode(excelSimpleObj.getString("actualContractCode"));
                    contractBaseInfo.setProjName(handContractDataObj.getString("project_name"));
                    contractBaseInfo.setProjCode(handContractDataObj.getString("project_number"));
                    contractBaseInfo.setBizType("ZL");
                    contractBaseInfo.setLeaseType(ContractPaymentImporterHelper.extractLeaseType(handContractDataObj.getString("business_type_n")));
                    contractBaseInfo.setProjectType(projReviewBaseInfo.getProjectType());
                    contractBaseInfo.setProjSource(projReviewBaseInfo.getProjSource());
                    contractBaseInfo.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
                    contractBaseInfo.setProjBackground(projReviewBaseInfo.getProjBackground());
                    contractBaseInfo.setProjReviewId(projReviewBaseInfo.getId());
                    contractBaseInfo.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractBaseInfo.setProjSponsorUserId(createByUserId);
                    contractBaseInfo.setProjCosponsorUserIds(ContractPaymentImporterHelper.extractSponsorUserIds(handContractDataObj, userIdMap));
                    contractBaseInfo.setBizDeptId(createByDeptId);
                    contractBaseInfo.setBizDeptLeaderId(bizDeptLeaderId);
                    contractBaseInfo.setBizDivisionLeaderId(bizDivisionLeaderId);
                    contractBaseInfo.setContractStatus(ContractStatus.NEW.name());
//                    contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.NEW_UNCOMMIT.name());
                    contractBaseInfo.setProjItem(ContractPaymentImporterHelper.extractProjClassify(excelSimpleObj.getString("projectClassify")));
                    contractBaseInfo.setEstimatedLeaseDate(Optional.ofNullable(priceObj.getString("lease_start_date")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).map(LocalDateTime::toLocalDate).orElse(null));
                    contractBaseInfo.setApplyCreditAmount(Optional.ofNullable(detailSheetDataList.get(0).get(12)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractBaseInfo.setPaymentPlanDate(contractBaseInfo.getEstimatedLeaseDate());
                    contractBaseInfo.setActualLeaseDate(Optional.ofNullable(detailSheetDataList.get(3).get(14)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    contractBaseInfo.setActualFinishDate(Optional.ofNullable(excelSimpleObj.get("actualFinishDate")).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    contractBaseInfo.setCreateBy(createByUserId);
                    contractBaseInfo.setUpdateBy(createByUserId);
                    contractBaseInfoMapper.insert(contractBaseInfo);

                    // 报价信息
                    ContractLeasePrice contractLeasePrice = new ContractLeasePrice();
                    contractLeasePrice.setContractId(contractBaseInfo.getId());
                    contractLeasePrice.setCreateBy(createByUserId);
                    contractLeasePrice.setUpdateBy(createByUserId);

                    contractLeasePrice.setApplyCreditAmount(Optional.ofNullable(detailSheetDataList.get(0).get(12)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setLeaseMonthCount(Optional.ofNullable(detailSheetDataList.get(0).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(12L)).intValue()).orElse(null));
                    contractLeasePrice.setConsultingFee(Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setDownPayment(Optional.ofNullable(detailSheetDataList.get(2).get(12)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setLeaseRatePercent(Optional.ofNullable(detailSheetDataList.get(1).get(14)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));
                    contractLeasePrice.setEarnestMoney(Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setNominalPrice(Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setIrrPercent(Optional.ofNullable(detailSheetDataList.get(2).get(18)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));
                    contractLeasePrice.setDefaultInterestRate(Optional.ofNullable(detailSheetDataList.get(3).get(16)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));

                    contractLeasePrice.setRepayRate(ContractPaymentImporterHelper.extractRepayRate(priceObj.getString("annual_pay_times_n")));
                    contractLeasePrice.setRepayTimesTotal(priceObj.getInteger("lease_times"));
                    contractLeasePrice.setRentalCalcType(ContractPaymentImporterHelper.extractRentalCalcType(priceObj.getString("price_list_n")));
                    contractLeasePrice.setRateType(ContractPaymentImporterHelper.extractRateType(priceObj.getString("int_rate_display_n")));
                    contractLeasePrice.setLprType(ContractPaymentImporterHelper.extractLprType(priceObj.getString("lpr_rate_type_n")));
                    contractLeasePrice.setLprPercent(Optional.ofNullable(priceObj.getString("base_rate_id_n")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(100 * 10000)).intValue()).orElse(null));
                    contractLeasePrice.setLprAddPercent(Optional.ofNullable(priceObj.getString("floating_way_range")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(100 * 10000 / 10000)).intValue()).orElse(null));

                    contractLeasePrice.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractLeasePrice.setPayType(projReviewLeasePrice.getPayType());
                    contractLeasePrice.setProjCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
                    contractLeasePrice.setProjDownPayment(projReviewLeasePrice.getDownPayment());
                    contractLeasePrice.setProjConsultingFee(projReviewLeasePrice.getConsultingFee());
                    contractLeasePrice.setProjEarnestMoney(projReviewLeasePrice.getEarnestMoney());
                    contractLeasePrice.setProjIrrPercent(projReviewLeasePrice.getIrrPercent());
                    contractLeasePrice.setProjLeaseMonthCount(projReviewLeasePrice.getLeaseMonthCount());
                    contractLeasePriceMapper.insert(contractLeasePrice);

                    // 概算租金表
                    JSONArray cashflowArray = handContractDataObj.getJSONArray("cashflowArray");
                    List<ContractRentEstimate> contractRentEstimateList = new ArrayList<>();
                    cashflowArray.forEach(d -> {
                        JSONObject handCashflowObj = (JSONObject)d;
                        if (!"租金".equals(handCashflowObj.getString("cf_item_n"))) {
                            return;
                        }
                        ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
                        contractRentEstimate.setContractId(contractBaseInfo.getId());
                        contractRentEstimate.setCashFlowDate(extractDate(handCashflowObj.getString("due_date")));
                        contractRentEstimate.setCashFlowPhase(handCashflowObj.getInteger("times"));
                        contractRentEstimate.setRent(Optional.ofNullable(handCashflowObj.getString("due_amount")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(10000)).longValue()).orElse(null));
                        contractRentEstimate.setPrincipal(Optional.ofNullable(handCashflowObj.getString("principal")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(10000)).longValue()).orElse(null));
                        contractRentEstimate.setInterest(Optional.ofNullable(handCashflowObj.getString("interest")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(10000)).longValue()).orElse(null));
                        contractRentEstimate.setRemainingPrincipal(Optional.ofNullable(handCashflowObj.getString("outstanding_principal")).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(10000)).longValue()).orElse(null));
                        contractRentEstimate.setCreateBy(createByUserId);
                        contractRentEstimate.setUpdateBy(createByUserId);
                        contractRentEstimateList.add(contractRentEstimate);
                    });
                    contractRentEstimateList.forEach(contractRentEstimateMapper::insert);

                    // 承租人
                    JSONArray handBpArray = handContractDataObj.getJSONArray("clientArray");
                    List<ContractTenantry> contractTenantryList = handBpArray.stream()
                            .map(j -> (JSONObject)j)
                            .map(j -> {
                                if (!"承租人".equals(j.getString("bp_category_n")) && !"联合承租人".equals(j.getString("bp_category_n"))) {
                                    // 非承租人 返回空 下个阶段剔除
                                    return null;
                                }
                                Client client = clientMap.get(Optional.ofNullable(j.getString("bp_name_n")).map(String::trim).orElse(null));
                                // 如果客户不存在 先跳过
                                if (client == null) {
                                    log.error("缺少客户数据-承租人,合同编号:{},客户:{}", handContractDataObj.getString("contract_number"), j.getString("bp_name_n"));
                                    return null;
                                }
                                ContractTenantry contractTenantry = new ContractTenantry();
                                contractTenantry.setContractId(contractBaseInfo.getId());
                                contractTenantry.setLesseeId(client.getId());
                                contractTenantry.setLesseeType("承租人".equals(j.getString("bp_category_n")) ? LesseeTypeEnum.MAIN_LESSSEE.name() : LesseeTypeEnum.JOINT_LESSEE.name());
                                contractTenantry.setLesseeName(client.getClientName());
                                contractTenantry.setIsReport(Optional.ofNullable(j.getString("credit_reporting_flag_n")).map(s -> "是".equals(s) ? 1 : 0).orElse(null));
                                return contractTenantry;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if (contractTenantryList.stream()
                            .filter(b -> contractBaseInfo.getClientId().equals(b.getLesseeId()))
                            .count() == 0) {
                        // 主承租人没维护 维护下
                        ContractTenantry contractTenantry = new ContractTenantry();
                        contractTenantry.setContractId(contractBaseInfo.getId());
                        contractTenantry.setLesseeId(contractBaseInfo.getClientId());
                        contractTenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
                        contractTenantry.setLesseeName(Optional.ofNullable(clientMap.get(contractBaseInfo.getClientId())).map(Client::getClientName).orElse(null));
                        contractTenantry.setIsReport(null);
                        contractTenantryList.add(0, contractTenantry);
                    }
                    contractTenantryList.forEach(contractTenantryMapper::insert);

                    // 收款账户
                    JSONArray handAccountArray = handContractDataObj.getJSONArray("bankArray");
                    List<ContractAccount> contractAccountList = handAccountArray.stream()
                            .map(j -> (JSONObject)j)
                            .map(j -> {
                                ContractAccount contractAccount = new ContractAccount();
                                contractAccount.setContractId(contractBaseInfo.getId());
                                contractAccount.setAccountAddress(j.getString("bank_full_name"));
                                contractAccount.setAccountNum(j.getString("bank_account_num"));
                                contractAccount.setAccountName(j.getString("bank_account_name"));
                                contractAccount.setCreateBy(createByUserId);
                                contractAccount.setUpdateBy(createByUserId);
                                return contractAccount;
                            }).collect(Collectors.toList());
                    contractAccountList.forEach(contractAccountMapper::insert);

                    // 担保措施
                    // 担保措施 = 客户数组（bpArray）担保人 + 担保措施（guaranteeArray）
                    List<ContractGuarantor> contractGuarantorList = new ArrayList<>();
                    JSONArray handGuaranteeArray = handContractDataObj.getJSONArray("guaranteeArray");

                    Map<String, ContractGuarantor> guaranteeNameMap = new HashMap<>();
                    Map<Long, Integer> isReportMap = new HashMap<>();
                    contractGuarantorList.addAll(handGuaranteeArray.stream()
                            .map(j -> (JSONObject)j)
                            .map(j -> {
                                ContractGuarantor contractGuarantor = new ContractGuarantor();
                                String guarantorName = Optional.ofNullable(j.getString("bp_id_n")).map(String::trim).orElse(null);
                                if ("浙商租【2021】租字第(C-0022)号".equals(handContractDataObj.getString("contract_number"))
                                        && "余慧君".equals(guarantorName)) {
                                    guarantorName = "余惠君";
                                }
                                Client client = clientMap.get(guarantorName);
                                // 如果客户不存在 先跳过
                                if (client == null) {
                                    log.error("缺少客户数据-担保人,合同编号:{},客户:{}", handContractDataObj.getString("contract_number"), j.getString("bp_id_n"));
                                    return null;
                                }
                                contractGuarantor.setContractId(contractBaseInfo.getId());
                                contractGuarantor.setGuarantorType(client.getClientType());
                                contractGuarantor.setGuarantorIds(JSON.toJSONString(Arrays.asList(client.getId())));
                                contractGuarantor.setGuaranteeMethod(GuaranteeMethodEnum.JOINT_RESPONSIBILITY.name());
                                contractGuarantor.setIsReport(Optional.ofNullable(j.getString("credit_reporting_flag_n")).map(s -> "是".equals(s) ? 1 : 0).orElse(null));
                                isReportMap.put(client.getId(), contractGuarantor.getIsReport());
                                contractGuarantor.setCreateBy(createByUserId);
                                contractGuarantor.setUpdateBy(createByUserId);

                                guaranteeNameMap.put(client.getClientName(), contractGuarantor);
                                return contractGuarantor;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));

                    contractGuarantorList.addAll(handBpArray.stream()
                            .map(j -> (JSONObject)j)
                            .map(j -> {
                                if (!"担保人".equals(j.getString("bp_category_n"))) {
                                    // 非担保人 返回空 下个阶段剔除
                                    return null;
                                }
                                String guarantorName = Optional.ofNullable(j.getString("bp_id_n")).map(String::trim).orElse(null);
                                if ("浙商租【2021】租字第(C-0022)号".equals(handContractDataObj.getString("contract_number"))
                                        && "余慧君".equals(guarantorName)) {
                                    guarantorName = "余惠君";
                                }
                                Client client = clientMap.get(guarantorName);
                                // 如果客户不存在 先跳过
                                if (client == null) {
                                    log.error("缺少客户数据-担保人,合同编号:{},客户:{}", handContractDataObj.getString("contract_number"), j.getString("bp_id_n"));
                                    return null;
                                }
                                if (guaranteeNameMap.keySet().contains(j.getString("bp_name_n"))) {
                                    // 担保措施模块维护过了 不再维护 更新上报征信的标识
                                    ContractGuarantor existGuarantor = guaranteeNameMap.get(j.getString("bp_name_n"));
                                    if (!Objects.equals(1, existGuarantor.getIsReport())) {
                                        existGuarantor.setIsReport(Optional.ofNullable(j.getString("credit_reporting_flag_n")).map(s -> "是".equals(s) ? 1 : 0).orElse(null));
                                    }
                                    return null;
                                }
                                ContractGuarantor contractGuarantor = new ContractGuarantor();
                                contractGuarantor.setContractId(contractBaseInfo.getId());
                                contractGuarantor.setGuarantorType(client.getClientType());
                                contractGuarantor.setGuarantorIds(JSON.toJSONString(Arrays.asList(client.getId())));
                                contractGuarantor.setGuaranteeMethod(GuaranteeMethodEnum.JOINT_RESPONSIBILITY.name());
                                contractGuarantor.setIsReport(Optional.ofNullable(j.getString("credit_reporting_flag_n")).map(s -> "是".equals(s) ? 1 : 0).orElse(null));
                                contractGuarantor.setCreateBy(createByUserId);
                                contractGuarantor.setUpdateBy(createByUserId);
                                return contractGuarantor;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
                    contractGuarantorList.forEach(contractGuarantorMapper::insert);

                    // 租赁物清单 暂不处理 汉得没看到有数据

                    // 付款主表
//                    JSONObject paymentObj = paymentArrayObj.getJSONObject(handContractDataObj.getString("contract_number"));
//                    if (paymentObj == null) {
//                        log.error("没有付款数据:{}", handContractDataObj.getString("contract_number"));
//                        continue;
//                    }
//                    JSONArray paymentFlowArray = paymentObj.getJSONArray("paymentFlowArray");
                    PaymentBaseInfo paymentBaseInfo = new PaymentBaseInfo();
                    paymentBaseInfo.setPaymentCode(PaymentBaseInfoService.generatePaymentCode(contractBaseInfo.getContractCode(), 1));
                    paymentBaseInfo.setContractId(contractBaseInfo.getId());
                    paymentBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                    paymentBaseInfo.setClientId(contractBaseInfo.getClientId());
                    paymentBaseInfo.setPayables("授信款");
                    paymentBaseInfo.setApplyPaymentDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd HH:mm:ss")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null));
                    paymentBaseInfo.setApplyPaymentAmount(Optional.ofNullable(detailSheetDataList.get(5).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setEarnestMoney(Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setDownPayment(Optional.ofNullable(detailSheetDataList.get(2).get(12)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setConsultingFee(Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setNominalPrice(Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setPaymentProcessStatus(ProcessStatus.UN_SUBMIT.name());
                    paymentBaseInfo.setPaymentStatus(PaymentStatusEnum.NEW.name());
                    paymentBaseInfo.setPaidInDate(contractBaseInfo.getEstimatedLeaseDate());
                    paymentBaseInfo.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                    paymentBaseInfo.setActualDetailCount(0);
                    paymentBaseInfo.setConBizDeptId(createByDeptId);
                    paymentBaseInfo.setConBizDeptLeaderId(bizDeptLeaderId);
                    paymentBaseInfo.setConBizDivisionLeaderId(bizDivisionLeaderId);
                    paymentBaseInfo.setCreateBy(createByUserId);
                    paymentBaseInfo.setUpdateBy(createByUserId);
                    paymentBaseInfoMapper.insert(paymentBaseInfo);

                    // 合同 - 借据
                    ContractReceipt contractReceipt = new ContractReceipt();
                    contractReceipt.setContractId(contractBaseInfo.getId());
                    contractReceipt.setPaymentApplyCode(paymentBaseInfo.getPaymentCode());
                    contractReceipt.setCreateBy(createByUserId);
                    contractReceipt.setUpdateBy(createByUserId);
                    contractReceiptMapper.insert(contractReceipt);

                    // 合同实际租金表 需要先处理付款主表 然后新增借据 然后再插实际租金表
                    List<ContractRentActual> rentActualList = new ArrayList<>();
                    Map<Integer, ContractRentActual> rentActualMap = new HashMap<>();
                    for (int rentRow = 6;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        ContractRentActual rentActual = new ContractRentActual();
                        // 借据id
                        rentActual.setReceiptId(contractReceipt.getId());
                        rentActual.setContractId(contractBaseInfo.getId());
                        rentActual.setCashFlowPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d)).map(b -> b.intValue()).orElse(null));
                        rentActual.setCashFlowCode(ContractPaymentImporterHelper.getCashFlowCode(paymentBaseInfo.getPaymentCode(), rentActual.getCashFlowPhase()));
                        rentActual.setCashFlowDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        rentActual.setRent(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setRemainingPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setCreateBy(createByUserId);
                        rentActual.setUpdateBy(createByUserId);

                        rentActualList.add(rentActual);
                        rentActualMap.put(rentActual.getCashFlowPhase(), rentActual);
                    }
                    rentActualList.forEach(contractRentActualMapper::insert);

                    // 付款计划
//                    List<PaymentPlanedDetail> paymentPlanedDetailList = new ArrayList<>();
//                    paymentFlowArray.forEach(d -> {
//                        JSONObject paymentFlowObj = (JSONObject) d;
//                        PaymentPlanedDetail paymentPlanedDetail = new PaymentPlanedDetail();
//                        paymentPlanedDetail.setPaymentId(paymentBaseInfo.getId());
//                        paymentPlanedDetail.setOppositeAccount(paymentFlowObj.getString("bp_bank_account_num"));
//                        paymentPlanedDetail.setOppositeAccountName(paymentFlowObj.getString("bp_bank_account_name"));
//                        paymentPlanedDetail.setOppositeAccountBank(paymentFlowObj.getString("bp_bank_branch_name"));
//                        paymentPlanedDetail.setPaymentMethod(PaymentMethod.WIRE_TRANSFER.name());
//                        paymentPlanedDetail.setPaymentAmount(extractAmount("payment_amount"));
//                        paymentPlanedDetail.setPostscript(paymentFlowObj.getString("remarks"));
//                        paymentPlanedDetail.setCreateBy(createByUserId);
//                        paymentPlanedDetail.setUpdateBy(createByUserId);
//                        paymentPlanedDetailList.add(paymentPlanedDetail);
//                    });
//                    paymentPlanedDetailList.forEach(paymentPlanedDetailMapper::insert);

                    // 实际支付记录
                    PaymentActualDetail paymentActualDetail = new PaymentActualDetail();
                    paymentActualDetail.setPaymentId(paymentBaseInfo.getId());
                    paymentActualDetail.setSeqCode("01");
                    paymentActualDetail.setContractId(contractBaseInfo.getId());
                    paymentActualDetail.setInfoSource("手动录入");
                    paymentActualDetail.setPaymentMethod(PaymentMethod.WIRE_TRANSFER.name());
                    paymentActualDetail.setPaidInDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    paymentActualDetail.setPaidInAmount(Optional.ofNullable(detailSheetDataList.get(5).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentActualDetail.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                    paymentActualDetail.setCreateBy(createByUserId);
                    paymentActualDetail.setUpdateBy(createByUserId);
                    paymentActualDetailMapper.insert(paymentActualDetail);

                    // 收款核销 主表
                    List<CollectionBaseInfo> collectionBaseInfoList = new ArrayList<>();
                    int rentRecordCount = 0;
                    for (int rentRow = 6;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
                        collectionBaseInfo.setContractId(contractBaseInfo.getId());
                        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
                        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
                        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());
                        collectionBaseInfo.setCashFlowItem(CashFlowItemEnum.RENT.name());
                        collectionBaseInfo.setPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d)).map(b -> b.intValue()).orElse(null));
                        collectionBaseInfo.setCode(ContractPaymentImporterHelper.getCollectionCode(collectionBaseInfo.getCashFlowItem(), collectionBaseInfo.getPaymentCode(), collectionBaseInfo.getPhase(), collectionBaseInfo.getContractCode()));

                        collectionBaseInfo.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        collectionBaseInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(19)).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        collectionBaseInfo.setCashFlowAmount(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPlanCollectionAmount(collectionBaseInfo.getCashFlowAmount());
                        collectionBaseInfo.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPenaltyInterestUpdate(1);
                        collectionBaseInfo.setPenaltyInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(31)).map(d -> "-".equals(d) ? null : d).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setRentActualId(rentActualMap.get(collectionBaseInfo.getPhase()).getId());
                        collectionBaseInfo.setCreateBy(createByUserId);
                        collectionBaseInfo.setUpdateBy(createByUserId);
                        rentRecordCount++;

                        List<CollectionRecordInfo> recordList = ContractPaymentImporterHelper.buildCollectionRecordList(detailSheetDataList, collectionBaseInfo, rentRow);
                        collectionBaseInfo.setAllRecordSort(recordList.size());
                        collectionBaseInfo.setCollectionAmount(recordList.isEmpty() ? 0L : LongUtil.null2zero(collectionBaseInfo.getPrincipal()) + LongUtil.null2zero(collectionBaseInfo.getInterest()) + LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                        collectionBaseInfo.setWriteOffStatus(recordList.size() > 0 ? CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name() : CollectionWriteOffStatusEnum.UNCOLLECTION.name());
                        collectionBaseInfo.setCollectionPrincipal(recordList.stream().mapToLong(CollectionRecordInfo::getPrincipal).sum());
                        collectionBaseInfo.setCollectionInterest(recordList.stream().mapToLong(CollectionRecordInfo::getInterest).sum());
                        collectionBaseInfo.setCollectionPenaltyInterest(recordList.stream().mapToLong(CollectionRecordInfo::getPenaltyInterest).sum());
                        collectionBaseInfoMapper.insert(collectionBaseInfo);
                        recordList.forEach(r -> {
                            r.setCollectionId(collectionBaseInfo.getId());
                            collectionRecordInfoMapper.insert(r);
                        });
                        collectionBaseInfoList.add(collectionBaseInfo);
                    }
                    // 收款核销 主表 咨询费
                    Long otherAmount = Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (otherAmount > 0) {
                        CollectionBaseInfo otherAmountCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, paymentBaseInfo, CashFlowItemEnum.OTHERAMOUNT, createByUserId);
                        otherAmountCbi.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        otherAmountCbi.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        otherAmountCbi.setCashFlowAmount(otherAmount);
                        otherAmountCbi.setCollectionAmount(otherAmount);
                        otherAmountCbi.setPlanCollectionAmount(otherAmount);
                        otherAmountCbi.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        otherAmountCbi.setAllRecordSort(1);
                        collectionBaseInfoMapper.insert(otherAmountCbi);
                        // 明细
                        CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(otherAmountCbi);
                        recordInfo.setCollectionDate(otherAmountCbi.getCollectionDate());
                        recordInfo.setCollectionAmount(otherAmountCbi.getCollectionAmount());
                        collectionRecordInfoMapper.insert(recordInfo);

                        collectionBaseInfoList.add(otherAmountCbi);
                    }
                    // 收款核销 主表 名义货价
                    Long nominalPrice = Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (nominalPrice > 0) {
                        CollectionBaseInfo nominalPriceCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, paymentBaseInfo, CashFlowItemEnum.NOMINAL_PRICE, createByUserId);
                        nominalPriceCbi.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        nominalPriceCbi.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        nominalPriceCbi.setCashFlowAmount(nominalPrice);
                        nominalPriceCbi.setCollectionAmount(nominalPrice);
                        nominalPriceCbi.setPlanCollectionAmount(nominalPrice);
                        nominalPriceCbi.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        nominalPriceCbi.setAllRecordSort(1);
                        collectionBaseInfoMapper.insert(nominalPriceCbi);
                        // 明细
                        CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(nominalPriceCbi);
                        recordInfo.setCollectionDate(nominalPriceCbi.getCollectionDate());
                        recordInfo.setCollectionAmount(nominalPriceCbi.getCollectionAmount());
                        collectionRecordInfoMapper.insert(recordInfo);

                        collectionBaseInfoList.add(nominalPriceCbi);
                    }

                    // 保证金收款记录 主表 保证金
                    Long earnestMoney = Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (earnestMoney > 0) {
                        MarginBaseInfo marginBaseInfo = new MarginBaseInfo();
                        marginBaseInfo.setContractId(contractBaseInfo.getId());
                        marginBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        marginBaseInfo.setMarginCode(ContractPaymentImporterHelper.getBzjCode(contractBaseInfo.getContractCode()));
                        marginBaseInfo.setClientId(contractBaseInfo.getClientId());
                        marginBaseInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginBaseInfo.setPlanMarginDate(marginBaseInfo.getCollectionDate());
                        marginBaseInfo.setCollectionAmount("已结清".equals(excelSimpleObj.getString("contractStatus")) ? 0L : earnestMoney);
                        marginBaseInfo.setPlanMarginAmount(earnestMoney);
                        marginBaseInfo.setContractIsSettle("已结清".equals(excelSimpleObj.getString("contractStatus")) ? 1 : 0);
                        marginBaseInfo.setBackAmount("已结清".equals(excelSimpleObj.getString("contractStatus")) ? earnestMoney : 0L);
                        marginBaseInfo.setCreateBy(createByUserId);
                        marginBaseInfo.setUpdateBy(createByUserId);
                        marginBaseInfoMapper.insert(marginBaseInfo);
                        // 明细
                        // 收款
                        MarginRecordInfo marginCollectionInfo = new MarginRecordInfo();
                        marginCollectionInfo.setMarginId(marginBaseInfo.getId());
                        marginCollectionInfo.setDataSource("3");
                        marginCollectionInfo.setSourceFlag(1);
                        marginCollectionInfo.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
                        marginCollectionInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
                        marginCollectionInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginCollectionInfo.setCollectionAmount(earnestMoney);
                        marginCollectionInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        marginCollectionInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                        marginCollectionInfo.setOurAccountNumber("33050161612700000175");
                        marginCollectionInfo.setSortId(String.valueOf(1));
                        marginCollectionInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                        marginCollectionInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                        marginCollectionInfo.setCreateBy(createByUserId);
                        marginCollectionInfo.setUpdateBy(createByUserId);
                        marginRecordInfoMapper.insert(marginCollectionInfo);
                        // 退款
                        if ("已结清".equals(excelSimpleObj.getString("contractStatus"))) {
                            MarginRecordInfo marginRefundInfo = new MarginRecordInfo();
                            marginRefundInfo.setMarginId(marginBaseInfo.getId());
                            marginRefundInfo.setDataSource("3");
                            marginRefundInfo.setSourceFlag(1);
                            marginRefundInfo.setCollectionType(RecordTypeEnum.REFUND_MARGIN.name());
                            marginRefundInfo.setRecordType(RecordTypeEnum.REFUND.name());
                            marginRefundInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                            marginRefundInfo.setCollectionAmount(earnestMoney);
                            marginRefundInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                            marginRefundInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                            marginRefundInfo.setOurAccountNumber("33050161612700000175");
                            marginRefundInfo.setSortId(String.valueOf(1));
                            marginRefundInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                            marginRefundInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                            marginRefundInfo.setCreateBy(createByUserId);
                            marginRefundInfo.setUpdateBy(createByUserId);
                            marginRecordInfoMapper.insert(marginRefundInfo);
                        }

                    }

                }

                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    private CollectionBaseInfo buildSpecialCollectionBaseInfo(ContractBaseInfo contractBaseInfo, PaymentBaseInfo paymentBaseInfo,
                                                              CashFlowItemEnum cashFlowItemEnum, Long createByUserId) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setContractId(contractBaseInfo.getId());
        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());
        collectionBaseInfo.setCashFlowItem(cashFlowItemEnum.name());
        collectionBaseInfo.setPhase(0);
        collectionBaseInfo.setCode(ContractPaymentImporterHelper.getCollectionCode(collectionBaseInfo.getCashFlowItem(), collectionBaseInfo.getPaymentCode(), collectionBaseInfo.getPhase(), collectionBaseInfo.getContractCode()));

        collectionBaseInfo.setCreateBy(createByUserId);
        collectionBaseInfo.setUpdateBy(createByUserId);
        return collectionBaseInfo;
    }

    public void genVersion() {
        List<String> contractCodeList = Arrays.asList("浙商租【2021】租字第(A-0067)号","中拓租【2019】租字第(A-0002)号");
        List<ContractBaseInfo> contractList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, contractCodeList));
        List<Long> contractIdList = contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIdList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIdList))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());

        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理.xlsx"));
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
        for (List<Object> rowData : contractSheetList) {
            if (rowData.size() > 5
                    && rowData.get(5) instanceof String
                    && ((String) rowData.get(5)).contains("号")
            ) {
                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
                JSONObject rowDataObj = new JSONObject();
                excelDataObj.put(actualContractCode, rowDataObj);
                rowDataObj.put("contractStatus", rowData.get(44));
            }
        }

        transactionTemplate.execute(status -> {
            try {
                contractList.forEach(c -> {
                    log.info("生成合同版本数据:{}", c.getContractCode());
                    boolean settleFlag = "已结清".equals(excelDataObj.getJSONObject(c.getContractCode()).getString("contractStatus"));
                    contractService.recordContractStatus(c.getId(), settleFlag ? ContractStatus.SETTLE : ContractStatus.START_RENT, null);
                    contractVersionService.recordVersion(c.getId(), VersionTypeEnum.EFFECT, 3L, null, VersionTypeConstants.NORMAL);
                });
                paymentIdList.forEach(p -> {
                    log.info("生成付款版本数据:{}", p);
                    paymentService.recordPaymentStatus(p, RecordStatus.TAKE_EFFECT, ProcessStatus.APPROVAL_PASS);
                    paymentVersionService.recordVersion(p, VersionTypeEnum.EFFECT, 3L, null, VersionTypeConstants.NORMAL);
                });
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }});
    }
    /**
     * 清空导入的数据
     */
    public void clearData() {
        List<String> clearContractCodeList = Arrays.asList("浙商租【2021】租字第(A-0067)号","中拓租【2019】租字第(A-0002)号");
        List<Long> contractIdList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, clearContractCodeList))
                .stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIdList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIdList))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        List<Long> collectionIdList= collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIdList))
                .stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
        List<Long> marginIdList = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIdList))
                .stream().map(MarginBaseInfo::getId).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            try {
                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name()).in(CommonVersion::getMainId, contractIdList));
                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.PAYMENT.name()).in(CommonVersion::getMainId, paymentIdList));
                contractBaseInfoMapper.delete(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId, contractIdList));
                contractBaseInfoLibMapper.delete(Wrappers.<ContractBaseInfoLib>lambdaQuery().in(ContractBaseInfoLib::getOriginId, contractIdList));
                contractLeasePriceMapper.delete(Wrappers.<ContractLeasePrice>lambdaQuery().in(ContractLeasePrice::getContractId, contractIdList));
                contractLeasePriceLibMapper.delete(Wrappers.<ContractLeasePriceLib>lambdaQuery().in(ContractLeasePriceLib::getContractId, contractIdList));
                contractRentEstimateMapper.delete(Wrappers.<ContractRentEstimate>lambdaQuery().in(ContractRentEstimate::getContractId, contractIdList));
                contractRentEstimateLibMapper.delete(Wrappers.<ContractRentEstimateLib>lambdaQuery().in(ContractRentEstimateLib::getContractId, contractIdList));
                paymentBaseInfoMapper.delete(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIdList));
                paymentBaseInfoLibMapper.delete(Wrappers.<PaymentBaseInfoLib>lambdaQuery().in(PaymentBaseInfoLib::getOriginId, paymentIdList));
                contractReceiptMapper.delete(Wrappers.<ContractReceipt>lambdaQuery().in(ContractReceipt::getContractId, contractIdList));
                contractReceiptLibMapper.delete(Wrappers.<ContractReceiptLib>lambdaQuery().in(ContractReceiptLib::getContractId, contractIdList));
                contractRentActualMapper.delete(Wrappers.<ContractRentActual>lambdaQuery().in(ContractRentActual::getContractId, contractIdList));
                contractRentActualLibMapper.delete(Wrappers.<ContractRentActualLib>lambdaQuery().in(ContractRentActualLib::getContractId, contractIdList));
                collectionBaseInfoMapper.delete(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getId, collectionIdList));
                collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, collectionIdList));
                contractTenantryMapper.delete(Wrappers.<ContractTenantry>lambdaQuery().in(ContractTenantry::getContractId, contractIdList));
                contractTenantryLibMapper.delete(Wrappers.<ContractTenantryLib>lambdaQuery().in(ContractTenantryLib::getContractId, contractIdList));
                contractAccountMapper.delete(Wrappers.<ContractAccount>lambdaQuery().in(ContractAccount::getContractId, contractIdList));
                contractAccountLibMapper.delete(Wrappers.<ContractAccountLib>lambdaQuery().in(ContractAccountLib::getContractId, contractIdList));
                contractGuarantorMapper.delete(Wrappers.<ContractGuarantor>lambdaQuery().in(ContractGuarantor::getContractId, contractIdList));
                contractGuarantorLibMapper.delete(Wrappers.<ContractGuarantorLib>lambdaQuery().in(ContractGuarantorLib::getContractId, contractIdList));
                paymentPlanedDetailMapper.delete(Wrappers.<PaymentPlanedDetail>lambdaQuery().in(PaymentPlanedDetail::getPaymentId, paymentIdList));
                paymentPlanedDetailLibMapper.delete(Wrappers.<PaymentPlanedDetailLib>lambdaQuery().in(PaymentPlanedDetailLib::getPaymentId, paymentIdList));
                paymentActualDetailMapper.delete(Wrappers.<PaymentActualDetail>lambdaQuery().in(PaymentActualDetail::getPaymentId, paymentIdList));
                marginBaseInfoMapper.delete(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getId, marginIdList));
                marginRecordInfoMapper.delete(Wrappers.<MarginRecordInfo>lambdaQuery().in(MarginRecordInfo::getMarginId, marginIdList));
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }});
        log.info("删除合同id:{}", JSON.toJSONString(contractIdList));
    }

//    public static void main(String[] args) throws Exception {
//        String handContractDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得合同模块导出_20220928.json").getInputStream(), Charset.defaultCharset());
//        JSONArray handContractDataArray = JSONArray.parseArray(handContractDataString);
//        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-叶芳整理(2).xlsx"));
//        tzExcelReader.setSheet("业务合同情况汇总表");
//        List<List<Object>> contractSheetList = tzExcelReader.read();
//        Set<String> allExcelHandContractCode = new HashSet<>(),repeatExcelHandContractCode = new HashSet<>();
//        for (List<Object> rowData : contractSheetList) {
//            if (rowData.size() > 5
//                    && rowData.get(5) instanceof String
//                    && ((String) rowData.get(5)).contains("号")
//            ) {
//                String handContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
//                if (allExcelHandContractCode.contains(handContractCode)) {
//                    repeatExcelHandContractCode.add(handContractCode);
//                }
//                allExcelHandContractCode.add(handContractCode);
//            }
//        }
////        Set<String> allHandContractCodeSet = handContractDataArray.stream().map(d -> ((JSONObject)d).getString("contract_number").trim().replaceAll("（", "(").replaceAll("）", ")")).collect(Collectors.toSet());
////        allExcelHandContractCode.removeAll(allHandContractCodeSet);
////        System.out.println(JSON.toJSONString(allExcelHandContractCode));
//        System.out.println(JSON.toJSONString(repeatExcelHandContractCode));
//    }

    public static void main(String[] args) throws Exception {
        String handContractDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得合同模块导出_20220928.json").getInputStream(), Charset.defaultCharset());
        JSONArray handContractDataArray = JSONArray.parseArray(handContractDataString);
        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-叶芳整理(2).xlsx"));
        tzExcelReader.setSheet("业务合同情况汇总表");
        JSONObject dataObj = JSONObject.parseObject("{\"中拓租【2020】租字第(A-0010)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"江苏徐钢钢铁集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0010)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0001)号\":{\"contractStatus\":\"累计逾期3次以上\",\"detailSheetName\":\"河南骏化发展股份有限公司202103\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0016)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0014)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"湖州市南浔新城投资发展有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0014)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0009)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"山西通才工贸有限公司202103\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2021】租字第(A-0010)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0020)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山东仁丰特种材料股份有限公司202107\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0033)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0001)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"杭州宏迈机械设备有限公司直租\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0001)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0043)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江逸盛新材料有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0061)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0062)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"天下行租车有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0067)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0088)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"湖北绿色家园材料技术股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0042)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0047)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"广西梧州市金海不锈钢有限公司03\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0059)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0005)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"武汉吉象合力工业车辆有限公司直租\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0002)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(C-0027)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江中通通信有限公司2203\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(C-0002)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0091)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙商中拓集团股份有限公司租赁2207\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0044)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1005)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"当涂县清源水务投资有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0005)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0050)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"余姚市城西工业开发建设有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0063)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(C-0030)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"厦门创德信环保设备有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(C-GCJX-0004)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0018)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"德清县下渚湖湿地旅游发展有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0018)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-1001)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"兰溪市聚业建设开发有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2020】租字第(A-0001)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2019】租字第(A-0016)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙商中拓集团股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0016)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1043)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁波市奉化区城市投资发展集团有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0043)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0021)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西高义钢铁有限公司202009\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0031)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0059)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"余姚市联海实业有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0068)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0017)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"厦门海福租赁有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(HF-0001)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0025)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"广东禄宇航运有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0025)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-0051)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"余姚市城西工业开发建设有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0064)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0076)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"桐庐县国有资产投资经营有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0018)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0099)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江华眼视觉科技有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0047)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0032)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"诸暨市城东新城建设有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0046)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0017)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"嵊州市交通投资发展集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0006)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(C-0013)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"杭州宏迈机械设备有限公司直租2111\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0005)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2019】租字第(A-0009)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"山西通才工贸有限公司201908\",\"actualFinishDate\":1642176000000,\"actualContractCode\":\"中拓租【2019】租字第(A-0009)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2019】租字第(A-0005)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"广西梧州市金海不锈钢有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0005)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0034)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"山东仁丰特种材料股份有限公司202101\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0034)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0027)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山东银鹰化纤有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0041)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(C-0006)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"中建锦程机械设备(上海)有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0003)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0030)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"江苏省镔鑫钢铁集团有限公司202012\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0030)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0011)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"广西朗知森商贸有限公司租赁2007\",\"actualFinishDate\":1642089600000,\"actualContractCode\":\"中拓租【2020】租字第(A-0011)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0087)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"宁波万众汽车零部件有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0037)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0042)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"杭加(广东)建筑节能新材料有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0056)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(C-0024)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"中建锦程机械设备(上海)有限公司03\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(C-GCJX-0001)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0046)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁波市奉化区红胜开发建设有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0058)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0090)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"福建龙麟集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0043)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(BA-0032)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"广西朗知森商贸有限公司租赁2106\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0032)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0071)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁波市奉化区公共交通有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0015)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(C-0031)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"厦门海福租赁有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(C-GCJX-0005)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0094)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"清徐县美特好农产品配送物流有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0038)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2019】租字第(A-0011)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"江苏省镔鑫钢铁集团有限公司201910\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0011)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1002)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"兰溪市聚业建设开发有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2020】租字第(A-0002)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0015)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"湖州市南浔新城投资发展有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0015)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2019】租字第(A-0015)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江中拓供应链管理有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0015)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1044)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁波市奉化区城市投资发展集团有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0044)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0019)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"德清县下渚湖湿地旅游发展有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0019)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0039)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"山西通才工贸有限公司202110\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0052)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0018)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"上海裕亿机械设备有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0006)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0022)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"宜宾丝丽雅股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0022)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2022】租字第(A-0075)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"桐庐县国有资产投资经营有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0017)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0054)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江逸盛新材料有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0062)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0098)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"上海鼎衡航运科技有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0039)号\",\"projectClassify\":\"谨慎支持类\"},\"汉得.合同系统编号\":{\"contractStatus\":\"融资状态\",\"detailSheetName\":\"客户名称\",\"actualFinishDate\":\"结清日期\",\"actualContractCode\":\"风控.合同编号\",\"projectClassify\":\"行业分类（初始）\"},\"浙商租【2022】租字第(A-0079)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"余姚经济开发区建设投资发展有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0023)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0035)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"德清县城市建设发展总公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0049)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2019】租字第(A-0008)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"广西梧州市金海不锈钢有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0008)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0061)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"山西万美医药科技有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0075)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0082)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"清徐县美特好农产品配送物流有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0020)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0112)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"广东万嘉通通信科技有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0061)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0007)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西建邦集团有限公司通才铁路专用线分公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0007)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1013)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"舟山普陀城市投资发展集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0013)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0026)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"广东禄宇航运有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0026)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-0003)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江鹊山建设有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0024)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0026)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"华劲集团赣州纸品有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0025)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(C-0007)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"杭州宏迈机械设备有限公司直租2109\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0004)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0049)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"舟山市定海宝利投资有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0054)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(C-0029)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"江苏德龙镍业有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0031)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0012)号\":{\"contractStatus\":\"累计逾期3次以上\",\"detailSheetName\":\"河南骏化发展股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0012)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0031)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"嵊州市经济开发区东方投资有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0031)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0041)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"诸暨市农村发展投资有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0028)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(C-0022)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"中建锦程机械设备(上海)有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JLG-0008)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0086)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"诸暨华海氨纶有限公司2206\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0036)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0007)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"南通中盾能源有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0026)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0022)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"乐山杭加节能新材料有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0027)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0045)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁波市奉化区红胜开发建设有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0057)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0006)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"舟山市定海城区建设开发有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0066)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0070)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"开玙供应链管理(无锡)有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0009)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0093)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"漳州虎鲸冷链物流有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0046)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0100)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"菏泽城际公交有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0048)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0016)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西通才工贸有限公司202009\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0016)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2019】租字第(A-0010)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"江苏省镔鑫钢铁集团有限公司201911\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0010)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1045)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"江苏徐钢钢铁集团有限公司202108\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0045)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1003)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江湖州环太湖集团有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0003)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0104)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西通才工贸有限公司20220801\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0053)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0109)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"山东仁丰特种材料股份有限公司202208\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0059)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2019】租字第(A-0014)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁夏晟晏实业集团能源循环经济有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0014)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2022】租字第(A-0108)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"新疆博海水泥有限公司2208\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0062)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-1040)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"扶绥县昊腾工程建设有限公司租赁\",\"actualFinishDate\":1658764800000,\"actualContractCode\":\"浙商租【2021】租字第(A-0040)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0038)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"郑州沃特节能科技股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0051)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(C-0011)号\":{\"contractStatus\":\"累计逾期3次以上\",\"detailSheetName\":\"通冠机械租赁股份有限公司202111\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(XB-0002)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0074)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"长兴宏达水利建设发展有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0014)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0097)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江长兴综合物流园区发展有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0051)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0019)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"昌乐新迈纸业有限公司2106\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0034)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0053)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"阳光王子(寿光)特种纸有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0060)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0015)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"杭州安沃普机械有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(XB-0003)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0078)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"嘉善嘉港燃气有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0016)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0034)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江豪邦化工有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0048)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-0057)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"宁波象山交通开发建设集团有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0071)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0060)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"诸暨华海氨纶有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0074)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0081)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"余姚经济开发区建设投资发展有限公司03\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0025)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-1018)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"诸暨市新城投资开发集团有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0018)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(BA-0022)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"京商第一建设有限公司租赁\",\"actualFinishDate\":1652284800000,\"actualContractCode\":\"浙商租【2021】租字第(A-0005)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0111)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"湖州申太建设发展有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0066)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0004)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"浙江临杭物流发展有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0004)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0002)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"盐城海瀛控股集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0011)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0048)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"舟山市定海宝利投资有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0053)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(C-0008)号\":{\"contractStatus\":\"累计逾期3次以上\",\"detailSheetName\":\"通冠机械租赁股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(XB-0001)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0013)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"新疆博海水泥有限公司\",\"actualFinishDate\":1661097600000,\"actualContractCode\":\"中拓租【2020】租字第(A-0013)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-0040)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"诸暨市农村发展投资有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0029)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0021)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"江苏博汇纸业有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0035)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0063)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"四川环龙新材料有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0077)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0089)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"江苏振江新能源装备股份有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0039)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0006)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"滨州中裕食品有限公司202103\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0012)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0005)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"舟山市定海城区建设开发有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0065)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(C-0026)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"厦门创德信环保设备有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(C-GCJX-0003)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(C-0004)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"杭州宏迈机械设备有限公司回租\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(JN-0001)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-0025)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"上海鼎衡航运科技有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0038)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2022】租字第(A-0103)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西通才工贸有限公司20220802\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0052)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0092)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"福州聚福广通供应链有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0045)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1023)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"东莞市金田纸业有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0023)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2021】租字第(A-1004)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江湖州环太湖集团有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0004)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2019】租字第(A-0013)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"宁夏晟晏实业集团能源循环经济有限公司1\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2019】租字第(A-0013)号\",\"projectClassify\":\"谨慎支持类\"},\"浙商租【2021】租字第(A-0037)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江交科供应链管理有限公司租赁2109\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0050)号\",\"projectClassify\":\"协同业务类\"},\"浙商租【2021】租字第(A-0014)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"泰兴市滨江污水处理有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0008)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2020】租字第(A-0020)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"山西高义钢铁有限公司202012\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0030)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0001)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"昌乐新迈纸业有限公司2006\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0001)号\",\"projectClassify\":\"适度支持类\"},\"浙商租【2022】租字第(A-0096)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"浙江长兴综合物流园区发展有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0050)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-0056)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"宁波象山交通开发建设集团有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0070)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0110)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"湖州申太建设发展有限公司01\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0065)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2022】租字第(A-0080)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"余姚经济开发区建设投资发展有限公司02\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2022】租字第(A-0024)号\",\"projectClassify\":\"鼓励介入类\"},\"浙商租【2021】租字第(A-1019)号\":{\"contractStatus\":\"正常\",\"detailSheetName\":\"诸暨市新城投资开发集团有限公司2\",\"actualFinishDate\":\"\",\"actualContractCode\":\"浙商租【2021】租字第(A-0019)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2018】租字第(C-0006)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"响水巨合金属制品有限公司酸洗线3\",\"actualFinishDate\":1642694400000,\"actualContractCode\":\"中拓租【2018】租字第(C-0006)号\",\"projectClassify\":\"适度支持类\"},\"中拓租【2020】租字第(A-0009)号\":{\"contractStatus\":\"逾期\",\"detailSheetName\":\"湖州新型城市投资发展集团有限公司\",\"actualFinishDate\":\"\",\"actualContractCode\":\"中拓租【2020】租字第(A-0009)号\",\"projectClassify\":\"鼓励介入类\"},\"中拓租【2019】租字第(A-0002)号\":{\"contractStatus\":\"已结清\",\"detailSheetName\":\"山东博汇纸业股份有限公司03\",\"actualFinishDate\":1653235200000,\"actualContractCode\":\"中拓租【2019】租字第(A-0002)号\",\"projectClassify\":\"适度支持类\"}}");
        JSONObject aDataObj = new JSONObject();
        for (String k : dataObj.keySet()) {
            aDataObj.put(dataObj.getJSONObject(k).getString("actualContractCode"), dataObj.getJSONObject(k));
        }

        ExcelReader dExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/导入合同数据整理.xlsx"));
        List<List<Object>> dataList = dExcelReader.read();
        dExcelReader.close();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).size() > 2 && dataList.get(i).get(0) instanceof String && ((String) dataList.get(i).get(0)).contains("租")) {
                dataList.get(i).add(aDataObj.getJSONObject(((String) dataList.get(i).get(0))).getString("detailSheetName"));
            }
        }
        ExcelWriter dExcelWriter = ExcelUtil.getWriter(new File("/Users/wang/Desktop/导入合同数据整理.xlsx"));
        dExcelWriter.write(dataList, false);
        dExcelWriter.flush();
        dExcelWriter.close();
    }

}
