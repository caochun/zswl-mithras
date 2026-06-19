import { useState, useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, Button, TableStore, Select, App, Form } from '@zswl/components'
import { Space, message } from 'antd'
import { create, all } from 'mathjs'
import { hasValue, rules, numToFixed } from '@/utils'
import styles from './index.less'
import Api from '@/api/budgetManagement/parameterConfigApi'
import { myInputEditable, myInputRender, IS_FU_XIANG_BO_DONG } from '../context'

const mathjs = create(all)

const EditTable = ({ table: $table, editable, typeInfo, ...rest }) => {
  const newColumns = useMemo(() => {
    console.log('typeInfo', typeInfo, editable)
    let Column = []
    if (typeInfo.title === 'FTP定价') {
      Column = [
        {
          title: 'FTP行业分类',
          dataIndex: 'ftpIndustryClassification',
          matchOption: 'ftpIndustryCategoryEnum',
        },
        {
          title: '1年期（含）',
          dataIndex: 'oneYearTerm',
          editable: (record) => {
            return myInputEditable({
              record,
              dataIndex: 'oneYearTerm',
              editable,
            })
          },
          render: (text) => numToFixed(text / 10000),
        },
        {
          title: '1-3年期（含）',
          dataIndex: 'oneToThreeYearTerm',
          editable: (record) =>
            myInputEditable({
              record,
              dataIndex: 'oneToThreeYearTerm',
              editable,
            }),
          render: (text) => numToFixed(text / 10000),
        },
        {
          title: '3年以上',
          dataIndex: 'moreThanThreeYears',
          editable: (record) =>
            myInputEditable({
              record,
              dataIndex: 'moreThanThreeYears',
              editable,
            }),
          render: (text) => numToFixed(text / 10000),
        },
      ]
    } else if (typeInfo.title === '风险准备金计提比例') {
      Column = [
        {
          title: 'FTP行业分类',
          dataIndex: 'ftpIndustryCategory',
          matchOption: 'ftpIndustryCategoryEnum',
        },
        {
          title: '年限',
          dataIndex: 'termRange',
          matchOption: 'relatedTermRange',
        },
        {
          title: '计提比例',
          dataIndex: 'riskReserve',
          editable: (record) => {
            return myInputEditable({
              record,
              dataIndex: 'riskReserve',
              editable,
            })
          },
          render: (text) => numToFixed(text / 10000),
        },
      ]
    } else if (typeInfo.title === '费用比例') {
      Column = [
        { title: '业务部门', dataIndex: 'deptName' },
        {
          title: '费用比例',
          dataIndex: 'expenseRatio',
          editable: (record) =>
            myInputEditable({
              record,
              dataIndex: 'expenseRatio',
              editable,
            }),
          render: (text) => numToFixed(text / 10000),
        },
      ]
    }
    return Column
  }, [typeInfo, editable])
  return (
    <Table
      scroll={false}
      rowKey={'rowId'}
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
    const { list, id } = await $table.submit()
    if (list) {
      await Api.postSettingModify({
        configValue: JSON.stringify(transform(list)),
        id: typeInfo.id,
      })
      message.success('保存成功')
      baseStore.$editModal.close()
      baseStore.$table.search()
      setEditable(false)
    }
  }
  const transform = (list) => {
    return list.map((item, i) => {
      if (item.rowId) {
        delete item.rowId
      }
      if (item.riskReserve) {
        item.riskReserve = mathjs.multiply(mathjs.bignumber(item.riskReserve), 10000).toString()
      }
      if (item.expenseRatio) {
        item.expenseRatio = mathjs.multiply(mathjs.bignumber(item.expenseRatio), 10000).toString()
      }
      if (item.oneYearTerm) {
        item.oneYearTerm = mathjs.multiply(mathjs.bignumber(item.oneYearTerm), 10000).toString()
      }
      if (item.oneToThreeYearTerm) {
        item.oneToThreeYearTerm = mathjs
          .multiply(mathjs.bignumber(item.oneToThreeYearTerm), 10000)
          .toString()
      }
      if (item.moreThanThreeYears) {
        item.moreThanThreeYears = mathjs
          .multiply(mathjs.bignumber(item.moreThanThreeYears), 10000)
          .toString()
      }
      return item
    })
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
