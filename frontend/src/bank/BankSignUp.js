import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import { Link as RouterLink, useHistory } from 'react-router-dom';
import Copyright from '../user/auth/Copyright'
import LocationSelector from '../generic/LocationSelector';
import ImageUploader from '../generic/ImageUploader';
import { useState } from 'react';
import { notifyUser, signUpUser } from '../store/action';
import { useDispatch } from 'react-redux';
import { signUpBloodBank } from '../store/action';

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));

const usernameMatcher = /^[A-Za-z]\w{5,29}$/
export const passwordMatcher = /^.{8,20}$/
const emailMatcher = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}/

export default function BankSignUp(props) {
  const userType = props.userType || 'bloodbank'
  const classes = useStyles();

  const [error, setError] = useState({username: true, email: true, password: true, confirmPassword: true})

  const [username, setUsername] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [longitude, setLongitude] = useState(null)
  const [latitude, setLatitude] = useState(null)
  const [about, setAbout] = useState('');
  const [imageURL, setImgURL] = useState(null)

  const dispatch = useDispatch()
  const history = useHistory()

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Avatar className={classes.avatar}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">
          Sign up
        </Typography>
        <Typography variant="subtitle2" align='center'>
          Make sure:<br /> 
          Username: uses A~Z, a~z and _ with length 6~29 <br />
          Password: 8~20 characters + matches Confirm Password <br />
          Valid Email and All fields are filled.
        </Typography>
        <form className={classes.form} noValidate onSubmit={e => { e.preventDefault(); }}>
          <Grid container spacing={2} alignItems="center">
          <Grid item xs={12}>
              <TextField
                autoComplete="name"
                name="firstName"
                variant="outlined"
                required
                fullWidth
                label="Username"
                autoFocus
                value={username}
                error={error.username}
                onChange={e => setUsername(e.target.value)}
                onBlur={ e => {
                  if (!usernameMatcher.test(username)) {
                    setError({...error, username: true})
                    dispatch(notifyUser("Invalid Username (must be > 5 chars)"))
                  }
                  else setError({...error, username: false})
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                autoComplete="name"
                name="firstName"
                variant="outlined"
                required
                fullWidth
                label="BloodBank Name"
                value={name}
                onChange={e => setName(e.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                variant="outlined"
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                value={email}
                error={error.email}
                onChange={e => setEmail(e.target.value)}
                onBlur={ e => {
                  if (!emailMatcher.test(email)) {
                    setError({...error, email: true})
                    dispatch(notifyUser("Invalid E-mail"))
                  } else setError({...error, email: false})
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                variant="outlined"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                autoComplete="current-password"
                error={error.password}
                value={password}
                onChange={e => setPassword(e.target.value)}
                onBlur={ e => {
                  if (!passwordMatcher.test(password)) {
                    setError({...error, password: true})
                    dispatch(notifyUser("Invalid Password (not 8 chars)"))
                  } else setError({...error, password: false})
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                variant="outlined"
                required
                fullWidth
                name="password"
                label="Confirm Password"
                type="password"
                autoComplete="current-password"
                error={error.passwordMatcher}
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                onBlur={ e => {
                  if (password !== confirmPassword) {
                    setError({ ...error, confirmPassword: true})
                    dispatch(notifyUser("Passwords don't match"))
                  } else setError({...error, confirmPassword: false})
                }}
              />
            </Grid>
            <Grid item xs={4}>
              <Typography variant='body2'>
                Approximate Location
              </Typography>
            </Grid>
            <Grid item xs={8}>
              <LocationSelector onSelected={
                (lat, long) => {setLatitude(lat); setLongitude(long)}
              } lat={0} long={0} />
            </Grid>
            <Grid item xs={4}>
              <Typography variant='body2'>
                Profile Picture
              </Typography>
            </Grid>
            <Grid item xs={8}>
              <ImageUploader onUpload={
                url => setImgURL(url)
              }/>
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="About BloodBank"
                multiline
                rows={4}
                value={about}
                onChange={ e => setAbout(e.target.value) }
                variant="outlined"
                fullWidth
              />
            </Grid>
          </Grid>
          <Button
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
            disabled={error.username || error.email || error.confirmPassword || error.password}
            onClick={ e => {
              e.preventDefault()
              const data = {
                name, latitude, longitude, password,
                imageURL, about, email, username
              }
              signUpBloodBank(data).then(() => {
                dispatch(notifyUser('Check your Email!'))
                history.push('/bloodbank/signin')
              }).catch(e => console.log(e))
            }}
          >
            Sign Up
          </Button>
          <Grid container justifyContent="flex-end">
            <Grid item>
              <Link to={`/${userType}/signin`} variant='body2' component={RouterLink}>
                Already have an account? Sign in
              </Link>
            </Grid>
          </Grid>
        </form>
      </div>
      <Box mt={5}>
        <Copyright />
      </Box>
    </Container>
  );
}