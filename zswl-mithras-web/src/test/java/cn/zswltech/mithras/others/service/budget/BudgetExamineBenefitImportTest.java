package cn.zswltech.mithras.others.service.budget;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.budget.domain.enums.BudgetExamineBenefitEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetExamineBudgetExecuteEnum;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamine;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamineBenefit;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetExamineBudgetExecute;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.budget.*;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.third.jk.JinKongMonthlyReportService;
import com.aspose.slides.Collections.ArrayList;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @ClassName FinanceFlowAutoWriteOffTest
 * @Description 效益考核表
 * @Author jackerhe
 * @Date 2024/7/26 18:04
 * @Version 1.0
 **/
public class BudgetExamineBenefitImportTest extends ApplicationTest {
    @Resource
    private BudgetExamineBenefitService budgetExamineBenefitService;
    @Resource
    private OrgDOMapper orgDOMapper;

    @Test
    public void initHistoryFtpIndustryCategory() throws Exception {
        List<Map<String, Object>> dataList = ExcelUtil.getReader("/Users/dingqi/Downloads/预算管理初始化/FTP行业分类.xlsx").read(0, 1, 320);
        Writer writer = FileUtil.getWriter("/Users/dingqi/ftp_industry_init.sql", StandardCharsets.UTF_8, true);
        for (Map<String, Object> columnMap : dataList) {
            String clientName = columnMap.get("客户名称").toString();
            String ftpIndustryCategoryDisplay = columnMap.get("FTP行业分类").toString();
            FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByDisplay(ftpIndustryCategoryDisplay);
            if (Objects.isNull(ftpIndustryCategoryEnum)) {
                throw new MithrasException("未知的FTP行业分类[" + clientName + "]");
            }
            Client client = SpringUtil.getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, clientName));
            if (Objects.isNull(client)) {
                System.out.println("未知的客户[" + clientName + "]");
                continue;
            }
            writer.write(String.format("update proj_review_base_info set ftp_industry_category = '%s', update_time = update_time where ftp_industry_category is null and client_id = %s;\n", ftpIndustryCategoryEnum.name(), client.getId()));
            writer.write(String.format("update proj_review_base_info_lib set ftp_industry_category = '%s', update_time = update_time where ftp_industry_category is null and client_id = %s;\n", ftpIndustryCategoryEnum.name(), client.getId()));
            writer.write(String.format("update proj_pricing_base_info set ftp_industry_category = '%s', update_time = update_time where ftp_industry_category is null and client_id = %s;\n", ftpIndustryCategoryEnum.name(), client.getId()));
            writer.write(String.format("update proj_pricing_base_info_lib set ftp_industry_category = '%s', update_time = update_time where ftp_industry_category is null and client_id = %s;\n", ftpIndustryCategoryEnum.name(), client.getId()));
        }
        writer.flush();
    }

    @Test
    public void syncBcmAssistTest() {
        SpringUtil.getBean(JinKongMonthlyReportService.class).syncBcmFflexfiledAssistMfByDept(2025, 4);
    }

    @Test
    public void importBudgetExamineBudgetExecuteFile() {
        String filePath = "/Users/dingqi/Documents/各部门考核数据底稿2508.xlsx";
        Map<String, Long> sheetName2BudgetExamineIdMap = new HashMap<>();
        sheetName2BudgetExamineIdMap.put("2508", 6L);
        List<OrgDO> allDept = SpringUtil.getBean(SysUserService.class).listBizDept();
        ExcelReader excelReader = ExcelUtil.getReader(filePath);
        List<BudgetExamineBudgetExecute> initList = new LinkedList<>();
        for (Map.Entry<String, Long> entry : sheetName2BudgetExamineIdMap.entrySet()) {
            // 清空原数据
            SpringUtil.getBean(BudgetExamineBudgetExecuteService.class).remove(Wrappers.<BudgetExamineBudgetExecute>lambdaQuery().eq(BudgetExamineBudgetExecute::getBudgetExamineId, entry.getValue()));
            BudgetExamine budgetExamine = SpringUtil.getBean(BudgetExamineService.class).getById(entry.getValue());
            if (Objects.isNull(budgetExamine)) {
                continue;
            }
            List<List<Object>> dataList = excelReader.setSheet(entry.getKey()).read();
            if (CollectionUtil.isEmpty(dataList)) {
                continue;
            }
            OrgDO currentOrg = null;
            Map<String, BudgetExamineBudgetExecute> currentOrgFieldMap = new HashMap<>();
            for (List<Object> row : dataList) {
                String excelText = Optional.ofNullable(row.get(0)).map(Object::toString).orElse("UNKNOWN");
                // 遇到备注则保存处理后的数据并清空临时变量
                if (StrUtil.equals(excelText, "备注")) {
                    initList.addAll(currentOrgFieldMap.values());
                    currentOrg = null;
                    currentOrgFieldMap.clear();
                    continue;
                }
                Optional<OrgDO> optional = allDept.stream().filter(e -> e.getName().equals(excelText)).findFirst();
                if (optional.isPresent()) {
                    currentOrg = optional.get();
                    // 初始化本部门的数据
                    for (BudgetExamineBudgetExecuteEnum item : BudgetExamineBudgetExecuteEnum.values()) {
                        BudgetExamineBudgetExecute init = new BudgetExamineBudgetExecute();
                        init.setBudgetExamineId(budgetExamine.getId());
                        init.setFieldName(item.name());
                        init.setBelongDeptId(currentOrg.getId());
                        currentOrgFieldMap.put(item.name(), init);
                    }
                }
                if (Objects.isNull(currentOrg)) {
                    continue;
                }
                BudgetExamineBudgetExecuteEnum fieldItem = BudgetExamineBudgetExecuteEnum.findByExcelName(excelText.trim());
                if (Objects.isNull(fieldItem)) {
                    continue;
                }
                // 从初始化数据中取出对应filed的对象
                BudgetExamineBudgetExecute execute = currentOrgFieldMap.get(fieldItem.name());
                if (Objects.isNull(execute)) {
                    continue;
                }
                long factor = 100000000L;
                if (fieldItem == BudgetExamineBudgetExecuteEnum.EXPENSE_LEVEL_RATE) {
                    factor = 1000000L;
                }
                if (fieldItem == BudgetExamineBudgetExecuteEnum.CUMULATIVE_PROJECT_APPROVAL_COUNT) {
                    factor = 1;
                }
                if (fieldItem == BudgetExamineBudgetExecuteEnum.EXPENSE_EFFICIENCY) {
                    factor = 10000;
                }
                // 本月数
                execute.setCurrentMonth(this.convert(row.get(1), factor));
                // 本年累计
                execute.setTotalYear(this.convert(row.get(2), factor));
                // 上年同期
                execute.setLastYearPeriod(this.convert(row.get(3), factor));
                // 同比
                execute.setOnYear(this.convert(row.get(4), 1000000));
                // 全年预算目标
                execute.setAnnualBudgetTarget(this.convert(row.get(5), factor));
                // 进度预算目标
                execute.setProgressBudgetTarget(this.convert(row.get(6), factor));
                // 进度预算完成率
                execute.setProgressBudgetCompletionRate(this.convert(row.get(7), 10000));
                // 全年预算完成率
                execute.setAnnualBudgetCompletionRate(this.convert(row.get(8), 10000));
            }
        }
        if (CollectionUtil.isNotEmpty(initList)) {
            SpringUtil.getBean(BudgetExamineBudgetExecuteService.class).saveBatch(initList);
        }
    }

    private Long convert(Object excelText, long factor) {
        if (Objects.isNull(excelText)) {
            return null;
        }
        if (!NumberUtil.isNumber(excelText.toString())) {
            return null;
        }
        BigDecimal b = new BigDecimal(excelText.toString().trim().replace(",", "")).multiply(BigDecimal.valueOf(factor));
        if (factor == 1) {
            return b.longValue();
        } else {
            return Util.mithrasLongDecimalTwo(b.longValue());
        }
    }

    @Test
    public void importBudgetExamineBenefitFile() {
        writeOffNotice(7L, 2025, 7, "考核主表2507");
        budgetExamineBenefitService.initCompanyData(7L);
        budgetExamineBenefitService.initMonthData(7L);
        writeOffNotice(6L, 2025, 8, "考核主表2508");
        budgetExamineBenefitService.initCompanyData(6L);
        budgetExamineBenefitService.initMonthData(6L);
    }

    public void writeOffNotice(Long budgetExamineId, Integer budgetExamineYear, Integer budgetExamineMonth, String sheetName) {
        // 清空原数据
        SpringUtil.getBean(BudgetExamineBenefitService.class).remove(Wrappers.<BudgetExamineBenefit>lambdaQuery().eq(BudgetExamineBenefit::getBudgetExamineId, budgetExamineId));
        Map<String, Long> deptName2Id = orgDOMapper.selectAll().stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId, (a, b) -> a));
        deptName2Id.put("高端装备部", deptName2Id.get("高端装备业务部"));
        Map<String, Map<String, Map<String, String>>> deptName2Column = parseByDepartment("/Users/dingqi/Documents/考核表2509.xlsx", sheetName);
        if (ObjectUtil.isNotEmpty(deptName2Column)) {
            List<BudgetExamineBenefit> budgetExamineBenefits = new ArrayList();
            deptName2Column.forEach((deptName, columnMap) -> {
                //每个部门下一个
                Long deptId = deptName2Id.get(deptName);
                if (ObjectUtil.isEmpty(deptId)) {
                    return;
                }
                columnMap.forEach((k, v) -> {
                    BudgetExamineBenefitEnum byName = BudgetExamineBenefitEnum.findByExcelName(k);
                    if (ObjectUtil.isEmpty(byName)) {
                        return;
                    }
                    BudgetExamineBenefit examineBenefit = new BudgetExamineBenefit();
                    examineBenefit.setBudgetExamineId(budgetExamineId);
                    examineBenefit.setBudgetExamineYear(budgetExamineYear);
                    examineBenefit.setBudgetExamineMonth(budgetExamineMonth);
                    examineBenefit.setBelongDeptId(deptId);
                    examineBenefit.setFieldName(byName.name());
                    if (StrUtil.equalsAny(byName.name(), BudgetExamineBenefitEnum.BUSINESS_INVESTMENT_SCALE.name(), BudgetExamineBenefitEnum.END_OF_MONTH_ASSET_TOTAL.name())) {
                        examineBenefit.setFieldValue(getLongValue(v.get("本年累计")) * 10000);
                    } else {
                        examineBenefit.setFieldValue(getLongValue(v.get("本年累计")));
                    }
                    examineBenefit.setFieldLevel(3);
                    budgetExamineBenefits.add(examineBenefit);
//                    if ("期初余额".equals(k)){
//                        BudgetExamineBenefit yearBenefit = new BudgetExamineBenefit();
//                        yearBenefit.setBudgetExamineId(budgetExamineId);
//                        yearBenefit.setBudgetExamineYear(budgetExamineYear);
//                        yearBenefit.setBudgetExamineMonth(budgetExamineMonth);
//                        yearBenefit.setBelongDeptId(deptId);
//                        yearBenefit.setFieldName(byName.name());
//                        yearBenefit.setFieldValue(getLongValue(v.get("本年累计")));
//                        budgetExamineBenefits.add(yearBenefit);
//                    }
                });
                // Excel没有的款项科目手动补全
                BudgetExamineBenefit fundIncomeBenefit = new BudgetExamineBenefit();
                fundIncomeBenefit.setBudgetExamineId(budgetExamineId);
                fundIncomeBenefit.setBudgetExamineYear(budgetExamineYear);
                fundIncomeBenefit.setBudgetExamineMonth(budgetExamineMonth);
                fundIncomeBenefit.setBelongDeptId(deptId);
                fundIncomeBenefit.setFieldName(BudgetExamineBenefitEnum.FUND_INCOME.name());
                fundIncomeBenefit.setFieldLevel(3);
                budgetExamineBenefits.add(fundIncomeBenefit);
                BudgetExamineBenefit otherIncomeBenefit = new BudgetExamineBenefit();
                otherIncomeBenefit.setBudgetExamineId(budgetExamineId);
                otherIncomeBenefit.setBudgetExamineYear(budgetExamineYear);
                otherIncomeBenefit.setBudgetExamineMonth(budgetExamineMonth);
                otherIncomeBenefit.setBelongDeptId(deptId);
                otherIncomeBenefit.setFieldName(BudgetExamineBenefitEnum.OTHER_INCOME.name());
                otherIncomeBenefit.setFieldLevel(3);
                budgetExamineBenefits.add(otherIncomeBenefit);
            });
            budgetExamineBenefitService.saveBatch(budgetExamineBenefits);
        }
    }

    private Long getLongValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return 0L;
        }
        return new BigDecimal(value).multiply(new BigDecimal("10000")).longValue();
    }

    /**
     * 解析Excel并按部门分组
     * @param filePath Excel文件路径
     * @param sheetName 要解析的Sheet名称
     * @return Map<部门, Map<数据名称, Map<表头, 值>>>
     */
    public Map<String, Map<String, Map<String, String>>> parseByDepartment(String filePath, String sheetName) {
        // 最终结果：部门 -> 数据名称 -> 表头 -> 值
        Map<String, Map<String, Map<String, String>>> result = new LinkedHashMap<>();

        // 1. 创建Excel读取器
        ExcelReader reader = ExcelUtil.getReader(new File(filePath), sheetName);

        // 2. 读取关键行（行索引从0开始）
        List<Object> departmentRow = reader.readRow(2);  // 第三行（部门行）
        List<Object> headerRow = reader.readRow(4);       // 第四行（表头行）
        List<List<Object>> dataRows = reader.read(5, Integer.MAX_VALUE); // 数据行（从第五行开始）

        // 3. 解析部门及其对应的列范围（处理跨列合并）
        Map<Integer, String> columnToDepartment = parseDepartmentColumns(departmentRow);

        // 4. 解析表头（列索引 -> 表头名称）
        Map<Integer, String> columnToHeader = parseHeaderColumns(headerRow);

        // 5. 遍历数据行，填充结果
        for (List<Object> dataRow : dataRows) {
            if (CollUtil.isEmpty(dataRow)) continue;

            // 提取第一列的数据名称（可能合并单元格，取第一个非空值）
            String dataName = getDataName(dataRow.get(0));
            if (StrUtil.isBlank(dataName)) continue;

            // 遍历所有列（从第二列开始，列索引>=1）
            for (int col = 1; col < dataRow.size(); col++) {
                String department = columnToDepartment.get(col); // 当前列对应的部门
                String header = columnToHeader.get(col);        // 当前列对应的表头
                String value = getCellValue(dataRow.get(col));   // 当前列的值

                if (StrUtil.isBlank(department) || StrUtil.isBlank(header)) continue;

                // 初始化部门数据结构
                result.putIfAbsent(department, new LinkedHashMap<>());
                Map<String, Map<String, String>> dataNameMap = result.get(department);
                dataNameMap.putIfAbsent(dataName, new LinkedHashMap<>());
                dataNameMap.get(dataName).put(header, value);
            }
        }
        reader.close();
        return result;
    }

    /**
     * 解析部门列（处理跨列合并，返回列索引 -> 部门名称）
     */
    private static Map<Integer, String> parseDepartmentColumns(List<Object> departmentRow) {
        Map<Integer, String> columnToDepartment = new LinkedHashMap<>();
        String currentDepartment = null;

        for (int col = 0; col < departmentRow.size(); col++) {
            Object cellValue = departmentRow.get(col);
            if (cellValue != null && StrUtil.isNotBlank(cellValue.toString())) {
                currentDepartment = cellValue.toString().trim();
            }
            if (currentDepartment != null) {
                columnToDepartment.put(col, currentDepartment);
            }
        }
        return columnToDepartment;
    }

    /**
     * 解析表头列（列索引 -> 表头名称）
     */
    private static Map<Integer, String> parseHeaderColumns(List<Object> headerRow) {
        return IntStream.range(0, headerRow.size())
                .boxed()
                .collect(Collectors.toMap(
                        col -> col,
                        col -> headerRow.get(col) == null ? "" : headerRow.get(col).toString().trim(),
                        (oldVal, newVal) -> oldVal, // 冲突时保留旧值（一般不会冲突）
                        LinkedHashMap::new
                ));
    }

    /**
     * 提取数据名称（处理合并单元格的空值）
     */
    private static String getDataName(Object cellValue) {
        if (cellValue == null) return "";
        String value = cellValue.toString().trim();
        return StrUtil.isBlank(value) ? "" : value;
    }

    /**
     * 提取单元格值（处理空值和类型）
     */
    private static String getCellValue(Object cellValue) {
        if (cellValue == null) return "";
        return cellValue.toString().trim();
    }


}
