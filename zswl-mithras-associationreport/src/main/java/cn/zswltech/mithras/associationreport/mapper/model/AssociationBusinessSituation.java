package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 业务情况表
 * @author vico
 * @date 2025-04-18
 */
@Data
public class AssociationBusinessSituation extends BasicAssociationReport implements Serializable , IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 自增主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 行号 | 同一批次数据从1开始递增
    */
    @TableField("row_num")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @TableField("unif_soci_cred_code")
    private String unifSociCredCode;

    /**
    * 总收入_期初数（万元）
    */
    @TableField("tot_incm_abop")
    private BigDecimal totIncmAbop;

    /**
    * 总收入_本期发生额（万元）
    */
    @TableField("tot_incm_aotc")
    private BigDecimal totIncmAotc;

    /**
    * 总收入_期末数（万元）
    */
    @TableField("tot_incm_aeop")
    private BigDecimal totIncmAeop;

    /**
    * 经营租赁业务收入_期初数（万元）
    */
    @TableField("oper_leas_busi_incm_abop")
    private BigDecimal operLeasBusiIncmAbop;

    /**
    * 经营租赁业务收入_本期发生额（万元）
    */
    @TableField("oper_leas_busi_incm_aotc")
    private BigDecimal operLeasBusiIncmAotc;

    /**
    * 经营租赁业务收入_期末数（万元）
    */
    @TableField("oper_leas_busi_incm_aeop")
    private BigDecimal operLeasBusiIncmAeop;

    /**
    * 融资租赁业务收入_期初数（万元）
    */
    @TableField("fnl_busi_incm_abop")
    private BigDecimal fnlBusiIncmAbop;

    /**
    * 融资租赁业务收入_本期发生额（万元）
    */
    @TableField("fnl_busi_incm_aotc")
    private BigDecimal fnlBusiIncmAotc;

    /**
    * 融资租赁业务收入_期末数（万元）
    */
    @TableField("fnl_busi_incm_aeop")
    private BigDecimal fnlBusiIncmAeop;

    /**
    * 利息收入_期初数（万元）
    */
    @TableField("intr_incm_abop")
    private BigDecimal intrIncmAbop;

    /**
    * 利息收入_本期发生额（万元）
    */
    @TableField("intr_incm_aotc")
    private BigDecimal intrIncmAotc;

    /**
    * 利息收入_期末数（万元）
    */
    @TableField("intr_incm_aeop")
    private BigDecimal intrIncmAeop;

    /**
    * 费用收入_期初数（万元）
    */
    @TableField("fee_incm_abop")
    private BigDecimal feeIncmAbop;

    /**
    * 费用收入_本期发生额（万元）
    */
    @TableField("fee_incm_aotc")
    private BigDecimal feeIncmAotc;

    /**
    * 费用收入_期末数（万元）
    */
    @TableField("fee_incm_aeop")
    private BigDecimal feeIncmAeop;

    /**
    * 其他收入_期初数（万元）
    */
    @TableField("oth_incm_abop")
    private BigDecimal othIncmAbop;

    /**
    * 其他收入_本期发生额（万元）
    */
    @TableField("oth_incm_aotc")
    private BigDecimal othIncmAotc;

    /**
    * 其他收入_期末数（万元）
    */
    @TableField("oth_incm_aeop")
    private BigDecimal othIncmAeop;

    /**
    * 租赁资产_期初数（万元）
    */
    @TableField("leas_ast_abop")
    private BigDecimal leasAstAbop;

    /**
    * 租赁资产_本期发生额（万元）
    */
    @TableField("leas_ast_aotc")
    private BigDecimal leasAstAotc;

    /**
    * 租赁资产_期末数（万元）
    */
    @TableField("leas_ast_aeop")
    private BigDecimal leasAstAeop;

    /**
    * 经营性租赁资产_期初数（万元）
    */
    @TableField("oper_leas_ast_abop")
    private BigDecimal operLeasAstAbop;

    /**
    * 经营性租赁资产_本期发生额（万元）
    */
    @TableField("oper_leas_ast_aotc")
    private BigDecimal operLeasAstAotc;

    /**
    * 经营性租赁资产_期末数（万元）
    */
    @TableField("oper_leas_ast_aeop")
    private BigDecimal operLeasAstAeop;

    /**
    * 融资租赁资产_期初数（万元）
    */
    @TableField("fin_leas_ast_abop")
    private BigDecimal finLeasAstAbop;

    /**
    * 融资租赁资产_本期发生额（万元）
    */
    @TableField("fin_leas_ast_aotc")
    private BigDecimal finLeasAstAotc;

    /**
    * 融资租赁资产_期末数（万元）
    */
    @TableField("fin_leas_ast_aeop")
    private BigDecimal finLeasAstAeop;

    /**
    * 直接租赁资产_期初数（万元）
    */
    @TableField("dirt_leas_ast_abop")
    private BigDecimal dirtLeasAstAbop;

    /**
    * 直接租赁资产_本期发生额（万元）
    */
    @TableField("dirt_leas_ast_aotc")
    private BigDecimal dirtLeasAstAotc;

    /**
    * 直接租赁资产_期末数（万元）
    */
    @TableField("dirt_leas_ast_aeop")
    private BigDecimal dirtLeasAstAeop;

    /**
    * 售后回租资产_期初数（万元）
    */
    @TableField("slbk_ast_abop")
    private BigDecimal slbkAstAbop;

    /**
    * 售后回租资产_本期发生额（万元）
    */
    @TableField("slbk_ast_aotc")
    private BigDecimal slbkAstAotc;

    /**
    * 售后回租资产_期末数（万元）
    */
    @TableField("slbk_ast_aeop")
    private BigDecimal slbkAstAeop;

    /**
    * 跨省融资租赁资产余额_期初数（万元）
    */
    @TableField("iprv_fnl_ast_bal_abop")
    private BigDecimal iprvFnlAstBalAbop;

    /**
    * 跨省融资租赁资产余额_本期发生额（万元）
    */
    @TableField("iprv_fnl_ast_bal_aotc")
    private BigDecimal iprvFnlAstBalAotc;

    /**
    * 跨省融资租赁资产余额_期末数（万元）
    */
    @TableField("iprv_fnl_ast_bal_aeop")
    private BigDecimal iprvFnlAstBalAeop;

    /**
    * 跨省售后回租资产余额_期初数（万元）
    */
    @TableField("iprv_slbk_ast_bal_abop")
    private BigDecimal iprvSlbkAstBalAbop;

    /**
    * 跨省售后回租资产余额_本期发生额（万元）
    */
    @TableField("iprv_slbk_ast_bal_aotc")
    private BigDecimal iprvSlbkAstBalAotc;

    /**
    * 跨省售后回租资产余额_期末数（万元）
    */
    @TableField("iprv_slbk_ast_bal_aeop")
    private BigDecimal iprvSlbkAstBalAeop;

    /**
    * 融资租赁投放额_期初数（万元）
    */
    @TableField("fnl_rels_abop")
    private BigDecimal fnlRelsAbop;

    /**
    * 融资租赁投放额_本期发生额（万元）
    */
    @TableField("fnl_rels_aotc")
    private BigDecimal fnlRelsAotc;

    /**
    * 融资租赁投放额_期末数（万元）
    */
    @TableField("fnl_rels_aeop")
    private BigDecimal fnlRelsAeop;

    /**
    * 直接租赁投放额_期初数（万元）
    */
    @TableField("dirt_leas_rels_abop")
    private BigDecimal dirtLeasRelsAbop;

    /**
    * 直接租赁投放额_本期发生额（万元）
    */
    @TableField("dirt_leas_rels_aotc")
    private BigDecimal dirtLeasRelsAotc;

    /**
    * 直接租赁投放额_期末数（万元）
    */
    @TableField("dirt_leas_rels_aeop")
    private BigDecimal dirtLeasRelsAeop;

    /**
    * 售后回租投放额_期初数（万元）
    */
    @TableField("slbk_rels_abop")
    private BigDecimal slbkRelsAbop;

    /**
    * 售后回租投放额_本期发生额（万元）
    */
    @TableField("slbk_rels_aotc")
    private BigDecimal slbkRelsAotc;

    /**
    * 售后回租投放额_期末数（万元）
    */
    @TableField("slbk_rels_aeop")
    private BigDecimal slbkRelsAeop;

    /**
    * 固定收益类证券投资余额_期初数（万元）
    */
    @TableField("fix_payf_scr_ivsm_abop")
    private BigDecimal fixPayfScrIvsmAbop;

    /**
    * 固定收益类证券投资余额_本期发生额（万元）
    */
    @TableField("fix_payf_scr_ivsm_aotc")
    private BigDecimal fixPayfScrIvsmAotc;

    /**
    * 固定收益类证券投资_期末数（万元）
    */
    @TableField("fix_payf_scr_ivsm_aeop")
    private BigDecimal fixPayfScrIvsmAeop;

    /**
    * 国债余额_期初数（万元）
    */
    @TableField("trea_abop")
    private BigDecimal treaAbop;

    /**
    * 国债余额_本期发生额（万元）
    */
    @TableField("trea_aotc")
    private BigDecimal treaAotc;

    /**
    * 国债余额_期末数（万元）
    */
    @TableField("trea_aeop")
    private BigDecimal treaAeop;

    /**
    * 资产减值损失准备_期初数（万元）
    */
    @TableField("ipoa_loss_abop")
    private BigDecimal ipoaLossAbop;

    /**
    * 资产减值损失准备_本期发生额（万元）
    */
    @TableField("ipoa_loss_aotc")
    private BigDecimal ipoaLossAotc;

    /**
    * 资产减值损失准备_期末数（万元）
    */
    @TableField("ipoa_loss_aeop")
    private BigDecimal ipoaLossAeop;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
    * 报表周期 | 格式：yyyymm
    */
    @TableField("report_instance_period")
    private String reportInstancePeriod;

    /**
    * 批次号 | 从0000递增，最大9999
    */
    @TableField("batch_no")
    private String batchNo;

    /**
    * 版本号 | 格式：报表周期版本流水号
    */
    @TableField("version")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @TableField("op")
    private String op;

    /**
    * 上报时间 | 文件上传时间
    */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
    * 写入时间 | 数据库记录时间
    */
    @TableField("write_time")
    private LocalDateTime writeTime;

    public void calculate() {
        // 总收入-本期发生额 = 期末数 - 期初数
        totIncmAotc = totIncmAeop.subtract(totIncmAbop);
        // 经营租赁业务收入-本期发生额 = 期末数 - 期初数
        operLeasBusiIncmAotc = operLeasBusiIncmAeop.subtract(operLeasBusiIncmAbop);
        // 其他收入-本期发生额 = 期末数 - 期初数
        othIncmAotc = othIncmAeop.subtract(othIncmAbop);
        // 融资租赁业务收入-期末数 = 总收入 - 经营租赁业务收入 - 其他收入
        fnlBusiIncmAeop = totIncmAeop.subtract(operLeasBusiIncmAeop).subtract(othIncmAeop);
        // 融资租赁业务收入-本期发生额 = 期末数 - 期初数
        fnlBusiIncmAotc = fnlBusiIncmAeop.subtract(fnlBusiIncmAbop);
        // 费用收入-本期发生额 = 期末数 - 期初数
        feeIncmAotc = feeIncmAeop.subtract(feeIncmAbop);
        // 利息收入-期末数 = 融资租赁业务收入 - 费用收入
        intrIncmAeop = fnlBusiIncmAeop.subtract(feeIncmAeop);
        // 利息收入-本期发生额 = 期末数 - 期初数
        intrIncmAotc = intrIncmAeop.subtract(intrIncmAbop);
        // 直接租赁资产-本期发生额 = 期末数 - 期初数
        dirtLeasAstAotc = dirtLeasAstAeop.subtract(dirtLeasAstAbop);
        // 售后回租资产-本期发生额 = 期末数 - 期初数
        slbkAstAotc = slbkAstAeop.subtract(slbkAstAbop);
        // 融资租赁资产-期末数 = 直接租赁资产 + 售后回租资产
        finLeasAstAeop = dirtLeasAstAeop.add(slbkAstAeop);
        // 融资租赁资产-本期发生额 = 期末数 - 期初数
        finLeasAstAotc = finLeasAstAeop.subtract(finLeasAstAbop);
        // 经营租赁资产-本期发生额 = 期末数 - 期初数
        operLeasAstAotc = operLeasAstAeop.subtract(operLeasAstAbop);
        // 租赁资产-期末数 = 经营租赁资产 + 融资租赁资产
        leasAstAeop = operLeasAstAeop.add(finLeasAstAeop);
        // 租赁资产-本期发生额 = 期末数 - 期初数
        leasAstAotc = leasAstAeop.subtract(leasAstAbop);
        // 跨省售后回租资产余额-本期发生额 = 期末数 - 期初数
        iprvSlbkAstBalAotc = iprvSlbkAstBalAeop.subtract(iprvSlbkAstBalAbop);
        // 跨省融资租赁资产余额（承租人为省外）-本期发生额 = 期末数 - 期初数
        iprvFnlAstBalAotc = iprvFnlAstBalAeop.subtract(iprvFnlAstBalAbop);
        // 直接租赁投放额-本期发生额 = 期末数 - 期初数
        dirtLeasRelsAotc = dirtLeasRelsAeop.subtract(dirtLeasRelsAbop);
        // 售后回租投放额-本年发生额 = 期末数 - 期初数
        slbkRelsAotc = slbkRelsAeop.subtract(slbkRelsAbop);
        // 融资租赁投放额-期末数 = 直接租赁投放额 + 售后回租投放额
        fnlRelsAeop = dirtLeasRelsAeop.add(slbkRelsAeop);
        // 融资租赁投放额-本期发生额 = 期末数 - 期初数
        fnlRelsAotc = fnlRelsAeop.subtract(fnlRelsAbop);
        // 固定收益类证券投资余额-本期发生额 = 期末数 - 期初数
        fixPayfScrIvsmAotc = fixPayfScrIvsmAeop.subtract(fixPayfScrIvsmAbop);
        // 国债余额-本期发生额 = 期末数 - 期初数
        treaAotc = treaAeop.subtract(treaAbop);
        // 资产减值损失准备-本期发生额 = 期末数 - 期初数
        ipoaLossAotc = ipoaLossAeop.subtract(ipoaLossAbop);
    }

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
