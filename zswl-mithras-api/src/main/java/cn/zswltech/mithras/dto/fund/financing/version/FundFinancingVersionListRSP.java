package cn.zswltech.mithras.dto.fund.financing.version;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 融资管理版本返回体
 * @author: jackerhe
 * @date: 2023/2/24 2:30 下午
 **/
@Data
@ApiModel("融资管理版本返回体")
public class FundFinancingVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
