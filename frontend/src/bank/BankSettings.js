import { Box, Button, Grid, Paper, TextField, Typography } from "@material-ui/core"
import { useState } from "react"
import { changePassword } from "../store/action"
import { passwordMatcher } from "../user/auth/SignUp"

const BankSettings = props => {
  const [oldPassword, setOldPassword] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

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

  return <>{Notice}{PasswordSettings}</>
}

export default BankSettings