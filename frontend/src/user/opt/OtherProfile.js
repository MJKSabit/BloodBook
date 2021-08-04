import Posts from "./Posts"
import UserCard from "./UserCard"
import { useParams } from "react-router-dom"
import { useState } from "react"
import { useEffect } from "react"
import { getUserProfile } from "../../store/action"
import { Box, CircularProgress } from "@material-ui/core"

const OtherProfile = props => {
  let {username} = useParams()
  const [profile, setProfile] = useState(null)

  useEffect( () => {
    getUserProfile(username).then(
      data => setProfile(data),
      err => console.log(err)
    )
  }, [])

  if (profile === null) 
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return (<>
    <UserCard user={profile} />
    <Posts url={`/user/posts/${username}`} label='Posts'/>
  </>)
}

export default OtherProfile