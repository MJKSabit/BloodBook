import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect
} from "react-router-dom";
import User from './user/User';
import { Provider } from 'react-redux';
import store from './store';
import Admin from './admin';
import { Snackbar } from '@material-ui/core';
import { useState } from 'react';
import BloodBank from './bank';
import ActivateAccount from './jwt/ActivateAccount';
import ResetPassword from './jwt/ResetPassword';

function App() {
  const [open, setOpen] = useState(false);
  const [notification, setNotification] = useState(store.getState().notification)

  store.subscribe( () => {
    const stateNotification = store.getState().notification
    if (stateNotification !== null && stateNotification!==notification) {
      setNotification(stateNotification)
      if (!open)
        setOpen(true)
    }
  }, [])

  return (
    <Provider store={store}>
      <Router>
        <Switch>
          <Route path='/user'>
            <User />
          </Route>
          <Route path='/admin'>
            <Admin />
          </Route>
          <Route path='/bloodbank'>
            <BloodBank />
          </Route>
          <Route path='/activate/:jwt'>
            <ActivateAccount />
          </Route>
          <Route path='/activate'>
            <ActivateAccount />
          </Route>
          <Route path='/forgot/:jwt'>
            <ResetPassword />
          </Route>
          <Route path='/forgot'>
            <ResetPassword />
          </Route>
          <Route>
            <Redirect to='/user' />
          </Route>
        </Switch>
      </Router>
      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        open={open}
        autoHideDuration={2000}
        onClose={ () => {setOpen(false)}}
        message={notification}
      />
    </Provider>
  );
}

export default App;
