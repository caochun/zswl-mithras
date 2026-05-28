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
@ApiModel("当前用户按钮状态-返回体")
public class UserButtonStatusRSP {
    @ApiModelProperty("是否可以点击移交客户的按钮")
    private Boolean canTransferClient;

}
