import { useRef } from 'react'
import { observer } from '@zswl/admin'
import ModalEditTable from '../../../ModalEditTable/KpiModalEditTable'
import { InputNumberEditable } from '@/components/Format'
import Api from '@/api/kpi/baseSet/baseSetApi'
import { uniqueId } from 'lodash'

const KpiBaseSetPutPrizeFactor = ({ typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getPutPrizeFactor({ baseId: typeInfo.id })
    detailData.current = res
    return res.configValue.map((v) => ({ ...v, id: uniqueId() }))
  }

  const saveData = async ({ list }) => {
    await Api.savePutPrizeFactor({ ...detailData.current, configValue: list, id: typeInfo.id })
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
              title: '项目类别',
              dataIndex: 'projectType',
            },
            {
              title: '系数',
              dataIndex: 'projectRadio',
              editable: () => {
                return editable ? InputNumberEditable({ precision: 2, addonAfter: '%' }) : false
              },
              render: (value) => value + '%',
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(KpiBaseSetPutPrizeFactor)
