import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  Redirect
} from "react-router-dom";
import User from './user/User';

function App() {

  return (
    <Router>
      <Switch>
        <Route path='/user'>
          <User />
        </Route>
        <Route path='/admin'>
          Admin Page
        </Route>
        <Route path='/bloodbank'>
          BloodBank Page
        </Route>
        <Route>
          <Redirect to='/user' />
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
