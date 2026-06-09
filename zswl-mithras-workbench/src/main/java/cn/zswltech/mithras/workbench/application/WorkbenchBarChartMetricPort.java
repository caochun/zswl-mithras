package cn.zswltech.mithras.workbench.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface WorkbenchBarChartMetricPort {
    int countProjectEstablish(String deptScope, LocalDateTime startTime);

    BigDecimal calculateProjectEstablishAmount(String deptScope, LocalDateTime startTime);

    int countProjectReview(String deptScope, LocalDateTime startTime);

    BigDecimal calculateProjectReviewAmount(String deptScope, LocalDateTime startTime);

    int countContract(String deptScope, LocalDateTime startTime);

    BigDecimal calculateContractAmount(String deptScope, LocalDateTime startTime);

    int countPayment(String deptScope, LocalDateTime startTime);

    BigDecimal calculatePaymentAmount(String deptScope, LocalDate startDate);
}
