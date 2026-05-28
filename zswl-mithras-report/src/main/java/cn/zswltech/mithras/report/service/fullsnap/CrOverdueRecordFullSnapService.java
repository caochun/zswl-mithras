package cn.zswltech.mithras.report.service.fullsnap;

import cn.zswltech.mithras.report.mapper.fullsnap.CrOverdueRecordFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrOverdueRecordFullSnap;
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
public class CrOverdueRecordFullSnapService extends ServiceImpl<CrOverdueRecordFullSnapMapper, CrOverdueRecordFullSnap> implements IService<CrOverdueRecordFullSnap> {

}
