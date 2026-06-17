package cn.zswltech.mithras.application.orchestration.adapter.document;

import cn.zswltech.mithras.basedata.persistence.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.persistence.model.GeneralDictionary;
import cn.zswltech.mithras.document.application.port.DocumentDictionaryPort;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentDictionaryPortAdapter implements DocumentDictionaryPort {

    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;

    @Override
    public List<String> listCodesByDictKey(String dictKey) {
        return generalDictionaryMapper.selectList(Wrappers.<GeneralDictionary>lambdaQuery()
                        .eq(GeneralDictionary::getDictKey, dictKey))
                .stream()
                .map(GeneralDictionary::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getCode, code)
                .last(StringUtil.mysqlLimitOne())) != null;
    }

    @Override
    public void add(String dictKey, String dictDesc, String code, String display, Integer sort) {
        GeneralDictionary dict = new GeneralDictionary();
        dict.setDictDesc(dictDesc);
        dict.setDictKey(dictKey);
        dict.setCode(code);
        dict.setDisplay(display);
        dict.setSort(sort);
        generalDictionaryMapper.insert(dict);
    }

    @Override
    public void deleteByDictKeyAndCode(String dictKey, String code) {
        generalDictionaryMapper.delete(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getDictKey, dictKey)
                .eq(GeneralDictionary::getCode, code));
    }
}
