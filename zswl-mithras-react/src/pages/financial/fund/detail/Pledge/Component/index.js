import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'

export function ProjSelect({ bizDeptId, financingId, ...rest }) {
  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    if (!bizDeptId) {
      setList([])
      return
    }
    const data = [{ label: '哈哈11', value: '11' }]
    setList(data)
  }, 500)

  useEffect(() => {
    getList()
  }, [financingId, bizDeptId])

  return (
    <Select
      allowClear
      options={list}
      placeholder="请选择！"
      onSearch={(v) => getList(v)}
      {...rest}
    />
  )
}

export function ContractSelect({ projId, financingId, ...rest }) {
  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    if (!projId) {
      setList([])
      return
    }
    const data = [{ label: '哈哈22', value: '11', otherValue: 1333 }]
    setList(data)
  }, 500)

  useEffect(() => {
    getList()
  }, [financingId, projId])

  return (
    <Select
      data={123}
      allowClear
      labelInValue
      options={list}
      placeholder="请选择！"
      onSearch={(v) => getList(v)}
      {...rest}
    />
  )
}
