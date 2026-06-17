import { observer } from '@zswl/admin'
import { useRef } from 'react'
import ModalEditTable from '@/pages/kpi/Component/ModalEditTable'
import { InputCalcEditable } from '@/components/Format/editable'
import { formulaData } from '@/utils/kpi'
import { FormulaValueTip } from '@/components'
import Api from './api'

const VERSIBLE = 'P'

const Index = ({ baseStore, typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    const result = res.configValue.map((item, index) => {
      return {
        ...item,
        $$index: index,
        coefficient: item.configValueType === 'FORMULA' ? `=${item.coefficient}` : item.coefficient,
      }
    })
    return result
  }

  const saveData = async ({ list, values }) => {
    const configValue = []
    list.forEach(({ group, rangeCode }, index) => {
      configValue.push({
        group,
        rangeCode,
        coefficient: formulaData(values[index].coefficient).value,
        configValueType: formulaData(values[index].coefficient).configValueType,
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
        rowKey={'$$index'}
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        baseStore={baseStore}
        columns={(editable) => {
          return [
            {
              title: '区间',
              dataIndex: 'group',
              matchOption: 'profitAdjustGroupEnum',
              onCell: (_, index) => {
                if (index === 2) {
                  return { rowSpan: 2 }
                }

                if (index === 3) {
                  return { rowSpan: 0 }
                }
              },
            },
            {
              title: '部门利润完成率',
              dataIndex: 'rangeCode',
              matchOption: 'profitAdjustRangeEnum',
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
