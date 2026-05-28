import { Select } from '@zswl/components'
import { http } from '@zswl/admin'
import { useEffect, useState } from 'react'

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
