package cn.zswltech.mithras.fund.directfinancing.application.directfinancing;

import cn.zswltech.mithras.fund.model.FundDirectFinancingRepayRecord;
import cn.zswltech.mithras.fund.mapper.FundDirectFinancingRepayRecordMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author yangxiong
* @description 针对表【fund_direct_financing_repay_record(直融资金还款记录)】的数据库操作Service实现
* @createDate 2024-05-22 14:01:45
*/
@Service
public class FundDirectFinancingRepayRecordService extends ServiceImpl<FundDirectFinancingRepayRecordMapper, FundDirectFinancingRepayRecord>
implements IService<FundDirectFinancingRepayRecord> {

}
