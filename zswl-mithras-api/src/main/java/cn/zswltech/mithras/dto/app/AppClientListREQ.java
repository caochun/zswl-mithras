package cn.zswltech.mithras.dto.app;

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
@ApiModel("融租易APP客户详情-请求体")
public class AppClientListREQ extends PageReq {

    @ApiModelProperty("客户名称")
    private String clientName;

}
