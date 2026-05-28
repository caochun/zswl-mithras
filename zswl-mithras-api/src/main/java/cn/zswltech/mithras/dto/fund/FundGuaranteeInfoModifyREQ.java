package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Data
@ApiModel("担保信息编辑-请求体")
public class FundGuaranteeInfoModifyREQ {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "总担保额度")
    private Long totalGuaranteeLimit;
    @ApiModelProperty(value = "担保生效时间from")
    private LocalDate effectiveTimeFrom;
    @ApiModelProperty(value = "担保生效时间to")
    private LocalDate effectiveTimeTo;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "删除文件id")
    private List<Long> delFileIds;
    @ApiModelProperty(value = "1生效/0失效")
    private Integer effective;
}
