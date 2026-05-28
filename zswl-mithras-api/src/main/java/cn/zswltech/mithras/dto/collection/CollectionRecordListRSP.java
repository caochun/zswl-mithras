package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2022-08-16
 **/
@Data
public class CollectionRecordListRSP {

    @ApiModelProperty("记录列表")
    private List<Records> records;

//    @ApiModelProperty("已核销人")
//    private List<String> users;

    @ApiModelProperty("收款记录汇总")
    private Sum sum;

    @Data
    public static class Records {
        @ApiModelProperty("记录id")
        private Long id;

        @ApiModelProperty("编号")
        private Integer sortId;

        @ApiModelProperty("保证金明细Id")
        private String marginId;

        @ApiModelProperty("信息来源")
        private String dataSource;

        @ApiModelProperty("收款类型")
        private String collectionType;

        @ApiModelProperty("收款方式 1票据, 0其他")
        private Integer  collectionWay;

        @ApiModelProperty("实收日期")
        private LocalDate collectionDate;

        @ApiModelProperty("实收金额")
        private Long collectionAmount;

//        @ApiModelProperty("核销状态")
        private String writeOffStatus;

        @ApiModelProperty("是否开票")
        private String invoice;

//        @ApiModelProperty("附言")
//        private String postscript;

        @ApiModelProperty("本金")
        private Long principal;

        @ApiModelProperty("利息")
        private Long interest;

        @ApiModelProperty("罚息")
        private Long penaltyInterest;

        @ApiModelProperty("核销方式 WriteOffTypeEnum")
        private String writeOffType;
        private LocalDateTime createTime;
        private Long createBy;
        private LocalDateTime updateTime;
        private Long updateBy;
        private String bankDetailNo;
        @ApiModelProperty(value = "deduction_margin_base_id")
        private Long deductionMarginBaseId;
        //抵扣保证金编号
        @ApiModelProperty(value = "deduction_margin_base_code")
        private String deductionMarginBaseCode;
        @ApiModelProperty("抵扣合同编号")
        private String contractCode;
    }

    @Data
    public static class Sum{
        @ApiModelProperty("实收金额")
        private Long collectionAmount;

        @ApiModelProperty("本金")
        private Long principal;

        @ApiModelProperty("利息")
        private Long interest;

        @ApiModelProperty("罚息")
        private Long penaltyInterest;
    }
}
