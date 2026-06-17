package cn.zswltech.mithras.margin.application.port;

import cn.zswltech.mithras.dto.margin.MarginBaseInfoListREQ;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface MarginBaseInfoListQueryPort {

    Page<MarginBaseInfo> pageList(Page<MarginBaseInfo> page, MarginBaseInfoListREQ req);
}
