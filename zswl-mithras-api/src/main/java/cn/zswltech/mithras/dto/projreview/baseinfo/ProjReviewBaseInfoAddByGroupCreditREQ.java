package cn.zswltech.mithras.dto.projreview.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 项目评审 从集团授信 评审入口新增
 * @author zhaozhengkang
 * @date 2022-11-16
 */
@Data
@ApiModel("项目评审基本信息表新增（从集团授信）-请求体")
public class ProjReviewBaseInfoAddByGroupCreditREQ {

    @ApiModelProperty(value = "集团授信评审ID")
    @NotNull
    private Long groupCreditReviewId;

    @ApiModelProperty(value = "项目名称")
    @NotBlank
    private String projName;

    @ApiModelProperty(value = "客户id")
    @NotNull
    private Long clientId;

    @ApiModelProperty(value = "业务类型")
    @NotNull
    private String bizType;

    private boolean createPricing = true;
}
