package cn.zswltech.mithras.report.service.fullsnap;

import cn.zswltech.mithras.report.mapper.fullsnap.CrFiveClassFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrFiveClassFullSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-五级分类表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrFiveClassFullSnapService extends ServiceImpl<CrFiveClassFullSnapMapper, CrFiveClassFullSnap> implements IService<CrFiveClassFullSnap> {

}
