package cn.zswltech.mithras.contract.overdue.application.query;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 10:48
 */
@Data
public class CollectionPageQuery extends PageReq {

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "业务部门")
    private Long bizDept;

    @ApiModelProperty(value = " 项目主办")
    private Long projectSponsor;

    @ApiModelProperty(value = "逾期")
    private Boolean overdue;

    private Boolean isBizUser;

    private List<Long> deptIdList;

    private Long currentUserId;
}
