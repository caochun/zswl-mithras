/*
package cn.zswltech.mithras.creditreport.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
//@TableName("credit_report")
public class CreditReportDO extends BaseModel implements IEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String creditCode;

    private String clientName;

    private String cscCode;

    private String zhongZhengCode;

    private Long applyUser;

    private Long applyOrg;

    private String applyStatus;

    private LocalDateTime applyTime;

    private String selectStatus;

    private String selectErrorCode;
    //失败原因
    private String selectErrorReason;

    private LocalDateTime selectTime;

    private String projCode;

    private String projName;

    private String selectVersion;

    private String selectGoal;

    private String reportFormat;

    private Long projId;

    private String projIdDataType;

    private Long clientId;

    //授权开始日期
    private LocalDate authorizationBeganDate;

    //授权结束日期
    private LocalDate authorizationEndDate;

    //档案编号
    private String archiveId;

    //查询交易流水号
    private String serialNumber;


    @TableLogic(value = "0", delval = "1")
    private Long deleted;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }

}
*/
