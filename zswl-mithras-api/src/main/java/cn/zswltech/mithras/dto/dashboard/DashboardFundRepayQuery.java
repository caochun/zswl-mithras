package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

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
    private List<Long> ids;
}
