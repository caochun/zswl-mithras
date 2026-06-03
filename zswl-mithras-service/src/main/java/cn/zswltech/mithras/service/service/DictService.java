package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.basedata.mapper.corp.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.GeneralDictionary;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author wangchuanhao
 * @date 2022/7/25 2:11 PM
 */
@Service
public class DictService {

    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;

    public String label2Display(String dictKey, String code) {
        return Optional.ofNullable(generalDictionaryMapper.selectOne(
                        Wrappers.<GeneralDictionary>lambdaQuery()
                                .eq(GeneralDictionary::getDictKey, dictKey)
                                .eq(GeneralDictionary::getCode, code)
                                .last("LIMIT 1")
                )).map(GeneralDictionary::getDisplay).orElse(null);
    }

    public String label2DisplayWithDefault(String dictKey, String code) {
        return Optional.ofNullable(label2Display(dictKey, code)).orElse(code);
    }

}
