package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.zswltech.mithras.blackgray.dto.excel.BlackGrayUploadModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BlackGrayWarehouseRecordUploadRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/1/15 5:18 下午
 * @Version 1.0
 **/
@Data
public class BlackGrayWarehouseRecordUploadRSP {

    @ApiModelProperty(value = "符合条件条数")
    private List<BlackGrayUploadModel> addList;

    @ApiModelProperty(value = "失败条数")
    private List<Body> ErrorList;

    @Data
    public static class Body{
        private String enterpriseName;

        private String unifiedSocialCreditCode;

        private List<String> errorFields;
    }
}
