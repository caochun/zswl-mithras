package cn.zswltech.mithras.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardClientOverviewStatisticsRSP {
    private String group;
    private String groupCode;
    private Long quantity;
    private Long incrementThisMonth;
}
