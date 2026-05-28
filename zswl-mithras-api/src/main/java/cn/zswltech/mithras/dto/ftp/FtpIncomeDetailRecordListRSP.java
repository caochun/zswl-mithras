package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @description 资金管理-融资管理-ftp收益记录表
 * @author vico
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益记录表列表-返回体")
public class FtpIncomeDetailRecordListRSP {

    @ApiModelProperty(value = "ftp基本信息 id")
    private Long ftpIncomeId;

    @ApiModelProperty(value = "产品名称")
    private String abbreviation;

    private List<IncomeDetailRecordListBody> bodys;

    private IncomeDetailRecordListBody count;

    @Data
    public static class IncomeDetailRecordListBody {
        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private Long id;

        /**
         * 计息日期
         */
        @ApiModelProperty(value = "计息日期")
        private LocalDate interestDate;

        /**
         * 剩余本金
         */
        @ApiModelProperty(value = "剩余本金")
        private Long remainingPrincipal;

        /**
         * ftp收益率
         */
        @ApiModelProperty(value = "ftp收益率")
        private Long ftpYieldRate;

        /**
         * ftp日收益率
         */
        @ApiModelProperty(value = "ftp日收益率")
        private BigDecimal ftpYieldRateDay;

        /**
         * ftp收益
         */
        @ApiModelProperty(value = "ftp收益")
        private Long ftpIncome;

        /**
         * ftp收益
         */
        @ApiModelProperty(value = "ftp当年累计收益")
        private Long ftpIncomeCurrentYear;
    }


    }
