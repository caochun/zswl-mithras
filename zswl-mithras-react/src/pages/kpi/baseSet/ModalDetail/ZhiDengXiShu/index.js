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
        specialityLevel: item.careerLevel.indexOf('B') > -1 ? item.careerLevel : '',
        manageLevel: item.careerLevel.indexOf('M') > -1 ? item.careerLevel : '',
      }
    })
    return result
  }

  const saveData = async ({ list, values }) => {
    const configValue = []
    list.forEach(({ manageLevel, specialityLevel }, index) => {
      configValue.push({
        careerLevel: manageLevel || specialityLevel,
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
            { title: '管理序列', dataIndex: 'manageLevel' },
            { title: '专业序列', dataIndex: 'specialityLevel' },
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
              render: (value) => value,
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
