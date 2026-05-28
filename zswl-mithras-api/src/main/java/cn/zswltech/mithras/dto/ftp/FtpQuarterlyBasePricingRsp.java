package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ftp_quarterly_base_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("sheet1数据-返回体")
public class FtpQuarterlyBasePricingRsp extends ListBaseRSP {

    @ApiModelProperty(value = "所属指引id")
    private Long guidanceId;

    @ApiModelProperty(value = "孟月(FIRST_MONTH)、仲月(SECOND_MONTH)、季月(THIRD_MONTH)、均值(MEAN_VALUE)，见枚举monthType")
    private String monthType;

    @ApiModelProperty(value = "国有(STATE_OWNED)、民营上市企业(PRIVATE_LISTED)、民营非上市企业(PRIVATE_NON_LISTED)、其他(OTHER)，见枚举enterpriseType")
    private String enterpriseType;

    @ApiModelProperty(value = "1年期(ONE_YEAR)、1-3年(ONE_TO_THREE_YEARS)、3年以上(MORE_THAN_THREE_YEARS)，见枚举creditTerm")
    private String creditTerm;

    @ApiModelProperty(value = "项目分类,枚举projectClassify，ENCOURAGEMENT(鼓励类),MODERATE_SUPPORT(适度支持类),CAUTIOUS(谨慎类),CONSTRUCTION_MACHINERY(工程机械类（厂商担保模式）),INTRA_GROUP_COLLABORATION(集团内协同业务)")
    private String projectClassify;

    @ApiModelProperty(value = "利率值，展示需除以10000")
    private Integer percentValue;

}
