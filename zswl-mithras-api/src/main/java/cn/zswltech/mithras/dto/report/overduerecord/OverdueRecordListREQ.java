package cn.zswltech.mithras.dto.report.overduerecord;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 征信报送-逾期表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-逾期表查询入参")
public class OverdueRecordListREQ extends AccountListBaseREQ {
}
