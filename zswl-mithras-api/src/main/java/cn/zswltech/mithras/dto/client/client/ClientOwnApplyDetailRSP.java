package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户详情页面是否有编辑查看权限-请求体")
public class ClientOwnApplyDetailRSP {

    @ApiModelProperty("是否只展示工商信息")
    public Boolean showCommerceInfo;


    @ApiModelProperty("是否能编辑")
    public Boolean canEdit;

}
