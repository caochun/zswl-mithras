import ConversionRates from '@/components/Dashboard/OperationView/ConversionRates'
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
