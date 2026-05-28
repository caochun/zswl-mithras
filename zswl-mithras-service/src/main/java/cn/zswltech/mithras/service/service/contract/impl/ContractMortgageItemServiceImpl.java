package cn.zswltech.mithras.service.service.contract.impl;

import cn.zswltech.mithras.service.mapper.contract.ContractMortgageItemMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItem;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractMortgageItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName ContractMortgageItemServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/20 3:03 下午
 * @Version 1.0
 **/
@Service
public class ContractMortgageItemServiceImpl extends ServiceImpl<ContractMortgageItemMapper, ContractMortgageItem> implements ContractMortgageItemService {
    @Override
    public List<ContractMortgageItem> listByMortgageId(Long mortgageId) {
        LambdaQueryWrapper<ContractMortgageItem> query = Wrappers.lambdaQuery();
        query.eq(ContractMortgageItem::getMortgageId, mortgageId);
        return this.list(query);
    }

    @Override
    public void removeByMortgageId(Long mortgageId) {
        if (Objects.isNull(mortgageId)) {
            throw new MithrasException("抵押措施主数据id不能为空");
        }
        LambdaQueryWrapper<ContractMortgageItem> query = Wrappers.lambdaQuery();
        query.eq(ContractMortgageItem::getMortgageId, mortgageId);
        this.remove(query);
    }
}
