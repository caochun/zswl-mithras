package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalQueryCompanyREQ extends PageReq{

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

}
