package cn.zswltech.mithras.dto.client.shareholder;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("股东信息列表-请求体")
public class CorpShareholderInfoListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("clientId")
    private Long clientId;

    @ApiModelProperty("排序字段：认缴金额：paidTotal，出资占比：capitalPercent，实缴金额：actualPaidTotal ")
    private String orderField;


    @ApiModelProperty("排序类型，倒排：desc，正排：asc")
    private String orderType;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/

}
