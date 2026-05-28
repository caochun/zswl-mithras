package cn.zswltech.mithras.dto.groupcreditestablish;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 创建集团授信评审之前的集团授信立项查询接口请求体
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("创建集团授信评审之前的集团授信立项查询接口请求体")
@Data
public class GroupCreditEstablishVagueListREQ {

    /**
     *立项项目模糊名称
     **/
    @ApiModelProperty("立项项目模糊名称")
    private String projVagueName;
}
