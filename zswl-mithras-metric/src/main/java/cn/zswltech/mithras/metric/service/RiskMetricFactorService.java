package cn.zswltech.mithras.metric.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.metric.factor.RiskMetricFactorFileListReq;
import cn.zswltech.mithras.dto.metric.factor.RiskMetricFactorListReq;
import cn.zswltech.mithras.dto.metric.factor.RiskMetricFactorPageListReq;
import cn.zswltech.mithras.dto.metric.factor.RiskMetricFactorPageListRsp;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorType;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.MissingFactorException;
import cn.zswltech.mithras.metric.mapper.RiskMetricFactorMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorFile;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorMerge;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.*;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.BANK_INTEREST_REPAY;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskMetricFactorService extends ServiceImpl<RiskMetricFactorMapper, RiskMetricFactor> {

    private static final String SPLITTER = "@";

    @Resource
    private RiskMetricFactorFileService factorFileService;
    @Resource
    private RiskMetricFactorReportFileStore reportFileStore;
    private Map<String, List<String>> refreshConfigMap;
    @Resource
    private SystemConfigService configService;

    public Map<String, Long> findMetricValueMap(String factorTable, int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        LambdaQueryWrapper<RiskMetricFactor> query = Wrappers.lambdaQuery();
        query.eq(RiskMetricFactor::getFactorDate, LocalDate.of(year, month, date.lengthOfMonth()));
        query.eq(RiskMetricFactor::getFactorTable, factorTable);
        return this.list(query).stream().collect(Collectors.toMap(RiskMetricFactor::getFactorName, RiskMetricFactor::getFactorValue));
    }

    @PostConstruct
    public void init() {
        String refreshConfigJson = null;
        Response<SystemConfigDO> rsp = configService.getConfig("guanyuan.refreshJson");
        SystemConfigDO configDO = rsp.getData();
        if (null != configDO) {
            refreshConfigJson = configDO.getConfigValue();
        }
        if (isBlank(refreshConfigJson)) {
            refreshConfigJson = "{}";
        }
        refreshConfigMap = JSONUtil.toBean(refreshConfigJson, new TypeReference<Map<String, List<String>>>() {
        }, true);
    }

    public PageR<RiskMetricFactorPageListRsp> detailPageList(RiskMetricFactorPageListReq req) {
        Page<RiskMetricFactor> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<RiskMetricFactor> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.eq(RiskMetricFactor::getFactorDate, req.getFactorDate());
        conditionQuery.eq(RiskMetricFactor::getFactorTable, req.getFactorTable());
        if (StrUtil.isNotBlank(req.getFactorName())) {
            conditionQuery.like(RiskMetricFactor::getFactorName, req.getFactorName());
        }
        Page<RiskMetricFactor> pageDbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageDbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<RiskMetricFactorPageListRsp> list = pageDbResult.getRecords().stream().map(e -> {
            RiskMetricFactorPageListRsp rsp = new RiskMetricFactorPageListRsp();
            rsp.setId(e.getId());
            rsp.setFactorTable(e.getFactorTable());
            rsp.setFactorName(e.getFactorName());
            rsp.setFactorValue(e.getFactorValue());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(list, pageDbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeFile(Long id) {
        RiskMetricFactorFile factorFile = factorFileService.getById(id);
        factorFileService.removeById(id);
        if (null != factorFile) {
            RiskMetricFactorTable riskMetricFactorTable = RiskMetricFactorTable.ofName(factorFile.getSheetName());
            if (Objects.nonNull(riskMetricFactorTable)) {
                this.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                        .eq(RiskMetricFactor::getFactorDate, factorFile.getSheetDate())
                        .eq(RiskMetricFactor::getFactorTable, riskMetricFactorTable.display)
                );
            }
        }
    }

    public Page<RiskMetricFactorFile> fileList(RiskMetricFactorFileListReq req) {
        if (null != req.getSheetDate()) {
            req.setSheetDate(req.getSheetDate().with(TemporalAdjusters.lastDayOfMonth()));
        }
        return factorFileService.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskMetricFactorFile>lambdaQuery()
                        .like(isNotBlank(req.getSheetName()), RiskMetricFactorFile::getSheetName, req.getSheetName())
                        .eq(isNotNull(req.getSheetDate()), RiskMetricFactorFile::getSheetDate, req.getSheetDate())
                        .orderByDesc(RiskMetricFactorFile::getSheetDate)
        );
    }

    public Page<RiskMetricFactor> list(RiskMetricFactorListReq req) {
        if (null != req.getFactorDate()) {
            req.setFactorDate(req.getFactorDate().with(TemporalAdjusters.lastDayOfMonth()));
        }
        if (isNotBlank(req.getFactorTable())) {
            RiskMetricFactorTable table = RiskMetricFactorTable.valueOf(req.getFactorTable());
            req.setFactorTable(table.display);
        }

        return this.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskMetricFactor>lambdaQuery()
//                        .select(info -> !info.getColumn().equals("factor_json"))
                        .like(isNotBlank(req.getFactorName()), RiskMetricFactor::getFactorName, req.getFactorName())
                        .eq(isNotBlank(req.getFactorTable()), RiskMetricFactor::getFactorTable, req.getFactorTable())
                        .eq(isNotNull(req.getFactorDate()), RiskMetricFactor::getFactorDate, req.getFactorDate())
        );
    }

    /**
     * @param cellValue 核算组织：浙江浙商融资租赁有限公司; 开始期间：2022年11期;  结束期间：2022年11期;  科目表：浙江交通投资集团统一科目表
     * @return 期值
     */
    private LocalDate extractSubjectDate(String cellValue) {
        String pattern = "\\d{4}年\\d{1,2}期";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(cellValue);
        List<LocalDate> dateList = new ArrayList<>(2);
        while (matcher.find()) {
            dateList.add(LocalDateTimeUtil.parseDate(matcher.group(), "yyyy年M期"));
        }
        //暂时支持当期的科目表导入
        if (dateList.size() != 2) {
            err("科目表格式不正确，未提取到开始日期和结束日期");
        }
        if (!dateList.get(0).equals(dateList.get(1))) {
            err("只支持导入一起的科目余额表，开始和时间不一致");
        }
        return dateList.get(1);
    }

    @SneakyThrows
//    @Transactional(rollbackFor = Exception.class)
    public void importByFile(MultipartFile file) {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<RiskMetricFactor> list = new ArrayList<>(512);
        List<Sheet> sheets = reader.getSheets();
        //多个sheet，只上传一次文件
        Long fileId = null;
        for (Sheet sheet : sheets) {
            String tableName = sheet.getRow(0).getCell(0).getStringCellValue();
            LocalDate date = null;
            RiskMetricFactorTable factorTable = RiskMetricFactorTable.ofDisplay(tableName.trim());
            if (null == factorTable) {
                log.error("未知的表格，导入跳过，【{}】", tableName);
                continue;
            }
            switch (factorTable) {
                case BANK_INTEREST_REPAY: {
                    date = LocalDateTimeUtil.of(sheet.getRow(1).getCell(0).getDateCellValue()).toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
                    int contentStartRow = 5;
                    int endCol = 18;
                    //防止死循环，以防万一
                    int lastRow = sheet.getLastRowNum();
                    Row headerRow = sheet.getRow(3);
                    rowFor:
                    for (int i = contentStartRow; i < lastRow; i++) {
                        Row row = sheet.getRow(i);
                        JSONObject jo = new JSONObject();
                        for (int j = 0; j <= endCol; j++) {
                            Cell cell = row.getCell(j);
                            //行结束
                            if (null == cell || (j == 0 && isBlank(cell.toString()))) {
                                break rowFor;
                            }
                            CellType cellType = cell.getCellType();
                            Object value;
                            if (cellType == NUMERIC) {
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    value = cell.getDateCellValue();
                                } else {
                                    value = cell.getNumericCellValue();
                                }
                            } else if (cellType == FORMULA) {
                                value = cell.getNumericCellValue();
                            } else {
                                value = cell.getStringCellValue();
                            }
                            String headerName = headerRow.getCell(j).getStringCellValue().trim();
                            if (equalsAny(headerName, "借款金额（万元)", "已还本金（万元）", "未还本金（万元）") && value != null && isNotBlank(value.toString())) {
                                value = toMithrasUnit(new BigDecimal(value.toString()).multiply(new BigDecimal("10000")));
                            }
                            jo.set(headerName, value);
                        }
                        RiskMetricFactor factor = new RiskMetricFactor();
                        factor.setFactorJson(jo.toString());
                        factor.setFactorDate(date);
                        factor.setFactorSource(RiskMetricFactorType.IMPORT.name());
                        factor.setFactorTable(tableName);
                        list.add(factor);
                    }
                    this.remove(Wrappers.<RiskMetricFactor>lambdaQuery().eq(RiskMetricFactor::getFactorDate, date).eq(RiskMetricFactor::getFactorTable, tableName));
                    break;
                }
                case CAPITAL_BALANCE: {
                    Cell cell = sheet.getRow(2).getCell(2);
                    if (cell.getCellType() == NUMERIC) {
                        date = LocalDateTimeUtil.of(cell.getDateCellValue()).toLocalDate();
                    } else {
                        String dateStr = cell.getStringCellValue();//2022年11月30日
                        date = LocalDateTimeUtil.parseDate(dateStr, "yyyy年MM月dd日").with(TemporalAdjusters.lastDayOfMonth());
                    }
                    Map<String, BigDecimal> valueMap = extractValue(sheet, 6, 0, 0);
                    LocalDate finalDate = date;
                    valueMap.forEach((k, v) -> {
                        RiskMetricFactor factor = new RiskMetricFactor();
                        factor.setFactorDate(finalDate);
                        factor.setFactorName(k);
                        factor.setFactorSource(RiskMetricFactorType.IMPORT.name());
                        factor.setFactorTable(tableName);
                        factor.setFactorValue(toMithrasUnit(v));
                        list.add(factor);
                    });
                    this.remove(Wrappers.<RiskMetricFactor>lambdaQuery().eq(RiskMetricFactor::getFactorDate, date).eq(RiskMetricFactor::getFactorTable, tableName));
                    break;
                }
                case PROFIT: {
                    String yearStr = sheet.getRow(2).getCell(3).getStringCellValue();//2022年
                    String monthDayStr = sheet.getRow(2).getCell(4).getStringCellValue();//01月-11月
                    date = LocalDateTimeUtil.parseDate(yearStr + monthDayStr.split("-")[1], "yyyy年MM月").with(TemporalAdjusters.lastDayOfMonth());
                    Map<String, BigDecimal> valueMap = extractValue(sheet, 8, 0, 0);
                    LocalDate finalDate = date;
                    valueMap.forEach((k, v) -> {
                        RiskMetricFactor factor = new RiskMetricFactor();
                        factor.setFactorDate(finalDate);
                        factor.setFactorName(k);
                        factor.setFactorSource(RiskMetricFactorType.IMPORT.name());
                        factor.setFactorTable(tableName);
                        factor.setFactorValue(toMithrasUnit(v));
                        list.add(factor);
                    });
                    this.remove(Wrappers.<RiskMetricFactor>lambdaQuery().eq(RiskMetricFactor::getFactorDate, date).eq(RiskMetricFactor::getFactorTable, tableName));
                    break;
                }
                case CASH_FLOW: {
                    String yearStr = sheet.getRow(2).getCell(3).getStringCellValue();//2022年
                    String monthDayStr = sheet.getRow(2).getCell(4).getStringCellValue();//01月-11月
                    date = LocalDateTimeUtil.parseDate(yearStr + monthDayStr.split("-")[1], "yyyy年MM月").with(TemporalAdjusters.lastDayOfMonth());
                    Map<String, BigDecimal> valueMap = extractValue(sheet, 8, 0, 0);
                    LocalDate finalDate = date;
                    valueMap.forEach((k, v) -> {
                        RiskMetricFactor factor = new RiskMetricFactor();
                        factor.setFactorDate(finalDate);
                        factor.setFactorName(k);
                        factor.setFactorSource(RiskMetricFactorType.IMPORT.name());
                        factor.setFactorTable(tableName);
                        factor.setFactorValue(toMithrasUnit(v));
                        list.add(factor);
                    });
                    this.remove(Wrappers.<RiskMetricFactor>lambdaQuery().eq(RiskMetricFactor::getFactorDate, date).eq(RiskMetricFactor::getFactorTable, tableName));
                    break;
                }
                case SUBJECT_BALANCE: {
                    String infoCell = sheet.getRow(1).getCell(0).getStringCellValue();
                    date = extractSubjectDate(infoCell).with(TemporalAdjusters.lastDayOfMonth());
                    Map<String, BigDecimal> valueMap = extractValue(sheet, 12, 0, 1);
                    LocalDate finalDate = date;
                    valueMap.forEach((k, v) -> {
                        RiskMetricFactor factor = new RiskMetricFactor();
                        factor.setFactorDate(finalDate);
                        factor.setFactorName(k);
                        factor.setFactorSource(RiskMetricFactorType.IMPORT.name());
                        factor.setFactorTable(tableName);
                        factor.setFactorValue(toMithrasUnit(v));
                        list.add(factor);
                    });
                    this.remove(Wrappers.<RiskMetricFactor>lambdaQuery().eq(RiskMetricFactor::getFactorDate, date).eq(RiskMetricFactor::getFactorTable, tableName));
                    break;
                }
                default:
                    break;
            }
            if (date != null) {
                RiskMetricFactorFile factorFile = new RiskMetricFactorFile();
                factorFile.setSheetDate(date);
                factorFile.setSheetName(factorTable.name());
                factorFile.setFileId(fileId);
                LambdaUpdateWrapper<RiskMetricFactorFile> keyCondition = Wrappers.<RiskMetricFactorFile>lambdaUpdate()
                        .eq(RiskMetricFactorFile::getSheetDate, date)
                        .eq(RiskMetricFactorFile::getSheetName, factorTable.name());
                factorFileService.saveOrUpdate(factorFile, keyCondition);
                if (null == fileId) {
                    factorFile = factorFileService.getOne(keyCondition);
                    fileId = reportFileStore.addReportFile(file, factorFile.getId());
                    factorFile.setFileId(fileId);
                    //不要更新update time
                    factorFile.setUpdateTime(null);
                    factorFileService.updateById(factorFile);
                }
            }
            refreshGuanyuanDataSet(factorTable);
            if (factorTable == BANK_INTEREST_REPAY) {
                //银行还息统计表，只导入第一个sheet
                break;
            }
        }
        //
        this.saveBatch(list);

    }

    public void refreshGuanyuanDataSet(RiskMetricFactorTable table) {
        try {
            List<String> list = refreshConfigMap.get(table.name());
            if (CollUtil.isNotEmpty(list)) {
                for (String s : list) {
                    HttpUtil.get(s);
                }
            }
        } catch (Exception e) {
            log.error("刷新观远数据集失败", e);
        }
    }

    /**
     * @param sheet
     * @param maxCol
     * @param xExtraDelta 取横向中文字眼时，多扩展delta列文字
     * @param yExtraDelta 取纵向中文字眼时，多扩展delta行文字，
     * @return
     */
    private Map<String, BigDecimal> extractValue(Sheet sheet, int maxCol, int xExtraDelta, int yExtraDelta) {
        Map<String, BigDecimal> valueMap = new LinkedHashMap<>(128);
        //2022年11月30日
        //
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i < lastRowNum; i++) {
            for (int j = 0; j < maxCol; j++) {
                if (null == sheet.getRow(i) || null == sheet.getRow(i).getCell(j)) {
                    continue;
                }
                Cell cell = sheet.getRow(i).getCell(j);
                CellType cellType = cell.getCellType();
                if (cellType == NUMERIC) {
                    BigDecimal value = new BigDecimal(String.valueOf(cell.getNumericCellValue()));
                    String xStr = null;
                    String yStr = null;
                    for (int p = j; p >= 0; p--) {
                        Cell xCell = sheet.getRow(i).getCell(p);
                        if (null == sheet.getRow(i) || null == sheet.getRow(i).getCell(p)) {
                            continue;
                        }
                        if (xCell.getCellType() != NUMERIC && !xCell.toString().trim().isEmpty()) {
                            xStr = xCell.getStringCellValue();
                            if (xExtraDelta > 0) {
                                for (int m = xExtraDelta; m > 0; m--) {
                                    xStr = sheet.getRow(i).getCell(p - xExtraDelta).getStringCellValue() + SPLITTER + xStr;
                                }
                            }
                            break;
                        }
                    }
                    for (int p = i; p >= 0; p--) {
                        Cell xCell = sheet.getRow(p).getCell(j);
                        if (null == sheet.getRow(p) || null == sheet.getRow(p).getCell(j)) {
                            continue;
                        }
                        if (xCell.getCellType() != NUMERIC && !xCell.toString().trim().isEmpty()) {
                            yStr = xCell.getStringCellValue();
                            if (yExtraDelta > 0) {
                                for (int m = yExtraDelta; m > 0; m--) {
                                    yStr = sheet.getRow(p - yExtraDelta).getCell(j).getStringCellValue() + SPLITTER + yStr;
                                }
                            }
                            break;
                        }
                    }
                    if (null != xStr && null != yStr) {
                        valueMap.put((xStr.trim() + "@" + yStr.trim()).trim(), value);
                    }
                }
            }
        }

        return valueMap;
    }


    public RiskMetricFactor getFactor(String name, String table, LocalDate dateTime) {
        RiskMetricFactor riskMetricFactor = baseMapper.selectOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, name)
                .eq(RiskMetricFactor::getFactorTable, table)
                .le(RiskMetricFactor::getFactorDate, dateTime.with(TemporalAdjusters.lastDayOfMonth()))
                .ge(RiskMetricFactor::getFactorDate, dateTime.with(TemporalAdjusters.firstDayOfMonth()))
                .last("limit 1"));
        if (riskMetricFactor == null) {
            throw new MissingFactorException("财报" + table + "缺少信息:" + name + "，请补充。");
        }
        return riskMetricFactor;
    }

    private Long toMithrasUnit(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(new BigDecimal("10000")).longValue();
    }

}
