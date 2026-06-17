package cn.zswltech.mithras.application.orchestration.adapter.margin;

import cn.zswltech.mithras.application.orchestration.adapter.margin.mapper.MarginBaseInfoListQueryMapper;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoListREQ;
import cn.zswltech.mithras.margin.application.port.MarginBaseInfoListQueryPort;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MarginBaseInfoListQueryPortAdapter implements MarginBaseInfoListQueryPort {

    @Resource
    private MarginBaseInfoListQueryMapper marginBaseInfoListQueryMapper;

    @Override
    public Page<MarginBaseInfo> pageList(Page<MarginBaseInfo> page, MarginBaseInfoListREQ req) {
        return marginBaseInfoListQueryMapper.pageList(page, req);
    }
}
