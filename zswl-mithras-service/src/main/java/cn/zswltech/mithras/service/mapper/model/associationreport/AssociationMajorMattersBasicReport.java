package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 重大事项报告表-基本信息
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
public class AssociationMajorMattersBasicReport extends BasicAssociationReport implements Serializable, IEntity {

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
    * 填报人联系方式
    */
    @TableField("inft_cont_mode")
    private String inftContMode;

    /**
    * 企业名称
    */
    @TableField("corp_name")
    private String corpName;

    /**
    * 法定资本(万元)
    */
    @TableField("legl_cptl")
    private BigDecimal leglCptl;

    /**
    * 营业地址
    */
    @TableField("busi_addr")
    private String busiAddr;

    /**
    * 公司法人名称
    */
    @TableField("corp_legp_name")
    private String corpLegpName;

    /**
    * 分支机构数量
    */
    @TableField("brch_ins_num")
    private Integer brchInsNum;

    /**
    * 董事长姓名
    */
    @TableField("chrm_name")
    private String chrmName;

    /**
    * 总经理姓名
    */
    @TableField("gmgr_name")
    private String gmgrName;

    /**
    * 联系方式
    */
    @TableField("cont_mode")
    private String contMode;

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
