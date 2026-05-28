package cn.zswltech.mithras.dto.contract.leaseitem;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description
 */
@Data
public class ContractPreChooseLeaseItemRSP {
    @ApiModelProperty("租赁物审核管理id")
    private Long leaseItemInfoId;

    @ApiModelProperty("表头")
    private List<String> headerList;

    @ApiModelProperty("租赁物分页列表")
    private PageR<RowDataModel> pageList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RowDataModel {
        @ApiModelProperty("单条数据id")
        private Long itemId;

        @ApiModelProperty("是否已被选择")
        private Integer hasChoose;

        @ApiModelProperty("是否可被选择")
        private Integer canChoose;

        @ApiModelProperty("租赁物数据")
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
