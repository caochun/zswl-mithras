package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/15 11:40
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseFileNameComparisonREQ {

    @ApiModelProperty(value = "文件名")
    @NotEmpty(message = "文件名不能为空")
    private List<String> fileNameList;

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @ApiModelProperty(value = "ocr文件类型")
    @NotEmpty(message = "ocr文件类型不能为空")
    private String leaseOCRType;

    @ApiModelProperty(value = "操作类型(REPLACE:替换发票)")
    private String operateType;
}
