package cn.zswltech.mithras.dto.interestpay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class InterestPayCalDetailMultiRSP  {

    @ApiModelProperty(value = "产品名称")
    private String abbreviation;

    private List<InterestPayCalDetailRSP> dates;
}
