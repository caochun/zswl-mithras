package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 租金催收发送邮件详情页请求体
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:22 PM
 */
@Data
@ApiModel("租金催收发送邮件详情页请求体")
public class RentCollectionEmailDetailREQ {

    @ApiModelProperty("收款id")
    private Long collectionId;

}
