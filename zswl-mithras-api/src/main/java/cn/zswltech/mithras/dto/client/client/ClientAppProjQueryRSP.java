package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("融租易APP客户项立项新建之后到未完成项目评审的项目名称-返回体")
public class ClientAppProjQueryRSP {

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

}
