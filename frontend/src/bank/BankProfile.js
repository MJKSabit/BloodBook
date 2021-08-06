import DateFnsUtils from "@date-io/date-fns"
import { Avatar, Backdrop, Box, Button, Card, CardContent, Checkbox, CircularProgress, Dialog, DialogContent, DialogTitle, FormControlLabel, Grid, Paper, TextField, Typography } from "@material-ui/core"
import { Email, LocationOn } from "@material-ui/icons"
import { KeyboardDatePicker, MuiPickersUtilsProvider } from "@material-ui/pickers"
import { useEffect } from "react"
import { useState } from "react"
import { useHistory, useParams } from "react-router-dom"
import LocationSelector from "../generic/LocationSelector"
import LocationViewer from "../generic/LocationViewer"
import store from "../store"
import { addEvent, getBankBloodCount, getOtherBankProfile, notifyUser } from "../store/action"
import Events from "./Events"
import "../user/opt/profile.css"

const ProfileCard = props => {
  const {user} = props

  if (!user)
    return null

  return <Box mt={5}>
    <Paper>
      <Box p={6} >
        <Grid container spacing={4}>
          <Grid item xs={12} sm={4}>
            <div className={'square'}>
                <Avatar 
                  style={{position: 'absolute', top: '0', left: '0', height: '100%', width: '100%'}}
                  src={user.imageURL}/>
              </div>
          </Grid>
          <Grid item xs={12} sm={8}>
            <Grid
              container
              direction="column"
              justifyContent="flex-start"
              alignItems="flex-start"
              spacing={2}
            >
              <Grid item>
                <Typography variant='h4'>
                  {user.name}
                </Typography>
              </Grid>
              <Grid item>
                <Grid
                  container
                  alignItems="center"
                  spacing={1}
                >
                  <Grid item>
                    <Email />
                  </Grid>
                  <Grid item>
                    {user.user.email}
                  </Grid>
                </Grid>
              </Grid>
              <Grid item>
                <Grid
                  container
                  alignItems="center"
                  spacing={1}
                >
                  <Grid item>
                    <LocationOn/>
                  </Grid>
                  <Grid item>
                    <LocationViewer location={user.user.location} />
                  </Grid>
                </Grid>
              </Grid>
              <Grid item>
                <Typography variant='subtitle2' style={{whiteSpace: "pre-wrap"}}>
                  {user.about}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  </Box>
}

const BGCountCard = ({bloodGroup, inStock}) => {
  return <Card variant='outlined'>
    <CardContent>
      <Typography color='secondary' component='p'>
        {bloodGroup}
      </Typography>
      <Typography variant='h2' align='right'>
        {inStock}
      </Typography>
    </CardContent>
  </Card>
}

const CountCard = (props) => {
  const {username} = props
  const [data, setData] = useState(null)

  useEffect(() => {
    getBankBloodCount(username).then(
      dt => setData(dt),
      err => console.log(err)
    )
  }, [])

  if (data === null) 
    return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return <Paper>
    <Box p={3} mt={5}>
      <Typography variant='body1' style={{marginBottom: '20px'}}>Blood in Stock</Typography>
      <Grid container spacing={3}>
        { data.map(value => (
          <Grid item md={3} sm={4} xs={6}>
            <BGCountCard bloodGroup={value.bloodGroup} inStock={value.inStock} />
          </Grid>
        ))}
      </Grid>
    </Box>
  </Paper>
}

const CreateEvent = (props) => {
  const location = store.getState().profile.user.location
  const history = useHistory()

  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [selectedDate, setSelectedDate] = useState(new Date())
  const [notify, setNotify] = useState(true)
  const [info, setInfo] = useState('');
  const [longitude, setLongitude] = useState(location.longitude)
  const [latitude, setLatitude] = useState(location.latitude)

  const createEventDialog = (
    <Dialog fullWidth maxWidth='sm' open={open} onClose={() => setOpen(false)}>
      <DialogTitle>Create Event</DialogTitle>
      <DialogContent>
        <Grid container>
          <Grid item xs={12}>
            <MuiPickersUtilsProvider utils={DateFnsUtils} fullWidth>
              <KeyboardDatePicker
                inputVariant="outlined"
                fullWidth
                disableToolbar
                variant="inline"
                format="dd/MM/yyyy"
                margin="normal"
                id="date-picker-inline"
                label="Event Date"
                value={selectedDate} 
                onChange={ (e) => setSelectedDate(e)}
                KeyboardButtonProps={{
                  'aria-label': 'change date',
                }}
              />
            </MuiPickersUtilsProvider>
          </Grid>
          
          <Grid item xs={4}>
            <Typography variant='body2'>
              Location
            </Typography>
          </Grid>
          <Grid item xs={8}>
            <LocationSelector onSelected={
              (lat, long) => {setLatitude(lat); setLongitude(long)}
            } lat={latitude} long={longitude} />
          </Grid>

          <Grid item xs={12}>
            <TextField
              label="Event Information"
              multiline
              rows={4}
              value={info}
              onChange={ e => setInfo(e.target.value) }
              variant="outlined"
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={<Checkbox checked={notify} onChange={ (e) => setNotify(e.target.checked)} />}
              label="Notify BloodBook Users"
            />
          </Grid>

          <Grid item xs={12}>
            <Grid container spacing={5}>
              <Grid item xs={6}>
                <Button fullWidth variant="outlined" color='secondary' onClick={() => setOpen(false)}>Cancel</Button>
              </Grid>
              <Grid item xs={6}>
                <Button fullWidth variant="contained" color="primary" onClick={
                  () => {
                    setLoading(true)
                    addEvent({
                      info, 
                      location: {
                        latitude,
                        longitude
                      },
                      eventDate: selectedDate.getTime()
                    }, notify).then(
                      data => {
                        history.push('/bloodbank/post/'+data.id)
                        setOpen(false)
                        setLoading(false)
                      }, 
                      err => {console.log(err); setLoading(false)})
                  }
                }>Post</Button>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </DialogContent>
    </Dialog>
  )

  return <>
    <Box pt={5}>
      <Box>
        <Button fullWidth 
          onClick={() => setOpen(true)} variant="contained" color="primary" mt='10'>
          Create New Event
        </Button>
      </Box>
    </Box>
    <Backdrop open={loading} onClick={ () => store.dispatch(notifyUser('Still Loading!'))} style={{zIndex: '99999'}}>
        <CircularProgress color="inherit" />
      </Backdrop>
    {createEventDialog}
  </>
}

const BankProfile = (props) => {
  
  const [profile, setProfile] = useState(store.getState().profile)

  useEffect(() => store.subscribe( () => setProfile(store.getState().profile)), [])

  if (profile === null)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  const username = profile.user.username

  return <>
    <ProfileCard user={profile}/>
    <CountCard username={username} />
    <CreateEvent />
    <Box mt={5} mb={5}>
      <Events url={`/bloodbank/events/${username}`} label='Events'/>
    </Box>
  </>
}

export const OtherBankProfile = props => {
  const {username} = useParams()
  const userType = props.userType || 'bloodbank'

  const [profile, setProfile] = useState(null)

  useEffect( () => {
    getOtherBankProfile(username).then(
      prof => setProfile(prof)
    )
  }, [])

  if (profile === null)
  return <Box display='flex' style={{alignItems: 'center', justifyContent: 'center'}} my={5}> <CircularProgress /></Box>

  return <>
    <ProfileCard user={profile}/>
    <CountCard username={username} />
    <Box mt={5} mb={5}>
      <Events url={`/bloodbank/events/${username}`} label='Events' userType={userType}/>
    </Box>
  </>
}

export default BankProfile