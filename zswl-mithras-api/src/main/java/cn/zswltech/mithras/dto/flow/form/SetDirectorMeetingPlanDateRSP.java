package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/3/17
 * @description
 */
@Data
public class SetDirectorMeetingPlanDateRSP {
    @ApiModelProperty("董事会预计召开日期")
    private String directorMeetingPlanDate;
}
