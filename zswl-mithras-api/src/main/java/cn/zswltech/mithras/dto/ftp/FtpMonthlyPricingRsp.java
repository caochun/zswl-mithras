package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 月度ftp定价指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度ftp定价指导列表-返回体")
public class FtpMonthlyPricingRsp extends ListBaseRSP {

    @ApiModelProperty(value = "所属指引id")
    private Long guidanceId;

    @ApiModelProperty(value = "企业类型")
    private String enterpriseType;

    @ApiModelProperty(value = "期限")
    private String creditTerm;

    @ApiModelProperty(value = "项目分类")
    private String projectClassify;

    @ApiModelProperty(value = "值")
    private Integer value;

}
