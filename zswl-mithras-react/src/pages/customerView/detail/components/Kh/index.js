import { getQuery, observer } from '@zswl/admin'
import SingleViewRisk from '@/pages/customer/maintain/singleViewRisk'

function Index({ path, id, enterpriseName }) {
    return (
        <div style={{ height: '1042px' }}>
            <SingleViewRisk query={{ customerName: enterpriseName }} />
        </div>
    )
}

export default observer(Index)
