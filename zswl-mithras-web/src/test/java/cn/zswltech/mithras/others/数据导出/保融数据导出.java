package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.fund.model.FundFinancialSystemCallRecord;
import cn.zswltech.mithras.fund.application.financial.FundFinancialSystemCallRecordService;
import cn.zswltech.mithras.fund.application.financial.dto.FinancialSystemSubmitQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import groovy.util.logging.Slf4j;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * @author luyi
 */
@Slf4j
public class 保融数据导出 extends ApplicationTest {

    @Data
    public static class ExcelModel {
        private LocalDate incomeDate;
        private Integer incomePhase;
        private BigDecimal beginOfBalance;
        private BigDecimal rent;
        private BigDecimal income;
        private BigDecimal incomeWithoutTax;
        private BigDecimal tax;
        private BigDecimal endOfBalance;
        private String discount;
    }

    public static void main(String[] args) throws Exception {
        String s = "(1798, 3844, '20260115155838', '%s', %s, %s, %s, %s, %s, %s, %s, '0.000110635448946369')";
        List<String> sqlList = new LinkedList<>();
        List<ExcelModel> rows = ExcelUtil.getReader("/Users/dingqi/Downloads/浙商租【2025】租字第(C-0003)号-收入分摊表-工程装备 V2.xlsx").setSheet("质保金投放期初差异调整至2026.1.1")
                .addHeaderAlias("日期", "incomeDate")
                .addHeaderAlias("期项", "incomePhase")
                .addHeaderAlias("长期应收款期初余额（元）", "beginOfBalance")
                .addHeaderAlias("租金（元）", "rent")
                .addHeaderAlias("含税收入（元）", "income")
                .addHeaderAlias("不含税收入（元）", "incomeWithoutTax")
                .addHeaderAlias("税额（元）", "tax")
                .addHeaderAlias("长期应收款期末余额（元）", "endOfBalance")
                .read(0, 229, 1462, ExcelModel.class);
        for (ExcelModel row : rows) {
            String incomeDate = LocalDateTimeUtil.format(row.getIncomeDate(), DatePattern.NORM_DATE_PATTERN);
            Long beginOfBalance = row.getBeginOfBalance().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            Long rent = 0L;
            if (Objects.nonNull(row.getRent())) {
                rent = row.getRent().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            }
            Long income = row.getIncome().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            Long incomeWithoutTax = row.getIncomeWithoutTax().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            Long tax = row.getTax().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            Long endOfBalance = row.getEndOfBalance().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
            sqlList.add(String.format(s, incomeDate, row.getIncomePhase(), beginOfBalance, rent, income, incomeWithoutTax, tax, endOfBalance, row.getDiscount()));
        }
        String result = StrUtil.join(",\n", sqlList);
        OutputStream os = FileUtil.getOutputStream("/Users/dingqi/收入分摊数据订正.sql");
        os.write(result.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    @Resource
    private FundFinancialSystemCallRecordService financialSystemCallRecordService;

    @Test
    @SneakyThrows
    //导出，一类数据只可导出一批
    public void export() {
        List<FundFinancialSystemCallRecord> list = financialSystemCallRecordService.list(Wrappers.<FundFinancialSystemCallRecord>lambdaQuery()
                .in(FundFinancialSystemCallRecord::getBatchNumber, "REPAYMENT@20250424172852", "RECEIPT@20250424172852", "CONTRACT@20250424172852"));
        Map<String, List<?>> dataMap = new HashMap<>();
        list.forEach(e -> {
            String queryList = JSONUtil.parseObj(e.getQuery())
                    .getJSONObject("body").getStr("list");
            List<?> data;
            if ("贷款合同推送".equals(e.getType())) {
                data = JSONUtil.toList(queryList, FinancialSystemSubmitQuery.ContractBody.class);
            } else if ("贷款借据推送".equals(e.getType())) {
                data = JSONUtil.toList(queryList, FinancialSystemSubmitQuery.ReceiptBody.class);
            } else {
                data = JSONUtil.toList(queryList, FinancialSystemSubmitQuery.RepayBody.class);
            }
            dataMap.put(e.getType(), data);
        });

        // 目标文件路径
        String filePath = "/Users/vico/Downloads/推送保融数据0424-01.xlsx";

        // 创建 OutputStream（推荐使用 try-with-resources 自动关闭流）
        try (OutputStream out = createFileOutputStream(filePath)) {
            // 这里可以调用你的 Excel 导出方法（例如之前实现的 exportExcel）
            this.exportExcel(dataMap, out);
            System.out.println("文件输出流创建成功，路径: " + filePath);
        } catch (IOException e) {
            System.err.println("文件创建失败: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 导出 Excel 到输出流（支持多 Sheet 页）
     *
     * @param dataMap Sheet 数据集合，Key 为 Sheet 名称，Value 为数据列表（支持 Bean、Map、List 等格式）
     * @param out     输出流（如 HttpServletResponse 的输出流）
     */
    @SneakyThrows
    public void exportExcel(Map<String, List<?>> dataMap, OutputStream out) {
        if (dataMap == null || dataMap.isEmpty()) {
            throw new IllegalArgumentException("导出数据不能为空");
        }

        // 初始化 ExcelWriter，默认创建第一个 Sheet（后续会重命名）
        ExcelWriter writer = ExcelUtil.getWriter(true); // 使用 xlsx 格式
        boolean isFirstSheet = true;

        try {
            for (Map.Entry<String, List<?>> entry : dataMap.entrySet()) {
                String sheetName = entry.getKey();
                List<?> data = entry.getValue();

                // 处理空数据
               /* if (data == null || data.isEmpty()) {
                    continue; // 跳过空数据 Sheet
                }*/

                // 设置当前 Sheet 页
                if (isFirstSheet) {
                    // 重命名第一个 Sheet 页
                    writer.renameSheet(sheetName);
                    isFirstSheet = false;
                } else {
                    // 创建新 Sheet 页并重置配置（避免列标题污染）
                    writer.setSheet(sheetName);
                    writer.reset(); // 重置样式和标题别名
                }

                // 自动写入数据（根据数据类型处理标题）
                writer.write(data, true);

                // 自动调整列宽（可选）
                writer.autoSizeColumnAll();
            }

            // 输出到流
            writer.flush(out, true);
        } finally {
            writer.close();
        }
    }

    /**
     * 创建文件输出流（自动创建目录）
     */
    private static FileOutputStream createFileOutputStream(String filePath) throws IOException {
        File file = new File(filePath);

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
        }

        // 创建文件输出流（覆盖模式）
        return new FileOutputStream(file);
    }
}

