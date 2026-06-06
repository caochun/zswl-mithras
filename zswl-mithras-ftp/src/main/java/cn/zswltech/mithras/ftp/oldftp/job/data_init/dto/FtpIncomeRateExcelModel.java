package cn.zswltech.mithras.ftp.oldftp.job.data_init.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/21 16:16
 **/
@Data
public class FtpIncomeRateExcelModel {

    @Alias("融资编号")
    private String financeCode;

    @Alias("银行")
    private String bank;

    @Alias("FTP收益率")
    private String ftpIncomeRate;

    @Alias("业务类型")
    private String bizType;
}
