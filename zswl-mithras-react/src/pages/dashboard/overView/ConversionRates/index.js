import { DashboardOperationConversionRates as ConversionRates } from '@/components/Dashboard/DashboardEntries'
import { initYearQueryDate } from '@/dashboard/DashboardUtilsOperation'

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
