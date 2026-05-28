package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @description 金融局报送-对外融资信息清单表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("金融局报送-对外融资信息清单表编辑-请求体")
public class AssociationExternalFinancingModifyREQ {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
    * 行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）
    */
    @ApiModelProperty(value = "行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @ApiModelProperty(value = "企业统一社会信用代码")
    private String unifSociCredCode;

    /**
    * 序号
    */
    @ApiModelProperty(value = "序号")
    private String onum;

    /**
    * 借款余额 | 单位：万元
    */
    @ApiModelProperty(value = "借款余额 | 单位：万元")
    private BigDecimal loanBal;

    /**
    * 融资业务类型 | 数据字典：evt00052
    */
    @ApiModelProperty(value = "融资业务类型 | 数据字典：evt00052")
    private String finBusiTypeCode;

    /**
    * 资金提供方
    */
    @ApiModelProperty(value = "资金提供方")
    private String cptlProv;

    /**
    * 融资利率
    */
    @ApiModelProperty(value = "融资利率")
    private BigDecimal finIntr;

    /**
    * 融资借款日期
    */
    @ApiModelProperty(value = "融资借款日期")
    private String finLoanDate;

    /**
    * 融资到期日期
    */
    @ApiModelProperty(value = "融资到期日期")
    private String finMatuDate;

    /**
    * 报表实例编号 | 格式：uuid
    */
    @ApiModelProperty(value = "报表实例编号 | 格式：uuid")
    private String reportInstanceId;

}
