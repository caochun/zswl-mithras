package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrOverdueRecordMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-逾期信息表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrOverdueRecordService extends ServiceImpl<CrOverdueRecordMapper, CrOverdueRecord> implements IService<CrOverdueRecord> {

}
