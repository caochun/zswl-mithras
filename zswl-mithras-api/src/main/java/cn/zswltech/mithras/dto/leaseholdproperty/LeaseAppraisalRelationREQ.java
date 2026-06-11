package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalRelationREQ {

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "不得为空")
    private Long leaseItemId;

    @ApiModelProperty(value = "列表信息")
    private List<AppraisalCompanyRelationREQ> relationList;

    @Data
    public static class AppraisalCompanyRelationREQ{

        @ApiModelProperty(value = "评估机构id")
        @NotNull(message = "不得为空")
        private Long companyId;

        @ApiModelProperty(value = "用途")
        private String purpose;

        @ApiModelProperty(value = "是否被选中")
        private String selectType;

        @ApiModelProperty(value = "是否白名单准入")
        private Integer isWhitelist;
    }


}
