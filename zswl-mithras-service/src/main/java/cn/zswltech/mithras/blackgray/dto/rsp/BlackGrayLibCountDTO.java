package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
@Accessors(chain = true)
public class BlackGrayLibCountDTO {
    private String orgCode;
    private String orgName;
    private Long blackCount = 0L;
    private Long grayCount = 0L;
}
