package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayInfoQuery extends CommonAuthQuery {
    private LocalDate queryDateFrom;
    private LocalDate queryDateTo;
    private String contractCode;
    private Long clientId;
    private LocalDate actualPayDateFrom;
    private LocalDate actualPayDateTo;
    // 2024-10-24 添加借据编号
    private String paymentCode;
}
