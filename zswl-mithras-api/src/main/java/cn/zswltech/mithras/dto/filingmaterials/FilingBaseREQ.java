package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author lllin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilingBaseREQ {
    @NotNull
    @ApiModelProperty("申请id")
    private Long id;
}
