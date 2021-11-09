import './App.css';
import ChemItemDashboard from './components/chemitem/ChemItemDashboard';
import Header from './components/layout/Header';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route, Switch} from "react-router-dom";
import AddChemItem from './components/chemitem/AddChemItem';
import { Provider } from 'react-redux'
import store from './store'
import Landing from './components/Landing';
import AddUser from './components/user/AddUser';
import Login from './components/login/Login';
import { refreshTokenAndUser } from './securityUtils/securityUtils';
import SecuredRoute from './securityUtils/SecuredRoute'
import UserDashboard from './components/user/UserDashboard';
import ChangePassword from './components/user/ChangePassword';
import UpdateUser from './components/user/UpdateUser';


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
            <SecuredRoute exact path="/change-password" component={ChangePassword} />

            <SecuredRoute exact path="/users" component={UserDashboard} />
            <SecuredRoute exact path="/add-user" component={AddUser} />
            <SecuredRoute exact path="/update-user/:id" component={UpdateUser} />

            <SecuredRoute exact path="/chem-items" component={ChemItemDashboard} />
            <SecuredRoute exact path="/add-chem-item" component={AddChemItem} />
            
            
          </Switch>

        </div>
      </Router>
    </Provider>


  );
}

export default App;
