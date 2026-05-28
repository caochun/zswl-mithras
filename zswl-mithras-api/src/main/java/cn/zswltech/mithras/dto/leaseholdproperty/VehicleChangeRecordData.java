package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/7/1 17:19
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class VehicleChangeRecordData {

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "变更记录列表")
    private List<ChangeRecord> changeRecordList;

    @Data
    public static class ChangeRecord {

        @ApiModelProperty(value = "姓名/名称")
        private String name;

        @ApiModelProperty(value = "变更日期")
        private LocalDate changeDate;
    }
}

