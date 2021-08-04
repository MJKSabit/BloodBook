import { Avatar, Box, List, ListItem, ListItemAvatar, ListItemText, Paper, Typography } from "@material-ui/core"
import { useEffect } from "react"
import { useState } from "react"
import { useHistory, useParams } from "react-router-dom"
import { getPostDetails } from "../../store/action"
import { Post } from "./Posts"

export const BankList = (props) => {
  const {banks} = props
  const userType = props.userType || 'user'
  const urlPrefix = userType === 'user' ? '/user/bloodbank/' : '/bloodbank/profile/'
  const history = useHistory()

  if (banks === null)
    return null

  return <Paper>
    <Box mt={5} mb={5}>
      <List>
        { banks.map( bank => (
          <ListItem>
            <ListItemAvatar>
              <Avatar src={bank.imageURL}/>
            </ListItemAvatar>
            <ListItemText
              primary={bank.name}
              secondary={`@${bank.user.username}`}
              onClick={() => history.push(`${urlPrefix}${bank.user.username}`)}
              />
          </ListItem>
        ))}
      </List>
    </Box>
  </Paper>
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
    return <Paper style={{padding: '20px'}}>Still Loading...</Paper>
  
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