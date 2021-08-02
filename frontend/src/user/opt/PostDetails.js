import { useEffect } from "react"
import { useState } from "react"
import { useHistory, useParams } from "react-router-dom"
import { getPostDetails } from "../../store/action"
import { Post } from "./Posts"

const PostDetails = (props) => {
  let {id} = useParams()
  const history = useHistory()

  const [post, setPost] = useState(null)
  const [banks, setBanks] = useState(null)

  useEffect(() => {
    getPostDetails(id).then(
      data => {setPost(data.post); setBanks(data.bloodBanks)},
      err => {history.goBack(); console.log(err)}
    )
  })

  if (post === null)
    return null
  
  return (
    <>
      <Post post={post}/>
      {banks}
    </>
  )
}

export default PostDetails