import { observer } from '@zswl/admin'
import { useRef } from 'react'
import ModalEditTable from '../../ModalEditTable'
import { formulaData } from '@/utils/domains/kpi/KpiUtils'

import { FormulaValueTip } from '@/components'
import { Tooltip } from 'antd'
import Api from './api'

const TYPE_ENMU = {
  PUBLIC: '公用事业类',
  CONSTRUCTION_MACHINERY: '工程机械类',
  OTHER: '其他',
  NORMAL: '正常类',
  ATTENTION: '关注类',
  SECONDARY: '次级类',
  SUSPICIOUS: '可疑类',
  LOSS: '损失类',
}

const VERSIBLE = 'T'

const Index = ({ baseStore, typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    const result = res.configValue.map((item, index) => {
      return {
        ...item,
        provisionRadio:
          item.configValueType === 'FORMULA' ? `=${item.provisionRadio}` : item.provisionRadio,
        $$index: index,
      }
    })
    return result
  }

  const saveData = async ({ list, values }) => {
    const configValue = []
    list.forEach((item, index) => {
      configValue.push({
        ...item,
        provisionRadio: formulaData(values[index].provisionRadio).value,
        configValueType: formulaData(values[index].provisionRadio).configValueType,
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
              title: '项目类型',
              dataIndex: 'projectType',
              render: (value) => TYPE_ENMU[value],
              onCell: (_, index) => {
                if ([0, 5, 10].includes(index)) {
                  return { rowSpan: 5 }
                }
                return { rowSpan: 0 }
              },
            },
            {
              title: '五级分类类型',
              dataIndex: 'assetClassify',
              render: (value) => TYPE_ENMU[value],
            },
            {
              title: (
                <div>
                  <span>拨备计提比例(%)</span>
                  <FormulaValueTip versible={VERSIBLE}></FormulaValueTip>
                </div>
              ),
              dataIndex: 'provisionRadio',
              editable: () => {
                return editable ? InputCalcEditable({ acceptCode: [VERSIBLE] }) : false
              },
              render: (value) => {
                return <Tooltip title={value}>{value}</Tooltip>
              },
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
