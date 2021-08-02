import CreatePost from "./CreatePost"
import Posts from "./Posts"

const PostPage = props => {
  return (
    <>
      <CreatePost />
      <Posts url='/user/posts?for=my' label='Posts For Me'/>
    </>
  )
}


export default PostPage