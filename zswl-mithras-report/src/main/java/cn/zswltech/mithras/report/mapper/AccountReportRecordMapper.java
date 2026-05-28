package cn.zswltech.mithras.report.mapper;

import cn.zswltech.mithras.report.mapper.model.AccountReportRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 征信报送-账户表报送辅助
 * 同借据多付款申请 只报送一个
* @author wang
* @date 2023-4-6
*/
public interface AccountReportRecordMapper extends BaseMapper<AccountReportRecord> {

}