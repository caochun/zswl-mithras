package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrMortgageProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrMortgageProcSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-抵押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrMortgageProcSnapService extends ServiceImpl<CrMortgageProcSnapMapper, CrMortgageProcSnap> implements IService<CrMortgageProcSnap> {

}
