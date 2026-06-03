package cn.zswltech.mithras.fund.application;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundDirectFinancingCollectRecord;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundDirectFinancingCollectRecordMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author yangxiong
* @description 针对表【fund_direct_financing_collect_record(直融资金收款记录)】的数据库操作Service实现
* @createDate 2024-05-22 14:01:45
*/
@Service
public class FundDirectFinancingCollectRecordService extends ServiceImpl<FundDirectFinancingCollectRecordMapper, FundDirectFinancingCollectRecord>
implements IService<FundDirectFinancingCollectRecord> {

}
