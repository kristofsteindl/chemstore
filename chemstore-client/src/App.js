import './App.css';
import Dashboard from './components/Dashboard';
import Header from './components/layout/Header';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route, Switch} from "react-router-dom";
import AddChemItem from './components/chemitem/AddChemItem';
import { Provider } from 'react-redux'
import store from './store'
import Landing from './components/Landing';
import Register from './components/usermanagement/Register';
import Login from './components/usermanagement/Login';
import { refreshTokenAndUser } from './securityUtils/securityUtils';
import SecuredRoute from './securityUtils/SecuredRoute'


refreshTokenAndUser()

function App() {
  return (
    <Provider store={store}>
      <Router>
        <div className="App">
          <Header />
          <Route exact path="/" component={Landing} />
          <Route exact path="/login" component={Login} />
          
          <Switch>
            <SecuredRoute exact path="/register" component={Register} />
            <SecuredRoute exact path="/dashboard" component={Dashboard} />
            <SecuredRoute exact path="/addChemItem" component={AddChemItem} />
          </Switch>

        </div>
      </Router>
    </Provider>


  );
}

export default App;
