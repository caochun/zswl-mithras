package cn.zswltech.mithras.blackgray.vo;

import cn.zswltech.mithras.blackgray.excel.Excel;
import lombok.Data;

@Data
public class BlackGrayGroupExportVO {

    @Excel(name = "集团名称")
    private String groupName;

    @Excel(name = "统一社会信用代码")
    private String groupCreditCode;

    @Excel(name = "下属企业在库数")
    private Integer entCount = 0;

    @Excel(name = "黑灰标识")
    private String blackGrayType;
}
