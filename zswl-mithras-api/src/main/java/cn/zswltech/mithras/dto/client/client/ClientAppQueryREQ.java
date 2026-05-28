package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易APP企查查-请求体")
public class ClientAppQueryREQ extends PageReq {

    @ApiModelProperty("评估机构名称")
    private String companyName;

}
