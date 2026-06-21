import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from '../CreditColumns'
import { Button } from '@zswl/components'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'

const nameColumns = [
  '授信名称',
  '授信编号',
  '授信主体',
  '授信主体评级',
  '存量风险敞口(元)',
  '授信额度(元)',
  '项目批复金额(元)',
  '额度是否可循环',
  '额度有效期(月)',
  '风控行业分类',
  '授信说明',
  '主办',
  '协办',
  '业务部门',
  '业务部门负责人',
  '业务分管领导',
  '风控经理',
  '法务经理',
]

const columns = getDescColumns(ALL_COLUMNS, nameColumns)

function CreditReviewBaseInfo({ detail, saveData, isLog, canEdit = true, initEdit, updateInfo }) {
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
      columns={columns}
      initEdit={initEdit}
    />
  )
}

export default observer(CreditReviewBaseInfo)
