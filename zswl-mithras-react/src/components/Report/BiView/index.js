import { observer } from '@zswl/admin'

function Index({ url, style, ...res }) {
  return <iframe src={`${url}`} style={style} {...res} />
}

export default observer(Index)
