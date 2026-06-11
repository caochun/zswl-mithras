package cn.zswltech.mithras.dashboard.model;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardFundRepayQuery extends PageReq {
    private LocalDate repayDateFrom;
    private LocalDate repayDateTo;
    private String writeOffState;
    private List<String> writeOffStateList;
    private String financingCode;
    private String financingName;
    List<Long> ids;
}
