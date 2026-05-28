package cn.zswltech.mithras.dto.finance.accountage;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表列表-请求体")
public class FinanceAccountAgeBaseInfoListREQ extends PageReq {

    @ApiModelProperty(value = "状态")
    private String status;

}
