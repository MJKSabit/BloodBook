import { Route, Switch } from "react-router-dom";

export default function Admin() {
  return (
    <Switch>
      <Route path='/admin/1'>
        1
      </Route>
      <Route path='/admin/2'>
        2
      </Route>
    </Switch>
  )
}