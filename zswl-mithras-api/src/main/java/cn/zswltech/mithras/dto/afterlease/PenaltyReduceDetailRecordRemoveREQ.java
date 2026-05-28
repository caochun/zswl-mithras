package cn.zswltech.mithras.dto.afterlease;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 罚息减免明细表
 * @author vico
 * @date 2024-08-21
 */
@Data
@ApiModel("罚息减免明细表删除-请求体")
public class PenaltyReduceDetailRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
