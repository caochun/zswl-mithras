package cn.zswltech.mithras.credit.groupcredit.establish.mapper;
import cn.zswltech.mithras.credit.groupcredit.establish.dto.persistence.GroupCreditEstablishListSelectDTO;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
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
