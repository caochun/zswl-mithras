package cn.zswltech.mithras.dto.projreview.meet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表新增-请求体")
public class ProjReviewMeetMinuteCreditDateCheckRSP {

    private Long id;

    /**
     * 授信到期日
     **/
    @ApiModelProperty("授信到期日")
    private LocalDate creditExpirationDate;

    /**
    * 补充说明
    */
    @ApiModelProperty(value = "是否过期 0 未过期 1 过期")
    private Integer effect;


}
