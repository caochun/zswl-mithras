package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
@Data
@ApiModel("批量id请求体")
public class MultiplePkREQ {
    @NotEmpty(message = "请先选择需要操作的数据")
    @ApiModelProperty("业务数据id列表")
    private List<Long> ids;

}
