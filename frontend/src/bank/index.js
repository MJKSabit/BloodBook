import { Typography } from "@material-ui/core";
import { useEffect, useState } from "react";
import { Redirect, Route, Switch } from "react-router-dom";
import store from "../store";
import ForgotPassword from "../user/auth/ForgotPassword";
import SignIn from "../user/auth/SignIn";
import BankSignUp from "./BankSignUp";

export default function BloodBank() {
  const [isSignedIn, setSignedIn] = useState(store.getState().jwt !== null)

  useEffect(() => {
    const unsubsrcibe = store.subscribe( 
      () => (null === store.getState().jwt ? setSignedIn(false) : setSignedIn(true))
    )

    return unsubsrcibe;
  }, [])

  const notSignedIn = (<Switch>
    <Route path="/bloodbank/signin" exact>
      <SignIn userType='bloodbank' />
    </Route>
    <Route path="/bloodbank/forgot" exact>
      <ForgotPassword userType='bloodbank' />
    </Route>
    <Route path="/bloodbank/signup" exact>
      <BankSignUp />
    </Route>
    <Route>
      <Redirect to="/bloodbank/signin" />
    </Route>
  </Switch>)

  const signedIn = (
    <Typography>Hello BloodBank</Typography>
  )

  return isSignedIn ? signedIn : notSignedIn
}