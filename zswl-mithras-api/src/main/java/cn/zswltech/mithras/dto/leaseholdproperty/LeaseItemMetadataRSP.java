package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description
 */
@Data
public class LeaseItemMetadataRSP {
    @ApiModelProperty("租赁物类型")
    private List<String> leaseItemTypes;

    @ApiModelProperty("权属文件类型")
    private List<String> ownershipFileTypes;

    @ApiModelProperty("价值认定文件")
    private List<String> valueIdentificationFiles;
}
