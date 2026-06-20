import { observer } from '@zswl/admin'
import { useRef } from 'react'
import ModalEditTable from '../../ModalEditTable/KpiModalEditTable'

import { formulaData } from '@/utils/domains/kpi/KpiUtils'
import { FormulaValueTip } from '@/components/FormulaValueTip/FormulaValueTipEntries'
import { businessDeptAssessApi as Api } from '@/api/kpi/baseSet/parameterConfigApi'

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
    list.forEach(({ rangeCode }, index) => {
      configValue.push({
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
              title: '业务部门综合考评得分',
              dataIndex: 'rangeCode',
              width: 200,
              matchOption: 'businessDeptAssessRangeEnum',
            },
            {
              title: (
                <div>
                  <span>部门综合考评系数</span>
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
