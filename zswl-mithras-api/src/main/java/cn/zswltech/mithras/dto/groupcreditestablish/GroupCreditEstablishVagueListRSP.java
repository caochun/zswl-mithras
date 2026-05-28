package cn.zswltech.mithras.dto.groupcreditestablish;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @description 创建集团授信评审之前的集团授信立项查询接口返回体
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("创建集团授信评审之前的集团授信立项查询接口返回体")
@Data
public class GroupCreditEstablishVagueListRSP {

    @ApiModelProperty("集团授信立项项目Id")
    private Long id;

    @ApiModelProperty("立项项目名称")
    private String projName;

    @ApiModelProperty("客户名称")
    private List<String> clientNames;

    @ApiModelProperty("授信额度")
    private Long applyCreditAmount;

}
