package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Data
@ApiModel("保单暂存表c详情-请求体")
public class PolicyInfoTmpDetailREQ{

    @NotNull
    private Long parentPolicyId;

}
