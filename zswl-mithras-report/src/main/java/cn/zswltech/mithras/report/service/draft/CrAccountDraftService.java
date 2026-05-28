package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-账户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrAccountDraftService extends ServiceImpl<CrAccountDraftMapper, CrAccountDraft> implements IService<CrAccountDraft> {

}
