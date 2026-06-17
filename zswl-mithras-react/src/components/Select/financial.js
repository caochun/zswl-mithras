import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import fundApi from '@/api/financial/fundApi'

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
