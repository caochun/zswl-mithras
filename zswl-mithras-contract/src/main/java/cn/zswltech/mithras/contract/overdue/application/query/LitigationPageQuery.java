package cn.zswltech.mithras.contract.overdue.application.query;

import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.contract.enums.overdue.LitigationStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:51
 */
@Data
public class LitigationPageQuery extends PageReq {
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "合同code")
    private String contractCodes;

    @ApiModelProperty(value = "诉讼状态")
    private LitigationStatus status;

    @ApiModelProperty(value = "诉讼登记编号")
    private String code;

    private Boolean isBizUser;

    private List<Long> deptIdList;

    private Long currentUserId;
}
