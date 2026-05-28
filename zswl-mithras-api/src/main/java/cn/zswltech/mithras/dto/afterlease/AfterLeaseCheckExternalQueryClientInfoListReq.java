package cn.zswltech.mithras.dto.afterlease;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 租后检查外部查询承租人/担保人信息
 * @author zhaozhengkang
 * @date 2022-11-17
 */
@Data
@ApiModel("租后检查外部查询承租人/担保人信息列表-请求体")
public class AfterLeaseCheckExternalQueryClientInfoListReq extends PageReq {

    @ApiModelProperty("所属查询id")
    private Long queryId;

}
