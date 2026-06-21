import ConversionRates from '../../OperationView/ConversionRates/DashboardOperationConversionRates'
import { initYearQueryDate } from '@/utils/domains/dashboard/DashboardUtilsOperation'

const DashboardOverviewConversionRates = ({ title }) => {
  return (
    <>
      <ConversionRates
        initialQuery={{ queryDate: initYearQueryDate }}
        title={title}
      ></ConversionRates>
    </>
  )
}
export default DashboardOverviewConversionRates
