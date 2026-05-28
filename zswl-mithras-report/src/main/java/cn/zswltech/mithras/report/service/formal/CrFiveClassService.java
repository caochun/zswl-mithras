package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrFiveClassMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrFiveClass;
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
public class CrFiveClassService extends ServiceImpl<CrFiveClassMapper, CrFiveClass> implements IService<CrFiveClass> {

}
