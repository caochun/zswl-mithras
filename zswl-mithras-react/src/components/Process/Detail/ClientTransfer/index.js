import { CustomerHandoverDetail as Handover } from '@/components/Customer/HandoverEntries'

const ClientTransfer = ({ id, businessVersion, canEditFlag, processInstanceId }) => {
  return (
    <Handover
      params={{ id }}
      canEdit={canEditFlag}
      query={{
        businessVersion,
        processInstanceId,
      }}
    />
  )
}
export default ClientTransfer
