package cn.zswltech.mithras.service.mapper.afterlease;


import cn.zswltech.mithras.service.mapper.dto.RentCollectionIndexListDTO;
import cn.zswltech.mithras.service.mapper.dto.RentCollectionIndexListParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:35 PM
 */
public interface RentCollectionIndexMapper {

    Page<RentCollectionIndexListDTO> indexList(Page page, @Param("dto") RentCollectionIndexListParam param);

}