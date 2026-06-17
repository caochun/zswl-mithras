package cn.zswltech.mithras.application.orchestration.adapter.third.tianyancha;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.third.tianyancha.application.port.TycIndustryTypePort;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TycIndustryTypePortAdapter implements TycIndustryTypePort {

    @Resource
    private IndustryTypeMapper industryTypeMapper;

    @Override
    public String toIndustryCode(TycBaseInfo.IndustryAll industryAll) {
        if (industryAll == null) {
            return null;
        }
        String code = null;
        IndustryType industryType1 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                .eq(IndustryType::getDisplay, industryAll.getCategory())
                .eq(IndustryType::getLevel, 1));
        if (industryType1 != null) {
            code = industryType1.getCode();
            IndustryType industryType2 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                    .eq(IndustryType::getDisplay, industryAll.getCategoryBig())
                    .eq(IndustryType::getParentId, industryType1.getId()));
            if (industryType2 != null) {
                code = industryType2.getCode();
                IndustryType industryType3 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                        .eq(IndustryType::getDisplay, industryAll.getCategoryMiddle())
                        .eq(IndustryType::getParentId, industryType2.getId()));
                if (industryType3 != null) {
                    code = industryType3.getCode();
                    IndustryType industryType4 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                            .eq(IndustryType::getDisplay, industryAll.getCategorySmall())
                            .eq(IndustryType::getParentId, industryType3.getId()));
                    if (industryType4 != null) {
                        code = industryType4.getCode();
                    }
                }
            }
        }
        return code;
    }

    @Override
    public List<String> findAllParentCodes(String industryType) {
        if (StrUtil.isBlank(industryType)) {
            return Collections.emptyList();
        }
        List<IndustryType> all = industryTypeMapper.selectList(Wrappers.lambdaQuery());
        if (CollectionUtil.isEmpty(all)) {
            return Collections.emptyList();
        }
        Deque<String> deque = new LinkedList<>();
        IndustryType dbModel = null;
        Map<Long, IndustryType> industryTypeMap = new HashMap<>(all.size() + all.size() / 2);
        for (IndustryType it : all) {
            if (Objects.equals(it.getCode(), industryType)) {
                dbModel = it;
            }
            industryTypeMap.put(it.getId(), it);
        }
        if (Objects.isNull(dbModel)) {
            return Collections.emptyList();
        }
        deque.offer(industryType);
        do {
            dbModel = industryTypeMap.get(dbModel.getParentId());
            if (Objects.nonNull(dbModel)) {
                deque.offer(dbModel.getCode());
            }
        } while (Objects.nonNull(dbModel));
        List<String> result = new LinkedList<>();
        while (!deque.isEmpty()) {
            result.add(deque.pollLast());
        }
        return result;
    }

    @Override
    public String findIndustryCodeByDisplay(String display) {
        if (StrUtil.isBlank(display)) {
            return null;
        }
        List<IndustryType> category = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                .eq(IndustryType::getDisplay, display));
        if (CollectionUtil.isEmpty(category)) {
            return null;
        }
        return category.get(0).getCode();
    }
}
