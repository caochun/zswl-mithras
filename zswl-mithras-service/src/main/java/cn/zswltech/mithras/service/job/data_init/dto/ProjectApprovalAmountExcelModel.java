package cn.zswltech.mithras.service.job.data_init.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/27 08:51
 **/
@Data
public class ProjectApprovalAmountExcelModel {

    @Alias("项目名称")
    private String projectName;

    @Alias("项目批复金额(万元)")
    private BigDecimal projectApprovalAmount;
}
