import Handover from '@/pages/customer/maintain/handover/[id$]'

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
