import { observer } from '@zswl/admin'
import { useRef } from 'react'
import ModalEditTable from '../../ModalEditTable'

import { formulaData } from '@/kpi/KpiUtils'
import Api from './api'

const Index = ({ baseStore, typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    const result = res.configValue.map((item) => {
      return {
        ...item,
        coefficient: item.configValueType === 'FORMULA' ? `=${item.coefficient}` : item.coefficient,
      }
    })
    return result
  }

  const saveData = async ({ list, values }) => {
    const configValue = []
    list.forEach((item) => {
      configValue.push({
        ...item,
        coefficient: formulaData(values[item.rangeCode].coefficient).value,
        configValueType: formulaData(values[item.rangeCode].coefficient).configValueType,
      })
    })
    await Api.saveList({
      ...detailData.current,
      configValue,
    })
  }

  return (
    <>
      <ModalEditTable
        rowKey={'rangeCode'}
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        baseStore={baseStore}
        columns={(editable) => {
          return [
            {
              title: '部门综合考评得分',
              dataIndex: 'rangeCode',
              matchOption: 'middleBackDeptAssessRangeEnum',
            },
            {
              title: (
                <div>
                  <span>部门综合考评系数</span>
                </div>
              ),
              dataIndex: 'coefficient',
              editable: () => {
                return editable
                  ? InputNumberEditable({
                      precision: 3,
                    })
                  : false
              },
              render: (value) => {
                return value
              },
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
