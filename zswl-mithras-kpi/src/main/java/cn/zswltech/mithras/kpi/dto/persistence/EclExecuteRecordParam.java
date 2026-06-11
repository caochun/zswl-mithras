package cn.zswltech.mithras.kpi.dto.persistence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName EclExecuteRecordParam
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/9/30 14:18
 * @Version 1.0
 **/
@Data
public class EclExecuteRecordParam {

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "测算日期")
    private LocalDate createTimeFrom;


}
