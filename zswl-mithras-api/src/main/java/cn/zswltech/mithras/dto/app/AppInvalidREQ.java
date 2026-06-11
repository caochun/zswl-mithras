package cn.zswltech.mithras.dto.app;

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
@ApiModel("融租易APP拜访记录作废-请求体")
public class AppInvalidREQ {


    @ApiModelProperty("拜访记录id")
    private Long id;

}
