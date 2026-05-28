package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
@Data
public class LeaseItemListRSP {
//    @ApiModelProperty("租赁成本")
//    @Deprecated
//    private Long totalCost;
//
//    /**
//     * @deprecated  已经弃用
//     */
//    @ApiModelProperty("租赁物总金额")
//    @Deprecated
//    private Long leaseItemTotalAmount;

    @ApiModelProperty("表头")
    private List<String> headerList;

    @ApiModelProperty("数据")
    private PageR<RowDataModel> pageList;

    @ApiModelProperty("Ocr是否上传过文件")
    private Boolean uploadedOcrFile;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RowDataModel {
        private Long itemId;
        private String matchColumns;
        private List<CellDataModel> dataList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CellDataModel {
        private String key;
        private Object value;
    }
}
