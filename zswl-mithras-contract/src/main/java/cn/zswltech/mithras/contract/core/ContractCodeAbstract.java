package cn.zswltech.mithras.contract.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ContractCodeAbstract
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 2:08 下午
 * @Version 1.0
 **/
public abstract class ContractCodeAbstract <M extends BaseMapper<ENTITY>, ENTITY> extends ServiceImpl<M, ENTITY> {

    @Transactional(rollbackFor = Throwable.class)
    public List<Long> getIdsByContractId(Long contractId){
        QueryWrapper<ENTITY> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").eq("contract_id", contractId);
        return baseMapper.selectObjs(queryWrapper).stream().map(o->Long.parseLong( o.toString())).collect(Collectors.toList());
    }

}
