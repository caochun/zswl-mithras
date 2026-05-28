package cn.zswltech.mithras.dto.dashboard.operation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardOperationBaseREQ {

    public final static String publicType = "PUBLIC_CATEGORY";
    public final static String industryType = "INDUSTRY_CATEGORY";

    @ApiModelProperty("公用组/产业组 PUBLIC_CATEGORY / INDUSTRY_CATEGORY")
    private String type;

    @ApiModelProperty("日期-起")
    private LocalDate queryDateFrom;
    @ApiModelProperty("日期-止")
    private LocalDate queryDateTo;

}
