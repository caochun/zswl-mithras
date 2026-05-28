package cn.zswltech.mithras.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import org.springframework.stereotype.Service;

/**
* @author yangxiong
* @description 针对表【cr_modify_data_snap(二代征信修改数据辅助表)】的数据库操作Service实现
* @createDate 2024-04-06 15:56:13
*/
@Service
public class CrModifyDataSnapService extends ServiceImpl<CrModifyDataSnapMapper, CrModifyDataSnap>
    implements IService<CrModifyDataSnap> {

}




