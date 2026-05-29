package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationBalanceSheetPartialService;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBalanceSheetPartial;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @date 2025/4/18
 * @description 资产负债表
 */
@Slf4j
@Component
public class AssociationBalanceSheetPartialData extends AbstractDataStore<AssociationBalanceSheetPartial> {
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate  = DateUtil.ensureQuarterLastDay(year, period);
        Map<String, Long> assetMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, dataDate.getYear(), dataDate.getMonthValue());
        boolean condition = CollectionUtil.isNotEmpty(assetMap);
        log.info("金融局报送【资产负债表】自动取值-前置数据校验结果:资产负债表 = {}", condition);
        return condition;
    }

    @Override
    protected List<AssociationBalanceSheetPartial> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【资产负债表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<资产负债表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司资产负债表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "项目");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<资产负债表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationBalanceSheetPartial> list = new LinkedList<>();
            try {
                /*if (rows.size() < 22 ) {
                    throw new MithrasException("表格格式不正确，确实必要行");
                }*/
                list.add(this.convert(rows, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【资产负债表】数据处理异常", e);
                throw new MithrasException("数据处理异常");
            }
        return list;
    }

    @Override
    protected List<AssociationBalanceSheetPartial> parseFromSystemData(AssociationReport currentReport) {
        // 查询财务报表
        LocalDate targetDate = this.ensureMetricDate(currentReport);
        Map<String, Long> assetMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, targetDate.getYear(), targetDate.getMonthValue());
        // 处理数据
        AssociationBalanceSheetPartial currentReportValue = new AssociationBalanceSheetPartial();
        Map<String, String> propertyValueMap = this.mapping();
        Field[] fields = ReflectUtil.getFields(AssociationBalanceSheetPartial.class);
        for (Field field : fields) {
            try {
                String factorName = propertyValueMap.get(field.getName());
                if (StrUtil.isBlank(factorName)) {
                    continue;
                }
                ReflectUtil.setFieldValue(currentReportValue, field, Optional.ofNullable(assetMap.get(factorName)).map(Util::millimeterLong2YuanBigDecimal).orElse(BigDecimal.ZERO));
            } catch (Exception e) {
                log.error("金融局报送【资产负债表】自动取值发生异常[fieldName:{}]", field.getName(), e);
            }
        }
        currentReportValue.calculate();
        return Collections.singletonList(currentReportValue);
    }

    @Override
    protected void check(List<AssociationBalanceSheetPartial> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e ->{
            // 资产总计年初值必填 ObjectUtil.isEmpty(e.getAstTotAboy()) || ObjectUtil.isEmpty(e.getLiabToeqAboy()) ||
            if ( isNotEqualBigDecimalSum(e.getAstTotAboy(), e.getLiabToeqAboy())) {
                errorList.add("（年初数）行次37：资产总计=行次77：负债及所有者权益总计");
            }
            // 资产总计_期末数必填ObjectUtil.isEmpty(e.getAstTotAeop()) || ObjectUtil.isEmpty(e.getLiabToeqAeop()) ||
            if (isNotEqualBigDecimalSum(e.getAstTotAeop(), e.getLiabToeqAeop())) {
                errorList.add("（期末数）行次37：资产总计=行次77：负债及所有者权益总计");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationBalanceSheetPartial> serviceBean() {
        return SpringUtil.getBean(AssociationBalanceSheetPartialService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0007;
    }

    private AssociationBalanceSheetPartial convert(List<List<Object>> rows, Map<String, Map<String, String>> dictNameMap) {
        AssociationBalanceSheetPartial bean = new AssociationBalanceSheetPartial();
        bean.setRowNum(1);
        bean.setOp("insert");
        // ########### 第一列年初数(元)
        // 行4 -> 货币资金
        bean.setCrcpAboy(parseBigDecimal(rows.get(4).get(3)));
        // 行5 -> 交易性金融资产
        bean.setTrdFinlAstAboy(parseBigDecimal(rows.get(5).get(3)));
        // 行6 -> 衍生金融资产
        bean.setDevdFinlAstAboy(parseBigDecimal(rows.get(6).get(3)));
        // 行7 -> 应收票据
        bean.setRecvBillAboy(parseBigDecimal(rows.get(7).get(3)));
        // 行8 -> 应收账款
        bean.setRecvAmtAboy(parseBigDecimal(rows.get(8).get(3)));
        // 行9 -> 应收款项融资
        bean.setRecvAmtFinAboy(parseBigDecimal(rows.get(9).get(3)));
        // 行10 -> 预付账款
        bean.setPiaAmtAboy(parseBigDecimal(rows.get(10).get(3)));
        // 行11 -> 其他应收款总计
        bean.setOthRecvAmtTotAboy(parseBigDecimal(rows.get(11).get(3)));
        // 行12 -> 应收股利
        bean.setRecvDivdAboy(parseBigDecimal(rows.get(12).get(3)));
        // 行13 -> 应收利息
        bean.setRecvIntrAboy(parseBigDecimal(rows.get(13).get(3)));
        // 行14 -> 其他应收款
        bean.setOthRecvAmtAboy(parseBigDecimal(rows.get(14).get(3)));
        // 行15 -> 存货
        bean.setInvAboy(parseBigDecimal(rows.get(15).get(3)));
        // 行16 -> 合同资产
        bean.setAgmtAstAboy(parseBigDecimal(rows.get(16).get(3)));
        // 行17 -> 持有待售资产
        bean.setHoldSaleAstAboy(parseBigDecimal(rows.get(17).get(3)));
        // 行18 -> 一年内到期的非流动资产
        bean.setNcoyAboy(parseBigDecimal(rows.get(18).get(3)));
        // 行19 -> 其他流动资产
        bean.setOthLiqdAstAboy(parseBigDecimal(rows.get(19).get(3)));
        // 行20 -> 流动资产合计
        bean.setLiqdAstTotAboy(parseBigDecimal(rows.get(20).get(3)));
        /*// 行21 -> 非流动资产（新增）
        bean.setNoLiqdAstTotAboy(parseBigDecimal(rows.get(21).get(3)));*/
        // 行22 -> 债权投资
        bean.setClamIvsmAboy(parseBigDecimal(rows.get(22).get(3)));
        // 行23 -> 其他债权投资
        bean.setOthClamIvsmAboy(parseBigDecimal(rows.get(23).get(3)));
        // 行24 -> 长期应收款
        bean.setLongRecvAmtAboy(parseBigDecimal(rows.get(24).get(3)));
        // 行25 -> 长期股权投资
        bean.setLsriAboy(parseBigDecimal(rows.get(25).get(3)));
        // 行26 -> 其他权益工具投资
        bean.setOthEquiInstIvsmAboy(parseBigDecimal(rows.get(26).get(3)));
        // 行27 -> 其他非流动金融资产
        bean.setOthNocrFinlAstAboy(parseBigDecimal(rows.get(27).get(3)));
        // 行28 -> 投资性房地产
        bean.setIvsmEsttAboy(parseBigDecimal(rows.get(28).get(3)));
        // 行29 -> 固定资产
        bean.setFixAstAboy(parseBigDecimal(rows.get(29).get(3)));
        // 行30 -> 在建工程
        bean.setUdcsProjAboy(parseBigDecimal(rows.get(30).get(3)));
        // 行31 -> 生产性生物资产
        bean.setProdBiolMatrAboy(parseBigDecimal(rows.get(31).get(3)));
        // 行32 -> 油气资产
        bean.setOlgsAstAboy(parseBigDecimal(rows.get(32).get(3)));
        // 行33 -> 使用权资产
        bean.setUseAstAboy(parseBigDecimal(rows.get(33).get(3)));
        // 行34 -> 无形资产
        bean.setImtrAstAboy(parseBigDecimal(rows.get(34).get(3)));
        // 行35 -> 开发支出
        bean.setDevPayAboy(parseBigDecimal(rows.get(35).get(3)));
        // 行36 -> 商誉
        bean.setGdwlAboy(parseBigDecimal(rows.get(36).get(3)));
        // 行37 -> 长期待摊费用
        bean.setLongTbatFeeAboy(parseBigDecimal(rows.get(37).get(3)));
        // 行38 -> 递延所得税资产
        bean.setDefrTaxAstAboy(parseBigDecimal(rows.get(38).get(3)));
        // 行39 -> 其他非流动资产
        bean.setOthNocrAstAboy(parseBigDecimal(rows.get(39).get(3)));
        // 行40 -> 非流动资产合计
        bean.setNocrAstTotAboy(parseBigDecimal(rows.get(40).get(3)));
        // 行45 -> 资产总计
        bean.setAstTotAboy(parseBigDecimal(rows.get(45).get(3)));

        // ########### 第二列年初数(元)
        // ====================== 负债部分 ======================
        bean.setShttLoanAboy(parseBigDecimal(rows.get(4).get(7)));          // 行4-短期借款
        bean.setTrdFinlLiabAboy(parseBigDecimal(rows.get(5).get(7)));       // 行5-交易性金融负债
        bean.setDevdFinlLiabAboy(parseBigDecimal(rows.get(6).get(7)));       // 行6-衍生金融负债
        bean.setPaybBillAboy(parseBigDecimal(rows.get(7).get(7)));           // 行7-应付票据
        bean.setPaybAmtAboy(parseBigDecimal(rows.get(8).get(7)));            // 行8-应付账款
        bean.setCiadAmtAboy(parseBigDecimal(rows.get(9).get(7)));            // 行9-预收账款
        bean.setAgmtLiabAboy(parseBigDecimal(rows.get(10).get(7)));          // 行10-合同负债
        bean.setPaybEmpCmpsAboy(parseBigDecimal(rows.get(11).get(7)));       // 行11-应付职工薪酬
        bean.setPaycTaxFeeAboy(parseBigDecimal(rows.get(12).get(7)));        // 行12-应交税费
        bean.setOthPaybAmtTotAboy(parseBigDecimal(rows.get(13).get(7)));     // 行13-其他应付款总计
        bean.setPaybIntrAboy(parseBigDecimal(rows.get(14).get(7)));          // 行14-应付利息
        bean.setPaybDivdAboy(parseBigDecimal(rows.get(15).get(7)));          // 行15-应付股利
        bean.setOthPaybAmtAboy(parseBigDecimal(rows.get(16).get(7)));        // 行16-其他应付款
        bean.setHoldSaleLiabAboy(parseBigDecimal(rows.get(17).get(7)));      // 行17-持有待售负债
        bean.setOneyNocrLiabAboy(parseBigDecimal(rows.get(18).get(7)));      // 行18-一年内到期的非流动负债
        bean.setOthLiqdLiabAboy(parseBigDecimal(rows.get(19).get(7)));       // 行19-其他流动负债
        bean.setLiqdLiabTotAboy(parseBigDecimal(rows.get(20).get(7)));       // 行20-流动负债合计

        // ====================== 非流动负债 ======================
        bean.setLongLoanAboy(parseBigDecimal(rows.get(22).get(7)));          // 行22-长期借款
        bean.setPaybBondAboy(parseBigDecimal(rows.get(23).get(7)));          // 行23-应付债券
        bean.setLongPaybAmtAboy(parseBigDecimal(rows.get(24).get(7)));       // 行24-长期应付款
        bean.setLongPaybEmpCmpsAboy(parseBigDecimal(rows.get(25).get(7)));   // 行25-长期应付职工薪酬
        bean.setLeasLiabAboy(parseBigDecimal(rows.get(26).get(7)));          // 行26-租赁负债
        bean.setExpeLiabAboy(parseBigDecimal(rows.get(27).get(7)));          // 行27-预计负债
        bean.setDefrPayfAboy(parseBigDecimal(rows.get(28).get(7)));          // 行28-递延收益
        bean.setDefrInctLiabAboy(parseBigDecimal(rows.get(29).get(7)));       // 行29-递延所得税负债
        bean.setOthNocrLiabAboy(parseBigDecimal(rows.get(30).get(7)));        // 行30-其他非流动负债
        bean.setNocrLiabTotAboy(parseBigDecimal(rows.get(31).get(7)));       // 行31-非流动负债合计
        // ====================== 负债合计 ======================
        bean.setLiabTotAboy(parseBigDecimal(rows.get(32).get(7)));       //  行32-负债合计

        // ====================== 所有者权益 ======================
        bean.setPaidCptlAboy(parseBigDecimal(rows.get(34).get(7)));      // 行34-实收资本（原33后移）
        bean.setOthEquiInstAboy(parseBigDecimal(rows.get(35).get(7)));   // 行35-其他权益工具
        bean.setCptlRsrvAboy(parseBigDecimal(rows.get(36).get(7)));      // 行36-资本公积
        bean.setOthCmphPayfAboy(parseBigDecimal(rows.get(37).get(7)));   // 行37-其他综合收益
        bean.setSpclRsrvAboy(parseBigDecimal(rows.get(38).get(7)));      // 行38-专项储备
        bean.setSurpRsrvAboy(parseBigDecimal(rows.get(39).get(7)));      // 行39-盈余公积
        bean.setRiskPrepAboy(parseBigDecimal(rows.get(40).get(7)));      // 行40-一般风险准备
        bean.setNoAssnProfAboy(parseBigDecimal(rows.get(41).get(7)));    // 行41-未分配利润
        bean.setAttrPrnCponEquiAboy(parseBigDecimal(rows.get(42).get(7)));//行42-归属母公司权益
        bean.setMishEquiAboy(parseBigDecimal(rows.get(43).get(7)));       // 行43-少数股东权益
        bean.setToeqAboy(parseBigDecimal(rows.get(44).get(7)));          // 行44-所有者权益合计
        bean.setLiabToeqAboy(parseBigDecimal(rows.get(45).get(7)));      // 行45-负债及权益总计

        // ########### 第一列年终数(元)
       // ====================== 流动资产 ======================
        bean.setCrcpAeop(parseBigDecimal(rows.get(4).get(2)));         // 行4-货币资金
        bean.setTrdFinlAstAeop(parseBigDecimal(rows.get(5).get(2)));   // 行5-交易性金融资产
        bean.setDevdFinlAstAeop(parseBigDecimal(rows.get(6).get(2)));  // 行6-衍生金融资产
        bean.setRecvBillAeop(parseBigDecimal(rows.get(7).get(2)));     // 行7-应收票据
        bean.setRecvAmtAeop(parseBigDecimal(rows.get(8).get(2)));      // 行8-应收账款
        bean.setRecvAmtFinAeop(parseBigDecimal(rows.get(9).get(2)));   // 行9-应收款项融资
        bean.setPiaAmtAeop(parseBigDecimal(rows.get(10).get(2)));      // 行10-预付账款
        bean.setOthRecvAmtTotAeop(parseBigDecimal(rows.get(11).get(2)));// 行11-其他应收款总计
        bean.setRecvDivdAeop(parseBigDecimal(rows.get(12).get(2)));    // 行12-应收股利
        bean.setRecvIntrAeop(parseBigDecimal(rows.get(13).get(2)));    // 行13-应收利息
        bean.setOthRecvAmtAeop(parseBigDecimal(rows.get(14).get(2))); // 行14-其他应收款
        bean.setInvAeop(parseBigDecimal(rows.get(15).get(2)));         // 行15-存货
        bean.setAgmtAstAeop(parseBigDecimal(rows.get(16).get(2)));     // 行16-合同资产
        bean.setHoldSaleAstAeop(parseBigDecimal(rows.get(17).get(2))); // 行17-持有待售资产
        bean.setNcoyAeop(parseBigDecimal(rows.get(18).get(2)));        // 行18-一年内到期非流动资产
        bean.setOthLiqdAstAeop(parseBigDecimal(rows.get(19).get(2)));  // 行19-其他流动资产
        bean.setLiqdAstTotAeop(parseBigDecimal(rows.get(20).get(2))); // 行20-流动资产合计


        // ====================== 非流动资产明细 ======================
        bean.setClamIvsmAeop(parseBigDecimal(rows.get(22).get(2)));    // 行22-债权投资
        bean.setOthClamIvsmAeop(parseBigDecimal(rows.get(23).get(2))); // 行23-其他债权投资
        bean.setLongRecvAmtAeop(parseBigDecimal(rows.get(24).get(2))); // 行24-长期应收款
        bean.setLsriAeop(parseBigDecimal(rows.get(25).get(2)));        // 行25-长期股权投资
        bean.setOthEquiInstIvsmAeop(parseBigDecimal(rows.get(26).get(2)));// 行26-其他权益工具投资
        bean.setOthNocrFinlAstAeop(parseBigDecimal(rows.get(27).get(2)));// 行27-其他非流动金融资产
        bean.setIvsmEsttAeop(parseBigDecimal(rows.get(28).get(2)));    // 行28-投资性房地产
        bean.setFixAstAeop(parseBigDecimal(rows.get(29).get(2)));      // 行29-固定资产
        bean.setUdcsProjAeop(parseBigDecimal(rows.get(30).get(2)));     // 行30-在建工程
        bean.setProdBiolMatrAeop(parseBigDecimal(rows.get(31).get(2)));// 行31-生产性生物资产
        bean.setOlgsAstAeop(parseBigDecimal(rows.get(32).get(2)));      // 行32-油气资产
        bean.setUseAstAeop(parseBigDecimal(rows.get(33).get(2)));      // 行33-使用权资产
        bean.setImtrAstAeop(parseBigDecimal(rows.get(34).get(2)));      // 行34-无形资产
        bean.setDevPayAeop(parseBigDecimal(rows.get(35).get(2)));      // 行35-开发支出
        bean.setGdwlAeop(parseBigDecimal(rows.get(36).get(2)));         // 行36-商誉
        bean.setLongTbatFeeAeop(parseBigDecimal(rows.get(37).get(2))); // 行37-长期待摊费用
        bean.setDefrTaxAstAeop(parseBigDecimal(rows.get(38).get(2)));  // 行38-递延所得税资产
        bean.setOthNocrAstAeop(parseBigDecimal(rows.get(39).get(2)));  // 行39-其他非流动资产
        bean.setNocrAstTotAeop(parseBigDecimal(rows.get(40).get(2)));  // 行40-非流动资产合计
        // ====================== 总计 ======================
        bean.setAstTotAeop(parseBigDecimal(rows.get(45).get(2)));      // 行45-资产总计

        // ########### 第二列年终数(元)
        // ====================== 流动负债 ======================
        bean.setShttLoanAeop(parseBigDecimal(rows.get(4).get(6)));       // 行4  -> 短期借款
        bean.setTrdFinlLiabAeop(parseBigDecimal(rows.get(5).get(6)));    // 行5  -> 交易性金融负债
        bean.setDevdFinlLiabAeop(parseBigDecimal(rows.get(6).get(6)));  // 行6  -> 衍生金融负债
        bean.setPaybBillAeop(parseBigDecimal(rows.get(7).get(6)));      // 行7  -> 应付票据
        bean.setPaybAmtAeop(parseBigDecimal(rows.get(8).get(6)));       // 行8  -> 应付账款
        bean.setCiadAmtAeop(parseBigDecimal(rows.get(9).get(6)));       // 行9  -> 预收账款
        bean.setAgmtLiabAeop(parseBigDecimal(rows.get(10).get(6)));     // 行10 -> 合同负债
        bean.setPaybEmpCmpsAeop(parseBigDecimal(rows.get(11).get(6)));  // 行11 -> 应付职工薪酬
        bean.setPaycTaxFeeAeop(parseBigDecimal(rows.get(12).get(6)));   // 行12 -> 应交税费
        bean.setOthPaybAmtTotAeop(parseBigDecimal(rows.get(13).get(6)));// 行13 -> 其他应付款总计
        bean.setPaybIntrAeop(parseBigDecimal(rows.get(14).get(6)));     // 行14 -> 应付利息
        bean.setPaybDivdAeop(parseBigDecimal(rows.get(15).get(6)));     // 行15 -> 应付股利
        bean.setOthPaybAmtAeop(parseBigDecimal(rows.get(16).get(6)));   // 行16 -> 其他应付款
        bean.setHoldSaleLiabAeop(parseBigDecimal(rows.get(17).get(6))); // 行17 -> 持有待售负债
        bean.setOneyNocrLiabAeop(parseBigDecimal(rows.get(18).get(6))); // 行18 -> 一年内到期非流动负债
        bean.setOthLiqdLiabAeop(parseBigDecimal(rows.get(19).get(6)));  // 行19 -> 其他流动负债
        bean.setLiqdLiabTotAeop(parseBigDecimal(rows.get(20).get(6)));  // 行20 -> 流动负债合计

        // ====================== 非流动负债 ======================
        bean.setLongLoanAeop(parseBigDecimal(rows.get(22).get(6)));     // 行22 -> 长期借款
        bean.setPaybBondAeop(parseBigDecimal(rows.get(23).get(6)));     // 行23 -> 应付债券
        bean.setLongPaybAmtAeop(parseBigDecimal(rows.get(24).get(6)));  // 行24 -> 长期应付款
        bean.setLongPaybEmpCmpsAeop(parseBigDecimal(rows.get(25).get(6)));// 行25->长期应付职工薪酬
        bean.setLeasLiabAeop(parseBigDecimal(rows.get(26).get(6)));     // 行26 -> 租赁负债
        bean.setExpeLiabAeop(parseBigDecimal(rows.get(27).get(6)));    // 行27 -> 预计负债
        bean.setDefrPayfAeop(parseBigDecimal(rows.get(28).get(6)));     // 行28 -> 递延收益
        bean.setDefrInctLiabAeop(parseBigDecimal(rows.get(29).get(6))); // 行29 -> 递延所得税负债
        bean.setOthNocrLiabAeop(parseBigDecimal(rows.get(30).get(6)));  // 行30 -> 其他非流动负债
        bean.setNocrLiabTotAeop(parseBigDecimal(rows.get(31).get(6))); // 行31 -> 非流动负债合计

        // ====================== 负债总计 ======================
        bean.setLiabTotAeop(parseBigDecimal(rows.get(32).get(6)));      // 行32 -> 负债合计

        // ====================== 所有者权益 ======================
        bean.setPaidCptlAeop(parseBigDecimal(rows.get(34).get(6)));     // 行34 -> 实收资本
        bean.setOthEquiInstAeop(parseBigDecimal(rows.get(35).get(6))); // 行35 -> 其他权益工具
        bean.setCptlRsrvAeop(parseBigDecimal(rows.get(36).get(6)));    // 行36 -> 资本公积
        bean.setOthCmphPayfAeop(parseBigDecimal(rows.get(37).get(6))); // 行37 -> 其他综合收益
        bean.setSpclRsrvAeop(parseBigDecimal(rows.get(38).get(6)));    // 行38 -> 专项储备
        bean.setSurpRsrvAeop(parseBigDecimal(rows.get(39).get(6)));    // 行39 -> 盈余公积
        bean.setRiskPrepAeop(parseBigDecimal(rows.get(40).get(6)));    // 行40 -> 一般风险准备
        bean.setNoAssnProfAeop(parseBigDecimal(rows.get(41).get(6)));  // 行41 -> 未分配利润
        bean.setAttrPrnCponEquiAeop(parseBigDecimal(rows.get(42).get(6)));// 行42->归属母公司权益
        bean.setMishEquiAeop(parseBigDecimal(rows.get(43).get(6)));    // 行43 -> 少数股东权益
        bean.setToeqAeop(parseBigDecimal(rows.get(44).get(6)));        // 行44 -> 所有者权益合计
        bean.setLiabToeqAeop(parseBigDecimal(rows.get(45).get(6)));    // 行45 -> 负债及权益总计
        return bean;
    }

    private Map<String, String> mapping() {
        Map<String, String> mapping = new HashMap<>(256);
        mapping.put("crcpAboy", "货币资金@年初余额");
        mapping.put("trdFinlAstAboy", "☆交易性金融资产@年初余额");
        mapping.put("devdFinlAstAboy", "衍生金融资产@年初余额");
        mapping.put("recvBillAboy", "应收票据@年初余额");
        mapping.put("recvAmtAboy", "应收账款@年初余额");
        mapping.put("recvAmtFinAboy", "☆应收款项融资@年初余额");
        mapping.put("piaAmtAboy", "预付款项@年初余额");
        mapping.put("othRecvAmtTotAboy", "其他应收款@年初余额");
        mapping.put("recvDivdAboy", "应收股利@年初余额");
        mapping.put("recvIntrAboy", "应收利息@年初余额");
        mapping.put("othRecvAmtAboy", "其他应收款@年初余额");
        mapping.put("invAboy", "存货@年初余额");
        mapping.put("agmtAstAboy", "☆合同资产@年初余额");
        mapping.put("holdSaleAstAboy", "持有待售资产@年初余额");
        mapping.put("ncoyAboy", "一年内到期的非流动资产@年初余额");
        mapping.put("othLiqdAstAboy", "其他流动资产@年初余额");
        mapping.put("liqdAstTotAboy", "流动资产合计@年初余额");
        mapping.put("clamIvsmAboy", "☆债权投资@年初余额");
        mapping.put("othClamIvsmAboy", "☆其他债权投资@年初余额");
        mapping.put("longRecvAmtAboy", "长期应收款@年初余额");
        mapping.put("lsriAboy", "长期股权投资@年初余额");
        mapping.put("othEquiInstIvsmAboy", "☆其他权益工具投资@年初余额");
        mapping.put("othNocrFinlAstAboy", "☆其他非流动金融资产@年初余额");
        mapping.put("ivsmEsttAboy", "投资性房地产@年初余额");
        mapping.put("fixAstAboy", "固定资产净额@年初余额");
        mapping.put("udcsProjAboy", "在建工程@年初余额");
        mapping.put("prodBiolMatrAboy", "生产性生物资产@年初余额");
        mapping.put("olgsAstAboy", "油气资产@年初余额");
        mapping.put("useAstAboy", "☆使用权资产@年初余额");
        mapping.put("imtrAstAboy", "无形资产@年初余额");
        mapping.put("devPayAboy", "开发支出@年初余额");
        mapping.put("gdwlAboy", "商誉@年初余额");
        mapping.put("longTbatFeeAboy", "长期待摊费用@年初余额");
        mapping.put("defrTaxAstAboy", "递延所得税资产@年初余额");
        mapping.put("othNocrAstAboy", "其他非流动资产@年初余额");
        mapping.put("nocrAstTotAboy", "非流动资产合计@年初余额");
        mapping.put("astTotAboy", "资产总计@年初余额");
        mapping.put("shttLoanAboy", "短期借款@年初余额");
        mapping.put("trdFinlLiabAboy", "☆交易性金融负债@年初余额");
        mapping.put("devdFinlLiabAboy", "衍生金融负债@年初余额");
        mapping.put("paybBillAboy", "应付票据@年初余额");
        mapping.put("paybAmtAboy", "应付账款@年初余额");
        mapping.put("ciadAmtAboy", "预收款项@年初余额");
        mapping.put("agmtLiabAboy", "☆合同负债@年初余额");
        mapping.put("paybEmpCmpsAboy", "应付职工薪酬@年初余额");
        mapping.put("paycTaxFeeAboy", "应交税费@年初余额");
        mapping.put("othPaybAmtTotAboy", "其他应付款@年初余额");
        mapping.put("paybIntrAboy", "应付利息@年初余额");
        mapping.put("paybDivdAboy", "应付股利@年初余额");
        mapping.put("othPaybAmtAboy", "其他应付款@年初余额");
        mapping.put("holdSaleLiabAboy", "持有待售负债@年初余额");
        mapping.put("oneyNocrLiabAboy", "一年内到期的非流动负债@年初余额");
        mapping.put("othLiqdLiabAboy", "其他流动负债@年初余额");
        mapping.put("liqdLiabTotAboy", "流动负债合计@年初余额");
        mapping.put("longLoanAboy", "长期借款@年初余额");
        mapping.put("paybBondAboy", "应付债券@年初余额");
        mapping.put("longPaybAmtAboy", "长期应付款@年初余额");
        mapping.put("longPaybEmpCmpsAboy", "长期应付职工薪酬@年初余额");
        mapping.put("leasLiabAboy", "☆租赁负债@年初余额");
        mapping.put("expeLiabAboy", "预计负债@年初余额");
        mapping.put("defrPayfAboy", "递延收益@年初余额");
        mapping.put("defrInctLiabAboy", "递延所得税负债@年初余额");
        mapping.put("othNocrLiabAboy", "其他非流动负债@年初余额");
        mapping.put("nocrLiabTotAboy", "非流动负债合计@年初余额");
        mapping.put("liabTotAboy", "负债合计@年初余额");
        mapping.put("paidCptlAboy", "实收资本（或股本）净额@年初余额");
        mapping.put("othEquiInstAboy", "其他权益工具@年初余额");
        mapping.put("cptlRsrvAboy", "资本公积@年初余额");
        mapping.put("othCmphPayfAboy", "其他综合收益@年初余额");
        mapping.put("spclRsrvAboy", "专项储备@年初余额");
        mapping.put("surpRsrvAboy", "盈余公积@年初余额");
        mapping.put("riskPrepAboy", "△一般风险准备@年初余额");
        mapping.put("noAssnProfAboy", "未分配利润@年初余额");
        mapping.put("attrPrnCponEquiAboy", "归属于母公司所有者权益合计@年初余额");
        mapping.put("mishEquiAboy", "*少数股东权益@年初余额");
        mapping.put("toeqAboy", "所有者权益（或股东权益）合计@年初余额");
        mapping.put("liabToeqAboy", "负债和所有者权益（或股东权益）总计@年初余额");
        mapping.put("crcpAeop", "货币资金@期末余额");
        mapping.put("trdFinlAstAeop", "☆交易性金融资产@期末余额");
        mapping.put("devdFinlAstAeop", "衍生金融资产@期末余额");
        mapping.put("recvBillAeop", "应收票据@期末余额");
        mapping.put("recvAmtAeop", "应收账款@期末余额");
        mapping.put("recvAmtFinAeop", "☆应收款项融资@期末余额");
        mapping.put("piaAmtAeop", "预付款项@期末余额");
        mapping.put("othRecvAmtTotAeop", "其他应收款@期末余额");
        mapping.put("recvDivdAeop", "应收股利@期末余额");
        mapping.put("recvIntrAeop", "应收利息@期末余额");
        mapping.put("othRecvAmtAeop", "其他应收款@期末余额");
        mapping.put("invAeop", "存货@期末余额");
        mapping.put("agmtAstAeop", "☆合同资产@期末余额");
        mapping.put("holdSaleAstAeop", "持有待售资产@期末余额");
        mapping.put("ncoyAeop", "一年内到期的非流动资产@期末余额");
        mapping.put("othLiqdAstAeop", "其他流动资产@期末余额");
        mapping.put("liqdAstTotAeop", "流动资产合计@期末余额");
        mapping.put("clamIvsmAeop", "☆债权投资@期末余额");
        mapping.put("othClamIvsmAeop", "☆其他债权投资@期末余额");
        mapping.put("longRecvAmtAeop", "长期应收款@期末余额");
        mapping.put("lsriAeop", "长期股权投资@期末余额");
        mapping.put("othEquiInstIvsmAeop", "☆其他权益工具投资@期末余额");
        mapping.put("othNocrFinlAstAeop", "☆其他非流动金融资产@期末余额");
        mapping.put("ivsmEsttAeop", "投资性房地产@期末余额");
        mapping.put("fixAstAeop", "固定资产净额@期末余额");
        mapping.put("udcsProjAeop", "在建工程@期末余额");
        mapping.put("prodBiolMatrAeop", "生产性生物资产@期末余额");
        mapping.put("olgsAstAeop", "油气资产@期末余额");
        mapping.put("useAstAeop", "☆使用权资产@期末余额");
        mapping.put("imtrAstAeop", "无形资产@期末余额");
        mapping.put("devPayAeop", "开发支出@期末余额");
        mapping.put("gdwlAeop", "商誉@期末余额");
        mapping.put("longTbatFeeAeop", "长期待摊费用@期末余额");
        mapping.put("defrTaxAstAeop", "递延所得税资产@期末余额");
        mapping.put("othNocrAstAeop", "其他非流动资产@期末余额");
        mapping.put("nocrAstTotAeop", "非流动资产合计@期末余额");
        mapping.put("astTotAeop", "资产总计@期末余额");
        mapping.put("shttLoanAeop", "短期借款@期末余额");
        mapping.put("trdFinlLiabAeop", "☆交易性金融负债@期末余额");
        mapping.put("devdFinlLiabAeop", "衍生金融负债@期末余额");
        mapping.put("paybBillAeop", "应付票据@期末余额");
        mapping.put("paybAmtAeop", "应付账款@期末余额");
        mapping.put("ciadAmtAeop", "预收款项@期末余额");
        mapping.put("agmtLiabAeop", "☆合同负债@期末余额");
        mapping.put("paybEmpCmpsAeop", "应付职工薪酬@期末余额");
        mapping.put("paycTaxFeeAeop", "应交税费@期末余额");
        mapping.put("othPaybAmtTotAeop", "其他应付款@期末余额");
        mapping.put("paybIntrAeop", "应付利息@期末余额");
        mapping.put("paybDivdAeop", "应付股利@期末余额");
        mapping.put("othPaybAmtAeop", "其他应付款@期末余额");
        mapping.put("holdSaleLiabAeop", "持有待售负债@期末余额");
        mapping.put("oneyNocrLiabAeop", "一年内到期的非流动负债@期末余额");
        mapping.put("othLiqdLiabAeop", "其他流动负债@期末余额");
        mapping.put("liqdLiabTotAeop", "流动负债合计@期末余额");
        mapping.put("longLoanAeop", "长期借款@期末余额");
        mapping.put("paybBondAeop", "应付债券@期末余额");
        mapping.put("longPaybAmtAeop", "长期应付款@期末余额");
        mapping.put("longPaybEmpCmpsAeop", "长期应付职工薪酬@期末余额");
        mapping.put("leasLiabAeop", "☆租赁负债@期末余额");
        mapping.put("expeLiabAeop", "预计负债@期末余额");
        mapping.put("defrPayfAeop", "递延收益@期末余额");
        mapping.put("defrInctLiabAeop", "递延所得税负债@期末余额");
        mapping.put("othNocrLiabAeop", "其他非流动负债@期末余额");
        mapping.put("nocrLiabTotAeop", "非流动负债合计@期末余额");
        mapping.put("liabTotAeop", "负债合计@期末余额");
        mapping.put("paidCptlAeop", "实收资本（或股本）净额@期末余额");
        mapping.put("othEquiInstAeop", "其他权益工具@期末余额");
        mapping.put("cptlRsrvAeop", "资本公积@期末余额");
        mapping.put("othCmphPayfAeop", "其他综合收益@期末余额");
        mapping.put("spclRsrvAeop", "专项储备@期末余额");
        mapping.put("surpRsrvAeop", "盈余公积@期末余额");
        mapping.put("riskPrepAeop", "△一般风险准备@期末余额");
        mapping.put("noAssnProfAeop", "未分配利润@期末余额");
        mapping.put("attrPrnCponEquiAeop", "归属于母公司所有者权益合计@期末余额");
        mapping.put("mishEquiAeop", "*少数股东权益@期末余额");
        mapping.put("toeqAeop", "所有者权益（或股东权益）合计@期末余额");
        mapping.put("liabToeqAeop", "负债和所有者权益（或股东权益）总计@期末余额");
        return mapping;
    }
}
