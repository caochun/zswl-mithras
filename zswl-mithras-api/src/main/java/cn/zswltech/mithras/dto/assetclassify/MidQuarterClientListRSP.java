package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("风控管理-资产分类-季中初分客户列表-返回体")
public class MidQuarterClientListRSP extends ListBaseRSP {

    @ApiModelProperty("资产五级分类id")
    private Long assetClassifyId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户归属部门id")
    private Long belongDeptId;

    @ApiModelProperty("客户归属部门名称")
    private String belongDeptName;

    @ApiModelProperty("客户归属主办id")
    private Long belongSponsorId;

    @ApiModelProperty("客户归属主办名称")
    private String belongSponsorName;
}
