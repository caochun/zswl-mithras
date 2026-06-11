package cn.zswltech.mithras.blackgray.excel.model;

import cn.zswltech.mithras.blackgray.excel.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 黑灰名单记录表导出
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-返回体")
public class BlackGrayWarehouseRecordExport {

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

    /**
     * 业务类型
     */
    @Excel(name = "业务类型")
    private String businessType;

    /**
     * 黑灰标识
     */
    @Excel(name = "黑灰标识")
    private String blackGrayType;

    @Excel(name = "入库日期",dateFormat = "yyyy-MM-dd")
    private Date warehouseTime;

    @Excel(name = "观察期")
    private String periodUnderObservation;

    /**
     * 申请原因
     */
    @Excel(name = "入库原因")
    private String applyReason;


    @Excel(name = "业务规模", scale = 2)
    private BigDecimal riskScale;

    /**
     * 所属集团
     */
    @Excel(name = "所属集团")
    private String membershipGroup;


    /**
     * 集团黑灰标识
     */
    @Excel(name = "所属集团黑灰标识")
    private String groupBlackGrayType;

/*

    */
/**
     * 报告机构
     *//*

    @Excel(name = "报告机构")
    private String applyOrganization;


    */
/**
     * 出库时间
     *//*

    @Excel(name = "计划出库时间",dateFormat = "yyyy-MM-dd")
    private Date planOutboundTime;
*/



}
