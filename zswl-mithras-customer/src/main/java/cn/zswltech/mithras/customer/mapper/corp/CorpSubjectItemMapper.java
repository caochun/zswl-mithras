package cn.zswltech.mithras.customer.mapper.corp;

import cn.zswltech.mithras.customer.model.client.CorpSubjectItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author junke
 */
public interface CorpSubjectItemMapper extends BaseMapper<CorpSubjectItem> {
    void batchInsert(@Param("list") List<CorpSubjectItem> toInsertData);
}
