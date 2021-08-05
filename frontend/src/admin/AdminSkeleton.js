import { AppBar, Box, Button, Container, makeStyles, Toolbar, Typography } from "@material-ui/core";
import { Redirect, Route, Switch, useHistory } from "react-router-dom";
import store from "../store";
import { signOut } from "../store/action";
import AdminSettings from "./AdminSettings";
import Overview from "./Overview";
import UserAccess from "./UserAccess";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  },
}));

const AdminSkeleton = props => {
  const classes = useStyles()
  const history = useHistory()

  const mainContent = <Switch>
    <Route path='/admin/overview' exact>
      <Overview />
    </Route>
    <Route path='/admin/view'>
      <UserAccess />
    </Route>
    <Route path='/admin/settings'>
      <AdminSettings />
    </Route>
    <Route>
      <Redirect to='/admin/overview' />
    </Route>
  </Switch>

  return (
    <div className={classes.root}>
      <AppBar position="sticky">
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            BloodBank Admin
          </Typography>
          <Button color="inherit" onClick={() => history.push('/admin/overview')}>Overview</Button>
          <Button color="inherit" onClick={() => history.push('/admin/view')}>Change Access</Button>
          <Button color="inherit" onClick={() => history.push('/admin/settings')}>Settings</Button>
          <Button color="secondary" variant='contained' onClick={ () => store.dispatch(signOut())}>
            Log Out
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth='md' >
        <Box mt={3} mb={3}>
          {mainContent}
        </Box>
      </Container>
    </div>
  )
}

export default AdminSkeleton