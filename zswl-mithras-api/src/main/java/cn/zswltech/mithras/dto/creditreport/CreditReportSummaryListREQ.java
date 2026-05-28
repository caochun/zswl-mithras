package cn.zswltech.mithras.dto.creditreport;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
/**
 * @description 征信报告-信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信息概要表列表-请求体")
public class CreditReportSummaryListREQ extends PageReq {
}
