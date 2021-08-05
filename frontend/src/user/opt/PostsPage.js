import { Box, CircularProgress } from "@material-ui/core"
import store from "../../store"
import PostSelection from "./PostSelection"

const PostPage = props => {
  if (!store.getState().profile)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return (
      <PostSelection />
  )
}


export default PostPage