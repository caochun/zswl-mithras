package cn.zswltech.mithras.report.mapper.draft;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @description 征信报送-逾期信息表
* @author wang
* @date 2022-10-08
*/
public interface CrOverdueRecordDraftMapper extends BaseMapper<CrOverdueRecordDraft> {

    List<CrOverdueRecordDraft> queryNotFinishHistoryList();

}