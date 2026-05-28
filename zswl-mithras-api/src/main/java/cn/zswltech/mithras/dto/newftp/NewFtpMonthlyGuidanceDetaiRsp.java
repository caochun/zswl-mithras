package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp报价表列表-返回体")
public class NewFtpMonthlyGuidanceDetaiRsp extends ListBaseRSP {

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
    @ApiModelProperty(value = "第十行")
    List<FtpValue> row10;
    @ApiModelProperty(value = "第十一行")
    List<FtpValue> row11;
    @ApiModelProperty(value = "第十二行")
    List<FtpValue> row12;
    @ApiModelProperty(value = "第十三行")
    List<FtpValue> row13;
    @ApiModelProperty(value = "第十四行")
    List<FtpValue> row14;
    @ApiModelProperty(value = "第十五行")
    List<FtpValue> row15;
    @ApiModelProperty(value = "第十六行")
    List<FtpValue> row16;
    @ApiModelProperty(value = "第十七行")
    List<FtpValue> row17;
    @ApiModelProperty(value = "第十八行")
    List<FtpValue> row18;
    @ApiModelProperty(value = "第十九行")
    List<FtpValue> row19;
    @ApiModelProperty(value = "第二十行")
    List<FtpValue> row20;
    @ApiModelProperty(value = "第二十一行")
    List<FtpValue> row21;
}

