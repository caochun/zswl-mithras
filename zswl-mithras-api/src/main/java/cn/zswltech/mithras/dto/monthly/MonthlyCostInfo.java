package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthlyCostInfo {


    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty("业务类型")
    private String type;

    public MonthlyCostInfo(String organizationName, String type) {
        this.organizationName = organizationName;
        this.type = type;
    }
}
