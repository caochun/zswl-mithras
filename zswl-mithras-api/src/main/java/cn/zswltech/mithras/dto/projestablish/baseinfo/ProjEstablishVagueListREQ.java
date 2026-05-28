package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 16:04
 */
@ApiModel("创建审批之前的立项查询接口请求体")
@Data
public class ProjEstablishVagueListREQ {

    /**
     *立项项目模糊名称
     **/
    @ApiModelProperty("立项项目模糊名称")
    private String projVagueName;

    @ApiModelProperty("立项项目状态 默认查询生效状态 RecordStatus")
    private String projReviewStatus;
}
