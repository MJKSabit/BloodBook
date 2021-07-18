import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  Redirect
} from "react-router-dom";
import User from './user/User';
import { Provider } from 'react-redux';
import store from './store';
import Admin from './admin';

function App() {

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
    </Provider>
  );
}

export default App;
