package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author: jackerhe
 * @date: 2023/11/30 9:39 上午
 **/
@Data
@ApiModel("黑灰名单库新增-请求体")
public class BlackGrayBusinessTypeReq {


    @ApiModelProperty(value = "所属机构ID")
    private Long orgId;

}
