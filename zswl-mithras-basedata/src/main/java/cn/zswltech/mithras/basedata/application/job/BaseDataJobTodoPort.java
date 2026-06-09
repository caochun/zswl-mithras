package cn.zswltech.mithras.basedata.application.job;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseDataJobTodoPort {

    String BASE_DATA_EXCHANGE_RATE_TODO_PROCESS_TYPE = "baseDataExchangeRateTodo";

    void createExchangeRateTodo(int year, int month, int dayOfMonth, LocalDateTime applyTime, List<Long> assigneeIds);
}
