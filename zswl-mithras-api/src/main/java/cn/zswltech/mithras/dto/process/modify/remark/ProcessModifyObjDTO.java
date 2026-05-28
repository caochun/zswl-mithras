package cn.zswltech.mithras.dto.process.modify.remark;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
public class ProcessModifyObjDTO {
    @NotBlank
    private String reason;
    @NotBlank
    private String originalContent;
    @NotBlank
    private String toBeContent;
    private LocalDateTime createTime;
    private Boolean isUpdate = false;
}
