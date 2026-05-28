package cn.zswltech.mithras.dto.fund;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description fund_guarantee_agency
 * @author zhaozhengkang
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_agency删除-请求体")
public class FundGuaranteeAgencyRemoveREQ {

    @NotNull
    @ApiModelProperty("ids")
    private List<Long> ids;

}
