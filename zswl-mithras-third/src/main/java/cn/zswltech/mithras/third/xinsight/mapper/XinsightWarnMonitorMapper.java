package cn.zswltech.mithras.third.xinsight.mapper;

import cn.zswltech.mithras.third.xinsight.model.XinsightWarnMonitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XinsightWarnMonitorMapper extends BaseMapper<XinsightWarnMonitor> {

    List<XinsightWarnMonitor> selectByTmStamp(@Param("lastMaxId") Long lastMaxId);
}
