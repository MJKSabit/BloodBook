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

function App() {
  const [open, setOpen] = useState(false);
  const [notification, setNotification] = useState(null)

  store.subscribe( () => {
    const stateNotification = store.getState().notification
    if (stateNotification !== null && stateNotification!==notification) {
      setNotification(stateNotification)
      if (!open)
        setOpen(true)
    }
  })

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
            BloodBank Page
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
