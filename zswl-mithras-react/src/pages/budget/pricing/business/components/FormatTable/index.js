import { useEffect, useMemo, useState } from 'react'
import { Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './index.less'
import _, { uniqueId } from 'lodash'
import { AmountFormat } from '@/components/Format'
import { saveServer } from '@/utils'

const FIRST_FIELD = 'firstName'

const FormatTable = (props) => {
  const { store, columns, borderTop, dataSource, needClass = true, ...rest } = props
  const table = useMemo(() => {
    if (store) return store
    return new TableStore({
      request: (params) => {
        return dataSource.map(({ id, ...rest }) => ({ id: id?.value ?? id ?? uniqueId(), ...rest }))
      },
      pagination: false,
    })
  }, [store, dataSource])
  return (
    <Table
      columnsFilter="components_FormatTable_1"
      onFilter={(key, val) => saveServer('components_FormatTable_1', val)}
      store={table}
      rowKey={FIRST_FIELD}
      className={needClass && styles.zStyleTable}
      columns={columns}
      bordered
      style={{ borderTop: borderTop && '1px solid #2558e6' }}
      {...rest}
    />
  )
}

FormatTable.getSecondTitleColumns = (firstEnum, secondEnum, column = {}) => {
  return firstEnum.map((item) => {
    return {
      title: item.label,
      children: secondEnum.map((third) => {
        const dataIndex = [item.value, third.value].join('-')
        return {
          title: third.label,
          dataIndex,
          ...column,
          render: (val, record, rowIndex) => {
            const value = val?.value ? val.value : val
            return <AmountFormat value={value} isChange={val?.isChange} />
          },
        }
      }),
    }
  })
}
FormatTable.res2TableData = (obj, filedArr = [], valueField = 'percentValue') => {
  const newObj = _.cloneDeep(obj)
  const newArr = []
  Object.entries(newObj).forEach(([key, value]) => {
    const newData = {}
    value.forEach((item) => {
      const filed = filedArr
        .map((v) => {
          return _.isFunction(v) ? v(item) : item[v]
        })
        .join('-')
      newData[filed] = item[valueField]
    })
    newData[FIRST_FIELD] = key
    newArr.push(newData)
  })
  return newArr
}
FormatTable.FIRST_FIELD = FIRST_FIELD
FormatTable.diffDetail = (detail, isLog, GROUP_BY, fieldValue = 'percentValue') => {
  const data = isLog
    ? detail.map((item, index) => {
        if (isLog[index][fieldValue]) {
          item[fieldValue] = {
            value: item[fieldValue],
            isChange: isLog[index][fieldValue],
          }
        }
        return item
      })
    : detail
  return data?.filter((v) => v[GROUP_BY])
}
export default observer(FormatTable)
