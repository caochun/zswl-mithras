package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description
 */
@Data
public class LeaseItemListExportREQ {
    @NotNull(message = "租赁物审核管理id不能为空")
    @ApiModelProperty("租赁物审核管理id")
    private Long id;

    @ApiModelProperty("租赁物清单指定数据id")
    private List<Long> itemIds;
}
