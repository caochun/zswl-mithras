package cn.zswltech.mithras.dashboard.mapper.model;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DashboardFundCostQuery extends PageReq {
    private String financingCode;
    private String orgName;
    private String financingTypeCode;
    private List<Long> ids;

    @ApiModelProperty("起息日开始")
    private LocalDate startDate;

    @ApiModelProperty("起息日结束")
    private LocalDate endDate;

    @NonNull
    private LocalDate queryDate;
}
