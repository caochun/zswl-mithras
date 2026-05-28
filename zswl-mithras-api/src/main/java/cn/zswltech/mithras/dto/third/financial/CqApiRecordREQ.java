package cn.zswltech.mithras.dto.third.financial;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/7/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CqApiRecordREQ extends PageReq {
    @ApiModelProperty("唯一标识编号")
    private String businessId;

    @ApiModelProperty("接口状态，0-推送失败，1-已推送")
    private Integer isDone;

    @ApiModelProperty("苍穹单据类型")
    private String billType;

    @ApiModelProperty("关联id")
    private String businessTitle;

    @ApiModelProperty("更新日期-起")
    private LocalDate updateTimeFrom;

    @ApiModelProperty("更新日期-止")
    private LocalDate updateTimeTo;
}
