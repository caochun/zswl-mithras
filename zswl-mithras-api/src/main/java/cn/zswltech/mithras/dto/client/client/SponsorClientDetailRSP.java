package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class SponsorClientDetailRSP {

    @ApiModelProperty("原所属经理名称")
    private String belongSponsorName;

    @ApiModelProperty("原所属经理id")
    private Long belongSponsorId;

    @ApiModelProperty("原所属部门id")
    private Long belongDeptId;

    @ApiModelProperty("原所属部门名称")
    private String belongDeptName;

    @ApiModelProperty("移交至部门名称")
    private String toDeptName;

    @ApiModelProperty("移交至经理名称")
    private String toSponsorName;

    @ApiModelProperty("移至协办列表")
    private List<String> toCosponsorNames;

    @ApiModelProperty("正式移交日期")
    private LocalDate transferDate;

    @ApiModelProperty("要移交的客户")
    List<SponsorClientListRSP> sponsorClientList;

}
