package cn.zswltech.mithras.application.orchestration.afterlease.convert;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AfterLeaseTypeConversionWorker {

    @Named("jsonStringToLongList")
    public List<Long> jsonStringToLongList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSON.parseArray(jsonStr, Long.class);
    }
}
