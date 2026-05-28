package cn.zswltech.mithras.dto.client.shareholder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@Data
@ApiModel("股东信息删除-请求体")
public class CorpShareholderInfoRemoveREQ {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("clientId")
    private Long clientId;

    @ApiModelProperty("股东姓名")
    private String shareholderName;
}
