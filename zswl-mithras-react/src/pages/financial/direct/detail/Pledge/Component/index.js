import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import _debounce from 'lodash/debounce'
import Api from '@/api/financial/fundApi'

export function ProjSelect({ bizDeptId, financingId, ...rest }) {
  const [list, setList] = useState([])
  const getList = _debounce(async (val) => {
    if (!bizDeptId) {
      setList([])
      return
    }
    // const res = await Api.postProjList({
    //   projName: val,
    //   bizDeptId,
    //   financingId,
    // })
    // const data = res.map(({ projReviewId, projName }) => ({
    //   label: projName,
    //   value: +projReviewId,
    // }))
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
