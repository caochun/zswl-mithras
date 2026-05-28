import { DeleteOutlined } from '@ant-design/icons'
import { observer } from '@zswl/admin'
import { Button, Form, Table, TableStore } from '@zswl/components'
import _ from 'lodash'
import { useEffect, useMemo, useState } from 'react'
import Store from './store'
import { hasValue } from '@/utils'
import { saveServer } from '@/utils'

function FormTable({
  value = [],
  onChange,
  columns = [],
  canAddDelete = true,
  onlyRead = false,
  beforeUpdate = (list, record, newData) => {},
  ...Column
}) {
  const store = useMemo(() => new Store({ onChange, beforeUpdate }), [])
  const { table, editIndex } = store
  useEffect(() => {
    const list = table.getList()
    const isUnanimous = JSON.stringify(list) !== JSON.stringify(value)
    setTimeout(() => {
      value.length && isUnanimous && table.setList(value)
    }, 0)
  }, [value])
  const newColumns = useMemo(() => {
    const formatColumns = columns.map(({ editable, ...restColumn }) => {
      return {
        ...restColumn,
        editable: (record, index) => {
          if (editIndex !== index) return false
          return _.isFunction(editable) ? editable?.(record, index) : editable
        },
      }
    })
    return [
      ...formatColumns,
      !onlyRead && {
        title: canAddDelete ? <Button.Add type="text" onClick={store.addRow} /> : '操作',
        dataIndex: 'add',
        width: 150,
        editable: (record, index) => {
          const idEdit = editIndex === index
          return [
            !idEdit && (
              <Button type="link" onClick={() => store.handleEdit(index)}>
                编辑
              </Button>
            ),
            !idEdit && canAddDelete && (
              <Button
                icon={<DeleteOutlined />}
                type="text"
                onClick={() => store.deleteRow(record, index)}
              />
            ),
            idEdit && (
              <Button key="ok" type="link" onClick={() => store.handleOk(record, index)}>
                确认
              </Button>
            ),
            idEdit && (
              <Button key="cancel" type="link" onClick={() => store.handleCancel(record, index)}>
                取消
              </Button>
            ),
          ].filter(Boolean)
        },
      },
    ]
  }, [columns, editIndex, store, onlyRead])
  return (
    <Table
      store={table}
      columns={newColumns}
      editable={!onlyRead}
      columnsFilter={'Form_Table_1'}
      onFilter={(key, val) => saveServer('Form_Table_1', val)}
      columnWidth={100}
      rowKey={'id'}
      scroll={{ x: 400 }}
      {...Column}
    />
  )
}
FormTable.required = () => ({
  validator(r, value) {
    const whetherToFillIn = (value || []).every((v) => Object.values(v).every((v) => hasValue(v)))
    if (!whetherToFillIn) return Promise.reject(`请填写完整表格`)
    return Promise.resolve()
  },
})
export default observer(FormTable)
