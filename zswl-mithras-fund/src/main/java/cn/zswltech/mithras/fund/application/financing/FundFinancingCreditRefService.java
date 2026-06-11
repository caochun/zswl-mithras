package cn.zswltech.mithras.fund.application.financing;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.fund.mapper.FundFinancingCreditRefMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCreditRef;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 融资授信关联表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-10-13
 */
@Service
public class FundFinancingCreditRefService extends ServiceImpl<FundFinancingCreditRefMapper, FundFinancingCreditRef> implements IService<FundFinancingCreditRef> {

    public List<FundFinancingCreditRef> queryByFinancingId(Long financingId) {
        if(financingId == null){
            return Collections.emptyList();
        }
        return Optional.ofNullable(list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .eq(FundFinancingCreditRef::getFinancingId, financingId))
        ).orElse(Collections.emptyList());
    }

    public List<FundFinancingCreditRef> queryByOrgId(Long orgId) {
        if(orgId == null){
            return Collections.emptyList();
        }
        return Optional.ofNullable(list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .eq(FundFinancingCreditRef::getOrganizationId, orgId))
        ).orElse(Collections.emptyList());
    }

    public List<FundFinancingCreditRef> queryByCreditId(Long creditId) {
        if(creditId == null){
            return Collections.emptyList();
        }
        return Optional.ofNullable(list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .eq(FundFinancingCreditRef::getCreditId, creditId))
        ).orElse(Collections.emptyList());
    }

    public Map<Long ,List<FundFinancingCreditRef>> queryBatchByFinancingId(Collection<Long> financingIdList) {
        if(CollectionUtil.isEmpty(financingIdList)){
            return Collections.emptyMap();
        }
        List<FundFinancingCreditRef> list = list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .in(FundFinancingCreditRef::getFinancingId, financingIdList));
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return Optional.ofNullable(list.stream().collect(Collectors.groupingBy(FundFinancingCreditRef::getFinancingId))).orElse(Collections.emptyMap());
    }


    public Map<Long ,List<FundFinancingCreditRef>> queryBatchByOrgId(Collection<Long> orgIdList) {
        if(CollectionUtil.isEmpty(orgIdList)){
            return Collections.emptyMap();
        }
        List<FundFinancingCreditRef> list = list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .in(FundFinancingCreditRef::getOrganizationId, orgIdList));
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return Optional.ofNullable(list.stream().collect(Collectors.groupingBy(FundFinancingCreditRef::getOrganizationId))).orElse(Collections.emptyMap());
    }

    public Map<Long, List<FundFinancingCreditRef>> queryBatchByCreditId(Collection<Long> creditIdList) {
        if(CollectionUtil.isEmpty(creditIdList)){
            return Collections.emptyMap();
        }
        List<FundFinancingCreditRef> list = list(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                .in(FundFinancingCreditRef::getCreditId, creditIdList));
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return Optional.ofNullable(list.stream().collect(Collectors.groupingBy(FundFinancingCreditRef::getCreditId))).orElse(Collections.emptyMap());
    }

    public void removeRefByFinancingId(Long financingId){
        remove(Wrappers.<FundFinancingCreditRef>lambdaQuery()
                .eq(FundFinancingCreditRef::getFinancingId, financingId));
    }

    public void saveRef(Long financingId, Long creditId, Long orgId) {
        FundFinancingCreditRef ref = new FundFinancingCreditRef();
        ref.setFinancingId(financingId);
        ref.setCreditId(creditId);
        ref.setOrganizationId(orgId);
        save(ref);
    }

    /**
     * 翻单后更新授信信息
     * @param oldCreditId 原授信
     * @param newCreditId 新授信
     * @param financingIdList 需要变更的融资合同
     */
    public void updateRef(Long oldCreditId, Long newCreditId, List<Long> financingIdList){
        if(CollectionUtil.isEmpty(financingIdList) || oldCreditId == null || newCreditId == null){
            return;
        }
        List<FundFinancingCreditRef> needUpdateRefList = this.list(Wrappers.<FundFinancingCreditRef>lambdaQuery()
                .eq(FundFinancingCreditRef::getCreditId, oldCreditId)
                .in(FundFinancingCreditRef::getFinancingId, financingIdList));
        if(CollectionUtil.isNotEmpty(needUpdateRefList)){
            this.update(Wrappers.<FundFinancingCreditRef>lambdaUpdate()
                    .in(FundFinancingCreditRef::getId, needUpdateRefList.stream().map(FundFinancingCreditRef::getId).collect(Collectors.toList()))
                    .set(FundFinancingCreditRef::getCreditId, newCreditId));
        }

    }
}
