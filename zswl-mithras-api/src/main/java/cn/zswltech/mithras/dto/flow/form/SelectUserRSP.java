package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选人
 *
 * @author wangchuanhao
 * @date 2022/8/8 2:44 PM
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SelectUserRSP {

    @ApiModelProperty("成员id")
    private Long userId;

    @ApiModelProperty("成员名称")
    private String userName;

    @ApiModelProperty("成员id-value")
    private Long value;

    @ApiModelProperty("成员名称-label")
    private String label;


}
