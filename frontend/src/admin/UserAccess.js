import { Box, Button, Card, CardActions, CardHeader, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, FormControlLabel, Grid, IconButton, InputLabel, MenuItem, Paper, Select } from "@material-ui/core"
import { Edit, Lock } from "@material-ui/icons"
import { useEffect } from "react"
import { useState } from "react"
import ReactJson from "react-json-view"
import store from "../store"
import { getUserData, getUserList, notifyUser, setUserData, setUserStatus } from "../store/action"

const UserCard = ({user, active, banned, role}) => {
  return 
}

const UserAccess = props => {

  const [active, setActive] = useState(true)
  const [banned, setBanned] = useState(false)
  const [role, setRole] = useState('USER')

  const url = `/admin/users?role=${role}&active=${active}&banned=${banned}`

  const [users, setUsers] = useState([])
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setPage(0)
    setUsers([])
    setHasMore(true)
    setLoading(false)
  }, [active, banned, role])

  const fetchData = () => {
    if (url && hasMore) {
      setLoading(true)
      getUserList(url, page).then(response => {    
        setUsers(users.concat(response.content))
        setHasMore(response.hasNext)
        setPage(response.page+1)
        setLoading(false)
      })
    }
  }

  const [username, setUsername] = useState(null)
  const [dialogActive, setDialogActive] = useState(true)
  const [dialogBanned, setDialogBanned] = useState(false)
  const [openPrivilageDialog, setOpenPrivilageDialog] = useState(false);

  const handlePrivilageDialogClose = () => {
    setOpenPrivilageDialog(false);
  };

  const handleChangePrivilage = (username) => {
    setUsername(username)
    setDialogActive(active)
    setDialogBanned(banned)
    setOpenPrivilageDialog(true)
  }

  const privilageDialog = <Dialog open={openPrivilageDialog} onClose={handlePrivilageDialogClose} >
    <DialogTitle>Change "{username}" Status</DialogTitle>
    <DialogContent>
      <Grid container >
        <Grid item xs={6} >
          <FormControlLabel
            control={<Checkbox checked={dialogActive} onChange={ (e) => setDialogActive(e.target.checked)} />}
            label="Active"
          />
        </Grid>
        <Grid item xs={4} >
          <FormControlLabel
            control={<Checkbox checked={dialogBanned} onChange={ (e) => setDialogBanned(e.target.checked)} />}
            label="Banned"
          />
        </Grid>
      </Grid>
    </DialogContent>
    <DialogActions>
      <Button onClick={handlePrivilageDialogClose} color="primary">
        Cancel
      </Button>
      <Button onClick={ () => {
        setOpenPrivilageDialog(false)
        setUserStatus({username, active: dialogActive, banned: dialogBanned}).then(
          () => {
            setUsers(users.filter( value => value.username !== username))
            store.dispatch(notifyUser('Updated "'+username+'"'))
          }
        )
        
      }} color='primary' variant='contained'>
        Update
      </Button>
    </DialogActions>
  </Dialog>

  const [openUserDetailsDialog, setOpenUserDetailsDialog] = useState(false);
  const [userDetails, setUserDetails] = useState(null)

  const handleUserDetailsChange = (username) => {
    store.dispatch(notifyUser("Loading ..."))
    getUserData(role.toLowerCase(), username).then( data => {
      setUsername(username)
      setUserDetails(data)
      setOpenUserDetailsDialog(true)
    })
  }

  const handleDetailsDialogClose = () => {
    setOpenUserDetailsDialog(false);
  };

  const detailsDialog = <Dialog open={openUserDetailsDialog} onClose={handleDetailsDialogClose}>
    <DialogTitle>Change "{username}" Details</DialogTitle>
    <DialogContent>
      <ReactJson src={userDetails} onEdit={ (e) => setUserDetails(e.updated_src) } 
        indentWidth={2} quotesOnKeys={false}/>
    </DialogContent>
    <DialogActions>
      <Button onClick={handleDetailsDialogClose} color="primary">
        Cancel
      </Button>
      <Button onClick={ () => {
        setOpenUserDetailsDialog(false)
        setUserData(role.toLowerCase(), username, userDetails).then(
          (data) => {
            store.dispatch(notifyUser('Updated "'+username+'" details'))
            setUsers(users.map(user => user.username === username ? data.user : user))
          }
        )}} color='primary' variant='contained'>
        Update
      </Button>
    </DialogActions>
  </Dialog>

  const userList = <Grid container alignContent='center' spacing={3} style={{marginTop: '10px'}}>
    {users.map(user => (
      <Grid item xs={12} sm={6} md={4} >
        <Card>
          <CardHeader title={user.username}
            subheader={user.email}/>
          <CardActions disableSpacing>
            <IconButton onClick={() => handleChangePrivilage(user.username)}>
              <Lock />
            </IconButton>
            <IconButton onClick={() => handleUserDetailsChange(user.username)}>
              <Edit />
            </IconButton>
          </CardActions>
        </Card>
      </Grid>
    ))}
    <Grid item xs={12}>
      <Box display='flex' flexDirection="row-reverse" marginY={2}>
        <Box style={{visibility: hasMore ? 'visible': 'hidden'}}>
          <Button variant='contained' color='primary' onClick={() => fetchData()} disabled={loading} fullWidth={true}>
            Load...
          </Button>
        </Box>
      </Box>
    </Grid>
  </Grid>

  return <Box p={3}>
    <Grid container alignItems="flex-end" >
      <Grid item xs={4} >
        <FormControlLabel
          control={<Checkbox checked={active} onChange={ (e) => setActive(e.target.checked)} />}
          label="Active"
        />
      </Grid>
      <Grid item xs={4} >
        <FormControlLabel
          control={<Checkbox checked={banned} onChange={ (e) => setBanned(e.target.checked)} />}
          label="Banned"
        />
      </Grid>
      <Grid item xs={4} >
        <FormControl fullWidth>
          <Select
            value={role}
            onChange={ (e) => setRole(e.target.value) }
          >
            <MenuItem value={'USER'}>User</MenuItem>
            <MenuItem value={'BLOODBANK'}>Blood Bank</MenuItem>
          </Select>
        </FormControl>
      </Grid>
    </Grid>
    {userList}
    {privilageDialog}
    {detailsDialog}
  </Box>
}

export default UserAccess