package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 09:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("放款材料清单-返回体")
public class PaymentMaterialsListRsp extends CommonFileSortWeight {
    @ApiModelProperty("材料文件id")
    private Long id;
    @ApiModelProperty("材料类型")
    private String materialsType;
    @ApiModelProperty("材料名称")
    private String fileName;
    @ApiModelProperty("上传人id")
    private Long createBy;
    @ApiModelProperty("上传人")
    private String creator;
    @ApiModelProperty("上传时间")
    private String createTime;
    @ApiModelProperty("上传时间戳")
    private long createTimestamp;

    @Override
    protected String sortKey() {
        return this.fileName;
    }

    @Override
    public long createTimestamp() {
        return this.createTimestamp;
    }
}