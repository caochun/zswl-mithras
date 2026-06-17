package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkdayCalendarPort;
import cn.zswltech.mithras.basedata.util.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AfterLeaseWorkdayCalendarPortAdapter implements AfterLeaseWorkdayCalendarPort {

    @Override
    public boolean isWorkday(LocalDate date) {
        return DateUtil.isWorkday(date);
    }

    @Override
    public int countWorkdayNumber(LocalDate beganDate, LocalDate targetDate) {
        return DateUtil.countWorkdayNumber(beganDate, targetDate);
    }

    @Override
    public LocalDate getNextWorkdayAfterDays(LocalDate startDate, int workdays) {
        return DateUtil.getNextWorkdayAfterDays(startDate, workdays);
    }
}
