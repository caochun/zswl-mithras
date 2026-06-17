package cn.zswltech.mithras.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFundCreditQuery {
    private String creditCode;
    private String organizationName;
    private String businessType;
    private List<Long> ids;

    @NonNull
    private LocalDate queryDate;
}
