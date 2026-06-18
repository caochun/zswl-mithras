import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import { http } from '@zswl/admin'
import financialManageOrgApi from '@/api/financial/financialManageOrg'
import fundApi from '@/api/financial/fundApi'

export function CreditOrgSelect({ params, mode, value, ...rest }) {
  const { onChange, ...otherRest } = rest ?? {}

  const getClientList = async (val) => {
    const res = await financialManageOrgApi.postOrganizationList({
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
  }

  const onSelectChange = (val) => {
    onChange?.(val)
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <Select
        debounceSearch
        options={getClientList}
        placeholder="请选择！"
        allowClear
        mode={mode}
        value={value}
        onChange={onSelectChange}
        {...otherRest}
      />
    </div>
  )
}

export function OrgListSelect(params) {
  const { onChange, apiParams = {}, ...otherRest } = params ?? {}

  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    const res = await fundApi.postOrgList({
      organizationName: val,
      ...apiParams,
    })
    const data = res.list?.map(({ organizationName, id, ...rest }) => ({
      label: organizationName,
      value: +id,
      ...rest,
    }))
    setList(data)
  }, 500)

  useEffect(() => {
    getList()
  }, [])

  const onSelectChange = (val) => {
    onChange?.(val, list)
    if (!val) {
      getList()
    }
  }

  return (
    <Select
      allowClear
      options={list}
      placeholder="融资机构"
      onSearch={(v) => getList(v)}
      onChange={onSelectChange}
      {...otherRest}
    />
  )
}

export function BankListSelect(params) {
  const { onChange, ...otherRest } = params ?? {}
  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    const apiFn = params.api ? params.api : fundApi.postPayAccountBackList
    const res = await apiFn({
      accountBank: val,
    })
    const data = Array.from(new Set(res.map(({ accountBank }) => accountBank))).map(
      (accountBank) => ({
        label: accountBank,
        value: accountBank,
      })
    )
    setList(data)
  }, 500)

  useEffect(() => {
    getList()
  }, [])

  const onSelectChange = (val) => {
    onChange?.(val)
    if (!val) {
      getList()
    }
  }

  return (
    <Select
      allowClear
      options={list}
      placeholder="银行名称"
      onSearch={(v) => getList(v)}
      onChange={onSelectChange}
      {...otherRest}
    />
  )
}

// 认购机构
export function SubscribeOrgSelect({ mode, ...rest }) {
  const getOrgList = async () => {
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
