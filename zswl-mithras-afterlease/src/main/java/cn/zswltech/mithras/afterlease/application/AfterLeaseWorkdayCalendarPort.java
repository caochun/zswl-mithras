package cn.zswltech.mithras.afterlease.application;

import java.time.LocalDate;

public interface AfterLeaseWorkdayCalendarPort {

    boolean isWorkday(LocalDate date);

    int countWorkdayNumber(LocalDate beganDate, LocalDate targetDate);

    LocalDate getNextWorkdayAfterDays(LocalDate startDate, int workdays);
}
