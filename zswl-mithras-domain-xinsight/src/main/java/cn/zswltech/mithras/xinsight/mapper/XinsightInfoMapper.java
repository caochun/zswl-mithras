package cn.zswltech.mithras.xinsight.mapper;

import cn.zswltech.mithras.xinsight.model.XinsightInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 慧眼系统 查询mapper
 * @author liushaokang
 * @date 2026/1/5
 */
public interface XinsightInfoMapper extends BaseMapper<XinsightInfo> {

    List<XinsightInfo> selectByTmStamp(@Param("lastMaxId") Long lastMaxId);
}
