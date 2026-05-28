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
@ApiModel("融租易APP我的项目详情-请求体")
public class AppProjDetailREQ extends PageReq {

    @ApiModelProperty("项目id")
    private Long id;

    @ApiModelProperty(value = "项目阶段")
    private String stage;
}
