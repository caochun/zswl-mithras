package cn.zswltech.mithras.projectprocess.service.lib.projestablish.impl;

import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.ProjEstablishAocPriceLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishAocPriceLibServiceImpl
        extends ServiceImpl<ProjEstablishAocPriceLibMapper, ProjEstablishAocPriceLib>
        implements ProjEstablishAocPriceLibService {
    @Override
    public List<ProjEstablishAocPriceLib> listNewestByProjEstablishIds(Set<Long> projEstablishIds) {
        return baseMapper.listNewestByProjEstablishIds(projEstablishIds);
    }
}
