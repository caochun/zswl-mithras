package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 客户按钮状态（是否可保存、是否可提交审批、显示确认还是提交审批）
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户按钮状态查询-请求体")
public class ClientButtonStatusREQ {

    @NotNull
    @ApiModelProperty("id")
    public Long id;
}
