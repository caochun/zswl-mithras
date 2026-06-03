package cn.zswltech.mithras.xinsight.mapper;

import cn.zswltech.mithras.xinsight.model.XinsightWarnMonitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XinsightWarnMonitorMapper extends BaseMapper<XinsightWarnMonitor> {

    List<XinsightWarnMonitor> selectByTmStamp(@Param("lastMaxId") Long lastMaxId);
}
