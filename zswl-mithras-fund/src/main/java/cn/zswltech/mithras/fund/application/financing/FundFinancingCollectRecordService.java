package cn.zswltech.mithras.fund.application.financing;

import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingCollectRecord;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingCollectRecordMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author yangxiong
* @description 针对表【fund_financing_collect_record(间融资金收款记录)】的数据库操作Service实现
* @createDate 2024-05-22 14:01:45
*/
@Service
public class FundFinancingCollectRecordService extends ServiceImpl<FundFinancingCollectRecordMapper, FundFinancingCollectRecord>
implements IService<FundFinancingCollectRecord> {

}
