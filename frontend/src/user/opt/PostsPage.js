import store from "../../store"
import CreatePost from "./CreatePost"
import Posts from "./Posts"
import PostSelection from "./PostSelection"

const PostPage = props => {
  if (!store.getState().profile)
    return null

  return (
    <>
      <CreatePost />
      <PostSelection />
      {/* <Posts url='/user/posts?for=my' label='Posts For Me'/> */}
    </>
  )
}


export default PostPage