package cn.zswltech.mithras.dto.contract.leaseitem;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租赁物清单列表数据-返回体")
public class ContractLeaseItemListRSP extends ListBaseRSP {
//    @ApiModelProperty("租赁物总额")
//    @Deprecated
//    private Long leaseItemTotalAmount;

    @ApiModelProperty("租赁物清单表头")
    private List<String> headerList;

    @ApiModelProperty("租赁物清单")
    private PageR<RowDataModel> pageList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RowDataModel extends ListBaseRSP {
        @ApiModelProperty("租赁物清单单条数据id")
        private Long itemId;

        @ApiModelProperty("租赁物清单单条数据")
        private List<CellDataModel> dataList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CellDataModel {
        private String key;
        private Object value;
    }
//    @ApiModelProperty("账面原值总计")
//    private Long originalBookValueTotal;
//
//    @ApiModelProperty("账面净值总计")
//    private Long originalBookNetValueTotal;
//
//    @ApiModelProperty("评估原值总计")
//    private Long assessedValueTotal;
//
//    @ApiModelProperty("评估净值总计")
//    private Long assessedNetValueTotal;
//
//    private PageR<Data> pageData;
//
//    @EqualsAndHashCode(callSuper = true)
//    @lombok.Data
//    public static class Data extends ListBaseRSP {
//        @ApiModelProperty("序号")
//        private Integer sequence;
//
//        @ApiModelProperty("种类")
//        private String category;
//
//        @ApiModelProperty("识别号类型")
//        private String uniqueIdentifyCodeType;
//
//        @ApiModelProperty("唯一识别号")
//        private String uniqueIdentifyCode;
//
//        @ApiModelProperty("名称")
//        private String name;
//
//        @ApiModelProperty("供应商")
//        private String supplier;
//
//        @ApiModelProperty("数量")
//        private String quantity;
//
//        @ApiModelProperty("计量单位")
//        private String unit;
//
//        @ApiModelProperty("购置日期")
//        private String purchaseDate;
//
//        @ApiModelProperty("账面原值")
//        private Long originalBookValue;
//
//        @ApiModelProperty("账面净值")
//        private Long originalBookNetValue;
//
//        @ApiModelProperty("评估原值")
//        private Long assessedValue;
//
//        @ApiModelProperty("评估净值")
//        private Long assessedNetValue;
//
//        @ApiModelProperty("发票号")
//        private String invoiceCode;
//
//        @ApiModelProperty("存放地点")
//        private String storagePlace;
//    }
}
