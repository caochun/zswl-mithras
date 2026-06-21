import { observer } from '@zswl/admin'
import SingleViewRisk from '../../../SingleViewRisk/CustomerSingleViewRisk'

function CustomerUnifiedViewCustomerRisk({ path, id, enterpriseName }) {
    return (
        <div style={{ height: '1042px' }}>
            <SingleViewRisk query={{ customerName: enterpriseName }} />
        </div>
    )
}

export default observer(CustomerUnifiedViewCustomerRisk)
