import { Typography } from "@material-ui/core";
import { useEffect, useState } from "react";
import { Redirect, Route, Switch } from "react-router-dom";
import store from "../store";
import ForgotPassword from "../user/auth/ForgotPassword";
import SignIn from "../user/auth/SignIn";

export default function Admin() {
  const [isSignedIn, setSignedIn] = useState(store.getState().jwt !== null)

  useEffect(() => {
    const unsubsrcibe = store.subscribe( 
      () => (null === store.getState().jwt ? setSignedIn(false) : setSignedIn(true))
    )

    return unsubsrcibe;
  }, [])

  const notSignedIn = (<Switch>
    <Route path="/admin/signin" exact>
      <SignIn userType='admin' />
    </Route>
    <Route path="/admin/forgot" exact>
      <ForgotPassword userType='admin' />
    </Route>
    <Route>
      <Redirect to="/admin/signin" />
    </Route>
  </Switch>)

  const signedIn = (
    <Typography>Hello Admin</Typography>
  )

  return isSignedIn ? signedIn : notSignedIn
}