package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangchuanhao
 * @description 集团授信立项基本信息表
 * @date 2022-11-14
 */
@Data
@ApiModel("集团授信基本信息表新增-请求体")
public class GroupCreditEstablishBaseInfoAddRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

}
