package cn.zswltech.mithras.dto.report.fiveclass;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 征信报送-五级分类表新增入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-五级分类表新增入参")
public class FiveClassAddREQ {

    @ApiModelProperty("编辑区账户id")
    @NotNull
    private Long accountId;

    @ApiModelProperty("五级分类")
    private String fiveClass;

    @ApiModelProperty("五级分类认定日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate identificationDate;

    @NotNull(message = "新增原因不能为空")
    @ApiModelProperty("新增原因")
    private String reason;

}
