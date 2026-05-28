package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_info列表-返回体")
public class FundGuaranteeInfoListRSP {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "担保机构id")
    private Long agencyId;
    @ApiModelProperty(value = "担保编号")
    private String guaranteeCode;
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
    @ApiModelProperty("资料名称")
    private String materialName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty("是都生效")
    private Integer effective;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "文件列表")
    private List<FundMaterialListRSP> fileList;
    @ApiModelProperty(value = "担保时间范围")
    private String guaranteePeriod;
}
