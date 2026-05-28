package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrRepayPlanMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
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
public class CrRepayPlanService extends ServiceImpl<CrRepayPlanMapper, CrRepayPlan> implements IService<CrRepayPlan> {

}
