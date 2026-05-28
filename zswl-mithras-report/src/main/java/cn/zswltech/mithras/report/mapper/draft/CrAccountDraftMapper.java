package cn.zswltech.mithras.report.mapper.draft;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @description 征信报送-账户表
* @author wang
* @date 2022-10-08
*/
public interface CrAccountDraftMapper extends BaseMapper<CrAccountDraft> {

    void updateReportFlag(@Param("procBusinessKey") String procBusinessKey);

}