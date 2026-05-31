package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.creditreport.mapper.CreditRepayPlanMapper;
import cn.zswltech.mithras.creditreport.model.CrRepayPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-还款计划表
 *
 * @author ylzhang5
 * @date 2025/12/04
 */
@Service
public class CreditRepayPlanService extends ServiceImpl<CreditRepayPlanMapper, CrRepayPlan> implements IService<CrRepayPlan> {

}
