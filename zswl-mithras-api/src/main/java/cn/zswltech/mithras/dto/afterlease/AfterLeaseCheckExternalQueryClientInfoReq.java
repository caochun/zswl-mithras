package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 租后检查外部查询承租人/担保人信息
 * @author zhaozhengkang
 * @date 2022-11-17
 */
@Data
@ApiModel("外部查询承租人/担保人信息客户详情-请求体")
public class AfterLeaseCheckExternalQueryClientInfoReq extends PageReq {

    @ApiModelProperty("所属查询id")
    private Long clientId;

}
