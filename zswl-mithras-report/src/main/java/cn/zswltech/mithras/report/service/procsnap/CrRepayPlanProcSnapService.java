package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrRepayPlanProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrRepayPlanProcSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-还款计划表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrRepayPlanProcSnapService extends ServiceImpl<CrRepayPlanProcSnapMapper, CrRepayPlanProcSnap> implements IService<CrRepayPlanProcSnap> {

}
