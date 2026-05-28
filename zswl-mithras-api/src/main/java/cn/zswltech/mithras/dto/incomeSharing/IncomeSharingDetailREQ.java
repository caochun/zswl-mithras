package cn.zswltech.mithras.dto.incomeSharing;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yupengfei
 * @date 2024/6/7 17:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingDetailREQ extends PageReq {

    @ApiModelProperty(value = "借据id")
    @NotNull(message = "借据id不能为空")
    private Long receiptId;

    @ApiModelProperty(value = "年月")
    private String yearAndMonth;

    @ApiModelProperty(value = "日期开始")
    @JsonIgnore
    private LocalDate incomeDateFrom;

    @ApiModelProperty(value = "日期结束")
    @JsonIgnore
    private LocalDate incomeDateTo;
}
