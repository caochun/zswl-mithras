package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrOverdueRecordDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-逾期信息表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrOverdueRecordDraftService extends ServiceImpl<CrOverdueRecordDraftMapper, CrOverdueRecordDraft> implements IService<CrOverdueRecordDraft> {

}
