package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 流程列表查询
 *
 * @author wangchuanhao
 * @date 2022/7/28 3:19 PM
 */
@Data
public class ProcessCcListREQ extends ProcessListREQ {

    @ApiModelProperty("抄送人id")
    private Long senderId;

    @ApiModelProperty("是否已读，1已读，0未读")
    private Integer readFlag;

    @ApiModelProperty("抄送时间从")
    private LocalDate ccTimeFrom;

    @ApiModelProperty("抄送时间到")
    private LocalDate ccTimeTo;

}
