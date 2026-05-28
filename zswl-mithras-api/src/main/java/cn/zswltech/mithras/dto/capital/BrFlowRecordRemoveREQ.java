package cn.zswltech.mithras.dto.capital;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 保融流水表
 * @author vico
 * @date 2024-06-17
 */
@Data
@ApiModel("保融流水表删除-请求体")
public class BrFlowRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
