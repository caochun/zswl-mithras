package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemRemoveREQ;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpSubjectItem;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.EvalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.*;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.service.constant.ResultMsg.*;
import static cn.zswltech.mithras.customer.domain.enums.GovernmentSubjectItemType.GOV_CAPITAL_BALANCE;
import static cn.zswltech.mithras.customer.domain.enums.GovernmentSubjectItemType.INCOME_EXPEND;
import static cn.zswltech.mithras.customer.domain.enums.SubjectItemType.*;
import static cn.zswltech.mithras.service.others.Util.mithrasLong2BigDecimal;
import static cn.zswltech.mithras.service.others.Util.toMithrasUnit;

/**
 * @author junke
 */
@Slf4j
@Service
public class CorpSubjectItemService extends ServiceImpl<CorpSubjectItemMapper, CorpSubjectItem> {

    @Value("#{'${mithras.subjectItem.necessaries.enterprise:}'.split(',')}")
    private List<String> enterpriseNecessaries;
    @Value("#{'${mithras.subjectItem.necessaries.government:}'.split(',')}")
    private List<String> governmentNecessaries;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Autowired
    private OssClient ossClient;

    private static boolean isYearReport(SubjectSheetData data) {
        return Objects.equals(SubjectQuarterType.TWELFTH.value, data.getQuarter());
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void importSubjectItemByExcel(MultipartFile excelFile, Long clientId) {
        String orgType = "1";
        CorpCommerceInfo commerceInfo = commerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId));
        if (isNotNull(commerceInfo)) {
            orgType = commerceInfo.getOrgType();
        }
        //企业
        if ("1".equals(orgType)) {
            checkAllSheetExists(ExcelUtil.getReader(excelFile.getInputStream()).getSheets(), CAPITAL_BALANCE.sheetName, PROFIT.sheetName, CASH_FLOW.sheetName);
            //
            ExcelReader capitalBalanceReader = ExcelUtil.getReader(excelFile.getInputStream(), CAPITAL_BALANCE.sheetName);
            ExcelReader profitReader = ExcelUtil.getReader(excelFile.getInputStream(), PROFIT.sheetName);
            ExcelReader cashFlowReader = ExcelUtil.getReader(excelFile.getInputStream(), CASH_FLOW.sheetName);
            List<SubjectSheetData> capitalBalanceSheetData = read(capitalBalanceReader);
            List<SubjectSheetData> profitBalanceSheetData = read(profitReader);
            List<SubjectSheetData> cashFlowSheetData = read(cashFlowReader);
            handleOrgSubjectImport(clientId, capitalBalanceSheetData, profitBalanceSheetData, cashFlowSheetData);
        } else {
            checkAllSheetExists(ExcelUtil.getReader(excelFile.getInputStream()).getSheets(), GovernmentSubjectItemMenu.GOV_CAPITAL_BALANCE.sheetName, GovernmentSubjectItemMenu.PROFIT.sheetName, GovernmentSubjectItemMenu.CASH_FLOW.sheetName);
            ExcelReader govCapitalBalanceReader = ExcelUtil.getReader(excelFile.getInputStream(), GovernmentSubjectItemMenu.GOV_CAPITAL_BALANCE.sheetName);
            ExcelReader profitReader = ExcelUtil.getReader(excelFile.getInputStream(), GovernmentSubjectItemMenu.PROFIT.sheetName);
            ExcelReader cashFlowReader = ExcelUtil.getReader(excelFile.getInputStream(), GovernmentSubjectItemMenu.CASH_FLOW.sheetName);
            List<SubjectSheetData> govCapitalBalanceSheetData = read(govCapitalBalanceReader);
            List<SubjectSheetData> profitSheetData = read(profitReader);
            List<SubjectSheetData> cashFlowSheetData = read(cashFlowReader);

            handleSubjectImport(clientId, govCapitalBalanceSheetData, profitSheetData, cashFlowSheetData);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleSubjectImport(Long clientId, List<SubjectSheetData> govCapitalBalanceSheetData, List<SubjectSheetData> profitSheetData, List<SubjectSheetData> cashFlowSheetData) {
        List<SubjectSheetData> allSheetData = new ArrayList<>();
        allSheetData.addAll(govCapitalBalanceSheetData);
        allSheetData.addAll(profitSheetData);
        allSheetData.addAll(cashFlowSheetData);

        //check necessary item，判断key是否存在
        Map<String, BigDecimal> checkMap = new HashMap<>();
        for (SubjectSheetData sd : allSheetData) {
            sd.getItemMap().forEach((k, v) -> checkMap.put(k, v.getValue()));
        }
        for (String necessary : governmentNecessaries) {
            if (!checkMap.containsKey(necessary) || null == checkMap.get(necessary)) {
                throw new MithrasException("缺少必要科目：" + necessary);
            }
        }
        //逐一判断值是否为空
        Set<String> set = new HashSet<>(governmentNecessaries);
        for (SubjectSheetData sd : allSheetData) {
            for (String key : set) {
                Item item = sd.getItemMap().get(key);
                if (null != item && item.getValue() == null) {
                    throw new MithrasException(item.getName() + "-" + key + "的值不能为空");
                }
            }
        }

        addItems(clientId, GovernmentSubjectItemMenu.GOV_CAPITAL_BALANCE.name(), govCapitalBalanceSheetData);
        addItems(clientId, GovernmentSubjectItemMenu.PROFIT.name(), profitSheetData);
        addItems(clientId, GovernmentSubjectItemMenu.CASH_FLOW.name(), cashFlowSheetData);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleOrgSubjectImport(Long clientId, List<SubjectSheetData> capitalBalanceSheetData, List<SubjectSheetData> profitBalanceSheetData, List<SubjectSheetData> cashFlowSheetData) {
        List<SubjectSheetData> allSheetData = new ArrayList<>();
        allSheetData.addAll(capitalBalanceSheetData);
        allSheetData.addAll(profitBalanceSheetData);
        allSheetData.addAll(cashFlowSheetData);
        //check necessary item，判断key是否存在
        Map<String, BigDecimal> checkMap = new HashMap<>();
        for (SubjectSheetData sd : allSheetData) {
            sd.getItemMap().forEach((k, v) -> checkMap.put(k, v.getValue()));
        }
        for (String necessary : enterpriseNecessaries) {
            if (!checkMap.containsKey(necessary) || null == checkMap.get(necessary)) {
                throw new MithrasException("缺少必要科目：" + necessary);
            }
        }
        //逐一判断值是否为空
        Set<String> set = new HashSet<>(enterpriseNecessaries);
        for (SubjectSheetData sd : allSheetData) {
            for (String key : set) {
                Item item = sd.getItemMap().get(key);
                if (null != item && item.getValue() == null) {
                    throw new MithrasException(item.getName() + "-" + key + "的值不能为空");
                }
            }
        }

        //end check necessary item
        Map<String, Map<String, BigDecimal>> allData = new HashMap<>();
        allSheetData.forEach(e -> {
            //如果是企业，且是年度的，才需要计算业务指标
            if (isYearReport(e)) {
                //同一个维度的放在一起
                String key = join("_", e.getReportType(), e.getYear(), e.getQuarter());
                allData.putIfAbsent(key, new HashMap<>());
                e.getItemMap().forEach((code, v) -> {
                    allData.get(key).put(code, v.getValue());
                });
            }
        });
        if (!allData.isEmpty()) {
            List<SubjectSheetData> calculateData = calculateData(clientId, allData);
            if (!calculateData.isEmpty()) {
                addItems(clientId, BIZ_INDEX.name(), calculateData);
            }
        }
        //
        addItems(clientId, CAPITAL_BALANCE.name(), capitalBalanceSheetData);
        addItems(clientId, PROFIT.name(), profitBalanceSheetData);
        addItems(clientId, CASH_FLOW.name(), cashFlowSheetData);
    }

    private List<SubjectSheetData> calculateData(Long clientId, Map<String, Map<String, BigDecimal>> allData) {
        List<SubjectSheetData> result = new ArrayList<>();
        Map<String, String> expMap = Arrays.stream(CalcBizSubjectEnum.values()).collect(Collectors.toMap(Enum::name, e -> e.exp));
        Map<String, String> descMap = Arrays.stream(CalcBizSubjectEnum.values()).collect(Collectors.toMap(Enum::name, e -> e.desc));
        allData.forEach((k, v) -> {
            List<String> values = StrUtil.split(k, "_");
            String reportType = values.get(0);
            Integer year = Integer.valueOf(values.get(1));
            Integer quarter = Integer.valueOf(values.get(2));
            //如果依赖上年的，需要设置上年的值进入context. last_G9001
            String lastIndexPrefix = "last_";
            for (String expression : expMap.values()) {
                while (expression.contains(lastIndexPrefix)) {
                    int start = expression.indexOf(lastIndexPrefix);
                    String lastCode = expression.substring(start, start + lastIndexPrefix.length() + 5);
                    String subjectCode = expression.substring(start + lastIndexPrefix.length(), start + lastIndexPrefix.length() + 5);
                    //先看下当前excel里面是否有这个值
                    Integer lastYear = year - 1;
                    Map<String, BigDecimal> map2 = allData.get(join("_", reportType, lastYear, quarter));
                    if (isNotNull(map2)) {
                        v.put(lastCode, map2.get(subjectCode));
                    } else {
                        //如果没有，去数据库查询下有没有
                        CorpSubjectItem lastItem = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
                                .eq(CorpSubjectItem::getClientId, clientId)
                                .eq(CorpSubjectItem::getYear, lastYear)
                                .eq(CorpSubjectItem::getQuarter, quarter)
                                .eq(CorpSubjectItem::getSubjectCode, subjectCode)
                                .eq(CorpSubjectItem::getReportType, reportType)
                        );
                        if (isNotNull(lastItem)) {
                            v.put(lastCode, mithrasLong2BigDecimal(lastItem.getSubjectValue()));
                        }
                    }
                    if (isNull(v.get(lastCode))) {
                        log.warn("未找到上一期的值, year:{}, quarter:{}, reportType:{}, subjectCode:{} ", year, quarter, reportType, lastCode);
                    }
                    expression = expression.replace(lastCode, "");
                }
            }
            try {
                Map<String, BigDecimal> eval = EvalUtil.eval(expMap, v);
                if (!eval.isEmpty()) {
                    SubjectSheetData sd = new SubjectSheetData();
                    sd.setYear(year);
                    sd.setQuarter(quarter);
                    sd.setReportType(reportType);
                    sd.setSheetName(BIZ_INDEX.sheetName);
                    Map<String, Item> map = new HashMap<>();
                    eval.forEach((p, q) -> map.put(p, new Item(descMap.get(p), q)));
                    sd.setItemMap(map);
                    result.add(sd);
                }
            } catch (Exception e) {
                String errMsg = join(":", reportType, year, quarter, e.getMessage());
                log.warn("计算业务指标错误." + errMsg, e);
//                throw new MithrasException(errMsg);
            }
        });
        return result;

    }

    private void addItems(Long clientId, String itemType, List<SubjectSheetData> dataList) {
        //delete all, then insert all
        for (SubjectSheetData sd : dataList) {
            List<CorpSubjectItem> toInsertData = new ArrayList<>(sd.getItemMap().size());
            sd.getItemMap().forEach((code, v) -> {
                CorpSubjectItem item = new CorpSubjectItem();
                item.setQuarter(sd.quarter);
                item.setReportType(sd.getReportType());
                item.setYear(sd.getYear());
                item.setSubjectCode(code);
                item.setSubjectName(v.getName());
                item.setSubjectValue(toMithrasUnit(v.getValue()));
                item.setSubjectType(itemType);
                item.setClientId(clientId);
                toInsertData.add(item);
            });
            if (CollUtil.isEmpty(toInsertData)) {
                continue;
            }
            subjectItemMapper.delete(Wrappers.<CorpSubjectItem>lambdaQuery()
                    .eq(CorpSubjectItem::getClientId, clientId)
                    .eq(CorpSubjectItem::getSubjectType, itemType)
                    .eq(CorpSubjectItem::getYear, sd.getYear())
                    .eq(CorpSubjectItem::getQuarter, sd.getQuarter())
                    .eq(CorpSubjectItem::getReportType, sd.getReportType())
            );
            subjectItemMapper.batchInsert(toInsertData);
        }
    }

    public List<SubjectSheetData> read(ExcelReader reader) {
        List<SubjectSheetData> result = new ArrayList<>();
        int rowCount = reader.getRowCount();
        int colCount = reader.getColumnCount(1);
        if (rowCount > 1000 || colCount > 100) {
            throw new MithrasException(SUBJECT_EXCEL_TOO_MANY);
        }
        if (colCount < 3 || rowCount < 5) {
            log.warn("没有数据");
            return result;
        }
        //从第3列开始
        for (int i = 2; i < colCount; i++) {
            Integer year = null;
            Integer quarter = null;
            SubjectReportType reportType = null;
            Map<String, Item> itemMap = new LinkedHashMap<>();
            //从第二行开始，第一行为："单位：元"
            for (int j = 1; j < rowCount; j++) {
                Cell cell = reader.getCell(i, j);
                if (isNull(cell)) {
                    log.warn("数据为空，跳过此列，结束解析");
                    break;
                }
                String cellStr = cell.toString();
                if (j == 1) {
                    reportType = SubjectReportType.ofDisplay(cellStr);
                    if (isNull(reportType)) {
                        throw new MithrasException(SUBJECT_REPORT_TYPE_ERROR);
                    }
                    continue;
                }
                if (j == 2) {
                    SubjectQuarterType type = SubjectQuarterType.ofDisplay(cellStr);
                    if (isNull(type)) {
                        throw new MithrasException(SUBJECT_QUARTER_TYPE_ERROR);
                    }
                    quarter = type.value;
                    continue;
                }
                if (j == 3) {
                    try {
                        //替换中文为空
                        year = Integer.valueOf(cellStr.trim().substring(0, 4));
                    } catch (Exception e) {
                        log.error("", e);
                        throw new MithrasException(SUBJECT_YEAR_ERROR);
                    }
                    continue;
                }
                //判断是否行已结束
                if ((isNull(reader.getCell(0, j)) || isBlank(reader.getCell(0, j).getStringCellValue())) &&
                        ((isNull(reader.getCell(1, j)) || isBlank(reader.getCell(1, j).getStringCellValue())))
                ) {
                    log.info("第一列和第二列皆为空，判断行已结束");
                    break;
                }
                //
                String subjectName = reader.getCell(1, j).getStringCellValue().trim();
                String subjectCode;
                if (reader.getCell(0, j) == null || isBlank(subjectCode = reader.getCell(0, j).getStringCellValue().trim())) {
                    log.info("科目代码为空，跳过。{}", subjectName);
                    continue;
                }
                BigDecimal subjectValue = null;
                if (isNotBlank(cellStr)) {
                    try {
                        subjectValue = new BigDecimal(String.valueOf(cell.getNumericCellValue()));
                    } catch (IllegalStateException e) {
                        log.warn("获取值失败。subjectCode:[{}], subjectName:[{}], subjectValue:[{}]; e:{}", subjectCode, subjectName, cellStr, e.getMessage());
                    }
                }

                if (null != subjectValue) {
                    //平衡检测必须为0，暂时不打开
                    /*if (subjectCode.endsWith(SUBJECT_ITEM_BALANCE_CODE)) {
                        if (BigDecimal.ZERO.compareTo(subjectValue) != 0) {
                            throw new MithrasException(SUBJECT_ITEM_BALANCE_ERROR);
                        }
                        continue;
                    }*/
                    subjectValue = subjectValue.setScale(2, RoundingMode.HALF_UP);
                    if (itemMap.containsKey(subjectCode)) {
                        throw new MithrasException("存在重复的客户代码：" + subjectCode);
                    }
                }
                itemMap.put(subjectCode, new Item(subjectName, subjectValue));
            }
            SubjectSheetData data = new SubjectSheetData();
            data.setQuarter(quarter);
            data.setYear(year);
            data.setReportType(reportType.name());
            data.setItemMap(itemMap);
            result.add(data);
        }
        return result;
    }

    private void checkAllSheetExists(List<Sheet> sheets, String... necessarySheetNames) {
        Set<String> sheetSet = sheets.stream().map(Sheet::getSheetName).collect(Collectors.toSet());
        for (String necessarySheetName : necessarySheetNames) {
            if (!sheetSet.contains(necessarySheetName)) {
                throw new MithrasException(String.format("sheet:%s不存在", necessarySheetName));
            }
        }
    }

    public List<CorpSubjectItemListRSP> list(CorpSubjectItemListREQ req) {
        TreeMap<String, CorpSubjectItemListRSP> map = new TreeMap<>(Comparator.reverseOrder());
        LambdaQueryWrapper<CorpSubjectItem> wrapper = Wrappers.<CorpSubjectItem>lambdaQuery()
                .eq(CorpSubjectItem::getClientId, req.getClientId())
                .eq(CorpSubjectItem::getSubjectType, req.getSubjectType())
                .eq(isNotBlank(req.getReportType()), CorpSubjectItem::getReportType, req.getReportType())
                .ge(isNotNull(req.getYearFrom()), CorpSubjectItem::getYear, req.getYearFrom())
                .le(isNotNull(req.getYearTo()), CorpSubjectItem::getYear, req.getYearTo())
                .eq(isNotNull(req.getQuarter()), CorpSubjectItem::getQuarter, req.getQuarter());
        List<CorpSubjectItem> list = subjectItemMapper.selectList(wrapper);
        for (CorpSubjectItem item : list) {
            String comboKey = join("_", item.getYear(), item.getQuarter() < 10 ? "0" + item.getQuarter() : item.getQuarter(), item.getReportType(), item.getSubjectType());
            map.putIfAbsent(comboKey, new CorpSubjectItemListRSP(item.getYear(), item.getQuarter(), item.getReportType(), item.getSubjectType()));
            map.get(comboKey).getItemList().add(new CorpSubjectItemListRSP.SubjectItem(item.getId(), item.getSubjectCode(), item.getSubjectName(), item.getSubjectValue()));
        }
        if (equal(req.getLatest(), Boolean.TRUE)) {
            List<CorpSubjectItem> latestList = subjectItemMapper.selectList(
                    Wrappers.<CorpSubjectItem>lambdaQuery()
                            .eq(CorpSubjectItem::getClientId, req.getClientId())
                            .eq(CorpSubjectItem::getSubjectType, req.getSubjectType())
                            .eq(isNotBlank(req.getReportType()), CorpSubjectItem::getReportType, req.getReportType())
                       /*     .ge(isNotNull(req.getYearFrom()), CorpSubjectItem::getYear, req.getYearFrom())
                            .le(isNotNull(req.getYearTo()), CorpSubjectItem::getYear, req.getYearTo())*/
            );
            if (!latestList.isEmpty()) {
                TreeMap<String, CorpSubjectItemListRSP> map2 = new TreeMap<>();
                for (CorpSubjectItem item : latestList) {
                    String comboKey = join("_", item.getYear(), item.getQuarter() < 10 ? "0" + item.getQuarter() : item.getQuarter(), item.getReportType(), item.getSubjectType());
                    map2.putIfAbsent(comboKey, new CorpSubjectItemListRSP(item.getYear(), item.getQuarter(), item.getReportType(), item.getSubjectType()));
                    map2.get(comboKey).getItemList().add(new CorpSubjectItemListRSP.SubjectItem(item.getId(), item.getSubjectCode(), item.getSubjectName(), item.getSubjectValue()));
                }
                if (!map.containsKey(map2.lastKey())) {
                    map.put(map2.lastEntry().getKey(), map2.lastEntry().getValue());
                }
            }
        }
        //资产负债表的百分比%为各科目除以资产合计，利润表的百分比%为各科目除以营业收入，现金流量表无需计算百分比%。同比%只需支持年度报告，计算公式为各科目的（本期/上期-1）
        Collection<CorpSubjectItemListRSP> values = map.values();
        List<String> displayDimensions = req.getDisplayDimensions();
        if (displayDimensions.contains(SubjectItemDisplayDimension.PERCENT.name())) {
            for (CorpSubjectItemListRSP value : values) {
                String sheetName = value.getSubjectType();
                Long toDivide = null;
                if (equal(sheetName, CAPITAL_BALANCE.name())) {
                    toDivide = value.getItemList().stream().filter(e -> equal(e.getSubjectCode(), "G9130")).findFirst().orElse(new CorpSubjectItemListRSP.SubjectItem()).getSubjectValue();
                }
                if (equal(sheetName, PROFIT.name())) {
                    toDivide = value.getItemList().stream().filter(e -> equal(e.getSubjectCode(), "H9170")).findFirst().orElse(new CorpSubjectItemListRSP.SubjectItem()).getSubjectValue();
                }
                if (isNotNull(toDivide)) {
                    for (CorpSubjectItemListRSP.SubjectItem subjectItem : value.getItemList()) {
                        Long subjectValue = subjectItem.getSubjectValue();
                        if (null != subjectValue) {
                            try {
                                subjectItem.setSubjectPercent(toMithrasUnit(
                                        new BigDecimal(subjectValue)
                                                .divide(new BigDecimal(toDivide == 0 ? 1 : toDivide), 4, RoundingMode.HALF_UP))
                                );
                            } catch (Exception e) {
                                log.warn("", e);
                            }
                        }
                    }
                }
            }
        }
        if (displayDimensions.contains(SubjectItemDisplayDimension.OVER_YEAR.name())) {
            for (CorpSubjectItemListRSP value : values) {
                //同比%只需支持年度报告
                if (equal(SubjectQuarterType.TWELFTH.value, value.getQuarter())) {
                    List<CorpSubjectItem> corpSubjectItems = subjectItemMapper.selectList(Wrappers.<CorpSubjectItem>lambdaQuery()
                            .eq(CorpSubjectItem::getClientId, req.getClientId())
                            .eq(CorpSubjectItem::getYear, value.getYear() - 1)
                            .eq(CorpSubjectItem::getQuarter, value.getQuarter())
                            .eq(CorpSubjectItem::getReportType, value.getReportType())
                            .eq(CorpSubjectItem::getSubjectType, value.getSubjectType())
                    );
                    //jdk8 toMap value为null时会报错，所以。。。
                    Map<String, Long> lastMap = new HashMap<>();
                    corpSubjectItems.forEach(e -> {
                        lastMap.put(e.getSubjectCode(), e.getSubjectValue());
                    });
                    for (CorpSubjectItemListRSP.SubjectItem subjectItem : value.getItemList()) {
                        if (lastMap.containsKey(subjectItem.getSubjectCode())) {
                            Long overYear = null;
                            Long lastValue = lastMap.get(subjectItem.getSubjectCode());
                            if (null == lastValue || equal(lastValue, 0L)) {
                                log.info("上期值不存在或为0， 不计算同比。 year:{}, quarter:{}, reportType:{}, subjectType:{}", value.getYear() - 1, value.getQuarter(), value.getReportType(), value.getSubjectType());
                            } else if (null != subjectItem.getSubjectValue()) {
                                overYear = toMithrasUnit(
                                        new BigDecimal(subjectItem.getSubjectValue())
                                                .divide(new BigDecimal(lastValue), 4, RoundingMode.HALF_UP)
                                                .subtract(BigDecimal.ONE)
                                );
                            }
                            subjectItem.setSubjectOverYear(overYear);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    public void downloadTemplate(OutputStream output, String filename) {
        ossClient.downLoad(output, filename);
    }

    public void remove(CorpSubjectItemRemoveREQ req) {
        subjectItemMapper.delete(Wrappers.<CorpSubjectItem>lambdaQuery()
                .eq(CorpSubjectItem::getQuarter, req.getQuarter())
                .eq(CorpSubjectItem::getYear, req.getYear())
                .eq(CorpSubjectItem::getReportType, req.getReportType())
                .eq(CorpSubjectItem::getSubjectType, req.getSubjectType())
                .eq(CorpSubjectItem::getClientId, req.getClientId())
        );

    }

    @Data
    public static class SubjectSheetData {
        private String reportType;
        private Integer year;
        private Integer quarter;
        private String sheetName;
        private Map<String, Item> itemMap;
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private String name;
        private BigDecimal value;
    }
}
