package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信立项基本信息表新增-请求体")
public class GroupCreditEstablishBaseInfoAddREQ {

    /**
     * 客户id
     */
    @NotNull
    @ApiModelProperty(value = "授信客户id")
    private Long clientId;

    /**
     * 授信名称
     */
    @NotBlank
    @ApiModelProperty(value = "授信名称")
    private String projName;

}
