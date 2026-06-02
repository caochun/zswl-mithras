package cn.zswltech.mithras.contract.overdue.application.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:40
 */
@Data
@ApiModel(value = "文书用印列表响应")
public class PrintingListDto {

    private Long id;

    private String code;

    private String type;

    private String reason;

    private String processStatus;

    private LocalDateTime createTime;

    private Long createBy;

    private String createByName;

    private String processId;

    private String applyName;

    private String applyDeptName;
}
