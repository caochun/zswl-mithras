package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
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
public class CrRepayPlanDraftService extends ServiceImpl<CrRepayPlanDraftMapper, CrRepayPlanDraft> implements IService<CrRepayPlanDraft> {

}
