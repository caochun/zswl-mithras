package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
  * @author dingqi
  * @date 2025/9/6
  * @description 
  */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppVisitFileBatchDownloadREQ extends AppPCVisitRecordREQ {
    @ApiModelProperty(value = "拜访记录id")
    private List<Long> visitRecordIds;
}
