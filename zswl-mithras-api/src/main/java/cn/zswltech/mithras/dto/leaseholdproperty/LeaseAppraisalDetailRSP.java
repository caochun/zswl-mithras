package cn.zswltech.mithras.dto.leaseholdproperty;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalDetailRSP {

    @ApiModelProperty(value = "id")
    private Long companyId;

    @ApiModelProperty(value = "评估机构")
    private String companyName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "营业许可证到期日")
    private LocalDate bizLicenseEndDate;

    @ApiModelProperty(value = "营业许可证是否为长期")
    private Boolean bizLicenceLongTerm;

    @ApiModelProperty(value = "业务范围")
    private String bizScope;

    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;

    /**
     * LeaseAppraisalPurposeEnum
     */
    @ApiModelProperty(value = "用途")
    private String purpose;

    /**
     * LeaseAppraisalSelectEnum
     */
    @ApiModelProperty(value = "是否被选中")
    private String selected;
}


