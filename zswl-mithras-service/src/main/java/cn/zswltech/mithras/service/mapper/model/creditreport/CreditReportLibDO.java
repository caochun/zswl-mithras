/*
package cn.zswltech.mithras.service.mapper.model.creditreport;

import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("credit_report_lib")
public class CreditReportLibDO extends CreditReportDO implements Serializable, ILib {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    */
/**
     * 变更编号
     * 版本号
     *//*

    private String version;

    */
/**
     * 临时数据表id
     * 需要用来比对数据 或者 流程拒绝时全量回写
     *//*

    private Long originId;

    */
/**
     * 记录原数据更新时间、创建时间等
     *//*

    private LocalDateTime dataCreateTime;

    private Long dataCreateBy;

    private LocalDateTime dataUpdateTime;

    private Long dataUpdateBy;

    private Integer versionType;
}
*/
