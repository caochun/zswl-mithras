package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalAddREQ {

    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "不得为空")
    private String creditCode;



}
