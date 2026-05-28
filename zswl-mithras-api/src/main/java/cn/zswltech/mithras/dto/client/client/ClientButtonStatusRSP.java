package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户按钮状态（是否可保存、是否可提交审批、显示确认还是提交审批）
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户按钮状态-返回体")
public class ClientButtonStatusRSP {

    @ApiModelProperty("是否可保存，1可保存，0不可保存")
    public Integer canSaveFlag;

    @ApiModelProperty("显示确认还是提交审批，1确认，2提交审批")
    public Integer effectButtonStyle;

    @ApiModelProperty("是否可确认或提交审批，1可提交审批，0不可提交")
    public Integer canEffectFlag;

}
