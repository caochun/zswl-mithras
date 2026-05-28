package cn.zswltech.mithras.report.service.draft;

import cn.zswltech.mithras.report.mapper.draft.CrClientDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrClientDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-客户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrClientDraftService extends ServiceImpl<CrClientDraftMapper, CrClientDraft> implements IService<CrClientDraft> {

}
