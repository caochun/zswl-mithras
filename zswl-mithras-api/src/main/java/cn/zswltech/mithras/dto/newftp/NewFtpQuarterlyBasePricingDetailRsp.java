package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 季度指导基础定价
 * @date 2023-05-21
 */
@Data
@ApiModel("季度指导基础定价列表-返回体")
public class NewFtpQuarterlyBasePricingDetailRsp extends ListBaseRSP {
    @ApiModelProperty(value = "第一行")
    List<FtpValue> row1;
    @ApiModelProperty(value = "第二行")
    List<FtpValue> row2;
    @ApiModelProperty(value = "第三行")
    List<FtpValue> row3;
    @ApiModelProperty(value = "第四行")
    List<FtpValue> row4;
    @ApiModelProperty(value = "第五行")
    List<FtpValue> row5;
    @ApiModelProperty(value = "第六行")
    List<FtpValue> row6;
    @ApiModelProperty(value = "第七行")
    List<FtpValue> row7;
    @ApiModelProperty(value = "第八行")
    List<FtpValue> row8;
    @ApiModelProperty(value = "第九行")
    List<FtpValue> row9;
}
