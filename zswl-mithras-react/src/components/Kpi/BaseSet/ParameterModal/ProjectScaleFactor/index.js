import { useRef } from 'react'
import { observer } from '@zswl/admin'
import ModalEditTable from '../../../ModalEditTable'
import { InputNumberEditable } from '@/components/Format'
import Api from '@/api/kpi/baseSet/baseSetApi'
import { uniqueId } from 'lodash'

const Index = ({ typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getProjectScaleFactor({ baseId: typeInfo.id })
    detailData.current = res
    return res.configValue.map((v) => ({ ...v, id: uniqueId() }))
  }

  const saveData = async ({ list }) => {
    await Api.saveProjectScaleFactor({ ...detailData.current, configValue: list, id: typeInfo.id })
  }

  return (
    <>
      <ModalEditTable
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        columns={(editable) => {
          return [
            {
              title: '投放规模（万元）',
              dataIndex: 'projectScale',
            },
            {
              title: '系数',
              dataIndex: 'projectRadio',
              editable: () => {
                return editable ? InputNumberEditable({ precision: 2 }) : false
              },
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
