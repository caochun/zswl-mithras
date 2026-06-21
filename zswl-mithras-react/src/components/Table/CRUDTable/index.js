import { Button, Form, Modal, ModalStore, Table, TableStore } from '@zswl/components'
import { message, Space } from 'antd'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useState } from 'react'
import AddModal from './AddModal'
import styles from './index.less'
import { saveServer } from '@/utils'

function CRUDTable(props, ref) {
  const {
    columns,
    title,
    detail = [],
    isLog,
    canEdit = true,
    tableApi,
    addApi,
    deleteApi,
    buttonFixed = 'right',
    actions = [],
    ...rest
  } = props
  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const { list = [], ...restTable } = (await tableApi?.(params)) || {}
        const newList = list.map(({ id, ...item }) => ({
          id: id?.value ?? id,
          ...item,
        }))
        return { list: newList, ...restTable }
      },
    })
  }, [])

  const addModal = new ModalStore({
    onFinish: async (values, initialValues) => {
      const res = await addApi?.(values, initialValues)
      const msg = initialValues ? '修改成功' : '添加成功'
      message.success(msg)
      table.search()
      addModal.close()
    },
  })
  const editRow = (record) => {
    // compare场景数据修改
    const result = {}
    Object.entries(record).forEach(([field, item]) => {
      const value = item?.value ?? item
      result[field] = value
    })
    addModal.open(result)
  }
  const deleteRow = async (record) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        const res = await deleteApi?.(record)
        table.search()
        message.success('删除成功')
      },
    })
  }
  const tableColumns = useMemo(() => {
    const newColumns = columns.map((item) => {
      return {
        ...item,
      }
    })
    if (canEdit) {
      newColumns.push({
        title: '操作',
        dataIndex: 'action',
        width: 100,
        fixed: 'right',
        render: (text, record) => {
          return (
            <Space>
              <Button type="a" onClick={() => editRow(record)} key="edit">
                编辑
              </Button>
              <Button type="a" onClick={() => deleteRow(record)} key="delete">
                删除
              </Button>
            </Space>
          )
        },
      })
    }

    return newColumns
  }, [columns])
  const ButtonList = useMemo(
    () => [
      canEdit && (
        <Button.Add onClick={() => addModal.open()} key="add">
          新增
        </Button.Add>
      ),
      ...actions,
    ],
    [actions, canEdit]
  )
  return (
    <>
      <Table
        store={table}
        columnsFilter={'Table_CRUDTable_1'}
              onFilter={(key,val) => saveServer('Table_CRUDTable_1',val)}
        
        columns={tableColumns}
        editable={false}
        extra={buttonFixed === 'right' && ButtonList}
        actions={buttonFixed === 'left' ? ButtonList : <h3>{title}</h3>}
        className={styles.desc}
        {...rest}
      />
      <AddModal store={addModal} columns={columns} />
    </>
  )
}

export default forwardRef(CRUDTable)
