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
 * @description 金融局报送-对外融资信息清单表
 * @author vico
 * @date 2025-04-18
 */
@Data
public class AssociationExternalFinancing extends BasicAssociationReport implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 自增主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）
    */
    @TableField("row_num")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码  ???
    */
    @TableField("unif_soci_cred_code")
    private String unifSociCredCode;

    /**
    * 序号
    */
    @TableField("onum")
    private String onum;

    /**
    * 借款余额 | 单位：万元
    */
    @TableField("loan_bal")
    private BigDecimal loanBal;

    /**
    * 融资业务类型 | 数据字典：evt00052
    */
    @TableField("fin_busi_type_code")
    private String finBusiTypeCode;

    /**
    * 资金提供方
    */
    @TableField("cptl_prov")
    private String cptlProv;

    /**
    * 融资利率
    */
    @TableField("fin_intr")
    private BigDecimal finIntr;

    /**
    * 融资借款日期
    */
    @TableField("fin_loan_date")
    private LocalDate finLoanDate;

    /**
    * 融资到期日期
    */
    @TableField("fin_matu_date")
    private LocalDate finMatuDate;

    /**
    * 报表实例编号 | 格式：uuid
    */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
    * 报表实例周期 | 格式：yyyyqq
    */
    @TableField("report_instance_period")
    private String reportInstancePeriod;

    /**
    * 批次号 | 4位字符，根据月报/季报/年报的上报周期，同期数据重复上传时从0000开始递增，9999后重置
    */
    @TableField("batch_no")
    private String batchNo;

    /**
    * 版本号 | 报表实例周期版本号
    */
    @TableField("version")
    private String version;

    /**
    * 操作标识 | 值域：insert/update
    */
    @TableField("op")
    private String op;

    /**
    * 上报时间 | 数据文件上传时间
    */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
    * 写入时间 | 数据库写入时间
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
