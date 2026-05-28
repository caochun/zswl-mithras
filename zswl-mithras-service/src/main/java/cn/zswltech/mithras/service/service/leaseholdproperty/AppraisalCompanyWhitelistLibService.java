package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.service.mapper.leaseholdproperty.AppraisalCompanyWhitelistLibMapper;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.AppraisalCompanyWhitelistLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description
 */
@Slf4j
@Service
public class AppraisalCompanyWhitelistLibService extends ServiceImpl<AppraisalCompanyWhitelistLibMapper, AppraisalCompanyWhitelistLib> {
    public AppraisalCompanyWhitelistLib getByIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<AppraisalCompanyWhitelistLib> query = Wrappers.lambdaQuery();
        query.eq(AppraisalCompanyWhitelistLib::getOriginId, originId);
        query.eq(AppraisalCompanyWhitelistLib::getVersion, version);
        return this.getOne(query);
    }
}
