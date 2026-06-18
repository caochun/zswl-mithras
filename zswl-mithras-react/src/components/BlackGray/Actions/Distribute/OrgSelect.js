import React, { useCallback, useEffect, useState } from 'react'
import { Select, Form } from '@zswl/components'
import { http } from '@zswl/admin'

function OrgSelect({ form, name, subName, code, type, lineType, fieldNames }) {
  const [list, setList] = useState([])

  const getList = useCallback(async () => {
    if (type === 'role') {
      const res = await http.get('/auth/getRoleByDeptCode', {
        params: {
          deptCode: code,
          lineType,
        },
      })
      setList(res)
    } else if (type === 'user') {
      const res = await http.get('/auth/getUserByRoleCode', {
        params: {
          roleCode: code,
        },
      })
      setList(res)
    }
  }, [code, type])

  useEffect(() => {
    if (code) {
      form.setFieldValue(name, undefined)
      type === 'role' && form.setFieldValue(subName, undefined)
      getList()
    }
  }, [code])

  return (
    <div id={name}>
      <Form.Item
        name={name}
        noStyle
        rules={[{ required: true, message: `请选择${type === 'role' ? '角色' : '用户'}` }]}
      >
        <Select
          style={{ width: '100%' }}
          options={list}
          fieldNames={fieldNames}
          getPopupContainer={() => document.getElementById(name)}
        />
      </Form.Item>
    </div>
  )
}

export default OrgSelect
