package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 选人
 *
 * @author wangchuanhao
 * @date 2022/8/8 2:44 PM
 */
@Data
public class SelectUserREQ {

    @ApiModelProperty("成员id")
    private Long userId;

}
