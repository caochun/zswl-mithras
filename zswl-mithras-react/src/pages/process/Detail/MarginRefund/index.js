import MarginRefund from '@/components/Contract/MarginRefund'
import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'


const ProjectEstablishment = (props) => {
    const { canEditFlag, businessVersion, id } = props

    return (
        <MarginRefund
            {...props}
            params={{ id }}
            query={{
                businessKey:id,
                canEditFlag: canEditFlag ? 'true' : 'false',
                businessVersion,
            }}
        />
    )
}
export default observer(ProjectEstablishment)
