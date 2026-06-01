package cn.zswltech.mithras.service.mapper.lib.projreview;

import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 基本信息版本表
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:31 上午
 **/
@Repository
public interface ProjReviewBaseInfoLibMapper extends BaseMapper<ProjReviewBaseInfoLib> {

    List<ProjReviewBaseInfoLib> listNewestPreviewByClientIds(@Param("clientIds") Set<Long> clientIds, @Param("region")List<String> region);
}
