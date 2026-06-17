import Handover from '@/components/Customer/HandoverDetail'

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
