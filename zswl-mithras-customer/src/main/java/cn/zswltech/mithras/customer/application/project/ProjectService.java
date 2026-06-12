package cn.zswltech.mithras.customer.application.project;

import cn.zswltech.mithras.customer.project.persistence.mapper.TmpClientProjectMapper;
import cn.zswltech.mithras.customer.project.persistence.model.TmpClientProject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luyi
 */
@Service
public class ProjectService {

    @Resource
    private TmpClientProjectMapper tmpClientProjectMapper;

    public boolean existClientRelatedProjects(Long clientId) {
        return tmpClientProjectMapper.selectCount(Wrappers.<TmpClientProject>lambdaQuery()
                .eq(TmpClientProject::getClientId, clientId)
                .eq(TmpClientProject::getStatus, 1)) > 0;
    }
}
