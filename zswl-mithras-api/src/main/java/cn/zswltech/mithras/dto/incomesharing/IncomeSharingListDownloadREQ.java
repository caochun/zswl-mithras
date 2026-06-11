package cn.zswltech.mithras.dto.incomesharing;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author yupengfei
 * @date 2024/6/7 17:44
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingListDownloadREQ {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "查询月份 yyyy-MM")
    private String yearAndMonth;
}
