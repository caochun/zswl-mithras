package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("风控管理-资产分类客户列表-请求体")
public class AssetClassifyClientListREQ extends PageReq {
    @NotNull(message = "资产五级分类id不能为空")
    @ApiModelProperty("资产五级分类id")
    private Long assetClassifyId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("分类结果")
    private String classifyResult;

    @ApiModelProperty("复核状态")
    private String reviewStatus;

    @ApiModelProperty("是否董事会审批场景")
    private Boolean boardMeeting;

    @ApiModelProperty("是否查询最近一个有效版本分类结果")
    private Boolean queryLatestVersionClassifyResult;

    @ApiModelProperty("业务部门ID")
    private Long belongDeptId;

    @ApiModelProperty("是否关联方")
    private Integer isRelated;
}
