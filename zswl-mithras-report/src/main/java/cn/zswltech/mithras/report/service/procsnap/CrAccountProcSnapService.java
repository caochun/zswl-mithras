package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrAccountProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrAccountProcSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-账户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrAccountProcSnapService extends ServiceImpl<CrAccountProcSnapMapper, CrAccountProcSnap> implements IService<CrAccountProcSnap> {

}
