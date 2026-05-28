package cn.zswltech.mithras.report.service.fullsnap;

import cn.zswltech.mithras.report.mapper.fullsnap.CrPledgeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrPledgeFullSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-质押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrPledgeFullSnapService extends ServiceImpl<CrPledgeFullSnapMapper, CrPledgeFullSnap> implements IService<CrPledgeFullSnap> {

}
