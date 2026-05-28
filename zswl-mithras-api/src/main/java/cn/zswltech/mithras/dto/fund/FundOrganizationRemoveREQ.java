package cn.zswltech.mithras.dto.fund;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 资金管理-机构表
 * @author zhaozhengkang
 * @date 2022-12-13
 */
@Data
@ApiModel("资金管理-机构表删除-请求体")
public class FundOrganizationRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private List<Long> ids;

}
