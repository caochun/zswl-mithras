package cn.zswltech.mithras.service.service.basedata;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.service.enums.basedata.BaseDataSpecialDateTypeEnum;
import cn.zswltech.mithras.service.mapper.basedata.BaseDataSpecialDateMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.HolidayExtractor;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.jni.Local;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/24
 * @description
 */
@Service
public class BaseDataSpecialDateService extends ServiceImpl<BaseDataSpecialDateMapper, BaseDataSpecialDate> {

    private static final NamedThreadLocal<List<BaseDataSpecialDate>> threadLocalA = new NamedThreadLocal<>("BaseDataSpecialDateService");

    public BaseDataSpecialDate find(LocalDate localDate) {
        LambdaQueryWrapper<BaseDataSpecialDate> query = Wrappers.lambdaQuery();
        query.eq(BaseDataSpecialDate::getSpecialDate, localDate);
        query.orderByDesc(BaseDataSpecialDate::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<BaseDataSpecialDate> findBySpecialDateAfter(LocalDate localDate) {
        return this.list(Wrappers.<BaseDataSpecialDate>lambdaQuery()
                .ge(BaseDataSpecialDate::getSpecialDate, localDate));
    }

    public List<BaseDataSpecialDate> findAllSpecialDate(){
        List<BaseDataSpecialDate> baseDataSpecialDates = threadLocalA.get();
        if (CollectionUtil.isNotEmpty(baseDataSpecialDates)) {
            return baseDataSpecialDates;
        }
        baseDataSpecialDates = this.list();
        threadLocalA.set(baseDataSpecialDates);
        return baseDataSpecialDates;
    }

    public List<BaseDataSpecialDate> between(LocalDate from, LocalDate to) {
        return this.list(Wrappers.<BaseDataSpecialDate>lambdaQuery()
                .le(BaseDataSpecialDate::getSpecialDate, to)
                .ge(BaseDataSpecialDate::getSpecialDate, from));
    }

    public long calculateWorkDays(LocalDate from, LocalDate to) {
        long days = 0L;
        LocalDate current = from;
        while (!current.isAfter(to)) {
            if (!LocalDateTimeUtil.isWeekend(current)) {
                days++;
            }
            current = current.plusDays(1L);
        }
        // 进一步判断中间是否包含特殊日期
        List<BaseDataSpecialDate> baseDataSpecialDateList = this.between(from, to);
        if (CollectionUtil.isNotEmpty(baseDataSpecialDateList)) {
            long holidays = baseDataSpecialDateList.stream().filter(e -> Objects.equals(BaseDataSpecialDateTypeEnum.HOLIDAY.name(), e.getSpecialType())).count();
            long workdays = baseDataSpecialDateList.stream().filter(e -> Objects.equals(BaseDataSpecialDateTypeEnum.WORKDAY.name(), e.getSpecialType())).count();
            days = days - holidays + workdays;
        }
        return days;
    }

    @Transactional(rollbackFor = Exception.class)
    public void initData(String content, int year) {
        List<BaseDataSpecialDate> list = new ArrayList<>();
        HolidayExtractor.HolidayWork extract = HolidayExtractor.extract(content, year);
        List<LocalDate> holidays = extract.getHolidays();
        for (LocalDate holiday : holidays) {
            BaseDataSpecialDate a = new BaseDataSpecialDate();
            a.setSpecialDate(holiday);
            a.setYear(year);
            a.setMonth(holiday.getMonthValue());
            a.setSpecialType(BaseDataSpecialDateTypeEnum.HOLIDAY.name());
            list.add(a);
        }
        List<LocalDate> workdays = extract.getWorkdays();
        for (LocalDate workday : workdays) {
            BaseDataSpecialDate a = new BaseDataSpecialDate();
            a.setSpecialDate(workday);
            a.setYear(year);
            a.setMonth(workday.getMonthValue());
            a.setSpecialType(BaseDataSpecialDateTypeEnum.WORKDAY.name());
            list.add(a);
        }
        //
        this.remove(Wrappers.<BaseDataSpecialDate>lambdaQuery().eq(BaseDataSpecialDate::getYear, year));
        this.saveBatch(list);
    }


    public List<LocalDate> handleHoliday(Map<LocalDate, BaseDataSpecialDate> baseDataSpecialDateMap, LocalDate localDate){
        List<LocalDate> resultDate = new ArrayList<>();
        if(!isFreeDay(baseDataSpecialDateMap.get(localDate), localDate)){
            do {
                resultDate.add(localDate);
                localDate = localDate.plusDays(1);
            } while (isFreeDay(baseDataSpecialDateMap.get(localDate), localDate));
        }
        return resultDate;
    }


    /**
     * 判断时间是否为休息日
     * @param baseDataSpecialDate 初始化的特殊日期
     * @param determinedDate 需要判断的日期
     * @return true:节假日
     */
    public boolean isFreeDay(BaseDataSpecialDate baseDataSpecialDate, LocalDate determinedDate){
        if(determinedDate == null){
            throw new MithrasException("判断是否为节假日失败，日期不得为空");
        }
        if(baseDataSpecialDate != null){
            return Objects.equals(baseDataSpecialDate.getSpecialType(), BaseDataSpecialDateTypeEnum.HOLIDAY.name());
        }
        return LocalDateTimeUtil.isWeekend(determinedDate);
    }

    /**
     * 判断传入时间是不是工作日
     */
    public boolean isWorkDay(LocalDate determinedDate){
        BaseDataSpecialDate specialDate = this.find(determinedDate);
        return !isFreeDay(specialDate, determinedDate);
    }
}
