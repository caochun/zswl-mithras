package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author luyi
 */
@Data
@ApiModel("承租人-公海客户")
public class CustomerRSP {
    @ApiModelProperty("客户id")
    private Long customerId ;
    @ApiModelProperty("客户姓名")
    private String customerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerRSP user = (CustomerRSP) o;
        return Objects.equals(customerId, user.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

}
