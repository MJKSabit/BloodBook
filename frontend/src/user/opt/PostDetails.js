import { Avatar, Box, CircularProgress, Divider, List, ListItem, ListItemAvatar, ListItemText, Paper, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { useHistory, useParams } from "react-router-dom"
import store from "../../store"
import { getPostDetails } from "../../store/action"
import { Post } from "./Posts"

export const BankList = (props) => {
  const {banks} = props
  const userType = props.userType || 'user'
  const urlPrefix = userType === 'user' ? '/user/bloodbank/' : '/bloodbank/profile/'
  const history = useHistory()

  if (banks === null)
  return <Typography variant='subtitle2' style={{paddingLeft: '10px', marginTop: '30px'}}>Only Post Owner can see list of bloodbanks!</Typography>

  if (banks.length === 0)
    return <Typography variant='subtitle2' style={{paddingLeft: '10px', marginTop: '30px'}}>No Matches!</Typography>

  return ( //<Paper square>
    <Box mt={5} mb={5}>
      <List>
        <Divider />
        { banks.map( bank => ([
          <ListItem button style={{padding: '20px'}} 
            onClick={() => history.push(`${urlPrefix}${bank.user.username}`)}>
            <ListItemAvatar>
              <Avatar src={bank.imageURL}/>
            </ListItemAvatar>
            <ListItemText
              primary={bank.name}
              secondary={`@${bank.user.username}`}
              />
          </ListItem>,
          <Divider/>]
        ))}
      </List>
    </Box>)
  // </Paper>
}

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
  }, [])

  if (post === null)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>
  
  return (
    <>
      <Post post={post}/>
      <Box mt={5}>
        <Typography color='textSecondary' variant='h5'> BloodBank matching requirements: </Typography>
      </Box>
      <BankList banks={banks} />
    </>
  )
}

export default PostDetails