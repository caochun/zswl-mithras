package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 高管信息一览表
 * @author hspcadmin
 * @date 2025-08-26
 */
@Data
public class AssociationSeniorExecutiveInfo extends BasicAssociationReport implements Serializable , IEntity {

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
    * 姓名
    */
    @TableField("name")
    private String name;

    /**
    * 证件号码
    */
    @TableField("cert_num")
    private String certNum;

    /**
    * 现任职务
    */
    @TableField("curr_duty_code")
    private String currDutyCode;

    /**
    * 任职时间
    */
    @TableField("aoff_time")
    private LocalDate aoffTime;

    /**
    * 批复文号
    */
    @TableField("aprv_file_num")
    private String aprvFileNum;

    /**
    * 最高学历
    */
    @TableField("high_edu_code")
    private String highEduCode;

    /**
    * 毕业院校
    */
    @TableField("grad_scho")
    private String gradScho;

    /**
    * 就读专业
    */
    @TableField("spjt")
    private String spjt;

    /**
    * 从事金融/经济工作时间
    */
    @TableField("have_finl_time")
    private String haveFinlTime;

    /**
    * 联系电话
    */
    @TableField("cont_tel")
    private String contTel;

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
