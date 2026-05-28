package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrPledgeDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrPledgeDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-质押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrPledgeDraftService extends ServiceImpl<CrPledgeDraftMapper, CrPledgeDraft> implements IService<CrPledgeDraft> {

}
