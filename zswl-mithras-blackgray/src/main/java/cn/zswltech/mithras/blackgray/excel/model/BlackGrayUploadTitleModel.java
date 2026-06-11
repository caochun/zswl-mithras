package cn.zswltech.mithras.blackgray.excel.model;

import cn.zswltech.mithras.blackgray.excel.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BlackGrayUploadTitleModel {

    @Excel(name = "企业名称", width = 30)
    private String enterpriseName;

    /**
     * 统一社会信用代码
     *//*
    @Excel(name = "统一社会信用代码", width = 30)
    private String unifiedSocialCreditCode;

    @Excel(name = "黑灰标识", combo = {"黑名单", "灰名单"})
    private String blackGrayType;*/

  /*  @Excel(name = "建议禁止/受限制准入业务", combo = {"涉信类", "保险类", "通用类"})
    private String businessType;*/

    @Excel(name = "禁止准入业务及原因", width = 50)
    private String applyReasonType;

    @Excel(name = "入库日期")
    private Date warehouseTime;

    @Excel(name = "业务规模(万元)", scale = 2)
    private BigDecimal riskScale;

    @Excel(name = "是否上报金控", combo = {"上报", "不上报"})
    private String reportFlag;

}
