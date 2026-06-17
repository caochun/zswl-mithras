import { EditDescription } from '@/components'
import { InputColumn, MatchOptionColumn } from '@/components/Format'
import { FounderSelect, OrgSelect } from '@/components/Select'
import { RegionCascader } from '@/components'
import { getUserInfo, isProjmanager } from '@/utils'
import { rules } from '@/utils/rules'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef } from 'react'
import Api from './api'

// 基本信息描述列表配置
const BaseInfoComponent = forwardRef(({ detail, canEdit }, ref) => {
  const desc = useRef(null)
  const userInfo = getUserInfo()

  const getBizDepList = async (val) => {
    const res = await Api.getOrgListByUserId({
      userId: val,
    })

    return res
  }
  const getBizDeptLeaderList = async (val) => {
    const res = await Api.getBusinessHeaderByDeptId({
      deptId: val,
    })

    return res
  }

  const sponsorUserChange = async (val) => {
    const res = await getBizDepList(val)
    const first = +res[0].value
    desc.current.form.setFieldsValue({
      sponsorUserId: val,
      belongDeptId: first,
    })
    bizDeptChange(first)
  }
  const bizDeptChange = async (val) => {
    const res = await getBizDeptLeaderList(val)
    desc.current.form.setFieldsValue({
      belongDeptId: val,
      bizDeptLeaderId: +res[0]?.value,
    })
  }

  const detailWithAreaName = useMemo(() => {
    if (!detail) return detail
    const areaNameArray = [detail.province, detail.city, detail.district].filter((item) => item)
    return {
      ...detail,
      areaName: areaNameArray.length > 0 ? areaNameArray : undefined,
    }
  }, [detail])

  const columnName = [
    InputColumn({
      title: '客户名称',
      dataIndex: 'clientName',
      editable: true,
      requiredMark: true,
    }),
    MatchOptionColumn({
      title: 'FTP行业',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
      editable: true,
      requiredMark: true,
    }),
    MatchOptionColumn({
      title: '风控行业',
      dataIndex: 'riskControlIndustryClassify',
      editable: true,
      requiredMark: true,
    }),
    MatchOptionColumn({
      title: '业务类型',
      dataIndex: 'leaseType',
      editable: true,
      requiredMark: true,
    }),
    {
      title: '项目主办',
      dataIndex: 'sponsorUserId',
      requiredMark: true,
      editable: {
        rules: [rules.required('请选择')],
        element: (
          <FounderSelect functionCode="selectfounder-buggetNotmonth" onChange={sponsorUserChange} />
        ),
      },
      render: (val, record) => {
        return record.sponsorUserName
      },
    },
    {
      title: '业务部门',
      dataIndex: 'belongDeptId',
      requiredMark: true,
      editable: {
        rules: [rules.required('请选择')],
        element: <OrgSelect functionCode="selectorgs-buggetNotmonth" onChange={bizDeptChange} />,
      },
      render: (val, record) => {
        return record.belongDeptName
      },
    },
    {
      title: '业务部门负责人',
      dataIndex: 'bizDeptLeaderId',
      requiredMark: true,
      editable: {
        rules: [rules.required('请选择')],
        element: (
          <FounderSelect functionCode="selectfounder-buggetNotmonth" params={{ job: 'leader' }} />
        ),
      },
      render: (val, record) => {
        return record.bizDeptLeaderName
      },
    },
    {
      title: '评估主体区域',
      dataIndex: 'areaName',
      requiredMark: true,
      editable: {
        rules: [rules.required('请选择')],
        element: <RegionCascader />,
      },
      render: (val, record) => {
        const areaCodes = [record.province, record.city, record.district].filter(Boolean)
        return areaCodes.length > 0 ? areaCodes.join(' / ') : '-'
      },
    },
  ]
  useImperativeHandle(ref, () => ({
    desc: desc.current,
  }))
  useEffect(() => {
    if (!detail.sponsorUserId && isProjmanager()) {
      sponsorUserChange(userInfo.id)
      desc.current.form.setFieldsValue({
        sponsorUserId: userInfo.id,
      })
    }
  }, [detail.sponsorUserId])

  useEffect(() => {
    desc.current.setBaseEdit(canEdit)
  }, [])
  return (
    <EditDescription
      title="基本信息"
      detail={detailWithAreaName}
      columns={columnName}
      hiddenButton
      ref={desc}
    />
  )
})

BaseInfoComponent.displayName = 'BaseInfoComponent'

export default BaseInfoComponent
