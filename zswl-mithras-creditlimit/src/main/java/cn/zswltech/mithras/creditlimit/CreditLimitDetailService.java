package cn.zswltech.mithras.creditlimit.service;

import cn.zswltech.mithras.creditlimit.mapper.CreditLimitDetailMapper;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimitDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
@Slf4j
@Service
public class CreditLimitDetailService extends ServiceImpl<CreditLimitDetailMapper, CreditLimitDetail> {
    public List<CreditLimitDetail> listByGrantingSubjectKey(String bizType, String grantingSubjectKey) {
        LambdaQueryWrapper<CreditLimitDetail> query = Wrappers.lambdaQuery();
        query.eq(CreditLimitDetail::getBizType, bizType);
        query.eq(CreditLimitDetail::getGrantingSubjectKey, grantingSubjectKey);
        return this.list(query);
    }

    public CreditLimitDetail getOneByThreeKeys(String bizType, String grantingSubjectKey, String bizTargetKey) {
        LambdaQueryWrapper<CreditLimitDetail> query = Wrappers.lambdaQuery();
        query.eq(CreditLimitDetail::getBizType, bizType);
        query.eq(CreditLimitDetail::getGrantingSubjectKey, grantingSubjectKey);
        query.eq(CreditLimitDetail::getBizTargetKey, bizTargetKey);
        return this.getOne(query);
    }

    public List<CreditLimitDetail> listByBizTargetKey(String bizType, String bizTargetKey) {
        LambdaQueryWrapper<CreditLimitDetail> query = Wrappers.lambdaQuery();
        query.eq(CreditLimitDetail::getBizType, bizType);
        query.eq(CreditLimitDetail::getBizTargetKey, bizTargetKey);
        return this.list(query);
    }
}
