package cn.zswltech.mithras.dto.contract.text;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bigbear
 * @date 2024/11/18 10:01
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "合同文本管理-台账列表请求参数")
public class ContractTextManageListREQ extends PageReq {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "签约方式 SigningWayEnum#name")
    private String signingWay;

    @ApiModelProperty(value = "推送时间开始 format: yyyy-MM-dd")
    private String pushTimeFrom;

    @ApiModelProperty(value = "推送时间结束 format: yyyy-MM-dd")
    private String pushTimeTo;
}
