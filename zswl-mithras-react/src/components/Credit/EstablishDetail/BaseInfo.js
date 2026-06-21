import { FounderSelect, ClientSelect } from '@/components/Select'
import { EditDescription } from '@/components/Table'
import { FiledFormat } from '@/components/Format'
import ALL_COLUMNS from '../CreditColumns'
import { getDescColumns, rules } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import { Button } from '@zswl/components'

const nameColumns = [
  '授信名称',
  '授信编号',
  {
    title: '授信主体',
    dataIndex: 'clientId',

    width: 160,
  },
  '授信主体评级',
  '存量风险敞口(元)',
  '授信额度(元)',
  '额度是否可循环',
  '额度有效期(月)',
  '授信说明',
  '主办',
  '协办',
  '业务部门',
  '业务部门负责人',
  '业务分管领导',
  '风控经理',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function CreditEstablishBaseInfo({ detail, saveData, isLog, canEdit = true, newProject, updateInfo }) {
  const initEdit = newProject === 'true'
  const items = useMemo(() => {
    const riskManager = columns.find((v) => v.title === '风控经理')
    riskManager.requiredMark = false
    riskManager.editable = false
    riskManager.render = (_, { riskControlManagerName }) => (
      <FiledFormat title={riskControlManagerName?.join('、')} />
    )
    return columns
  }, [detail])
  return (
    <EditDescription
      detail={{
        ...detail,
        clientId: {
          label: detail?.clientName,
          value: detail?.clientId,
        },
      }}
      cancelExtra={
        <Button type="primary" onClick={updateInfo} style={{ marginRight: 8 }}>
          更新评级信息
        </Button>
      }
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={items}
    />
  )
}

export default observer(CreditEstablishBaseInfo)
