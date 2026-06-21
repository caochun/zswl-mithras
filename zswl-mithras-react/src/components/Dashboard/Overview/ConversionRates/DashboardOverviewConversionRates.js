import ConversionRates from '../../OperationView/ConversionRates/DashboardOperationConversionRates'
import { initYearQueryDate } from '@/utils/domains/dashboard/DashboardUtilsOperation'

const Index = ({ title }) => {
  return (
    <>
      <ConversionRates
        initialQuery={{ queryDate: initYearQueryDate }}
        title={title}
      ></ConversionRates>
    </>
  )
}
export default Index
