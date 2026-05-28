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
 * @description 基本情况统计表
 * @author hspcadmin
 * @date 2025-08-22
 */
@Data
public class AssociationBasicSituation extends BasicAssociationReport implements Serializable, IEntity {

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
    * 法定代表人
    */
    @TableField("legr")
    private String legr;

    /**
    * 成立日期
    */
    @TableField("setp_date")
    private LocalDate setpDate;

    /**
    * 批准单位
    */
    @TableField("aprv_unit")
    private String aprvUnit;

    /**
    * 批准文号
    */
    @TableField("aprv_file_num")
    private String aprvFileNum;

    /**
    * 营运资金(万元)
    */
    @TableField("oper_cptl")
    private BigDecimal operCptl;

    /**
    * 国有资本(万元)
    */
    @TableField("stto_cptl")
    private BigDecimal sttoCptl;

    /**
    * 实收资本(万元)
    */
    @TableField("paid_cptl")
    private BigDecimal paidCptl;

    /**
    * 经济成分
    */
    @TableField("econ_clas_code")
    private String econClasCode;

    /**
    * 是否中央企业控股
    */
    @TableField("ctar_corp_hold_flag")
    private String ctarCorpHoldFlag;

    /**
    * 是否地方国企控股
    */
    @TableField("lcal_soe_hold_flag")
    private String lcalSoeHoldFlag;

    /**
    * 从业人员
    */
    @TableField("prti_num")
    private Integer prtiNum;

    /**
    * 注册地址
    */
    @TableField("reg_addr")
    private String regAddr;

    /**
    * 实际经营地址
    */
    @TableField("actl_oper_addr")
    private String actlOperAddr;

    /**
    * 企业类别(内资/内资试点/外资)
    */
    @TableField("corp_clas_code")
    private String corpClasCode;

    /**
    * 厂商系标志(厂商系/非厂商系)
    */
    @TableField("mnfr_flag")
    private String mnfrFlag;

    /**
    * 上市标志(上市/非上市)
    */
    @TableField("list_flag")
    private String listFlag;

    /**
    * 分支机构数量(家)
    */
    @TableField("brch_ins_num")
    private Integer brchInsNum;

    /**
    * 省外分支机构数量(家)
    */
    @TableField("oprv_brch_ins_num")
    private Integer oprvBrchInsNum;

    /**
    * 省内分支机构数量(家)
    */
    @TableField("wprv_brch_ins_num")
    private Integer wprvBrchInsNum;

    /**
    * 设立的其他融资租赁子公司数量
    */
    @TableField("fnl_chil_corp_num")
    private Integer fnlChilCorpNum;

    /**
    * 设立的特殊项目公司（spv)
    */
    @TableField("spcl_proj_corp_spv_vol")
    private Integer spclProjCorpSpvVol;

    /**
    * 分支机构地址
    */
    @TableField("brch_ins_addr")
    private String brchInsAddr;

    /**
    * 经批准的业务范围
    */
    @TableField("hsap_busi_scop")
    private String hsapBusiScop;

    /**
    * 实际控制人
    */
    @TableField("actl_ctlr")
    private String actlCtlr;

    /**
    * 实际控制人持股比例
    */
    @TableField("actl_ctlr_hold_rati")
    private BigDecimal actlCtlrHoldRati;

    /**
    * 公司联系人
    */
    @TableField("corp_conp")
    private String corpConp;

    /**
    * 联系电话
    */
    @TableField("cont_tel")
    private String contTel;

    /**
    * 联系邮箱
    */
    @TableField("cont_mail")
    private String contMail;

    /**
    * 公司网址
    */
    @TableField("corp_web")
    private String corpWeb;

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
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
