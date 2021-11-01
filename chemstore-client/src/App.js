import './App.css';
import Dashboard from './components/Dashboard';
import Header from './components/layout/Header';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route} from "react-router-dom";
import AddChemItem from './components/chemitem/AddChemItem';
import { Provider } from 'react-redux'
import store from './store'
import Landing from './components/Landing';
import Register from './components/usermanagement/Register';
import Login from './components/usermanagement/Login';
import { refreshTokenAndUser } from './securityUtils/securityUtils';

refreshTokenAndUser()

function App() {
  return (
    <Provider store={store}>
      <Router>
        <div className="App">
          <Header />
          {
            //Public Routes
          }
          <Route exact path="/" component={Landing} />
          <Route exact path="/register" component={Register} />
          <Route exact path="/login" component={Login} />
          {
            //Private Routes
          }
          <Route exact path="/dashboard" component={Dashboard} />
          <Route exact path="/addChemItem" component={AddChemItem} />
        </div>
      </Router>
    </Provider>


  );
}

export default App;
