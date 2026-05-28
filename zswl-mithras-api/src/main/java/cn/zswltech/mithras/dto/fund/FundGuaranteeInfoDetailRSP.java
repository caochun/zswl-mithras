package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 13:57
 */
@ApiModel("担保信息详情响应体")
@Data
public class FundGuaranteeInfoDetailRSP {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "担保机构id")
    private Long agencyId;
    @ApiModelProperty(value = "总担保额度")
    private Long totalGuaranteeLimit;
    @ApiModelProperty(value = "已使用额度")
    private Long usedGuaranteeLimit;
    @ApiModelProperty(value = "剩余额度")
    private Long remainingGuaranteeLimit;
    @ApiModelProperty(value = "担保生效时间from")
    private LocalDate effectiveTimeFrom;
    @ApiModelProperty(value = "担保生效时间to")
    private LocalDate effectiveTimeTo;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "生效/失效")
    private Integer effective;
    @ApiModelProperty("相关资料")
    private List<FundMaterialListRSP> fileListRSP;
}
