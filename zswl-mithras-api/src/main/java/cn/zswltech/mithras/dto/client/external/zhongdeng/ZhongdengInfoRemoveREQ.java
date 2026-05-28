package cn.zswltech.mithras.dto.client.external.zhongdeng;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 中登网数据
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:49 PM
 */
@Data
@ApiModel("中登网数据删除-请求体")
public class ZhongdengInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
