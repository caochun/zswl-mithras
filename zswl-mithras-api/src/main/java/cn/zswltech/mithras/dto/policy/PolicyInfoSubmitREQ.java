package cn.zswltech.mithras.dto.policy;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@ApiModel("保单维护提交-请求体")
public class PolicyInfoSubmitREQ {

    @ApiModelProperty("保单父id")
    @NotNull(message = "保单id不得为空")
    private Long parentId;
}
