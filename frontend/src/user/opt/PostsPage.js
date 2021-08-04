import { Box, CircularProgress, Paper } from "@material-ui/core"
import store from "../../store"
import CreatePost from "./CreatePost"
import Posts from "./Posts"
import PostSelection from "./PostSelection"

const PostPage = props => {
  if (!store.getState().profile)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return (
    <>
      <CreatePost />
      <PostSelection />
      {/* <Posts url='/user/posts?for=my' label='Posts For Me'/> */}
    </>
  )
}


export default PostPage