import { useState, useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, Button, TableStore, Select, App, Form } from '@zswl/components'
import { Space, message } from 'antd'
import { create, all } from 'mathjs'
import { hasValue, rules } from '@/utils'
import styles from './index.less'
import Api from '@/api/budget/pricing/baseSet/ftpBaseSet'
import { myInputEditable, myInputRender, IS_FU_XIANG_BO_DONG } from '../context'
import { saveServer } from '@/utils'

const mathjs = create(all)

const EditTable = ({ table: $table, editable, typeInfo, ...rest }) => {
  const newColumns = useMemo(() => {
    const isRate = typeInfo.category === 'MINIMUM_COMPREHENSIVE_COMPENSATION_RATE'
    const rateColumn = [
      {
        title: '企业性质',
        dataIndex: 'paramName',
      },
      {
        title: '项目地区',
        dataIndex: 'paramOtherName',
      },
      {
        title: '最低综合补偿率',
        dataIndex: 'value',
        editable: (record) =>
          myInputEditable({
            record,
            dataIndex: 'value',
            editable,
          }),
        render: (value) => myInputRender(value),
      },
    ]
    const Column = [
      { title: '计价因子', dataIndex: 'paramName' },

      {
        title: typeInfo.category === 'FINANCIAL_MARKET_VOLATILITY' ? '权重(%)' : '计价标准(%)',
        dataIndex: 'value',
        editable: (record) => {
          if (!editable) return false
          if (IS_FU_XIANG_BO_DONG(record.paramName)) {
            return {
              element: <Select options={'yesOrNo'}></Select>,
              rules: [rules.required('请选择')],
            }
          }
          return myInputEditable({ editable, record, dataIndex: 'value' })
        },
        render: (value, record) => {
          if (IS_FU_XIANG_BO_DONG(record.paramName)) {
            return App.matchOption('yesOrNo', value).label
          }
          return myInputRender(value)
        },
      },
    ]

    return isRate ? rateColumn : Column
  }, [typeInfo, editable])
  return (
    <Table
    columnsFilter="ModalDetail_ModalEditTable_1"
                onFilter={(key,val) => saveServer('ModalDetail_ModalEditTable_1',val)}

      scroll={false}
      rowKey={'id'}
      columnWidth={180}
      store={$table}
      columns={newColumns}
      bordered
      {...rest}
    />
  )
}
const BudgetPricingBaseSetModalEditTable = (props) => {
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

export default observer(BudgetPricingBaseSetModalEditTable)
