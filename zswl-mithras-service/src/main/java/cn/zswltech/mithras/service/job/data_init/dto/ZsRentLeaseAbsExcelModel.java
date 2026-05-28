package cn.zswltech.mithras.service.job.data_init.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author Lingma
 * @version 1.0
 * @description ZS租赁ABS Excel模型
 * @since 2025/8/21 16:17
 **/
@Data
public class ZsRentLeaseAbsExcelModel {

    @Alias("序号")
    private String serialNumber;

    @Alias("借款名称")
    private String loanName;

    @Alias("融租易产品名称")
    private String rzyProductName;

    @Alias("融租易融资编号")
    private String rzyFinancingCode;

    @Alias("证券代码")
    private String stockCode;

    @Alias("证券简称")
    private String stockAbbreviation;
}