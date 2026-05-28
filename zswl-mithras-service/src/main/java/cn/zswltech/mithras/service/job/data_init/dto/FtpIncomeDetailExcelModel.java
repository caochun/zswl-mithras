package cn.zswltech.mithras.service.job.data_init.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/21 16:17
 **/
@Data
public class FtpIncomeDetailExcelModel {

    @Alias("融资余额")
    private String balance;

    @Alias("还款本金")
    private String repayPrincipal;

    @Alias("融资编号")
    private String financeCode;

    @Alias("借款名称")
    private String loanName;

    @Alias("日期")
    private String dealTime;

    @Alias("FTP收益")
    private String ftpIncome;

    @Alias("FTP收益率")
    private String ftpIncomeRate;

}
