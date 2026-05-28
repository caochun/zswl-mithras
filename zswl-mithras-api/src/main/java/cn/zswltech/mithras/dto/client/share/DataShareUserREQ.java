package cn.zswltech.mithras.dto.client.share;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName DataShareREQ
 * @Author jackerhe
 * @Date 2022/8/1 11:05 上午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataShareUserREQ {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

}
