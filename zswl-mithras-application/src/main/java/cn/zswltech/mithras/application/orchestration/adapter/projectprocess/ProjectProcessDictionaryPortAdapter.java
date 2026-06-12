package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessDictionaryPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProjectProcessDictionaryPortAdapter implements ProjectProcessDictionaryPort {

    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    public Map<String, String> addressCode2Display(Collection<String> addressCodes) {
        if (addressCodes == null || addressCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        return addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(AddressDictionary::getCode, addressCodes))
                .stream()
                .collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (a, b) -> a));
    }
}
