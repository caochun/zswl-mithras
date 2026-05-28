package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@Data
public class  AssociationReportCreateREQ {
    @ApiModelProperty("报表类型code")
    @NotBlank(message = "报表类型不能为空")
    private String reportCategoryCode;

    @ApiModelProperty("年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

    @ApiModelProperty("周期类型code")
    @NotBlank(message = "周期类型不能为空")
    private String periodCategory;

    @ApiModelProperty("周期")
    @NotNull(message = "报表周期不能为空")
    private Integer period;

    @ApiModelProperty("初始数据来源")
    private String dataSource;

    @ApiModelProperty("是否展示在列表")
    private Integer isShow;

    @ApiModelProperty("是否要校验已存在")
    private Boolean checkExist = Boolean.TRUE;
}
