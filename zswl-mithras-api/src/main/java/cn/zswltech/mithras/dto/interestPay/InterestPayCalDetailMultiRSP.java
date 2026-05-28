package cn.zswltech.mithras.dto.interestPay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class InterestPayCalDetailMultiRSP  {

    @ApiModelProperty(value = "产品名称")
    private String abbreviation;

    private List<InterestPayCalDetailRSP> dates;
}
