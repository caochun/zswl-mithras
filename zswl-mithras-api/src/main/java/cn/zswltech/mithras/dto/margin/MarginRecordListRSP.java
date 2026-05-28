package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
public class MarginRecordListRSP {
    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("保证金明细code")
    private String marginCode;

    @ApiModelProperty("编号")
    private String sortId;

    @ApiModelProperty("信息来源")
    private String dataSource;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("应收日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty("应收金额")
    private Long planCollectionAmount;

    @ApiModelProperty("实收日期")
    private LocalDate collectionDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("抵扣合同编号")
    private String contractCode;

//    @ApiModelProperty("附言")
//    private String postscript;

//    @ApiModelProperty("核销")
//    private String writeOff;
//
//    @ApiModelProperty("复核")
//    private String review;
}
