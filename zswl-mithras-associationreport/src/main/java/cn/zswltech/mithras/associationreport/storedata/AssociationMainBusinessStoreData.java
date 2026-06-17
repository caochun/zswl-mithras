package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.application.AssociationReportAssetClassifyPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessClientSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessContractSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessCorpSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessFactPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessGuaranteeSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessLesseeSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessPaymentActualSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessPaymentSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessReceiptSnapshot;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationMainBusinessService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMainBusiness;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.associationreport.application.AssociationReportCollectionFactPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportCollectionSnapshot;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
@Component
public class AssociationMainBusinessStoreData extends AbstractDataStore<AssociationMainBusiness> {
    @Resource
    private AssociationDictionaryService associationDictionaryService;
    @Resource
    private AssociationReportCollectionFactPort associationReportCollectionFactPort;
    @Resource
    private AssociationReportAssetClassifyPort associationReportAssetClassifyPort;
    @Resource
    private AssociationReportMainBusinessFactPort mainBusinessFactPort;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        // 无需前置数据
        return true;
    }

    @Override
    protected List<AssociationMainBusiness> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【主要业务清单】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<主要业务清单>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司主要业务清单（月报）");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<主要业务清单>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationMainBusiness> list = new LinkedList<>();
        for (int i = 3; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (Objects.equals("合计", row.get(0))) {
                break;
            }
            try {
                list.add(this.convert(i-2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【主要业务清单】-第{}行数据处理异常", (i -2), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected List<AssociationMainBusiness> parseFromSystemData(AssociationReport associationReport) {
        // 金额相关数据计算截止日期
        LocalDate targetDate = this.ensureMetricDate(associationReport);
        // 字典
        Map<String, Map<String, String>> dictNameMap = associationDictionaryService.getDisplay2CodeMap();
        // 起租合同
        List<AssociationReportMainBusinessContractSnapshot> contractList = mainBusinessFactPort.listAllStartRentContracts();
        if (CollectionUtil.isEmpty(contractList)) {
            return Collections.emptyList();
        }
        List<AssociationMainBusiness> result = new LinkedList<>();
        for (AssociationReportMainBusinessContractSnapshot contractBaseInfo : contractList) {
            try {
                List<AssociationMainBusiness> associationMainBusinessList = this.buildData(contractBaseInfo, dictNameMap, targetDate);
                if (CollectionUtil.isNotEmpty(associationMainBusinessList)) {
                    result.addAll(associationMainBusinessList);
                }
            } catch (Exception e) {
                log.error("金融局报送【主要业务清单】自动取值发生异常[contractCode:{}]", contractBaseInfo.getContractCode(), e);
            }
        }
        return result;
    }

    @Override
    protected void check(List<AssociationMainBusiness> dataList) {
        List<String> errorMessageList = new LinkedList<>();
        for (AssociationMainBusiness associationMainBusiness : dataList) {
            // 合同类型取值字典范围
            if (Objects.equals(associationMainBusiness.getAgmtTypeCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<合同类型>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("F列：合同类型，取值字典范围");
            }
            // 项目行业分类取值字典范围
            if (Objects.equals(associationMainBusiness.getProjIndtClasCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<项目行业分类>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("H列：项目涉及行业，取值字典范围");
            }
            // 客户规模取值字典范围
            if (Objects.equals(associationMainBusiness.getCustScalCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<客户规模>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("K列：客户规模，取值字典范围");
            }
            // 综合融资成本不可超过24%
            if (Objects.nonNull(associationMainBusiness.getCmphFinCost())) {
                if (associationMainBusiness.getCmphFinCost().compareTo(BigDecimal.valueOf(0.24)) > 0) {
//                    errorMessageList.add(String.format("第%s行数据（不含表头）的<综合融资成本>超过24%%", associationMainBusiness.getRowNum()));
                    errorMessageList.add("O列：综合融资成本，不可超过24%");
                }
            }
            // 增信情况取值字典范围
            if (Objects.equals(associationMainBusiness.getUdpnSituCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<增信情况>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("P列：增信情况，取值字典范围");
            }
            // 逾期天数取值字典范围
            if (Objects.equals(associationMainBusiness.getOvduDaysCode(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<逾期天数>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("S列：逾期天数，取值字典范围");
            }
            // 是否纳入不良取值字典范围
            if (Objects.equals(associationMainBusiness.getNpFlag(), DICT_UNKNOWN_CODE)) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<是否纳入不良>不符合模板中的字典取值", associationMainBusiness.getRowNum()));
                errorMessageList.add("U列：是否纳入不良，取值字典范围");
            }
            // 客户数量必填
            if (Objects.isNull(associationMainBusiness.getCustVol())) {
//                errorMessageList.add(String.format("第%s行数据（不含表头）的<客户数量>不能为空", associationMainBusiness.getRowNum()));
                errorMessageList.add("W列：客户数量，必填字段");
            }
        }
        if (CollectionUtil.isNotEmpty(errorMessageList)) {
            throw new AssociationReportException(errorMessageList);
        }
    }

    @Override
    protected IService<AssociationMainBusiness> serviceBean() {
        return SpringUtil.getBean(AssociationMainBusinessService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0009;
    }

    private AssociationMainBusiness convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationMainBusiness associationMainBusiness = new AssociationMainBusiness();
        associationMainBusiness.setRowNum(rowNum);
        // 序号
        associationMainBusiness.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        // 合同名称
        associationMainBusiness.setAgmtName(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));
        // 合同编号
        associationMainBusiness.setAgmtNo(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null));
        // 合同签订日期
        associationMainBusiness.setAgmtSignDate(Optional.ofNullable(row.get(3)).filter(e -> StrUtil.isNotBlank(e.toString())).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0, 10), DatePattern.NORM_DATE_PATTERN)).orElse(null));
        // 合同到期日期
        associationMainBusiness.setAgmtMatuDate(Optional.ofNullable(row.get(4)).filter(e -> StrUtil.isNotBlank(e.toString())).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0, 10), DatePattern.NORM_DATE_PATTERN)).orElse(null));
        // 合同类型
        if (row.get(5) != null && StrUtil.isNotBlank(row.get(5).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.DIMLS079.name());
            if (Objects.nonNull(contractTypeMap)) {
                associationMainBusiness.setAgmtTypeCode(Optional.ofNullable(contractTypeMap.get(row.get(5).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                associationMainBusiness.setAgmtTypeCode(DICT_UNKNOWN_CODE);
            }
        }
        // 租赁物类型
        associationMainBusiness.setLasdType(Optional.ofNullable(row.get(6)).map(Object::toString).orElse(null));
        // 项目行业分类
        if (row.get(7) != null && StrUtil.isNotBlank(row.get(7).toString())) {
            Map<String, String> industryTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PUB00234.name());
            if (Objects.nonNull(industryTypeMap)) {
                associationMainBusiness.setProjIndtClasCode(Optional.ofNullable(industryTypeMap.get(row.get(7).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                associationMainBusiness.setProjIndtClasCode(DICT_UNKNOWN_CODE);
            }
        }
        // 客户名称
        associationMainBusiness.setCustName(Optional.ofNullable(row.get(8)).map(Object::toString).orElse(null));
        // 客户证件号码
        associationMainBusiness.setCustCertNum(Optional.ofNullable(row.get(9)).map(Object::toString).orElse(null));
        // 客户规模
        if (row.get(10) != null && StrUtil.isNotBlank(row.get(10).toString())) {
            Map<String, String> scaleTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00019.name());
            if (Objects.nonNull(scaleTypeMap)) {
                associationMainBusiness.setCustScalCode(Optional.ofNullable(scaleTypeMap.get(row.get(10).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                associationMainBusiness.setCustScalCode(DICT_UNKNOWN_CODE);
            }
        }
        // 融资租赁投放额
        associationMainBusiness.setFnlRels(Optional.ofNullable(row.get(11)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 收回本金
        associationMainBusiness.setWthdPrin(Optional.ofNullable(row.get(12)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 租金余额
        associationMainBusiness.setRentBal(Optional.ofNullable(row.get(13)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 综合融资成本
        associationMainBusiness.setCmphFinCost(Optional.ofNullable(row.get(14)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 增信情况
        if (row.get(15) != null && StrUtil.isNotBlank(row.get(15).toString())) {
            Map<String, String> udpnSuitTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00212.name());
            if (Objects.nonNull(udpnSuitTypeMap)) {
                associationMainBusiness.setUdpnSituCode(Optional.ofNullable(udpnSuitTypeMap.get(row.get(15).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                associationMainBusiness.setUdpnSituCode(DICT_UNKNOWN_CODE);
            }
        }
        // 增信方
        associationMainBusiness.setUdpn(Optional.ofNullable(row.get(16)).map(Object::toString).orElse(null));
        // 逾期租金
        associationMainBusiness.setOvduRent(Optional.ofNullable(row.get(17)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 逾期天数
        if (row.get(18) != null && StrUtil.isNotBlank(row.get(18).toString())) {
            Map<String, String> overdueDaysMap = dictNameMap.get(AssociationDictionaryCategoryEnum.EVT00051.name());
            if (Objects.nonNull(overdueDaysMap)) {
                associationMainBusiness.setOvduDaysCode(Optional.ofNullable(overdueDaysMap.get(row.get(18).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                associationMainBusiness.setOvduDaysCode(DICT_UNKNOWN_CODE);
            }
        }
        // 逾期处置情况
        associationMainBusiness.setOvduDspsProg(Optional.ofNullable(row.get(19)).map(Object::toString).orElse(null));
        // 是否纳入不良
        if (row.get(20) != null && StrUtil.isNotBlank(row.get(20).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(row.get(20).toString());
            if (Objects.nonNull(item)) {
                associationMainBusiness.setNpFlag(item.getCode().toString());
            } else {
                associationMainBusiness.setNpFlag(DICT_UNKNOWN_CODE);
            }
        }
        // 不良余额
        associationMainBusiness.setNpBal(Optional.ofNullable(row.get(21)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 客户数量
        associationMainBusiness.setCustVol(Optional.ofNullable(row.get(22)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));
        return associationMainBusiness;
    }

    private List<AssociationMainBusiness> buildData(AssociationReportMainBusinessContractSnapshot contractBaseInfo, Map<String, Map<String, String>> dictNameMap, LocalDate targetDate) {
        // 查询投放
        List<AssociationReportMainBusinessPaymentSnapshot> paymentBaseInfoList = mainBusinessFactPort.listPaymentsByContractIds(Collections.singletonList(contractBaseInfo.getId()));
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        List<AssociationMainBusiness> result = new LinkedList<>();
        // 查询收款
        List<AssociationReportCollectionSnapshot> collectionSnapshotList = associationReportCollectionFactPort.listByContractIds(Collections.singletonList(contractBaseInfo.getId()));
        // 查询主承租人
        AssociationReportMainBusinessLesseeSnapshot mainContractTenantry = mainBusinessFactPort.getMainLessee(contractBaseInfo.getId());
        if (Objects.isNull(mainContractTenantry)) {
            return Collections.emptyList();
        }
        // 查询担保、抵押、质押措施备用
        AssociationReportMainBusinessGuaranteeSnapshot guaranteeSnapshot = mainBusinessFactPort.getGuarantee(contractBaseInfo.getId());
        String udpnSituCode = this.ensureUdpnSituCode(guaranteeSnapshot, dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00212.name()));
        String udpn = this.ensureUdpn(guaranteeSnapshot);
        // 取客户相关信息备用
        AssociationReportMainBusinessClientSnapshot client = mainBusinessFactPort.getClient(mainContractTenantry.getLesseeId());
        AssociationReportMainBusinessCorpSnapshot corpCommerceInfo = mainBusinessFactPort.getCorpCommerceInfo(mainContractTenantry.getLesseeId());
        if (Objects.isNull(client) || Objects.isNull(corpCommerceInfo)) {
            return Collections.emptyList();
        }
        // 按照借据id分组
        Map<Long, List<AssociationReportMainBusinessPaymentSnapshot>> paymentBaseInfoMap = paymentBaseInfoList.stream().filter(e -> Objects.nonNull(e.getReceiptId())).collect(Collectors.groupingBy(AssociationReportMainBusinessPaymentSnapshot::getReceiptId));
        List<Long> receiptIds = new ArrayList<>(paymentBaseInfoMap.keySet());
        receiptIds.sort(Comparator.comparing(e -> e));
        for (int i = 0; i < receiptIds.size(); i++) {
            AssociationReportMainBusinessReceiptSnapshot contractReceipt = mainBusinessFactPort.getReceipt(receiptIds.get(i));
            if (Objects.isNull(contractReceipt)) {
                continue;
            }
            AssociationMainBusiness instance = new AssociationMainBusiness();
            // 合同名称 = 客户名称（多借据的话增加编号）
            if (receiptIds.size() > 1) {
                instance.setAgmtName(String.format("%s%02d", client.getClientName(), i + 1));
            } else {
                instance.setAgmtName(client.getClientName());
            }
            // 合同编号
            instance.setAgmtNo(contractBaseInfo.getContractCode());
            // 合同签订日期 = 借据最早投放日期
            instance.setAgmtSignDate(this.ensureAgmtSignDate(paymentBaseInfoMap.get(contractReceipt.getId())));
            // 合同到期日 = 借据最后一期还款日期
            instance.setAgmtMatuDate(this.ensureAgmtMatuDate(contractReceipt.getId()));
            // 合同类型
            instance.setAgmtTypeCode(this.ensureAgmtTypeCode(contractBaseInfo, dictNameMap.get(AssociationDictionaryCategoryEnum.DIMLS079.name())));
            // 租赁物类型
            instance.setLasdType(this.ensureLasdType(contractBaseInfo));
            // 项目涉及行业 = 取客户国标行业分类一级分类
            instance.setProjIndtClasCode(this.ensureProjIndtClasCode(corpCommerceInfo, dictNameMap.get(AssociationDictionaryCategoryEnum.PUB00234.name())));
            // 客户名称
            instance.setCustName(client.getClientName());
            // 客户证码
            instance.setCustCertNum(client.getUscCode());
            // 客户规模
            instance.setCustScalCode(this.ensureCustScalCode(corpCommerceInfo, dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00019.name())));
            // 融资租赁投放额 = 借据实际付款核销金额
            long payAmount = this.ensureFnlRels(paymentBaseInfoMap.get(receiptIds.get(i)));
            instance.setFnlRels(Util.millimeterLong2WanBigDecimal(payAmount));
            // 收回本金 = 借据实际还款核销金额
            long collectPrincipal = this.ensureWithPrin(contractReceipt.getId(), paymentBaseInfoMap.get(receiptIds.get(i)), collectionSnapshotList);
            instance.setWthdPrin(Util.millimeterLong2WanBigDecimal(collectPrincipal));
            // 租金余额
            long rentBalance = this.ensureRentBal(contractReceipt.getId(), collectionSnapshotList);
            instance.setRentBal(Util.millimeterLong2WanBigDecimal(rentBalance));
            // 综合融资成本 = 借据实际IRR
            if (Objects.nonNull(contractReceipt.getActualIrr())) {
                instance.setCmphFinCost(BigDecimal.valueOf(contractReceipt.getActualIrr()).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP));
            }
            // 增信情况
            instance.setUdpnSituCode(udpnSituCode);
            // 增信方
            instance.setUdpn(udpn);
            // 逾期租金
            long overdueRent = this.ensureOverdueRent(contractReceipt.getId(), collectionSnapshotList, targetDate);
            instance.setOvduRent(Util.millimeterLong2WanBigDecimal(overdueRent));
            // 逾期天数
            instance.setOvduDaysCode(this.ensureOvduDaysCode(contractReceipt.getId(), collectionSnapshotList, dictNameMap.get(AssociationDictionaryCategoryEnum.EVT00051.name()), targetDate));
            // 是否纳入不良
            instance.setNpFlag(this.ensureNpFlag(mainContractTenantry.getLesseeId()));
            // 不良余额 = 借据剩余本金
            if (StrUtil.equals(instance.getNpFlag(), YesOrNoNumberEnum.YES.getCode().toString())) {
                // 投放额 - 收回本金
                long principalBalance = payAmount - collectPrincipal;
                if (principalBalance < 0) {
                    principalBalance = 0;
                }
                instance.setNpBal(Util.millimeterLong2WanBigDecimal(principalBalance));
            } else {
                instance.setNpBal(BigDecimal.ZERO);
            }
            // 客户数量自动填充为1
            instance.setCustVol(1);
            result.add(instance);
        }
        return result;
    }

    private LocalDate ensureAgmtSignDate(List<AssociationReportMainBusinessPaymentSnapshot> paymentBaseInfoList) {
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        // 查询实际核销记录
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(AssociationReportMainBusinessPaymentSnapshot::getId).collect(Collectors.toSet());
        List<AssociationReportMainBusinessPaymentActualSnapshot> paymentActualDetailList = mainBusinessFactPort.listPaymentActualByPaymentIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return null;
        }
        paymentActualDetailList.sort(Comparator.comparing(AssociationReportMainBusinessPaymentActualSnapshot::getPaidInDate));
        return paymentActualDetailList.get(0).getPaidInDate();
    }

    private LocalDate ensureAgmtMatuDate(Long receiptId) {
        List<AssociationReportCollectionSnapshot> collectionSnapshotList = associationReportCollectionFactPort.listRentByReceiptId(receiptId);
        if (CollectionUtil.isEmpty(collectionSnapshotList)) {
            return null;
        }
        collectionSnapshotList.sort(Comparator.comparing(AssociationReportCollectionSnapshot::getPlanCollectionDate).reversed());
        return collectionSnapshotList.get(0).getPlanCollectionDate();
    }

    private String ensureAgmtTypeCode(AssociationReportMainBusinessContractSnapshot contractBaseInfo, Map<String, String> dictNameMap) {
        if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            return dictNameMap.get("售后回租");
        }
        if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            return dictNameMap.get("直接租赁");
        }
        if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.jyx_zu.name())) {
            return dictNameMap.get("经营性租赁");
        }
        if (StrUtil.equals(contractBaseInfo.getBizType(), ProjectBizType.BL.name())) {
            return dictNameMap.get("商业保理");
        }
        return null;
    }

    private String ensureLasdType(AssociationReportMainBusinessContractSnapshot contractBaseInfo) {
        // 根据租赁物流程中租赁物类型映射：「融租易——金融局报表」，当前类型映射如下，后续如果有类型变更则同步调整字典表
        // 生产设备——工业装备
        // 公交车——交通运输设备
        // 电站——其他
        // 环保——其他
        // 民办教育——其他
        // 医疗健康——其他
        // 机动车——交通运输设备
        // 船舶——交通运输设备
        // 通信基站——电子产品及通信设备
        // 公用事业——通用设备
        // 其他——其他租赁物
        // 非生产线设备——通用设备
        // 生产线——专用设备
        if (StrUtil.isBlank(contractBaseInfo.getLeaseItemTypes())) {
            return null;
        }
        // 从配置表取字典
        SystemConfigDO systemConfig = SpringUtil.getBean(SystemConfigService.class).getConfig("association_report_lasd_type").getData();
        Map<String, String> map = JSONUtil.toBean(systemConfig.getConfigValue(), new TypeReference<Map<String, String>>() {}, true);
        List<String> types = JSONUtil.toList(contractBaseInfo.getLeaseItemTypes(), String.class);
        List<String> resultList = types.stream().map(item -> Optional.ofNullable(map).map(e -> e.get(item)).orElse(null)).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        return StrUtil.join(",", resultList);
    }

    private String ensureProjIndtClasCode(AssociationReportMainBusinessCorpSnapshot corpCommerceInfo, Map<String, String> dictNameMap) {
        if (StrUtil.isBlank(corpCommerceInfo.getIndustryType())) {
            return null;
        }
        // 取第一个字符
        String firstChar = corpCommerceInfo.getIndustryType().substring(0, 1);
        String industryType = this.getIndustryTypeNameFromLocalCache(firstChar);
        return dictNameMap.get(industryType);
    }

    private String ensureCustScalCode(AssociationReportMainBusinessCorpSnapshot corpCommerceInfo, Map<String, String> dictNameMap) {
        if (StrUtil.equals(corpCommerceInfo.getOrgScale(), "BIG")) {
            return dictNameMap.get("大型企业");
        }
        if (StrUtil.equals(corpCommerceInfo.getOrgScale(), "MIDDLE")) {
            return dictNameMap.get("中型企业");
        }
        if (StrUtil.equals(corpCommerceInfo.getOrgScale(), "SMALL")) {
            return dictNameMap.get("小型企业");
        }
        if (StrUtil.equals(corpCommerceInfo.getOrgScale(), "TINY")) {
            return dictNameMap.get("微型企业");
        }
        return dictNameMap.get("其他");
    }

    private long ensureFnlRels(List<AssociationReportMainBusinessPaymentSnapshot> paymentBaseInfoList) {
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return 0L;
        }
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(AssociationReportMainBusinessPaymentSnapshot::getId).collect(Collectors.toSet());
        List<AssociationReportMainBusinessPaymentActualSnapshot> paymentActualDetailList = mainBusinessFactPort.listPaymentActualByPaymentIds(paymentIds);
        return paymentActualDetailList.stream().filter(e -> Objects.nonNull(e.getPaidInAmount())).mapToLong(AssociationReportMainBusinessPaymentActualSnapshot::getPaidInAmount).sum();
    }

    private long ensureWithPrin(Long receiptId, List<AssociationReportMainBusinessPaymentSnapshot> paymentBaseInfoList, List<AssociationReportCollectionSnapshot> collectionSnapshotList) {
        if (CollectionUtil.isEmpty(collectionSnapshotList)) {
            return 0L;
        }
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(AssociationReportMainBusinessPaymentSnapshot::getId).collect(Collectors.toSet());
        long firstRent = collectionSnapshotList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name()))
                .filter(e -> Objects.equals(e.getReceiptId(), receiptId) || (Objects.nonNull(e.getPaymentId()) && paymentIds.contains(e.getPaymentId())))
                .filter(e -> Objects.nonNull(e.getCollectionAmount()))
                .mapToLong(AssociationReportCollectionSnapshot::getCollectionAmount)
                .sum();
        long principal = collectionSnapshotList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                .filter(e -> Objects.nonNull(e.getCollectionPrincipal()))
                .mapToLong(AssociationReportCollectionSnapshot::getCollectionPrincipal)
                .sum();
        return firstRent + principal;
    }

    private long ensureRentBal(Long receiptId, List<AssociationReportCollectionSnapshot> collectionSnapshotList) {
        if (CollectionUtil.isEmpty(collectionSnapshotList)) {
            return 0L;
        }
        long remainingRent = 0L;
        for (AssociationReportCollectionSnapshot collectionSnapshot : collectionSnapshotList) {
            if (!StrUtil.equals(collectionSnapshot.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
                continue;
            }
            if (!Objects.equals(collectionSnapshot.getReceiptId(), receiptId)) {
                continue;
            }
            long plan = Optional.ofNullable(collectionSnapshot.getPlanCollectionAmount()).orElse(0L);
            long actual = Optional.ofNullable(collectionSnapshot.getCollectionAmount()).orElse(0L);
            long remaining = plan - actual;
            remainingRent = remainingRent + Math.max(remaining, 0);
        }
        return Math.max(remainingRent, 0);
    }

    private String ensureUdpnSituCode(AssociationReportMainBusinessGuaranteeSnapshot guaranteeSnapshot, Map<String, String> dictNameMap) {
        boolean guarantor = Objects.nonNull(guaranteeSnapshot) && guaranteeSnapshot.hasGuarantor();
        boolean mortgage = Objects.nonNull(guaranteeSnapshot) && guaranteeSnapshot.hasMortgage();
        boolean pledge = Objects.nonNull(guaranteeSnapshot) && guaranteeSnapshot.hasPledge();
        // 可用位运算优化
        if (guarantor && mortgage && pledge) {
            return dictNameMap.get("抵押+保证+质押");
        }
        if (guarantor && mortgage && !pledge) {
            return dictNameMap.get("抵押+保证");
        }
        if (guarantor && !mortgage && pledge) {
            return dictNameMap.get("质押+保证");
        }
        if (guarantor && !mortgage && !pledge) {
            return dictNameMap.get("保证");
        }
        if (!guarantor && mortgage && !pledge) {
            return dictNameMap.get("抵押");
        }
        if (!guarantor && !mortgage && pledge) {
            return dictNameMap.get("质押");
        }
        if (!guarantor && !mortgage && !pledge) {
            return dictNameMap.get("无");
        }
        return null;
    }

    private String ensureUdpn(AssociationReportMainBusinessGuaranteeSnapshot guaranteeSnapshot) {
        if (Objects.isNull(guaranteeSnapshot)) {
            return null;
        }
        List<String> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(guaranteeSnapshot.safeGuarantorNames())) {
            result.add("保证：" + StrUtil.join("、", guaranteeSnapshot.safeGuarantorNames()));
        }
        if (CollectionUtil.isNotEmpty(guaranteeSnapshot.safeMortgageDescriptions())) {
            result.add("抵押：" + StrUtil.join("、", guaranteeSnapshot.safeMortgageDescriptions()));
        }
        if (CollectionUtil.isNotEmpty(guaranteeSnapshot.safePledgeDescriptions())) {
            result.add("质押：" + StrUtil.join("、", guaranteeSnapshot.safePledgeDescriptions()));
        }
        return StrUtil.join("；", result);
    }

    private long ensureOverdueRent(Long receiptId, List<AssociationReportCollectionSnapshot> collectionSnapshotList, LocalDate targetDate) {
        long overdueRent = 0L;
        for (AssociationReportCollectionSnapshot collectionSnapshot : collectionSnapshotList) {
            if (collectionSnapshot.getPlanCollectionDate().isAfter(targetDate)) {
                continue;
            }
            if (!Objects.equals(collectionSnapshot.getReceiptId(), receiptId)) {
                continue;
            }
            if (!StrUtil.equals(collectionSnapshot.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
                continue;
            }
            long plan = Optional.ofNullable(collectionSnapshot.getPlanCollectionAmount()).orElse(0L);
            long actual = Optional.ofNullable(collectionSnapshot.getCollectionAmount()).orElse(0L);
            long remaining = plan - actual;
            overdueRent = overdueRent + Math.max(remaining, 0);
        }
        return overdueRent;
    }

    private String ensureOvduDaysCode(Long receiptId, List<AssociationReportCollectionSnapshot> collectionSnapshotList, Map<String, String> dictNameMap, LocalDate targetDate) {
        long maxOverdueDays = 0L;
        for (AssociationReportCollectionSnapshot collectionSnapshot : collectionSnapshotList) {
            if (collectionSnapshot.getPlanCollectionDate().isAfter(targetDate)) {
                continue;
            }
            if (!Objects.equals(collectionSnapshot.getReceiptId(), receiptId)) {
                continue;
            }
            if (!StrUtil.equals(collectionSnapshot.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
                continue;
            }
            long plan = Optional.ofNullable(collectionSnapshot.getPlanCollectionAmount()).orElse(0L);
            long actual = Optional.ofNullable(collectionSnapshot.getCollectionAmount()).orElse(0L);
            long remaining = plan - actual;
            if (remaining > 0) {
                long days = LocalDateTimeUtil.between(collectionSnapshot.getPlanCollectionDate().atStartOfDay(), targetDate.atStartOfDay(), ChronoUnit.DAYS);
                maxOverdueDays = Math.max(maxOverdueDays, days);
            }
        }
        if (maxOverdueDays == 0) {
            return dictNameMap.get("无逾期");
        }
        if (maxOverdueDays <= 90) {
            return dictNameMap.get("0-90");
        } else if (maxOverdueDays <= 365) {
            return dictNameMap.get("90-一年");
        } else {
            return dictNameMap.get("一年以上");
        }
    }

    private String ensureNpFlag(Long clientId) {
        // "根据最新的生效的五级分类映射：若为后三级的次级、可疑、损失，则填充为“是”，否则为“否”"
        if (associationReportAssetClassifyPort.isLatestEffectiveClassifyLastThree(clientId)) {
            return YesOrNoNumberEnum.YES.getCode().toString();
        } else {
            return YesOrNoNumberEnum.NO.getCode().toString();
        }
    }

    private String getIndustryTypeNameFromLocalCache(String firstChar) {
        if (StrUtil.equals(firstChar, "A")) {
            return "农、林、牧、渔业";
        }
        if (StrUtil.equals(firstChar, "B")) {
            return "采矿业";
        }
        if (StrUtil.equals(firstChar, "C")) {
            return "制造业";
        }
        if (StrUtil.equals(firstChar, "D")) {
            return "电力、热力、燃气及水生产和供应业";
        }
        if (StrUtil.equals(firstChar, "E")) {
            return "建筑业";
        }
        if (StrUtil.equals(firstChar, "F")) {
            return "批发和零售业";
        }
        if (StrUtil.equals(firstChar, "G")) {
            return "交通运输、仓储和邮政业";
        }
        if (StrUtil.equals(firstChar, "H")) {
            return "住宿和餐饮业";
        }
        if (StrUtil.equals(firstChar, "I")) {
            return "信息传输、软件和信息技术服务业";
        }
        if (StrUtil.equals(firstChar, "J")) {
            return "金融业";
        }
        if (StrUtil.equals(firstChar, "K")) {
            return "房地产业";
        }
        if (StrUtil.equals(firstChar, "L")) {
            return "租赁和商务服务业";
        }
        if (StrUtil.equals(firstChar, "M")) {
            return "科学研究和技术服务业";
        }
        if (StrUtil.equals(firstChar, "N")) {
            return "水利、环境和公共设施管理业";
        }
        if (StrUtil.equals(firstChar, "O")) {
            return "居民服务、修理和其他服务业";
        }
        if (StrUtil.equals(firstChar, "P")) {
            return "教育";
        }
        if (StrUtil.equals(firstChar, "Q")) {
            return "卫生和社会工作";
        }
        if (StrUtil.equals(firstChar, "R")) {
            return "文化、体育和娱乐业";
        }
        if (StrUtil.equals(firstChar, "S")) {
            return "公共管理、社会保障和社会组织";
        }
        if (StrUtil.equals(firstChar, "T")) {
            return "国际组织";
        }
        return null;
    }

}
