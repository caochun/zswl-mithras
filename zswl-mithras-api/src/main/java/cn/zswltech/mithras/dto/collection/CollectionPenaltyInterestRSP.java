package cn.zswltech.mithras.dto.collection;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2022-08-22
 **/
@Data
@ApiModel("罚息信息-返回体")
public class CollectionPenaltyInterestRSP {

    @ApiModelProperty("罚息日率")
    private Integer dailyRate;

    @ApiModelProperty("罚息金额")
    private Long penaltyInterestAmount;

    @ApiModelProperty("累计罚息")
    private Long sumAmount;

    @ApiModelProperty("核销罚息")
    private Long writeOffAmount;

    @ApiModelProperty("罚息余额")
    private Long lastAmount;

    @ApiModelProperty(value = "罚息减免金额")
    private Long creditAmount;

//    @ApiModelProperty("备注")
//    private String comment;
}
