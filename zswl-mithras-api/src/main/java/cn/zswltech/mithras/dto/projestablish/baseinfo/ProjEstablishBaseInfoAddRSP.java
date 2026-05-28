package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Data
@ApiModel("立项基本信息表新增-请求体")
public class ProjEstablishBaseInfoAddRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

}
