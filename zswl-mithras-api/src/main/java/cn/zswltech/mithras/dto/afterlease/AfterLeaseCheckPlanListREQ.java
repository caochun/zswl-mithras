package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-检查计划列表-请求体")
public class AfterLeaseCheckPlanListREQ extends PageReq {
    @ApiModelProperty("计划名称")
    private String planName;
    @ApiModelProperty("检查所属年份")
    private Integer year;
    @ApiModelProperty("计划类型，参考枚举AfterLeaseCheckPlanTypeEnum")
    private String planType;

    @ApiModelProperty("管理形式")
    private String checkWay;

    @ApiModelProperty("上次管理形式")
    private String lastCheckWay;

    @ApiModelProperty("创建时间")
    private LocalDate deadLineForm;
    @ApiModelProperty("创建时间")
    private LocalDate deadLineTo;

    @ApiModelProperty("创建时间")
    private LocalDate createTimeFrom;
    @ApiModelProperty("创建时间")
    private LocalDate createTimeTo;
}
