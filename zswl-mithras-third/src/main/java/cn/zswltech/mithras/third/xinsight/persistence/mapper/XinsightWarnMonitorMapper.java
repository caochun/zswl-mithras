package cn.zswltech.mithras.third.xinsight.persistence.mapper;

import cn.zswltech.mithras.third.xinsight.persistence.model.XinsightWarnMonitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XinsightWarnMonitorMapper extends BaseMapper<XinsightWarnMonitor> {

    List<XinsightWarnMonitor> selectByTmStamp(@Param("lastMaxId") Long lastMaxId);
}
