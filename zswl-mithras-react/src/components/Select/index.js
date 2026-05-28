import creditApi from '@/api/financial/financialManageOrg'
import Api from '@/api/groupCredit/common'
import { history, http } from '@zswl/admin'
import { App, Select } from '@zswl/components'
import { message, Select as RoSelect } from 'antd'
import { useCallback, useEffect, useMemo, useState } from 'react'
import IconFont from '../Icon'
import ReadOnly from '../ReadOnly'
import styles from './styles.less'

export function ApiSelect({ api, params, formatList, value, onlyRead = false, searchField, transformResult = (v) => v, labelInValue, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}
  const [options, setOptions] = useState([])
  useEffect(() => {
    getList()
  }, [JSON.stringify(params)])

  const getList = useCallback(
    async (val) => {
      const newParams = { ...params }
      if (searchField) {
        newParams[searchField] = val
      }
      const res = await api?.(newParams)
      const newRes = transformResult?.(res)
      setOptions(newRes)
      return newRes
    },
    [params, searchField, api, transformResult]
  )

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  const newValue = App.matchOption(options, value).label
  return onlyRead ? (
    <ReadOnly value={newValue} />
  ) : (
    <Select
      // 如果非全量数据返回，则debounceSearch设为true
      debounceSearch={false}
      placeholder="请选择"
      options={getList}
      value={value}
      allowClear
      onChange={onSelectChange}
      labelInValue={labelInValue}
      listHeight={180}
      {...otherRest}
    />
  )
}

export function CreditOrgSelect({ params, mode, value, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}

  const getClientList = async (val) => {
    try {
      const res = await creditApi.postOrganizationList({
        organizationName: val,
        pageSize: 10,
        ...params,
      })
      const data = res.list?.map(({ organizationName: label, id, ...restItem }) => ({
        ...restItem,
        label,
        value: +id,
      }))
      return data
    } catch (e) {
      setLoading(false)
    }
  }

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <Select debounceSearch options={getClientList} placeholder="请选择！" allowClear mode={mode} value={value} onChange={onSelectChange} {...otherRest} />
    </div>
  )
}

/**
 * 省份下拉
 */

export function ProvinceSelect(props) {
  return (
    <ApiSelect
      params={{ code: 156 }}
      api={(params) =>
        http.get('/select/region/child', {
          params,
          headers: {
            functionCode: props.functionCode,
          },
        })
      }
      {...props}
    ></ApiSelect>
  )
}

/**
 * 角色下拉框
 */
export function RoleSelect({ orgId, ...props }) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    if (orgId) {
      http.get('/role/list', { params: { orgId } }).then((res) => {
        setOptions(res || [])
      })
    }
  }, [orgId])
  return <Select options={options} {...props} fieldNames={{ value: 'id', label: 'name' }} />
}

/* 
  获取创建人列表
 name 创建人
 job 岗位
 sameDept 是否同部门
*/

export function FounderSelect({ params, mode, functionCode = 'selectfounder-groupCreditEstablish', value, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}

  const getFounder = async (val) => {
    const res = await Api.searchFounder(
      {
        name: val,
        job: 'projmanager',
        sameDept: false,
        ...params,
      },
      {
        functionCode,
      }
    )
    const data = res.map(({ label, value: newValue }) => ({ label, value: +newValue }))
    return data
  }

  const memoValue = useMemo(() => {
    if (value === null) return undefined
    return value
  }, [value])

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  return (
    <Select
      allowClear
      mode={mode}
      value={memoValue}
      options={getFounder}
      placeholder="请选择"
      getPopupContainer={() => document.body}
      onChange={onSelectChange}
      listHeight={180}
      {...otherRest}
    />
  )
}

export function OrgSelectZh({ params, mode, functionCode = 'selectorgs-groupCreditEstablish', isNumber = true, ...rest }) {
  const { onChange, valueType = 'string', filterKeys = [], ...otherRest } = rest ?? {}
  const [options, setOptions] = useState([])
  useEffect(async () => {
    const _options = await getOrgList({ ...params }, functionCode, isNumber)
    setOptions(_options)
  }, [])
  useEffect(() => {
    if (options.length > 0 && filterKeys.length > 0) {
      setOptions((pre) => pre.filter((item) => filterKeys.includes(+item.value)))
    }
  }, [filterKeys])

  const onSelectChange = (val) => {
    onChange?.(valueType === 'array' ? [val] : val)
  }

  return (
    <RoSelect
      allowClear
      options={options}
      placeholder="请选择！"
      mode={mode}
      onChange={onSelectChange}
      getPopupContainer={() => document.body}
      listHeight={180}
      {...otherRest}
    />
  )
}

// 客户统一视图专用
export const getOrgList2 = async (params, functionCode, isNumber) => {
  const res = await Api.getOrgList2(
    {
      // type: 1, //1:业务部门，2:领导层
      ...params,
    },
    {
      functionCode,
    }
  )
  const data = res.map(({ label, value, state }) => ({
    label,
    value: isNumber ? +value : value,
    state,
  }))
  return data
}

export const getOrgList = async (params, functionCode, isNumber, valueName, url) => {
  const res = await Api.getOrgList(
    {
      type: 1, //1:业务部门，2:领导层
      ...params,
    },
    {
      functionCode,
    },
    url
  )
  const data = res.map(({ label, value, state }) => ({
    label,
    value: valueName ? label : isNumber ? +value : value,
    state,
  }))
  return data
}

export function OrgSelect({ params, mode, functionCode = 'selectorgs-groupCreditEstablish', isNumber = true, valueName, url, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  return (
    <Select
      allowClear
      options={(val) => getOrgList({ name: val, ...params }, functionCode, isNumber, valueName, url)}
      placeholder="请选择！"
      mode={mode}
      onChange={onSelectChange}
      getPopupContainer={() => document.body}
      listHeight={180}
      {...otherRest}
    />
  )
}

export function ClientSelect({
  params,
  mode,
  isHymx,
  functionCode = 'clientlist-groupCreditEstablish',
  value,
  store,
  canJump = true,
  enterpriseName,
  transformResult = (v) => v,
  ...rest
}) {
  const { onChange, ...otherRest } = rest ?? {}
  const [list, setList] = useState([])

  const getList = async (val) => {
    const res = await Api.getClientList(
      {
        clientName: val,
        effected: true, // 只选择已生效客户
        pageSize: 9999,
        ...params,
      },
      {
        functionCode,
      }
    )
    const newRes = transformResult?.(res.list, val)
    setList(newRes)
    return newRes
  }

  const { value: clientId, clientType } = useMemo(() => {
    return list?.find((v) => v.value === value) || {}
  }, [list, value])

  const toDetail = () => {
    if (!clientId || clientType === 'NO-CORPORATION') {
      message.info('请选择授信主体')
      return
    }
    history.push(`/customer/maintain/detail/${clientId?.value ?? clientId}?clientType=${clientType}&flag=info&typeId=create`)
  }
  const onSelectChange = (val, option) => {
    onChange?.(val, option)
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <Select
        debounceSearch
        options={getList}
        placeholder="请选择！"
        allowClear
        mode={mode}
        value={value}
        onChange={onSelectChange}
        fieldNames={{ label: 'clientName', value: 'id' }}
        listHeight={180}
        {...otherRest}
      />

      {canJump && <IconFont type="icon-icon_link" onClick={toDetail} className={styles.icon} />}
    </div>
  )
}

// 认购机构
export function SubscribeOrgSelect({ mode, ...rest }) {
  const getOrgList = async (val) => {
    const res = await http.post('/fund/organization/list', { page: 1, pagesize: 1000 })
    const data = res?.list.map(({ organizationName, id }) => ({
      label: organizationName,
      value: id,
    }))
    return data
  }

  return <Select labelInValue allowClear options={getOrgList} placeholder="请选择！" mode={mode} {...rest} />
}

// 认购证券
export function SubscribeBondSelect({ financingId, mode, ...rest }) {
  const getOrgList = async () => {
    const res = await http.post('/fund/direct/financing/product/select', {
      financingId,
      page: 1,
      pagesize: 1000,
    })
    const data = res?.map(({ abbreviation, id }) => ({
      label: abbreviation,
      value: id,
    }))
    return data
  }

  return <Select allowClear options={getOrgList} placeholder="请选择！" mode={mode} {...rest} />
}

export function ProjectReviewSelect({ mode, params = {}, ...rest }) {
  const getList = async (val) => {
    const res = await http.post('/proj/review/base/info/list', {
      projName: val,
      page: 1,
      pagesize: 9999,
      projReviewStatus: 'TAKE_EFFECT',
      ...params,
    })

    return (
      res?.list?.map(({ projName, id }) => ({
        label: projName,
        value: id,
      })) ?? []
    )
  }
  return <Select allowClear options={getList} placeholder="请选择！" mode={mode} getPopupContainer={() => document.body} {...rest} />
}

export function ContractSelect({ functionCode = 'contractbaseinfo-list', value, ...rest } = {}) {
  const getList = async (val) => {
    const res = await http.post(
      '/contract/base/info/list',
      {
        page: 1,
        pageSize: 9999,
        contractCode: val,
      },
      {
        headers: {
          functionCode,
        },
      }
    )

    return res?.list ?? []
  }
  return <Select options={getList} value={value} getPopupContainer={() => document.body} allowClear {...rest} />
}
