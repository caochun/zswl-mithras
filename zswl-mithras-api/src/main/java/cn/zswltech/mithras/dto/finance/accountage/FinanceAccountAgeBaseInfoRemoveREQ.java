package cn.zswltech.mithras.dto.finance.accountage;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表删除-请求体")
public class FinanceAccountAgeBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
