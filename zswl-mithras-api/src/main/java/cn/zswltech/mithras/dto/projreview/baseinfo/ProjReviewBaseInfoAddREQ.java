package cn.zswltech.mithras.dto.projreview.baseinfo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 立项基本信息表
 * @author zhaozhengkang
 * @date 2022-08-02
 */
@Data
@ApiModel("项目评审基本信息表新增-请求体")
public class ProjReviewBaseInfoAddREQ {

    @ApiModelProperty(value = "立项ID")
    @NotNull(message = "立项Id为null")
    private Long projEstablishId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "客户Name")
    private String clientName;

    @ApiModelProperty(value = "业务类型")
    private String bizType;
}
