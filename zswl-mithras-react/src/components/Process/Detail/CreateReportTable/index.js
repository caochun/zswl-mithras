import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CreditTable from '@/components/CreditManage/CreditTableWaitEntries'

const ProcessDetailCreateReportTable = (props) => {
  const { canEditFlag, businessKey, businessVersion, processInstanceId } = props
  const renderContractType = useMemo(() => {
    return (
      <CreditTable
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          businessKey,
          businessVersion,
          processInstanceId,
        }}
      />
    )
  }, [businessKey, businessVersion, canEditFlag])

  return renderContractType
}
export default observer(ProcessDetailCreateReportTable)
