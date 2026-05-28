package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailDynamicTableRSP {
    @ApiModelProperty("表头行")
    private List<String> headerList;

    @ApiModelProperty("数据行")
    private List<List<Object>> dataList;

    @ApiModelProperty("FTP价格")
    private Integer ftp;
}
