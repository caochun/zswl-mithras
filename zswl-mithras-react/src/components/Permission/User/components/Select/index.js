import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import Api from '@/api/permission/user'

/**
 * 角色下拉框
 */
export function RoleSelect({ orgId, ...props }) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    if (orgId) {
      Api.getRoleList({ orgId }).then((res) => {
        setOptions(res || [])
      })
    }
  }, [orgId])
  return <Select options={options} {...props} fieldNames={{ value: 'id', label: 'name' }} />
}
