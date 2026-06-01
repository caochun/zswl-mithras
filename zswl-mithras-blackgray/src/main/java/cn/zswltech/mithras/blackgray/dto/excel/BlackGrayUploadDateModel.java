package cn.zswltech.mithras.blackgray.dto.excel;

import cn.zswltech.mithras.blackgray.excel.Excel;
import lombok.Data;

@Data
public class BlackGrayUploadDateModel {

    @Excel(name = "规则名称", width = 30)
    private String ruleName;

    @Excel(name = "规则编号", width = 30)
    private String ruleNumber;

    @Excel(name = "黑灰标识", width = 30)
    private String blackGrayType;

    @Excel(name = "业务类型", width = 30)
    private String suitBusiness;

    @Excel(name = "适用机构", width = 30)
    private String suitOrg;

}
