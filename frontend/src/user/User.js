import React, {useState, useEffect} from 'react'
import logo from '../logo.svg';
import '../App.css';
import SignIn from './auth/SignIn';
import { Redirect, Route, Switch } from 'react-router-dom';
import { useDispatch, useStore } from 'react-redux';
import SignUp from './auth/SignUp';
import ForgotPassword from './auth/ForgotPassword';


export default function User() {
  const store = useStore()
  const [isSignedIn, setSignedIn] = useState(store.getState().jwt !== null)

  useEffect(() => {
    const unsubsrcibe = store.subscribe( 
      () => (null === store.getState().jwt ? setSignedIn(false) : setSignedIn(true))
    )

    return unsubsrcibe;
  })

  const notSignedIn = (<Switch>
    <Route path="/user/signin" exact>
      <SignIn userType='user' />
    </Route>
    <Route path="/user/signup" exact>
      <SignUp userType='user' />
    </Route>
    <Route path="/user/forgot" exact>
      <ForgotPassword userType='user' />
    </Route>
    <Route>
      <Redirect to="/user/signin" />
    </Route>
  </Switch>)

  const signedIn = (<Switch>
    <Route path='/user/profile' exact>
      User Profile
    </Route>
    <Route path='/user/profile/:username'>
      Other User Profile
    </Route>
    <Route>
      <Redirect to='/user/profile'/>
    </Route>
  </Switch>)

  return isSignedIn ? signedIn : notSignedIn
}