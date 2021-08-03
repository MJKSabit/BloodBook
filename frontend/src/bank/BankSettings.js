import { Box, Button, Grid, Paper, TextField, Typography } from "@material-ui/core"
import { useState } from "react"
import ImageUploader from "../generic/ImageUploader"
import store from "../store"
import { changePassword, saveBankSettings } from "../store/action"
import { passwordMatcher } from "../user/auth/SignUp"

const BankSettings = props => {
  const [oldPassword, setOldPassword] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [imageURL, setImgURL] = useState(store.getState().profile && store.getState().profile.imageURL)
  const [about, setAbout] = useState(store.getState().profile && store.getState().profile.about);

  const About = <Paper>
    <Box p={4} mt={5} mb={5}>
      <Grid container spacing={2}  alignItems="center">
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
        <Grid item xs={12}>
          <TextField
            label="About"
            multiline
            rows={4}
            value={about}
            onChange={ e => setAbout(e.target.value) }
            variant="outlined"
            fullWidth
          />
        </Grid>

        <Grid item xs={12}>
        <Box display='flex' flexDirection="row-reverse">
          <Box>
            <Button color="primary" variant='contained' onClick={ (e) => {
              saveBankSettings({
                imageURL, 
                about
              }).then()
            }}>
              Save Settings
            </Button>
          </Box>
        </Box>
      </Grid>

      </Grid>
    </Box>
  </Paper>

  const Notice = <Paper>
    <Box p={4} mt={5} mb={5}>
    <Typography variant='subtitle1'>
      To change other settings, please contact us with proper document.
    </Typography>
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
            type='password'
            fullWidth
            label='Old Password'
            value={oldPassword}
            onChange={e => setOldPassword(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            variant="outlined"
            type='password'
            fullWidth
            label='New Password'
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            variant="outlined"
            type='password'
            fullWidth
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

  return <>{About}{Notice}{PasswordSettings}</>
}

export default BankSettings