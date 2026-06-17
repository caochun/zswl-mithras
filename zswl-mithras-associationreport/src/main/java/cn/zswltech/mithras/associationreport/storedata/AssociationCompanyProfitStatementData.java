package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.AssociationReportAmountUtils;
import cn.zswltech.mithras.associationreport.AssociationReportDateUtils;
import cn.zswltech.mithras.associationreport.application.AssociationReportMetricPort;
import cn.zswltech.mithras.associationreport.service.AssociationCompanyProfitStatementService;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatement;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @date 2025/4/18
 * @description 公司利润表数据表
 */
@Slf4j
@Component
public class AssociationCompanyProfitStatementData extends AbstractDataStore<AssociationCompanyProfitStatement> {
    @Resource
    private AssociationReportMetricPort metricPort;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate = AssociationReportDateUtils.ensureQuarterLastDay(year, period);
        Map<String, Long> profitMap = metricPort.profit(dataDate.getYear(), dataDate.getMonthValue());
        boolean condition = CollectionUtil.isNotEmpty(profitMap);
        log.info("金融局报送【利润表】自动取值-前置数据校验结果:利润表 = {}", condition);
        return condition;
    }

    @Override
    protected List<AssociationCompanyProfitStatement> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【利润表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<利润表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司利润表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "项目");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<利润表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationCompanyProfitStatement> list = new LinkedList<>();
        try {
            if (rows.size() < 21) {
                throw new MithrasException("表格格式不正确，缺失必要行");
            }
            list.add(this.convert(rows, dictNameMap));
        } catch (Exception e) {
            log.error("金融局报送【利润表】数据处理异常", e);
            throw new MithrasException("数据处理异常");
        }
        return list;
    }

    @Override
    protected List<AssociationCompanyProfitStatement> parseFromSystemData(AssociationReport currentReport) {
        // 取上一期
        int[] yearPeriod = this.ensureLastOneYearPeriod(currentReport);
        AssociationReport lastReport = associationReportQueryService.findByCategoryYearPeriod(currentReport.getReportCategoryCode(), yearPeriod[0], yearPeriod[1]);
        if (Objects.isNull(lastReport)) {
            throw new MithrasException(String.format("金融局报送【利润表】自动取值-没有找到上一期报送记录[current:%s]", currentReport.getReportInstanceId()));
        }
        AssociationCompanyProfitStatement lastReportValue = SpringUtil.getBean(AssociationCompanyProfitStatementService.class).getModelByReportInstanceId(lastReport.getReportInstanceId());
        // 查询财务报表
        LocalDate targetDate = this.ensureMetricDate(currentReport);
        Map<String, Long> profitMap = metricPort.profit(targetDate.getYear(), targetDate.getMonthValue());
        AssociationCompanyProfitStatement currentReportValue = new AssociationCompanyProfitStatement();
        currentReportValue.setBusiFeeCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("销售费用@上年同期累计数")).orElse(0L)));
        currentReportValue.setBusiFeeTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("销售费用@本年累计数")).orElse(0L)));
        currentReportValue.setBusiFeeActm(currentReportValue.getBusiFeeTyag().subtract(Optional.ofNullable(lastReportValue.getBusiFeeTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setCredDecrLossCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(-1 * Optional.ofNullable(profitMap.get("☆信用减值损失（损失以“－”号填列）@上年同期累计数")).orElse(0L)));
        currentReportValue.setCredDecrLossTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(-1 * Optional.ofNullable(profitMap.get("☆信用减值损失（损失以“－”号填列）@本年累计数")).orElse(0L)));
        currentReportValue.setCredDecrLossActm(currentReportValue.getCredDecrLossTyag().subtract(Optional.ofNullable(lastReportValue.getCredDecrLossTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setFinFeeCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("财务费用@上年同期累计数")).orElse(0L)));
        currentReportValue.setFinFeeTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("财务费用@本年累计数")).orElse(0L)));
        currentReportValue.setFinFeeActm(currentReportValue.getFinFeeTyag().subtract(Optional.ofNullable(lastReportValue.getFinFeeTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setInctFeeCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("减：所得税费用@上年同期累计数")).orElse(0L)));
        currentReportValue.setInctFeeTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("减：所得税费用@本年累计数")).orElse(0L)));
        currentReportValue.setInctFeeActm(currentReportValue.getInctFeeTyag().subtract(Optional.ofNullable(lastReportValue.getInctFeeTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setIpoaLossCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("资产减值损失（损失以“－”号填列）@上年同期累计数")).orElse(0L)));
        currentReportValue.setIpoaLossTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("资产减值损失（损失以“－”号填列）@本年累计数")).orElse(0L)));
        currentReportValue.setIpoaLossActm(currentReportValue.getIpoaLossTyag().subtract(Optional.ofNullable(lastReportValue.getIpoaLossTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setIvsmPayfCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("投资收益（损失以“－”号填列）@上年同期累计数")).orElse(0L)));
        currentReportValue.setIvsmPayfTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("投资收益（损失以“－”号填列）@本年累计数")).orElse(0L)));
        currentReportValue.setIvsmPayfActm(currentReportValue.getIvsmPayfTyag().subtract(Optional.ofNullable(lastReportValue.getIvsmPayfTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setMagFeeCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("管理费用@上年同期累计数")).orElse(0L)));
        currentReportValue.setMagFeeTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("管理费用@本年累计数")).orElse(0L)));
        currentReportValue.setMagFeeActm(currentReportValue.getMagFeeTyag().subtract(Optional.ofNullable(lastReportValue.getMagFeeTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setMainBusiIncmCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("其中：主营业务收入@上年同期累计数")).orElse(0L)));
        currentReportValue.setMainBusiIncmTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("其中：主营业务收入@本年累计数")).orElse(0L)));
        currentReportValue.setMainBusiIncmActm(currentReportValue.getMainBusiIncmTyag().subtract(Optional.ofNullable(lastReportValue.getMainBusiIncmTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setMainBusiCostCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("其中：主营业务成本@上年同期累计数")).orElse(0L)));
        currentReportValue.setMainBusiCostTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("其中：主营业务成本@本年累计数")).orElse(0L)));
        currentReportValue.setMainBusiCostActm(currentReportValue.getMainBusiCostTyag().subtract(Optional.ofNullable(lastReportValue.getMainBusiCostTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setMainBusiTaxAddCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("税金及附加@上年同期累计数")).orElse(0L)));
        currentReportValue.setMainBusiTaxAddTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("税金及附加@本年累计数")).orElse(0L)));
        currentReportValue.setMainBusiTaxAddActm(currentReportValue.getMainBusiTaxAddTyag().subtract(Optional.ofNullable(lastReportValue.getMainBusiTaxAddTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setNoprIncmCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("加：营业外收入@上年同期累计数")).orElse(0L)));
        currentReportValue.setNoprIncmTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("加：营业外收入@本年累计数")).orElse(0L)));
        currentReportValue.setNoprIncmActm(currentReportValue.getNoprIncmTyag().subtract(Optional.ofNullable(lastReportValue.getNoprIncmTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setNoprPayCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("减：营业外支出@上年同期累计数")).orElse(0L)));
        currentReportValue.setNoprPayTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("减：营业外支出@本年累计数")).orElse(0L)));
        currentReportValue.setNoprPayActm(currentReportValue.getNoprPayTyag().subtract(Optional.ofNullable(lastReportValue.getNoprPayTyag()).orElse(BigDecimal.ZERO)));
        currentReportValue.setOthBusiProfCply(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("加：其他收益@上年同期累计数")).orElse(0L) + Optional.ofNullable(profitMap.get("资产处置收益（损失以“－”号填列）@上年同期累计数")).orElse(0L)));
        currentReportValue.setOthBusiProfTyag(AssociationReportAmountUtils.millimeterLong2YuanBigDecimal(Optional.ofNullable(profitMap.get("加：其他收益@本年累计数")).orElse(0L) + Optional.ofNullable(profitMap.get("资产处置收益（损失以“－”号填列）@本年累计数")).orElse(0L)));
        currentReportValue.setOthBusiProfActm(currentReportValue.getOthBusiProfTyag().subtract(Optional.ofNullable(lastReportValue.getOthBusiProfTyag()).orElse(BigDecimal.ZERO)));
        // 计算非直接取值数据
        currentReportValue.calculate();
        return Collections.singletonList(currentReportValue);
    }

    @Override
    protected void check(List<AssociationCompanyProfitStatement> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (isNotEqualBigDecimalSum(e.getBusiProfCply(),
                    bigDecimalAddSum(e.getMainBusiProfCply(), e.getOthBusiProfCply()).subtract(bigDecimalAddSum(e.getBusiFeeCply(), e.getMagFeeCply(), e.getFinFeeCply(), e.getIpoaLossCply(), e.getCredDecrLossCply())))) {
                errorList.add("（上年同期）行号14：营业利润=行号7：主营业务利润+行号8：其他业务利润-行号9：营业费用-行号10：管理费用-行号11：财务费用-行号12：资产减值损失-行号13：信用减值损失");
            }
            if (isNotEqualBigDecimalSum(e.getProfGamtCply(), bigDecimalAddSum(e.getBusiProfCply(), e.getIvsmPayfCply(), e.getNoprIncmCply()).subtract(bigDecimalAddSum(e.getNoprPayCply())))) {
                errorList.add("（上年同期）行号18：利润总额=行号14：营业利润+行号15：投资收益+行号16：营业外收入-行号17：营业外支出");
            }

            if (isNotEqualBigDecimalSum(e.getNetProfCply(), bigDecimalAddSum(e.getProfGamtCply()).subtract(bigDecimalAddSum(e.getInctFeeCply())))) {
                errorList.add("（上年同期）行号20：净利润=行号18：利润总额-行号19：所得税费用");
            }

            if (isNotEqualBigDecimalSum(e.getMainBusiProfCply(), bigDecimalAddSum(e.getMainBusiIncmCply()).subtract(bigDecimalAddSum(e.getMainBusiCostCply(), e.getMainBusiTaxAddCply())))) {
                errorList.add("（上年同期）行号7：主营业务利润=行号4：主营业务收入-行号5：主营业务成本-行号6：主营业务税金及附加");
            }

            if (isNotEqualBigDecimalSum(e.getBusiProfActm(), bigDecimalAddSum(e.getMainBusiProfActm(), e.getOthBusiProfActm()).subtract(bigDecimalAddSum(e.getBusiFeeActm(), e.getMagFeeActm(), e.getFinFeeActm(), e.getIpoaLossActm(), e.getCredDecrLossActm())))) {
                errorList.add("（本季金额）行号14：营业利润=行号7：主营业务利润+行号8：其他业务利润-行号9：营业费用-行号10：管理费用-行号11：财务费用-行号12：资产减值损失-行号13：信用减值损失");
            }

            if (isNotEqualBigDecimalSum(e.getProfGamtActm(), bigDecimalAddSum(e.getBusiProfActm(), e.getIvsmPayfActm(), e.getNoprIncmActm()).subtract(bigDecimalAddSum(e.getNoprPayActm())))) {
                errorList.add("（本季金额）行号18：利润总额=行号14：营业利润+行号15：投资收益+行号16：营业外收入-行号17：营业外支出");
            }

            if (isNotEqualBigDecimalSum(e.getNetProfActm(), bigDecimalAddSum(e.getProfGamtActm()).subtract(bigDecimalAddSum(e.getInctFeeActm())))) {
                errorList.add("（本季金额）行号20：净利润=行号18：利润总额-行号19：所得税费用");
            }

            if (isNotEqualBigDecimalSum(e.getMainBusiProfActm(), bigDecimalAddSum(e.getMainBusiIncmActm()).subtract(bigDecimalAddSum(e.getMainBusiCostActm(), e.getMainBusiTaxAddActm())))) {
                errorList.add("（本季金额）行号7：主营业务利润=行号4：主营业务收入-行号5：主营业务成本-行号6：主营业务税金及附加");
            }

            if (isNotEqualBigDecimalSum(e.getBusiProfTyag(), bigDecimalAddSum(e.getMainBusiProfTyag(), e.getOthBusiProfTyag()).subtract(bigDecimalAddSum(e.getBusiFeeTyag(), e.getMagFeeTyag(), e.getFinFeeTyag(), e.getIpoaLossTyag(), e.getCredDecrLossTyag())))) {
                errorList.add("（本年累计）行号14：营业利润=行号7：主营业务利润+行号8：其他业务利润-行号9：营业费用-行号10：管理费用-行号11：财务费用-行号12：资产减值损失-行号13：信用减值损失");
            }

            if (isNotEqualBigDecimalSum(e.getProfGamtTyag(), bigDecimalAddSum(e.getBusiProfTyag(), e.getIvsmPayfTyag(), e.getNoprIncmTyag()).subtract(bigDecimalAddSum(e.getNoprPayTyag())))) {
                errorList.add("（本年累计）行号18：利润总额=行号14：营业利润+行号15：投资收益+行号16：营业外收入-行号17：营业外支出");
            }
            if (isNotEqualBigDecimalSum(e.getNetProfTyag(), bigDecimalAddSum(e.getProfGamtTyag()).subtract(bigDecimalAddSum(e.getInctFeeTyag())))) {
                errorList.add("（本年累计）行号20：净利润=行号18：利润总额-行号19：所得税费用");
            }
            if (isNotEqualBigDecimalSum(e.getMainBusiProfTyag(), bigDecimalAddSum(e.getMainBusiIncmTyag()).subtract(bigDecimalAddSum(e.getMainBusiCostTyag(), e.getMainBusiTaxAddTyag())))) {
                errorList.add("（本年累计）行号7：主营业务利润=行号4：主营业务收入-行号5：主营业务成本-行号6：主营业务税金及附加");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationCompanyProfitStatement> serviceBean() {
        return SpringUtil.getBean(AssociationCompanyProfitStatementService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0008;
    }

    //一行转多行
    private AssociationCompanyProfitStatement convert(List<List<Object>> excelRows, Map<String, Map<String, String>> dictNameMap) {
        AssociationCompanyProfitStatement bean = new AssociationCompanyProfitStatement();
        bean.setRowNum(1);
        bean.setOp("insert");

        // 第3行 主营业务收入
        List<Object> row3 = excelRows.get(3);
        bean.setMainBusiIncmActm(parseBigDecimal(row3.get(1)));  // 本季实际
        bean.setMainBusiIncmTyag(parseBigDecimal(row3.get(2)));   // 本年累计
        bean.setMainBusiIncmCply(parseBigDecimal(row3.get(3)));   // 去年同期

        // 第4行 主营业务成本
        List<Object> row4 = excelRows.get(4);
        bean.setMainBusiCostActm(parseBigDecimal(row4.get(1)));
        bean.setMainBusiCostTyag(parseBigDecimal(row4.get(2)));
        bean.setMainBusiCostCply(parseBigDecimal(row4.get(3)));

        // 第5行 主营业务税金及附加
        List<Object> row5 = excelRows.get(5);
        bean.setMainBusiTaxAddActm(parseBigDecimal(row5.get(1)));
        bean.setMainBusiTaxAddTyag(parseBigDecimal(row5.get(2)));
        bean.setMainBusiTaxAddCply(parseBigDecimal(row5.get(3)));

        // 第6行 主营业务利润（核心补充点）
        List<Object> row6 = excelRows.get(6);
        bean.setMainBusiProfActm(parseBigDecimal(row6.get(1)));  // 本季实际 = 收入(3行) - 成本(4行) - 税金(5行)
        bean.setMainBusiProfTyag(parseBigDecimal(row6.get(2)));   // 本年累计
        bean.setMainBusiProfCply(parseBigDecimal(row6.get(3)));   // 去年同期

        // ================= 其他业务模块 =================
        // 第7行 其他业务利润
        List<Object> row7 = excelRows.get(7);
        bean.setOthBusiProfActm(parseBigDecimal(row7.get(1)));
        bean.setOthBusiProfTyag(parseBigDecimal(row7.get(2)));
        bean.setOthBusiProfCply(parseBigDecimal(row7.get(3)));

        // ================= 费用模块 =================
        // 第8行 营业费用
        List<Object> row8 = excelRows.get(8);
        bean.setBusiFeeActm(parseBigDecimal(row8.get(1)));
        bean.setBusiFeeTyag(parseBigDecimal(row8.get(2)));
        bean.setBusiFeeCply(parseBigDecimal(row8.get(3)));

        // 第9行 管理费用
        List<Object> row9 = excelRows.get(9);
        bean.setMagFeeActm(parseBigDecimal(row9.get(1)));
        bean.setMagFeeTyag(parseBigDecimal(row9.get(2)));
        bean.setMagFeeCply(parseBigDecimal(row9.get(3)));

        // 第10行 财务费用
        List<Object> row10 = excelRows.get(10);
        bean.setFinFeeActm(parseBigDecimal(row10.get(1)));
        bean.setFinFeeTyag(parseBigDecimal(row10.get(2)));
        bean.setFinFeeCply(parseBigDecimal(row10.get(3)));

        // ================= 减值损失模块 =================
        // 第11行 资产减值损失
        List<Object> row11 = excelRows.get(11);
        bean.setIpoaLossActm(parseBigDecimal(row11.get(1)));
        bean.setIpoaLossTyag(parseBigDecimal(row11.get(2)));
        bean.setIpoaLossCply(parseBigDecimal(row11.get(3)));

        // 第12行 信用减值损失
        List<Object> row12 = excelRows.get(12);
        bean.setCredDecrLossActm(parseBigDecimal(row12.get(1)));
        bean.setCredDecrLossTyag(parseBigDecimal(row12.get(2)));
        bean.setCredDecrLossCply(parseBigDecimal(row12.get(3)));

        // ================= 利润模块 =================
        // 第13行 营业利润
        List<Object> row13 = excelRows.get(13);
        bean.setBusiProfActm(parseBigDecimal(row13.get(1)));
        bean.setBusiProfTyag(parseBigDecimal(row13.get(2)));
        bean.setBusiProfCply(parseBigDecimal(row13.get(3)));

        // 第14行 投资收益
        List<Object> row14 = excelRows.get(14);
        bean.setIvsmPayfActm(parseBigDecimal(row14.get(1)));
        bean.setIvsmPayfTyag(parseBigDecimal(row14.get(2)));
        bean.setIvsmPayfCply(parseBigDecimal(row14.get(3)));

        // 第15行 营业外收入
        List<Object> row15 = excelRows.get(15);
        bean.setNoprIncmActm(parseBigDecimal(row15.get(1)));
        bean.setNoprIncmTyag(parseBigDecimal(row15.get(2)));
        bean.setNoprIncmCply(parseBigDecimal(row15.get(3)));

        // 第16行 营业外支出
        List<Object> row16 = excelRows.get(16);
        bean.setNoprPayActm(parseBigDecimal(row16.get(1)));
        bean.setNoprPayTyag(parseBigDecimal(row16.get(2)));
        bean.setNoprPayCply(parseBigDecimal(row16.get(3)));

        // 第17行 利润总额
        List<Object> row17 = excelRows.get(17);
        bean.setProfGamtActm(parseBigDecimal(row17.get(1)));
        bean.setProfGamtTyag(parseBigDecimal(row17.get(2)));
        bean.setProfGamtCply(parseBigDecimal(row17.get(3)));

        // 第18行 所得税费用
        List<Object> row18 = excelRows.get(18);
        bean.setInctFeeActm(parseBigDecimal(row18.get(1)));
        bean.setInctFeeTyag(parseBigDecimal(row18.get(2)));
        bean.setInctFeeCply(parseBigDecimal(row18.get(3)));

        // 第19行 净利润
        List<Object> row19 = excelRows.get(19);
        bean.setNetProfActm(parseBigDecimal(row19.get(1)));
        bean.setNetProfTyag(parseBigDecimal(row19.get(2)));
        bean.setNetProfCply(parseBigDecimal(row19.get(3)));
        return bean;
    }

    private BigDecimal bigDecimalAddSum(BigDecimal... addends) {
        // 计算加数的和（每个加数为 null 时视为 0）
        BigDecimal sum = BigDecimal.ZERO;
        if (addends != null) {
            for (BigDecimal addend : addends) {
                BigDecimal validAddend = (addend != null) ? addend : BigDecimal.ZERO;
                sum = sum.add(validAddend);
            }
        }

        // 比较数值是否相等（compareTo 返回 0 表示数值相等，不考虑精度）
        return sum;
    }

}
