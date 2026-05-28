package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 租金催收生成文件
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:19 PM
 */
@Data
@ApiModel("租金催收生成文件请求体")
public class RentCollectionEmailGenREQ {

    @ApiModelProperty("收款主表id")
    private Long collectionId;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("我方银行账号id")
    private Long bankId;

}
