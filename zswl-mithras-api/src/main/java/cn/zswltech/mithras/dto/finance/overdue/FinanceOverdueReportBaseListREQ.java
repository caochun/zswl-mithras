package cn.zswltech.mithras.dto.finance.overdue;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期报送计划表列表-请求体")
public class FinanceOverdueReportBaseListREQ extends PageReq {

}
