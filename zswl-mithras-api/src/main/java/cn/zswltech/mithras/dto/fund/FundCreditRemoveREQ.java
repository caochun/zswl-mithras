package cn.zswltech.mithras.dto.fund;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description fund_credit
 * @author zhaozhengkang
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_credit删除-请求体")
public class FundCreditRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private List<Long> ids;

}
