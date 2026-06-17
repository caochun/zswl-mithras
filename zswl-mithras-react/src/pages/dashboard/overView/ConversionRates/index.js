import ConversionRates from '@/pages/dashboard/workbench/OperationView/ConversionRates'
import { initYearQueryDate } from '@/utils/dashboardOperation'

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
