package cn.zswltech.mithras.service.service.datacompare.compare;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import cn.zswltech.mithras.service.util.CompareUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @create: 2022-08-03
 **/
public class DefaultDataCompare<NEW extends IEntity, OLD extends ILib, RSP extends ListBaseRSP> extends AbstractDataCompare {

    private List<RSP> rsps;

    private String module;

    private BaseMapper<OLD> libMapper;

    private LibAbstractHandler<OLD, NEW, RSP> handler;

    private CommonVersionMapper commonVersionMapper;

    private String version;

    public DefaultDataCompare(List<RSP> rsps, BaseMapper<OLD> libMapper, LibAbstractHandler<OLD, NEW, RSP> handler, CommonVersionMapper commonVersionMapper, String module, String version) {
        super();
        this.rsps = rsps;
        this.libMapper = libMapper;
        this.handler = handler;
        this.commonVersionMapper = commonVersionMapper;
        this.module = module;
        this.version = version;
    }

    private List<Map<String, DiffValue>> compare(Long mainId) {
        return this.compare(mainId, VersionTypeConstants.NORMAL);
    }

    private List<Map<String, DiffValue>> compare(Long mainId, Integer versionType) {
        List<Map<String, DiffValue>> result = new LinkedList<>();
        for (RSP o : rsps) {
            QueryWrapper<OLD> qw = Wrappers.<OLD>query()
                    .eq(OLD.FIELD_ORIGIN_ID, o.getId())
                    .eq(OLD.FIELD_VERSION_TYPE, versionType)
                    .lt(StringUtils.isNotBlank(version), OLD.FIELD_VERSION, version)
                    .orderByDesc(OLD.FIELD_VERSION)
                    .last("LIMIT 1");
            OLD old = libMapper.selectOne(qw);
            if (old != null) {
                RSP oldrsp = handler.actualLib2Rsp(old);
                result.add(CompareUtil.compare(o, oldrsp));
            } else {
                result.add(CompareUtil.compare(o, null));
            }
        }
        int record = commonVersionMapper.selectCount(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, versionType)
                .eq(CommonVersion::getModule, module)
                .lt(StringUtils.isNotBlank(version), CommonVersion::getVersion, version)
        );
        if (record == 0) {
            result.forEach(o -> {
                o.values().forEach(dif -> dif.setIsChange(false));
            });
        }
        return result;
    }

    @Override
    public Map<String, DiffValue> compareone(Long mainId) {
        return compare(mainId).get(0);
    }

    @Override
    public Map<String, DiffValue> compareone(Long mainId, Integer versionType) {
        return compare(mainId,versionType).get(0);
    }

    @Override
    public List<Map<String, DiffValue>> comparelist(Long mainId) {
        return compare(mainId);
    }

    @Override
    public List<Map<String, DiffValue>> comparelist(Long mainId, Integer versionType) {
        return compare(mainId, versionType);
    }
}
