package cn.zswltech.mithras.service.service.basedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingListRSP;
import cn.zswltech.mithras.service.mapper.basedata.BaseDataLprMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Service
public class BaseDataLprService extends ServiceImpl<BaseDataLprMapper, BaseDataLpr> {

    private static final int EXCEL_ROW_LIMIT = 15000;

    public NewFtpLprPricingListRSP specificMonthLprWithDiff(LocalDate month) {
        LocalDate start = month.minusMonths(1);
        LocalDate end = month.with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        query.between(BaseDataLpr::getLprDate, start, end);
        Map<String, NewFtpLprPricingListRSP> map = list(query).stream()
                .collect(Collectors.toMap(e -> String.join("-",
                        String.valueOf(e.getLprDate().getYear()),
                        String.valueOf(e.getLprDate().getMonthValue())), this::convert));

        String thisMonthKey = String.join("-", String.valueOf(month.getYear()),
                String.valueOf(month.getMonthValue()));
        NewFtpLprPricingListRSP newFtpLprPricingListRSP = map.get(thisMonthKey);
        if (Objects.isNull(newFtpLprPricingListRSP)) {
            throw new MithrasException("LPR数据不完整，请检查LPR数据");
        }
        this.fillDiff(newFtpLprPricingListRSP, map);
        return newFtpLprPricingListRSP;
    }

    public List<NewFtpLprPricingListRSP> listWithDiff(int pageSize) {
        // 一个月一条数据，考虑到数据量不大且需要在内存中计算差值，直接取所有
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        // 保护措施
        query.last(StringUtil.mysqlLimit(0, pageSize));
        List<BaseDataLpr> all = this.getBaseMapper().selectList(query);
        List<NewFtpLprPricingListRSP> result = new LinkedList<>();
        for (BaseDataLpr baseDataLpr : all) {
            if (Objects.isNull(baseDataLpr.getLprDate())) {
                continue;
            }
            result.add(this.convert(baseDataLpr));
        }
        // 按照年月分组
        Map<String, NewFtpLprPricingListRSP> map = new HashMap<>();
        for (NewFtpLprPricingListRSP rsp : result) {
            if (Objects.isNull(rsp.getLprDate())) {
                continue;
            }
            int year = rsp.getLprDate().getYear();
            int month = rsp.getLprDate().getMonthValue();
            String key = year + "-" + month;
            map.put(key, rsp);
        }
        // 计算差额
        for (NewFtpLprPricingListRSP rsp : result) {
            this.fillDiff(rsp, map);
        }
        // 排序
        result.sort((e1, e2) -> {
            if (e1.getLprDate().isBefore(e2.getLprDate())) {
                return 1;
            } else if (e1.getLprDate().isEqual(e2.getLprDate())) {
                return 0;
            } else {
                return -1;
            }
        });
        return result;
    }

    public List<NewFtpLprPricingListRSP> listWithDiff() {
        return listWithDiff(500);
    }

    public boolean isExistByLprDate(LocalDate localDate) {
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        query.eq(BaseDataLpr::getLprDate, localDate);
        return this.count(query) > 0;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream) {
        List<BaseDataLprExcelModel> excelModelList = parseExcel(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("导入数据为空"));
        // 预处理
        for (BaseDataLprExcelModel excelModel : excelModelList) {
            // 判空
            Assert.notNull(excelModel.getLprDate(), () -> MithrasException.newException("LPR报价日不能为空"));
            Assert.notNull(excelModel.getOneYear(), () -> MithrasException.newException("1年期不能为空"));
            Assert.notNull(excelModel.getFiveYear(), () -> MithrasException.newException("5年期不能为空"));
        }
        // 找到已有的数据
        List<BaseDataLpr> all = this.list();
        // 根据日期分组
        Map<String, BaseDataLpr> map = new HashMap<>(all.size() * 2);
        if (!CollectionUtil.isEmpty(all)) {
            for (BaseDataLpr baseDataLpr : all) {
                map.put(LocalDateTimeUtil.format(baseDataLpr.getLprDate(), DatePattern.NORM_DATE_PATTERN), baseDataLpr);
            }
        }
        // 准备更新数据
        List<BaseDataLpr> toSaveList = new LinkedList<>();
        List<Long> toDeleteIdList = new LinkedList<>();
        for (BaseDataLprExcelModel excelModel : excelModelList) {
            String lprDataStr = LocalDateTimeUtil.format(excelModel.getLprDate(), DatePattern.NORM_DATE_PATTERN);
            BaseDataLpr exist = map.get(lprDataStr);
            if (Objects.nonNull(exist)) {
                toDeleteIdList.add(exist.getId());
            }
            BaseDataLpr toSave = new BaseDataLpr();
            toSave.setLprDate(excelModel.getLprDate());
            toSave.setOneYear(BigDecimal.valueOf(excelModel.getOneYear() * 100).setScale(2, RoundingMode.HALF_UP).toPlainString());
            toSave.setFiveYear(BigDecimal.valueOf(excelModel.getFiveYear() * 100).setScale(2, RoundingMode.HALF_UP).toPlainString());
            toSaveList.add(toSave);
        }
        // 执行数据更新
        if (CollectionUtil.isNotEmpty(toDeleteIdList)) {
            this.removeByIds(toDeleteIdList);
        }
        if (CollectionUtil.isNotEmpty(toSaveList)) {
            this.saveBatch(toSaveList);
        }
    }

    private List<BaseDataLprExcelModel> parseExcel(InputStream inputStream) {
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        Assert.isTrue(excelReader.getRowCount() <= EXCEL_ROW_LIMIT, () -> MithrasException.newException("最多支持导入" + EXCEL_ROW_LIMIT + "行数据"));
        excelReader.addHeaderAlias("LPR报价日", "lprDate");
        excelReader.addHeaderAlias("1年期", "oneYear");
        excelReader.addHeaderAlias("5年期", "fiveYear");
        return excelReader.read(0, 1, BaseDataLprExcelModel.class);
    }

    public NewFtpLprPricingListRSP convert(BaseDataLpr baseDataLpr) {
        int curYear = baseDataLpr.getLprDate().getYear();
        int curMonth = baseDataLpr.getLprDate().getMonthValue();
        NewFtpLprPricingListRSP rsp = new NewFtpLprPricingListRSP();
        rsp.setId(baseDataLpr.getId());
        rsp.setLprDate(LocalDate.of(curYear, curMonth, 1));
        if (StrUtil.isNotBlank(baseDataLpr.getOneYear())) {
            rsp.setOneYearLpr(new BigDecimal(baseDataLpr.getOneYear()).multiply(BigDecimal.valueOf(10000)).intValue());
        }
        if (StrUtil.isNotBlank(baseDataLpr.getFiveYear())) {
            rsp.setFiveYearLpr(new BigDecimal(baseDataLpr.getFiveYear()).multiply(BigDecimal.valueOf(10000)).intValue());
        }
        return rsp;
    }

    public void fillDiff(NewFtpLprPricingListRSP rsp, Map<String, NewFtpLprPricingListRSP> map) {
        int curYear = rsp.getLprDate().getYear();
        int curMonth = rsp.getLprDate().getMonthValue();
        int lastLprYear, lastLprMonth;
        if (curMonth == 1) {
            lastLprYear = curYear - 1;
            lastLprMonth = 12;
        } else if (curMonth == 2) {
            lastLprYear = curYear;
            lastLprMonth = 1;
        } else {
            lastLprYear = curYear;
            lastLprMonth = curMonth - 1;
        }
        NewFtpLprPricingListRSP curMonthLpr = map.get(curYear + "-" + curMonth);
        NewFtpLprPricingListRSP lastMonthLpr = map.get(lastLprYear + "-" + lastLprMonth);
        if (Objects.isNull(lastMonthLpr) || Objects.isNull(curMonthLpr)) {
            return;
        }
        Integer curOneYearLpr = curMonthLpr.getOneYearLpr();
        Integer lastOneYearLpr = lastMonthLpr.getOneYearLpr();
        Integer curFiveYearLpr = curMonthLpr.getFiveYearLpr();
        Integer lastFiveYearLpr = lastMonthLpr.getFiveYearLpr();
        if (Objects.nonNull(curOneYearLpr) && Objects.nonNull(lastOneYearLpr)) {
            rsp.setOneYearLprDiff(curOneYearLpr - lastOneYearLpr);
        }
        if (Objects.nonNull(curFiveYearLpr) && Objects.nonNull(lastFiveYearLpr)) {
            rsp.setFiveYearLprDiff(curFiveYearLpr - lastFiveYearLpr);
        }
    }

    public static class BaseDataLprExcelModel {
        private LocalDate lprDate;
        private Double oneYear;
        private Double fiveYear;

        public LocalDate getLprDate() {
            return lprDate;
        }

        public void setLprDate(LocalDate lprDate) {
            this.lprDate = lprDate;
        }

        public Double getOneYear() {
            return oneYear;
        }

        public void setOneYear(Double oneYear) {
            this.oneYear = oneYear;
        }

        public Double getFiveYear() {
            return fiveYear;
        }

        public void setFiveYear(Double fiveYear) {
            this.fiveYear = fiveYear;
        }
    }
}
