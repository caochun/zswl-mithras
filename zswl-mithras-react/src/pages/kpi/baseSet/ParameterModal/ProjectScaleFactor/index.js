import { useRef } from 'react'
import { observer } from '@zswl/admin'
import ModalEditTable from '@/pages/kpi/Component/ModalEditTable'
import { InputNumberEditable } from '@/components/Format'
import Api from './api'
import { message } from 'antd'
import { uniqueId } from 'lodash'

const mockData = {
  configCode: 'PROJECT_SCALE',
  configDesc: '项目规模系数',
  configValue: [
    {
      configValueType: 'VALUE',
      projectScale: '≤3000',
      projectRadio: '7.2',
    },
    {
      configValueType: 'VALUE',
      projectScale: '＞3000',
      projectRadio: '4.5',
    },
  ],
}

const Index = ({ typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList({ baseId: typeInfo.id })
    detailData.current = res
    return res.configValue.map((v) => ({ ...v, id: uniqueId() }))
  }

  const saveData = async ({ list }) => {
    await Api.saveList({ ...detailData.current, configValue: list, id: typeInfo.id })
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
