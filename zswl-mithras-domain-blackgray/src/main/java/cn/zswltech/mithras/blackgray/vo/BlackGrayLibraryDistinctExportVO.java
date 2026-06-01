package cn.zswltech.mithras.blackgray.vo;

import cn.zswltech.mithras.blackgray.excel.Excel;
import lombok.Data;

@Data
public class BlackGrayLibraryDistinctExportVO {

    private Long id;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @Excel(name = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    @Excel(name = "黑灰标识")
    private String blackGrayType;
}
