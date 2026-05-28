package cn.zswltech.mithras.blackgray.dto.excel;

import cn.zswltech.mithras.blackgray.excel.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @description 黑灰名单记录表导出
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表出库记录导出-返回体")
public class BlackGrayOutboundRecordExport {

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
     * 所属机构
     */
    @Excel(name = "所属机构")
    private String applyOrganization;

    /**
     * 业务类型
     */
    @Excel(name = "业务类型")
    private String businessType;


   /* *//**
     * 黑灰标识
     *//*
    @Excel(name = "黑灰标识")
    private String blackGrayType;*/

    /**
     * 出库时间
     */
    @Excel(name = "实际出库时间",dateFormat = "yyyy-MM-dd")
    private Date actualOutboundTime;

    @Excel(name = "更新时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
