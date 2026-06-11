package cn.zswltech.mithras.leaseholdproperty.mapper;

import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author yangxiong
* @description 针对表【lease_item_info(租赁物管理信息)】的数据库操作Mapper
* @createDate 2023-09-19 16:09:40
* @Entity cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemInfo
*/
public interface LeaseItemInfoMapper extends BaseMapper<LeaseItemInfo> {

    LeaseItemInfo queryLastestListByContractId(@Param("projReviewId") Long projReviewId);
}




