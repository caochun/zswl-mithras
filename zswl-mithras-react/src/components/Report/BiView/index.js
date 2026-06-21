import { observer } from '@zswl/admin'

function ReportBiView({ url, style, ...res }) {
  return <iframe src={`${url}`} style={style} {...res} />
}

export default observer(ReportBiView)
