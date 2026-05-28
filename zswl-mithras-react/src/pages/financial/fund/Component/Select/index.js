import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '@/pages/financial/fund/api'

export function OrgListSelect(params) {
  const { onChange, apiParams = {}, ...otherRest } = params ?? {}

  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    const res = await Api.postOrgList({
      organizationName: val,
      ...apiParams,
      // pageSize: 1000,
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
    const apiFn = params.api ? params.api : Api.postPayAccountBackList
    const res = await apiFn({
      accountBank: val,
      // pageSize: 1000,
    })
    // 去重

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
