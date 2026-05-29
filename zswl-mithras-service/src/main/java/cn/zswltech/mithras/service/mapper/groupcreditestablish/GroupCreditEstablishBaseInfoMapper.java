package cn.zswltech.mithras.service.mapper.groupcreditestablish;
import cn.zswltech.mithras.service.mapper.dto.GroupCreditEstablishListSelectDTO;
import cn.zswltech.mithras.service.mapper.dto.ProjEstablishListSelectDTO;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
* @description 集团授信立项基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
public interface GroupCreditEstablishBaseInfoMapper extends CustomBaseMapper<GroupCreditEstablishBaseInfo> {

    Page<GroupCreditEstablishBaseInfo> myList(Page<GroupCreditEstablishBaseInfo> page,
                                       @Param("dto") GroupCreditEstablishListSelectDTO selectDTO);

}