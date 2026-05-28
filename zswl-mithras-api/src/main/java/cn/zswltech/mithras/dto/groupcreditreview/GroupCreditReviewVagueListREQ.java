package cn.zswltech.mithras.dto.groupcreditreview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 创建合同之前的授信评审接口请求体
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("创建合同之前的授信评审接口请求体")
@Data
public class GroupCreditReviewVagueListREQ {

    /**
     *立项项目模糊名称
     **/
    @ApiModelProperty("项目模糊名称")
    private String projVagueName;
}
