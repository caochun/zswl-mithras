package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vico
 * @description 资金管理-融资管理-ftp收益表
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益表列表-返回体")
public class FtpIncomeBaseInfoListRSP {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty(value = "融资id")
    private Long fundFinancingId;

    /**
     * 融资类型 直融 or 间融 financingtypeenum
     */
    @ApiModelProperty(value = "融资类型 直融 or 间融 financingtypeenum")
    private String financingType;

    /**
     * 融资编号
     */
    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    /**
     * 融资机构id
     */
    @ApiModelProperty(value = "融资机构id")
    private List<Long> organizationId;

    /**
     * 融资机构名称
     */
    @ApiModelProperty(value = "融资机构名称")
    private List<String> organizationName;

    /**
     * 融资金额
     */
    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    /**
     * ftp收益率
     */
    @ApiModelProperty(value = "ftp收益率")
    private Long ftpYieldRate;

    @ApiModelProperty(value = "当年ftp收益率")
    private Long ftpIncomeCurrentYear;

    @ApiModelProperty(value = "当月ftp收益率")
    private Long ftpIncomeCurrentMonth;

    /**
     * 资金经理id
     */
    @ApiModelProperty(value = "资金经理id")
    private Long fundManagerId;

    @ApiModelProperty(value = "资金经理Name")
    private String fundManagerName;

    private LocalDateTime updateTime;

}
