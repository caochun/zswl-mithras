package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 涉法涉讼涉访信息表
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
public class AssociationLawInvolvedVisitRelatedInfo extends BasicAssociationReport implements Serializable, IEntity {

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
    * 信息类别
    */
    @TableField("case_clas_code")
    private String caseClasCode;

    /**
    * 合同名称
    */
    @TableField("agmt_name")
    private String agmtName;

    /**
    * 合同编号
    */
    @TableField("agmt_no")
    private String agmtNo;

    /**
    * 涉及金额(元)
    */
    @TableField("invl_amt")
    private BigDecimal invlAmt;

    /**
    * 是否销号
    */
    @TableField("canb_flag")
    private String canbFlag;

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
