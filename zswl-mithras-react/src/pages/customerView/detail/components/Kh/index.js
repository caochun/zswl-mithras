import { getQuery, observer } from '@zswl/admin'
import SingleViewRisk from '@/components/Customer/SingleViewRisk'

function Index({ path, id, enterpriseName }) {
    return (
        <div style={{ height: '1042px' }}>
            <SingleViewRisk query={{ customerName: enterpriseName }} />
        </div>
    )
}

export default observer(Index)
