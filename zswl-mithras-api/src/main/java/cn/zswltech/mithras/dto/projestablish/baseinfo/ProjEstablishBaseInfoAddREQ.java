package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 立项基本信息表
 * @author luyi
 * @date 2022-07-19
 */
@Data
@ApiModel("立项基本信息表新增-请求体")
public class ProjEstablishBaseInfoAddREQ {

    /**
    * 客户id
    */
    @NotNull
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @NotBlank
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
    * 项目名称
    */
    @NotBlank
    @ApiModelProperty(value = "项目名称")
    private String projName;

}
