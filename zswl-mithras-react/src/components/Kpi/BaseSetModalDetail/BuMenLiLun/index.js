import { observer } from '@zswl/admin'
import { useRef } from 'react'
import ModalEditTable from '../../ModalEditTable'

import { FormulaValueTip } from '@/components'
import { formulaData } from '@/utils/kpi'
import Api from './api'

const VERSIBLE = 'P'

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
              title: '部门利润完成率',
              dataIndex: 'rangeCode',
              matchOption: 'deptProfitFinishRadioRangeEnum',
            },
            {
              title: (
                <div>
                  <span>系数</span>
                  <FormulaValueTip versible={VERSIBLE}></FormulaValueTip>
                </div>
              ),
              dataIndex: 'coefficient',
              editable: () => {
                return editable ? InputCalcEditable({ acceptCode: [VERSIBLE] }) : false
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
