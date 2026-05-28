package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrFiveClassDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrFiveClassDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-五级分类表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrFiveClassDraftService extends ServiceImpl<CrFiveClassDraftMapper, CrFiveClassDraft> implements IService<CrFiveClassDraft> {

}
