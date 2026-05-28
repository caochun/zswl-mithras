package cn.zswltech.mithras.report.mapper.draft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @description 征信报送-还款计划表
* @author wang
* @date 2022-10-08
*/
public interface CrRepayPlanDraftMapper extends BaseMapper<CrRepayPlanDraft> {

    /**
     * 查询账户的最大期项
     * @return
     */
    List<CrRepayPlanDraft> selectAccountMaxPhaseList();
}