package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 租金催收首页列表请求体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表请求体")
@Data
public class RentCollectionListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("合同ID列表")
    private List<Long> contractIds;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("收款日期从")
    private LocalDate planCollectionDateFrom;

    @ApiModelProperty("收款日期到")
    private LocalDate planCollectionDateTo;

    @ApiModelProperty("过滤条件枚举：HIDE_FINISH：隐藏收款完成项；NOT_NOTICE_YET：只看未通知；OVERDUE：只看逾期；ALL：显示全部")
    private String filterConditionType;

    @ApiModelProperty("合同状态")
    private String contractStatus;

}
