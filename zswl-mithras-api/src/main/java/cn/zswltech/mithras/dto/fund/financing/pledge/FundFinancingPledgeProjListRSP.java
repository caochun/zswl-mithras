package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/20 2:57 下午
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("融资管理-根据业务部门id获取项目列表-返回体")
public class FundFinancingPledgeProjListRSP {

    /**
     * 项目评审id
     */
    @ApiModelProperty("项目评审id")
    private Long projReviewId;
    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String projName;

}
