package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 股东股权信息一览表-股东股权信息
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
public class AssociationShahStorInfo extends BasicAssociationReport implements Serializable , IEntity {

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
    * 序号
    */
    @TableField("onum")
    private String onum;

    /**
    * 股东全称
    */
    @TableField("shah_fn")
    private String shahFn;

    /**
    * 统一社会信用代码/身份证号
    */
    @TableField("shah_cert_num")
    private String shahCertNum;

    /**
    * 股东性质
    */
    @TableField("shah_char_code")
    private String shahCharCode;

    /**
    * 股东进入方式
    */
    @TableField("shah_gto_mode")
    private String shahGtoMode;

    /**
    * 变更前股东出资金额(万元)
    */
    @TableField("altr_bef_shah_fndr_amt")
    private BigDecimal altrBefShahFndrAmt;

    /**
    * 变更前出资比例
    */
    @TableField("altr_bef_fndr_rati")
    private BigDecimal altrBefFndrRati;

    /**
    * 股权转让标志
    */
    @TableField("stor_tran_flag")
    private String storTranFlag;

    /**
    * 增减资金金额(万元)
    */
    @TableField("iord_cptl_amt")
    private BigDecimal iordCptlAmt;

    /**
    * 最新出资金额(万元)
    */
    @TableField("last_fndr_amt")
    private BigDecimal lastFndrAmt;

    /**
    * 最新持股比例
    */
    @TableField("last_hold_rati")
    private BigDecimal lastHoldRati;

    /**
    * 批复文件号
    */
    @TableField("aprv_file_num")
    private String aprvFileNum;

    /**
    * 批复时间
    */
    @TableField("aprv_time")
    private LocalDate aprvTime;

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


    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
