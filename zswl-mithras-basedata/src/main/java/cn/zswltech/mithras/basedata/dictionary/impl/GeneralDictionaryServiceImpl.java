package cn.zswltech.mithras.basedata.dictionary.impl;

import cn.zswltech.mithras.basedata.persistence.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.persistence.model.GeneralDictionary;
import cn.zswltech.mithras.basedata.dictionary.GeneralDictionaryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @since 2023-08-14
 */
@Service
public class GeneralDictionaryServiceImpl extends ServiceImpl<GeneralDictionaryMapper, GeneralDictionary> implements GeneralDictionaryService {
    private static final String KEY = "zlwType";

    @Override
    public List<String> getNameList() {
        return this.list(Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, KEY))
                .stream().map(GeneralDictionary::getDisplay).collect(Collectors.toList());
    }
}
