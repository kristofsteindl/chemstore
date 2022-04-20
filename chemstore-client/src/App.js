import './App.css';
import Header from './components/layout/Header';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route, Switch} from "react-router-dom";
import AddChemItem from './components/chemitem/AddChemItem';
import { Provider } from 'react-redux'
import store from './store'
import Landing from './components/Landing';
import AddUser from './components/user/AddUser';
import Login from './components/login/Login';
import { refreshState } from './utils/securityUtils';
import SecuredRoute from './utils/SecuredRoute'
import UserDashboard from './components/user/UserDashboard';
import ChangePassword from './components/user/ChangePassword';
import UpdateUser from './components/user/UpdateUser';
import ManufacturerDashboard from './components/manufacturer/ManufacturerDashboard';
import AddManufacturer from './components/manufacturer/AddManufacturer';
import UpdateManufacturer from './components/manufacturer/UpdateManufacturer';
import ChemicalDashboard from './components/chemical/ChemicalDashboard';
import AddChemical from './components/chemical/AddChemical';
import UpdateChemical from './components/chemical/UpdateChemical';
import LabDashboard from './components/lab/LabDashboard';
import AddLab from './components/lab/AddLab';
import UpdateLab from './components/lab/UpdateLab';
import CategoryDashboard from './components/category/CategoryDashboard';
import AddCategory from './components/category/AddCategory';
import UpdateCategory from './components/category/UpdateCategory';
import ChemItemDashboard from './components/chemitem/ChemItemDashboard';
import UserManual from './components/layout/UserManual';
import ProjectDashboard from './components/project/ProjectDashboard';
import AddProject from './components/project/AddProject';
import UpdateProject from './components/project/UpdateProject';
import RecipeDashboard from './components/recipe/RecipeDashboard';
import AddUpdateRecipe from './components/recipe/AddUpdateRecipe';


refreshState()

function App() {
  return (
    <Provider store={store}>
      <Router>
        <div className="App">
          <Header />
          <Route exact path="/" component={Landing} />
          <Route exact path="/login" component={Login} />

          <Route exact path="/user-manual" component={UserManual} />
          
          <Switch>
            

            <SecuredRoute exact path="/change-password" component={ChangePassword} />

            <SecuredRoute exact path="/users" component={UserDashboard} />
            <SecuredRoute exact path="/add-user" component={AddUser} />
            <SecuredRoute exact path="/update-user/:id" component={UpdateUser} />

            <SecuredRoute exact path="/manufacturers" component={ManufacturerDashboard} />
            <SecuredRoute exact path="/add-manufacturer" component={AddManufacturer} />
            <SecuredRoute exact path="/update-manufacturer/:id" component={UpdateManufacturer} />

            <SecuredRoute exact path="/labs" component={LabDashboard} />
            <SecuredRoute exact path="/add-lab" component={AddLab} />
            <SecuredRoute exact path="/update-lab/:id" component={UpdateLab} />

            <SecuredRoute exact path="/chemicals" component={ChemicalDashboard} />
            <SecuredRoute exact path="/add-chemical" component={AddChemical} />
            <SecuredRoute exact path="/update-chemical/:id" component={UpdateChemical} />

            <SecuredRoute exact path="/categories" component={CategoryDashboard} />
            <SecuredRoute exact path="/add-category" component={AddCategory} />
            <SecuredRoute exact path="/update-category/:id" component={UpdateCategory} />

            <SecuredRoute exact path="/projects" component={ProjectDashboard} />
            <SecuredRoute exact path="/add-project" component={AddProject} />
            <SecuredRoute exact path="/update-project/:id" component={UpdateProject} />

            <SecuredRoute exact path="/recipes" component={RecipeDashboard} />
            <SecuredRoute exact path="/add-update-recipe" component={AddUpdateRecipe} />

            <SecuredRoute exact path="/chem-items" component={ChemItemDashboard} />
            <SecuredRoute exact path="/add-chem-item" component={AddChemItem} />
            
            
          </Switch>

        </div>
      </Router>
    </Provider>


  );
}

export default App;
