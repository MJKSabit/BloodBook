import DateFnsUtils from "@date-io/date-fns"
import { Box, Button, Checkbox, FormControlLabel, Grid, Link, Paper, TextField, Typography } from "@material-ui/core"
import { KeyboardDatePicker, MuiPickersUtilsProvider } from "@material-ui/pickers"
import { useEffect } from "react"
import { useState } from "react"
import ImageUploader from "../../generic/ImageUploader"
import LocationSelector from "../../generic/LocationSelector"
import store from "../../store"
import { changePassword, deleteMessengerToken, getMessengerToken, saveSettings } from "../../store/action"
import { passwordMatcher } from "../auth/SignUp"

const Settings = props => {
  const [profile, setProfile] = useState(store.getState().profile)

  useEffect(() => (
    store.subscribe( () => setProfile(store.getState().profile) )
  ), [])

  const [name, setName] = useState(profile && profile.name)
  const location = profile && profile.user.location

  const [longitude, setLongitude] = useState(location && location.longitude)
  const [latitude, setLatitude] = useState(location && location.latitude)
  const [about, setAbout] = useState(profile && profile.about);
  const [imageURL, setImgURL] = useState(profile && profile.imageURL)
  const [isActiveDonor, setActive] = useState(profile && profile.isActiveDonor)
  const [selectedDate, setSelectedDate] = useState(profile && new Date(profile.lastDonation));

  const [facebookToken, setFacebookToken] = useState('')

  const [oldPassword, setOldPassword] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

  if (!profile)
    return null

  const GeneralSettings = (<Paper>
    <Box p={4} mt={5} mb={5}>
    <Grid container spacing={2}  alignItems="center">
      <Grid item xs={12}>
        <Typography variant='h4'>
          General Settings
        </Typography>
      </Grid>
      <Grid item xs={12}>
        <TextField fullWidth variant='outlined'
          label='Full Name'
          value={name}
          onChange={e => setName(e.target.value)}
        />
      </Grid>
      
      <Grid item xs={4}>
        <Typography variant='body2'>
          Profile Picture
        </Typography>
      </Grid>
      <Grid item xs={8}>
        <ImageUploader onUpload={
          url => setImgURL(url)
        } init={imageURL}/>
      </Grid>

      <Grid item xs={4}>
        <Typography variant='body2'>
          Approximate Location
        </Typography>
      </Grid>
      <Grid item xs={8}>
        <LocationSelector onSelected={
          (lat, long) => {setLatitude(lat); setLongitude(long)}
        } lat={latitude} long={longitude} />
      </Grid>
      
      <Grid item xs={12}>
        <TextField
          label="About Yourself"
          multiline
          rows={4}
          value={about}
          onChange={ e => setAbout(e.target.value) }
          variant="outlined"
          fullWidth
        />
      </Grid>

      <Grid item xs={12}>
        <MuiPickersUtilsProvider utils={DateFnsUtils} fullWidth>
          <KeyboardDatePicker
            fullWidth
            disableToolbar
            variant="inline"
            format="dd/MM/yyyy"
            margin="normal"
            id="date-picker-inline"
            label="Last Donation Date"
            value={selectedDate} 
            onChange={ (e) => setSelectedDate(e)}
            KeyboardButtonProps={{
              'aria-label': 'change date',
            }}
          />
        </MuiPickersUtilsProvider>
      </Grid>
      
      <Grid item xs={12}>
        <FormControlLabel
          control={<Checkbox checked={isActiveDonor} onChange={ (e) => setActive(e.target.checked)} />}
          label="Active Blood Donor"
        />
      </Grid>
      
      <Grid item xs={12}>
        <Box display='flex' flexDirection="row-reverse">
          <Box>
            <Button color="primary" variant='contained' onClick={ (e) => {
              saveSettings({
                name, 
                imageURL, 
                lastDonation: selectedDate.getTime(), 
                isActiveDonor, 
                about, 
                latitude, 
                longitude
              }).then()
            }}>
              Save Settings
            </Button>
          </Box>
        </Box>
      </Grid>
    </Grid>
  </Box>
  </Paper>)

  const FacebookTokenSettings = <Paper>
    <Box p={4} mt={5} mb={5}>
      <Grid container spacing={2}  alignItems="center">
        <Grid item xs={8}>Facebook Connection</Grid>
        <Grid item xs={4}>
          <Button fullWidth disabled={profile.facebook === null} 
          variant='outlined' color='secondary' 
          onClick={ e => deleteMessengerToken()}>
            Disconnect
          </Button>
        </Grid>
        <Grid item xs={8}>
          <TextField fullWidth variant='outlined'
            label='Messenger Token'
            value={facebookToken}
          />
        </Grid>
        <Grid item xs={4}>
          <Button fullWidth color="primary" variant='contained' 
          onClick={ e => getMessengerToken().then(token => setFacebookToken(token))}>
            Generate
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Typography variant='subtitle2'>
            Copy this token and send a messeage to <Link href='https://www.facebook.com/BloodBook-101567685499403/'>BloodBook Facebook Page</Link>
          </Typography>
        </Grid>
      </Grid>
    </Box>
  </Paper>

  const PasswordSettings = <Paper>
    <Box p={4} mt={5} mb={5}>
      <Grid container spacing={2}  alignItems="center">
        <Grid item xs={12}>
          <Typography variant='subtitle1'>
            Make sure new password and new confirm password matches with length of 8~20
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <TextField
            variant="outlined"
            fullWidth
            label='Old Password'
            type='password'
            value={oldPassword}
            onChange={e => setOldPassword(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            variant="outlined"
            fullWidth
            label='New Password'
            type='password'
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            variant="outlined"
            fullWidth
            type='password'
            label='New Password (Confirm)'
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
          />
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <Box display='flex' flexDirection="row-reverse" mt={4}>
          <Box>
            <Button color="primary" variant='contained' onClick={ (e) => {
              changePassword(oldPassword, password).then(
                () => {setPassword(''); setOldPassword(''); setConfirmPassword('')}
              ).catch(err => console.log(err))
            }}
            disabled={password !== confirmPassword || !passwordMatcher.test(password) || oldPassword === ''}>
              Change Password
            </Button>
          </Box>
        </Box>
      </Grid>
    </Box>
  </Paper>

  return <>{GeneralSettings}{FacebookTokenSettings}{PasswordSettings}</>
}

export default Settings