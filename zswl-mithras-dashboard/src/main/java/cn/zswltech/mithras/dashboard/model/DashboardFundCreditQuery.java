package cn.zswltech.mithras.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/27
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFundCreditQuery {
    private String creditCode;
    private String organizationName;
    private String businessType;
    // 查询时间
    @NonNull
    private LocalDate queryDate;

    private List<Long> ids;
}
