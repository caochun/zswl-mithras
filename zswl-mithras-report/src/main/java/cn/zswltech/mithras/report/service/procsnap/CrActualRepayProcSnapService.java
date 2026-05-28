package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrActualRepayProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrActualRepayProcSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-实际还款表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrActualRepayProcSnapService extends ServiceImpl<CrActualRepayProcSnapMapper, CrActualRepayProcSnap> implements IService<CrActualRepayProcSnap> {

}
