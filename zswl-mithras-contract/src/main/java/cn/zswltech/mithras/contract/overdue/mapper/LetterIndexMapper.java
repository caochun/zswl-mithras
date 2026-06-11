package cn.zswltech.mithras.contract.overdue.mapper;

import cn.zswltech.mithras.contract.overdue.mapper.model.LetterIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/29 15:42
 */
public interface LetterIndexMapper extends BaseMapper<LetterIndex> {

    @Select("select `index` from oc_letter_index where year = #{year} for update")
    Integer findCollectLetterIndex(@Param("year") Integer year);

    @Update("update oc_letter_index set `index` = `index` + #{count} where year = #{year}")
    void incrementCollectLetterIndex(@Param("year") Integer year, @Param("count") int count);
}
