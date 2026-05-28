package cn.zswltech.mithras.dto.report.fiveclass;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 征信报送-五级分类表编辑入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-五级分类表编辑入参")
public class FiveClassModifyREQ {

    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "businessKey不能为空")
    @ApiModelProperty(value = "业务标识")
    private String businessKey;

    @NotNull(message = "修改原因不能为空")
    @ApiModelProperty("修改原因")
    private String reason;

    @ApiModelProperty("五级分类")
    private String fiveClass;

    @ApiModelProperty("五级分类认定日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate identificationDate;

}
