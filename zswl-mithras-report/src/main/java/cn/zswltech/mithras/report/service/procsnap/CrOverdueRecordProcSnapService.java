package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrOverdueRecordProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrOverdueRecordProcSnap;
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
public class CrOverdueRecordProcSnapService extends ServiceImpl<CrOverdueRecordProcSnapMapper, CrOverdueRecordProcSnap> implements IService<CrOverdueRecordProcSnap> {

}
