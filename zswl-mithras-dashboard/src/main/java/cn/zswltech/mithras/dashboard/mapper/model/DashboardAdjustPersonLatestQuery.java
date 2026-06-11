package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAdjustPersonLatestQuery {
    private String businessGroup;
    private List<Long> deptIdList;

}
