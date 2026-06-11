package cn.zswltech.mithras.projectprocess.versioning.projestablish.impl;

import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.versioning.projestablish.ProjEstablishBaseInfoLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishBaseInfoLibServiceImpl
        extends ServiceImpl<ProjEstablishBaseInfoLibMapper, ProjEstablishBaseInfoLib>
        implements ProjEstablishBaseInfoLibService {


    @Override
    public List<ProjEstablishBaseInfoLib> allNewstEffectVersion() {
        return baseMapper.allNewstEffectVersion();
    }
}
