import { useState, useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, Button, TableStore, Select, App, Form } from '@zswl/components'
import { Space, message } from 'antd'
import { create, all } from 'mathjs'
import { hasValue, rules } from '@/utils'
import styles from './index.less'
import Api from '@/api/budgetManagement/parameterConfigApi'
import { myInputEditable, myInputRender, IS_FU_XIANG_BO_DONG } from '../context'

const mathjs = create(all)

const EditTable = ({ table: $table, editable, typeInfo, ...rest }) => {
  const newColumns = useMemo(() => {
    const Column = [
      { title: 'FTP行业分类', dataIndex: 'paramName' },
      { title: '1年期（含）', dataIndex: 'paramName', 
        editable: (record) =>
          myInputEditable({
            record,
            dataIndex: 'value',
            editable,
        }),
        render: (text)=>  `${text}%`
      },
      { title: '1-3年期（含）', dataIndex: 'paramName',
        editable: (record) =>
          myInputEditable({
            record,
            dataIndex: 'value',
            editable,
        }),
        render: (text)=>  `${text}%`
      },
      { title: '3年以上（含）', dataIndex: 'paramName',
        editable: (record) =>
          myInputEditable({
            record,
            dataIndex: 'value',
            editable,
        }),
        render: (text)=>  `${text}%`
      },
    ]
    return  Column
  }, [typeInfo, editable])
  return (
    <Table
      scroll={false}
      rowKey={'id'}
      // columnWidth={180}
      store={$table}
      columns={newColumns}
      bordered
      {...rest}
    />
  )
}
const Index = (props) => {
  const { baseStore, dataSource, typeInfo, ...rest } = props
  const [editable, setEditable] = useState(typeInfo.isEdit)

  const $table = useMemo(() => {
    return new TableStore({
      request: async () => {
        return dataSource
      },
      pagination: false,
    })
  }, [dataSource])

  const saveEditData = async () => {
    const { list } = await $table.submit()
    if (list) {
      await Api.postSettingModify(transform(list))
      message.success('保存成功')
      baseStore.$editModal.close()
      baseStore.$table.search()
      setEditable(false)
    }
  }
  const transform = (list) => {
    const res = []
    list.map((item) => {
      if (IS_FU_XIANG_BO_DONG(item.paramName)) {
        res.push(item)
      } else {
        res.push({
          ...item,
          value: hasValue(item.value)
            ? mathjs.multiply(mathjs.bignumber(item.value), 10000).toString()
            : undefined,
        })
      }
    })
    return res
  }

  return (
    <>
      {typeInfo.isEdit ? (
        <div className={styles.header}>
          {!editable ? (
            <Button type="primary" onClick={() => setEditable(true)}>
              编辑
            </Button>
          ) : (
            <Space>
              <Button onClick={() => setEditable(false)}>取消</Button>
              <Button onClick={saveEditData} type="primary">
                确认
              </Button>
            </Space>
          )}
        </div>
      ) : null}
      <EditTable table={$table} editable={editable} typeInfo={typeInfo} {...rest} />
    </>
  )
}

export default observer(Index)
