package cn.zswltech.mithras.basedata.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRateQueryREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.basedata.mapper.BaseDataExchangeRateMapper;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataExchangeRate;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@Slf4j
@Service
public class BaseDataExchangeRateService extends ServiceImpl<BaseDataExchangeRateMapper, BaseDataExchangeRate> {
    private static final String USD = "USD";
    private static final String USD_DISPLAY = "美元";

    public PageR<BaseDataExchangeRSP> pageList(BaseDataExchangeRateQueryREQ req) {
        Page<BaseDataExchangeRate> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BaseDataExchangeRate> conditionQuery = Wrappers.lambdaQuery();
        if (Objects.nonNull(req.getYear())) {
            conditionQuery.eq(BaseDataExchangeRate::getTargetYear, req.getYear());
        }
        if (Objects.nonNull(req.getMonth())) {
            conditionQuery.eq(BaseDataExchangeRate::getTargetMonth, req.getMonth());
        }
        if (Objects.nonNull(req.getIsDraft())) {
            conditionQuery.eq(BaseDataExchangeRate::getIsDraft, req.getIsDraft());
        }
        conditionQuery.orderByDesc(BaseDataExchangeRate::getTargetYear);
        conditionQuery.orderByDesc(BaseDataExchangeRate::getTargetMonth);
        conditionQuery.orderByAsc(BaseDataExchangeRate::getId);
        Page<BaseDataExchangeRate> pageResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<BaseDataExchangeRSP> list = BeanUtil.copyToList(pageResult.getRecords(), BaseDataExchangeRSP.class);
        return PageR.of(list, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public Long add(BaseDataExchangeREQ req) {
        // 查询数据进行校验
        List<BaseDataExchangeRate> exsitList = this.queryByYearMonthCurrency(req.getTargetYear(), req.getTargetMonth(), req.getCurrency());
        if (CollectionUtil.isNotEmpty(exsitList)) {
            throw new MithrasException("存在同一时期该币种的汇率信息，请勿重复提交");
        }
        BaseDataExchangeRate toInsert = BeanUtil.copyProperties(req, BaseDataExchangeRate.class);
        this.save(toInsert);
        return toInsert.getId();
    }

    public Long modify(BaseDataExchangeREQ req) {
        if (Objects.isNull(req.getId())) {
            throw new MithrasException("id不能为空");
        }
        BaseDataExchangeRate current = this.getById(req.getId());
        if (Objects.isNull(current)) {
            throw new MithrasException("数据不存在");
        }
        // 查询数据进行校验
        List<BaseDataExchangeRate> exsitList = this.queryByYearMonthCurrency(req.getTargetYear(), req.getTargetMonth(), req.getCurrency());
        if (CollectionUtil.isNotEmpty(exsitList)) {
            for (BaseDataExchangeRate exchangeRate : exsitList) {
                if (!Objects.equals(exchangeRate.getId(), current.getId())) {
                    throw new MithrasException("存在同一时期该币种的汇率信息，请勿重复提交");
                }
            }
        }
        BaseDataExchangeRate toUpdate = BeanUtil.copyProperties(req, BaseDataExchangeRate.class);
        this.updateById(toUpdate);
        return toUpdate.getId();
    }

    public void delete(Long id) {
        BaseDataExchangeRate exist = this.getById(id);
        if (Objects.isNull(exist)) {
            throw new MithrasException("数据不存在");
        }
        this.removeById(id);
    }

    public List<BaseDataExchangeRate> queryByYearMonthCurrency(int year, int month, String currency) {
        LambdaQueryWrapper<BaseDataExchangeRate> query = Wrappers.lambdaQuery();
        query.eq(BaseDataExchangeRate::getTargetYear, year);
        query.eq(BaseDataExchangeRate::getTargetMonth, month);
        query.eq(BaseDataExchangeRate::getCurrency, currency);
        return this.list(query);
    }

    public BaseDataExchangeRate getEffectByYearMonthCurrency(int year, int month, String currency) {
        List<BaseDataExchangeRate> list = this.queryEffectByYearMonthCurrency(year, month, currency);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        list.sort(Comparator.comparing(BaseDataExchangeRate::getTargetDate).reversed());
        return list.get(0);
    }

    public List<BaseDataExchangeRate> queryEffectByYearMonthCurrency(int year, int month, String currency) {
        List<BaseDataExchangeRate> list = this.queryByYearMonthCurrency(year, month, currency);
        // 去掉草稿数据
        list.removeIf(e -> Objects.equals(e.getIsDraft(), YesOrNoNumberEnum.YES.getCode()));
        return list;
    }

    public List<BaseDataExchangeRate> queryDraftByYearMonth(int year, int month) {
        LambdaQueryWrapper<BaseDataExchangeRate> query = Wrappers.lambdaQuery();
        query.eq(BaseDataExchangeRate::getTargetYear, year);
        query.eq(BaseDataExchangeRate::getTargetMonth, month);
        query.eq(BaseDataExchangeRate::getIsDraft, YesOrNoNumberEnum.YES.getCode());
        return this.list(query);
    }

    public void deleteById(Long id) {
        BaseDataExchangeRate record = this.getById(id);
        if (Objects.isNull(record)) {
            throw new MithrasException("数据不存在");
        }
        // 如果是美元类型需要校验，确保对应月份必须要有数据
        if (StrUtil.equals(record.getCurrency(), USD)) {
            // 查询对应年月数据
            List<BaseDataExchangeRate> list = this.queryByYearMonthCurrency(record.getTargetYear(), record.getTargetMonth(), USD);
            if (list.size() <= 1) {
                throw new MithrasException(String.format("对应月份仅有一条%s汇率数据，不允许删除", USD_DISPLAY));
            }
        }
        this.removeById(id);
    }
}
