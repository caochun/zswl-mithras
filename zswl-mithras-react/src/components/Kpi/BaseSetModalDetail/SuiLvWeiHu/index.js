import { observer } from '@zswl/admin'
import { useRef } from 'react'
import { InputNumberEditable } from '@/components/Format'
import ModalEditTable from '../../ModalEditTable'
import { formulaData } from '@/kpi/KpiUtils'
import Api from './api'

const TAX_TYPE_ENMU = {
  ZZS: '增值税',
  CJS: '城建税',
  JYFJS: '教育附加税',
  DFJYFJS: '地方教育附加税',
  YHS: '印花税',
  ZL_ZZ: '直租',
  ZL_HZ: '回租',
  ZL_JYX: '经营性租赁',
  BL: '保理',
  ZR: '债权转让',
  RZZLHT: '融资租赁合同',
  ZXHT: '咨询合同',
  CGJXSHT: '采购及销售合同',
  JYZLHT: '经营租赁合同',
}

const Index = ({ baseStore, typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    const result = res.configValue.map((item, index) => {
      return {
        ...item,
        $$index: index,
      }
    })
    return result
  }

  const saveData = async ({ list, values }) => {
    const configValue = []
    list.forEach(({ taxType, bizType }, index) => {
      configValue.push({
        taxType,
        bizType,
        taxRate: formulaData(values[index].taxRate).value,
        configValueType: formulaData(values[index].taxRate).configValueType,
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
        columnWidth={160}
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        baseStore={baseStore}
        columns={(editable) => {
          return [
            {
              title: '税种',
              dataIndex: 'taxType',
              onCell: (_, index) => {
                if (index === 0) {
                  return { rowSpan: 5 }
                }
                if ([1, 2, 3, 4, 9, 10, 11].includes(index)) {
                  return { rowSpan: 0 }
                }
                if (index === 8) {
                  return { rowSpan: 4 }
                }
              },
              render: (value) => TAX_TYPE_ENMU[value],
            },
            {
              title: '业务类型',
              dataIndex: 'bizType',
              render: (value) => TAX_TYPE_ENMU[value],
            },
            {
              title: '税率',
              dataIndex: 'taxRate',
              editable: () => {
                return editable
                  ? InputNumberEditable({
                      required: true,
                      precision: 3,
                      addonAfter: '%',
                    })
                  : false
              },
              render: (value) => value + '%',
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
