package cn.zswltech.mithras.application.orchestration.adapter.margin.mapper;

import cn.zswltech.mithras.dto.margin.MarginBaseInfoListREQ;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MarginBaseInfoListQueryMapper {

    Page<MarginBaseInfo> pageList(Page<MarginBaseInfo> page, @Param("dto") MarginBaseInfoListREQ req);
}
