import { Box, Button, Container, Grid, Paper, TextField, Typography } from "@material-ui/core"
import { useState } from "react"
import { useHistory, useParams } from "react-router-dom"
import store from "../store"
import { notifyUser, resetPassword } from "../store/action"
import { passwordMatcher } from "../user/auth/SignUp"

const ResetPassword = props => {
  const {jwt} = useParams()
  const history = useHistory()

  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

  return <Container maxWidth='sm'>
    <Paper>
      <Box p={4} mt={5} mb={5}>
        <Grid container spacing={2}  alignItems="center">
          <Grid item xs={12}>
            <Typography variant='h4' align='center'>
              Reset Password
            </Typography>
            <Typography variant='subtitle1'>
              Make sure new password and new confirm password matches with length of 8~20
            </Typography>
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
                resetPassword(jwt, password).then(
                  () => {
                    store.dispatch(notifyUser('Password Reset Successfully'))
                    history.push('/')
                  },
                  () => {
                    store.dispatch(notifyUser('Can not reset password'))
                    history.push('/')
                  }
                )
              }}
              disabled={password !== confirmPassword || !passwordMatcher.test(password)}>
                Reset Password
              </Button>
            </Box>
          </Box>
        </Grid>
      </Box>
    </Paper>
  </Container>

  
}

export default ResetPassword